/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc;

import de.static_interface.sinkirc.command.IrcKickCommand;
import de.static_interface.sinkirc.irc_command.ExecCommand;
import de.static_interface.sinkirc.irc_command.HelpCommand;
import de.static_interface.sinkirc.irc_command.KickCommand;
import de.static_interface.sinkirc.irc_command.ListCommand;
import de.static_interface.sinkirc.irc_command.SayCommand;
import de.static_interface.sinkirc.irc_command.SetCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.IOException;
import java.util.logging.Level;

public class SinkIRC extends JavaPlugin {

    private static SinkIRC instance;
    private PircBotX ircBot;
    private String mainChannel;
    private boolean initialized = false;
    private Thread ircThread;

    public static SinkIRC getInstance() {
        return instance;
    }

    public PircBotX getIrcBot() {
        return ircBot;
    }

    public Channel getMainChannel() {
        return IrcUtil.getChannel(mainChannel);
    }

    @Override
    public void onEnable() {
        if (!checkDependencies() || initialized) {
            return;
        }

        instance = this;

        ircThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainChannel = SinkLibrary.getInstance().getSettings().getIrcChannel();

                    Configuration.Builder<PircBotX> configBuilder = new Configuration.Builder<>()
                            .setName(SinkLibrary.getInstance().getSettings().getIrcBotUsername())
                            .setLogin("SinkIRC")
                            .setAutoNickChange(true)
                            .setAutoReconnect(true)
                            .setServer(SinkLibrary.getInstance().getSettings().getIrcAddress(), SinkLibrary.getInstance().getSettings().getIrcPort())
                            .addListener(new PircBotXLinkListener())
                            .setVersion("SinkIRC for Bukkit - visit http://dev.bukkit.org/bukkit-plugins/sink-plugins/");

                    if (SinkLibrary.getInstance().getSettings().isIrcPasswordEnabled()) {
                        configBuilder = configBuilder.setServerPassword(SinkLibrary.getInstance().getSettings().getIrcPassword());
                    }
                    if (!SinkLibrary.getInstance().getSettings().isIrcAuthentificationEnabled()) {
                        configBuilder = configBuilder.addAutoJoinChannel(mainChannel);
                    }

                    ircBot = new PircBotX(configBuilder.buildConfiguration());
                    ircBot.startBot();

                    WaitForQueue queue = new WaitForQueue(ircBot);
                    if (SinkLibrary.getInstance().getSettings().isIrcAuthentificationEnabled()) {
                        ircBot.sendIRC().message(SinkLibrary.getInstance().getSettings().getIrcAuthBot(),
                                                 SinkLibrary.getInstance().getSettings().getIrcAuthMessage());
                        //Wait for AuthBot reply before joining
                        while (true) {
                            PrivateMessageEvent event;
                            try {
                                event = queue.waitFor(PrivateMessageEvent.class);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }

                            if (event.getUser().getNick().equals(SinkLibrary.getInstance().getSettings().getIrcAuthBot())) {
                                ircBot.sendIRC().joinChannel(SinkLibrary.getInstance().getSettings().getIrcChannel());
                                break;
                            }
                        }
                    }

                    //wait for bot join to main channel
                    while (true) {
                        JoinEvent event;
                        try {
                            event = queue.waitFor(JoinEvent.class);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }

                        if (event.getUser().getNick().equals(getIrcBot().getNick()) &&
                            event.getChannel().getName().equals(getMainChannel().getName())) {

                            for (User user : getMainChannel().getUsers()) {
                                SinkLibrary.getInstance().loadIrcUser(user);
                            }
                            break;
                        }
                    }

                } catch (IOException | IrcException e) {
                    e.printStackTrace();
                }
            }
        });
        ircThread.start();

        Bukkit.getPluginManager().registerEvents(new IrcListener(), this);

        SinkLibrary.getInstance().registerCommand("irckick", new IrcKickCommand(this));
        SinkLibrary.getInstance().registerCommand("help", new HelpCommand(this));
        SinkLibrary.getInstance().registerCommand("exec", new ExecCommand(this));
        SinkLibrary.getInstance().registerCommand("say", new SayCommand(this));
        SinkLibrary.getInstance().registerCommand("kick", new KickCommand(this));
        SinkLibrary.getInstance().registerCommand("list", new ListCommand(this));
        SinkLibrary.getInstance().registerCommand("set", new SetCommand(this));

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
        if (ircBot != null) {
            ircBot.sendIRC().quitServer("Plugin is reloading or server is shutting down...");
        }
        if (ircThread != null && !ircThread.isInterrupted()) {
            ircThread.interrupt();
        }
        ircThread = null;
        ircBot = null;
        instance = null;
    }
}
