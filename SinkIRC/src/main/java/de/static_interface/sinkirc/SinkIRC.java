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
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.logging.Level;

public class SinkIRC extends JavaPlugin {

    static PircBotX ircBot;
    static String mainChannel;
    private static boolean initialized = false;
    Thread ircThread;

    public static PircBotX getIrcBot() {
        return ircBot;
    }

    public static Channel getMainChannel() {
        return IrcUtil.getChannel(mainChannel);
    }

    @Override
    public void onEnable() {
        if (!checkDependencies() || initialized) {
            return;
        }

        ircThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainChannel = SinkLibrary.getInstance().getSettings().getIRCChannel();

                    Configuration.Builder<PircBotX> configBuilder = new Configuration.Builder<>()
                            .setName(SinkLibrary.getInstance().getSettings().getIRCBotUsername())
                            .setLogin("SinkIRC")
                            .setAutoNickChange(true)
                            .setAutoReconnect(true)
                            .setServer(SinkLibrary.getInstance().getSettings().getIRCAddress(), SinkLibrary.getInstance().getSettings().getIRCPort())
                            .addListener(new PircBotXLinkListener())
                            .setVersion("SinkIRC for Bukkit - visit http://dev.bukkit.org/bukkit-plugins/sink-plugins/");

                    if (SinkLibrary.getInstance().getSettings().isIRCPasswordEnabled()) {
                        configBuilder = configBuilder.setServerPassword(SinkLibrary.getInstance().getSettings().getIRCPassword());
                    }
                    if (!SinkLibrary.getInstance().getSettings().isIRCAuthentificationEnabled()) {
                        configBuilder = configBuilder.addAutoJoinChannel(mainChannel);
                    }

                    ircBot = new PircBotX(configBuilder.buildConfiguration());
                    ircBot.startBot();
                    if (SinkLibrary.getInstance().getSettings().isIRCAuthentificationEnabled()) {
                        ircBot.sendIRC().message(SinkLibrary.getInstance().getSettings().getIRCAuthBot(),
                                                 SinkLibrary.getInstance().getSettings().getIRCAuthMessage());
                        Thread.sleep(1000); //Todo
                    }
                    ircBot.sendIRC().joinChannel(SinkLibrary.getInstance().getSettings().getIRCChannel());
                } catch (IOException | IrcException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ircThread.start();

        Bukkit.getPluginManager().registerEvents(new IrcListener(), this);

        getCommand("irckick").setExecutor(new IrcKickCommand());

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
    }
}
