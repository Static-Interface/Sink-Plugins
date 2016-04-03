/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc;

import de.static_interface.sinkirc.command.IrcKickCommand;
import de.static_interface.sinkirc.config.SiSettings;
import de.static_interface.sinkirc.irc_command.ExecCommand;
import de.static_interface.sinkirc.irc_command.KickCommand;
import de.static_interface.sinkirc.irc_command.ListCommand;
import de.static_interface.sinkirc.irc_command.SayCommand;
import de.static_interface.sinkirc.irc_command.SetCommand;
import de.static_interface.sinkirc.queue.IrcQueue;
import de.static_interface.sinkirc.stream.IrcMessageStream;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SinkIRC extends JavaPlugin {

    private static SinkIRC instance;
    boolean threadStarted = false;
    private PircBotX ircBot;
    private boolean initialized = false;
    private Thread ircThread;
    private Map<String, List<String>> streamRedirects = new HashMap<>();
    private long lastCacheUpdate;

    public static SinkIRC getInstance() {
        return instance;
    }

    public Collection<Channel> getJoinedChannels() {
        return SinkIRC.getInstance().getIrcBot().getUserBot().getChannels();
    }

    public PircBotX getIrcBot() {
        return ircBot;
    }

    @Override
    public void onEnable() {
        if (!checkDependencies() || initialized) {
            return;
        }

        instance = this;

        File sinkIrcDirectory = new File(SinkLibrary.getInstance().getCustomDataFolder(), "SinkIRC");
        new SiSettings(new File(sinkIrcDirectory, "Settings.yml")).init();
        final Map<String, String> channelsWithPasswords = new HashMap<>();
        for (String s : SiSettings.SI_SERVER_CHANNELS.getValue()) {
            if (StringUtil.isEmptyOrNull(s)) {
                continue;
            }
            String[] parts = s.split(":");
            String channelName = parts[0];
            if (!channelName.startsWith("#")) {
                channelName = "#" + channelName;
            }
            String password = "";
            if (parts.length > 1) {
                password = StringUtil.formatArrayToString(parts, ":", 1);
            }
            channelsWithPasswords.put(channelName, password);
        }

        if (channelsWithPasswords.size() == 0) {
            throw new IllegalStateException("No channels configured!");
        }

        ircThread = new Thread(() -> {
            try {
                if (threadStarted) {
                    return;
                }

                threadStarted = true;
                Configuration.Builder<PircBotX> configBuilder = new Configuration.Builder<>()
                        .setName(SiSettings.SI_NICKNAME.getValue())
                        .setLogin("SinkIRC")
                        .setAutoNickChange(true)
                        .setMessageDelay(1)
                        .setShutdownHookEnabled(true)
                        .setServer(SiSettings.SI_SERVER_ADDRESS.getValue(),
                                   SiSettings.SI_SERVER_PORT.getValue())
                        .addListener(new PircBotXLinkListener())
                        .setVersion("SinkIRC for Bukkit - visit http://dev.bukkit.org/bukkit-plugins/sink-plugins/");

                String password = SiSettings.SI_SERVER_PASSWORD.getValue();
                if (!StringUtil.isEmptyOrNull(password)) {
                    configBuilder = configBuilder.setServerPassword(password);
                }
                if (!SiSettings.SI_AUTHENTIFICATION_ENABLED.getValue()) {
                    for (String s : channelsWithPasswords.keySet()) {
                        configBuilder = configBuilder.addAutoJoinChannel(s, channelsWithPasswords.get(s));
                    }
                }

                ircBot = new PircBotX(configBuilder.buildConfiguration());
                try {
                    ircBot.startBot();
                } catch (SocketException e) {
                    getLogger().log(Level.SEVERE,
                                    "Couldn't connect to host: " + SiSettings.SI_SERVER_ADDRESS.getValue(), e);
                    Bukkit.getPluginManager().disablePlugin(SinkIRC.this);
                    return;
                }
                WaitForQueue queue = new WaitForQueue(ircBot);
                if (SiSettings.SI_AUTHENTIFICATION_ENABLED.getValue()) {
                    ircBot.sendIRC().message(SiSettings.SI_AUTHENTIFICATION_AUTHBOT.getValue(),
                                             SiSettings.SI_AUTHENTIFICATION_MESSAGE.getValue());
                    //Wait for AuthBot reply before joining
                    while (true) {
                        PrivateMessageEvent event;
                        try {
                            event = queue.waitFor(PrivateMessageEvent.class);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }

                        if (event.getUser().getNick().equals(SiSettings.SI_AUTHENTIFICATION_AUTHBOT.getValue())) {
                            break;
                        }
                    }
                    for (String s : channelsWithPasswords.keySet()) {
                        ircBot.sendIRC().joinChannel(s, channelsWithPasswords.get(s));
                    }
                }

                List<String> waitQueue = new ArrayList<>(SiSettings.SI_SERVER_CHANNELS.getValue());

                //wait for bot join
                while (true) {
                    JoinEvent event;
                    try {
                        event = queue.waitFor(JoinEvent.class);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                    if (event.getUser().getNick().equals(getIrcBot().getNick()) && waitQueue.contains(event.getChannel().getName())) {
                        waitQueue.remove(event.getChannel().getName());
                        SinkLibrary.getInstance().registerMessageStream(new IrcMessageStream(event.getChannel().getName()));
                    }

                    if (waitQueue.size() == 0) {
                        break;
                    }
                }
            } catch (IOException | IrcException e) {
                e.printStackTrace();
            }
        });
        ircThread.start();

        IrcQueue.getInstance().start();
        Bukkit.getPluginManager().registerEvents(new IrcListener(), this);

        de.static_interface.sinklibrary.api.configuration.Configuration
                commandsConfig = new de.static_interface.sinklibrary.api.configuration.Configuration(new File(sinkIrcDirectory, "Commands.yml"));
        commandsConfig.init();

        Debug.log("Parsing streams...");

        SinkLibrary.getInstance().registerCommand("irckick", new IrcKickCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("exec", new ExecCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("say", new SayCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("kick", new KickCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("list", new ListCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("set", new SetCommand(this, commandsConfig));
        initialized = true;
    }

    private boolean checkDependencies() {
        Plugin sinkLibrary = Bukkit.getPluginManager().getPlugin("SinkLibrary");
        if (sinkLibrary == null) {
            Bukkit.getLogger().log(Level.WARNING, "This plugin requires SinkLibrary");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        return SinkLibrary.getInstance().validateApiVersion(SinkLibrary.API_VERSION, this);
    }

    @Override
    public void onDisable() {
        IrcQueue.getInstance().stop();

        if (ircBot != null) {
            ircBot.sendIRC().quitServer("Disabling...");
            try {
                Class clazz = ircBot.getClass();
                Method m = clazz.getDeclaredMethod("shutdown", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(ircBot, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ircThread != null && !ircThread.isInterrupted()) {
            ircThread.interrupt();
        }
        ircThread = null;
        ircBot = null;
        instance = null;
    }

    public Map<String, List<String>> getStreamRedirects() {
        if (lastCacheUpdate + (1000 * 10) <= System.currentTimeMillis()) {
            streamRedirects.clear();
            for (String s : SiSettings.SI_SERVER_STREAMS.getValue()) {
                if (StringUtil.isEmptyOrNull(s)) {
                    continue;
                }
                s = s.trim();
                String[] parts = s.split("\\Q>>\\E");
                if (parts.length != 2) {
                    SinkIRC.getInstance().getLogger().warning("Error at parsing config: " + s + ": invalid format: \"" + s + "\"");
                    continue;
                }

                String source = parts[0].trim();

                boolean isSourceChannel = source.startsWith("#");
                boolean isSourceStream = SinkLibrary.getInstance().getMessageStream(source) != null;
                if (!isSourceChannel && !isSourceStream) {
                    SinkIRC.getInstance().getLogger()
                            .warning("Error at parsing config: " + s + ": source channel or stream \"" + source + "\" not found");
                    continue;
                }

                String target = parts[1].trim();
                boolean isTargetChannel = target.startsWith("#");
                if (isSourceChannel && isTargetChannel) {
                    SinkIRC.getInstance().getLogger().warning("Error at parsing config: " + s + ": source & target are both channels");
                    continue;
                }

                boolean isTargetStream = SinkLibrary.getInstance().getMessageStream(target) != null;
                if (isSourceStream && isTargetStream) {
                    SinkIRC.getInstance().getLogger().warning("Error at parsing config: " + s + ": source & target are both streams");
                    continue;
                }

                if (!isTargetChannel && !isTargetStream) {
                    SinkIRC.getInstance().getLogger()
                            .warning("Error at parsing config: " + s + ": target channel or stream \"" + target + "\" not found");
                    continue;
                }

                List<String> targets = streamRedirects.get(source);
                if (targets == null) {
                    targets = new ArrayList<>();
                }
                targets.add(target);
                streamRedirects.put(source, targets);
            }
            lastCacheUpdate = System.currentTimeMillis();
        }
        return streamRedirects;
    }
}
