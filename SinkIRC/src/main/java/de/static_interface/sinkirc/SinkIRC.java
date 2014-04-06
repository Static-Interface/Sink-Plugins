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

import de.static_interface.sinkirc.commands.IrcKickCommand;
import de.static_interface.sinkirc.commands.IrcPrivateMessageCommand;
import de.static_interface.sinkirc.commands.IrclistCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.IrcException;

import java.io.IOException;
import java.util.logging.Level;

public class SinkIRC extends JavaPlugin
{
    private static boolean initialized = false;

    static SinkIRCBot sinkIrcBot;
    static String mainChannel;

    @Override
    public void onEnable()
    {
        if ( !checkDependencies() || !SinkLibrary.getSettings().isIRCBotEnabled() || initialized ) return;

        sinkIrcBot = new SinkIRCBot(this);

        new Thread()
        {
            @Override
            public void run()
            {
                mainChannel = SinkLibrary.getSettings().getIRCChannel();
                String host = SinkLibrary.getSettings().getIRCAddress();
                int port = SinkLibrary.getSettings().getIRCPort();
                boolean usingPassword = SinkLibrary.getSettings().isIRCPasswordEnabled();

                try
                {
                    if ( usingPassword )
                    {
                        String password = SinkLibrary.getSettings().getIRCPassword();
                        sinkIrcBot.connect(host, port, password);
                    }
                    else
                    {
                        sinkIrcBot.connect(host, port);
                    }

                    if ( SinkLibrary.getSettings().isIRCAuthentificationEnabled() )
                    {
                        String authBot = SinkLibrary.getSettings().getIRCAuthBot();
                        String authMessage = SinkLibrary.getSettings().getIRCAuthMessage();
                        sinkIrcBot.sendMessage(authBot, authMessage);
                        SinkLibrary.getCustomLogger().debug("Thread sleep for 3000ms");
                        Thread.sleep(3000);
                    }
                    sinkIrcBot.joinChannel(mainChannel);
                }
                catch ( IOException | IrcException e )
                {
                    SinkLibrary.getCustomLogger().severe("An Exception occurred while trying to connect to " + host + ':');
                    SinkLibrary.getCustomLogger().severe(e.getMessage());
                }
                catch ( InterruptedException ignored )
                {
                    sinkIrcBot.joinChannel(mainChannel);
                }
            }
        }.start();

        Bukkit.getPluginManager().registerEvents(new IRCListener(sinkIrcBot), this);
        getCommand("irclist").setExecutor(new IrclistCommand());
        getCommand("ircprivatemessage").setExecutor(new IrcPrivateMessageCommand());
        getCommand("irckick").setExecutor(new IrcKickCommand(sinkIrcBot));
        SinkLibrary.registerPlugin(this);
        initialized = true;
    }

    private boolean checkDependencies()
    {
        Plugin sinkLibrary = Bukkit.getPluginManager().getPlugin("SinkLibrary");
        Plugin sinkChat = Bukkit.getPluginManager().getPlugin("SinkChat");
        if ( sinkLibrary == null || sinkChat == null )
        {
            Bukkit.getLogger().log(Level.WARNING, "This plugin requires SinkLibrary and SinkChat");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    @Override
    public void onDisable()
    {
        sinkIrcBot.quitServer("Plugin is reloading or server is shutting down...");
        System.gc();
    }

    public static SinkIRCBot getIRCBot()
    {
        return sinkIrcBot;
    }

    public static String getMainChannel()
    {
        return mainChannel;
    }
}
