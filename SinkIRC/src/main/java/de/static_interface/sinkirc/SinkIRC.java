/*
 * Copyright (c) 2014 adventuria.eu / static-interface.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.static_interface.sinkirc;

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
        if ( !checkDependencies() ) return;

        if ( !SinkLibrary.getSettings().isIRCBotEnabled() )
        {
            return;
        }

        sinkIrcBot = new SinkIRCBot(this);
        SinkLibrary.registerPlugin(this);
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

        if ( !initialized )
        {
            Bukkit.getPluginManager().registerEvents(new IRCListener(sinkIrcBot), this);
            getCommand("irclist").setExecutor(new IrclistCommand());
            getCommand("ircmsg").setExecutor(new IrcPrivateMessageCommand());
            initialized = true;
        }
    }

    private boolean checkDependencies()
    {
        Plugin sinkLibrary = Bukkit.getPluginManager().getPlugin("SinkLibrary");
        Plugin sinkChat = Bukkit.getPluginManager().getPlugin("SinkChat");
        if ( sinkLibrary == null || sinkChat == null )
        {
            SinkLibrary.getCustomLogger().log(Level.WARNING, "This plugin requires SinkLibrary and SinkChat");
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
