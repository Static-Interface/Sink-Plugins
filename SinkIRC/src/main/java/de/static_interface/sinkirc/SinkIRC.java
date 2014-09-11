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
import de.static_interface.sinkirc.irc_commands.*;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.exceptions.NotInitializedException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.logging.Level;

public class SinkIRC extends JavaPlugin
{
    private static boolean initialized = false;

    static PircBotX ircBot;
    static String mainChannel;
    Thread ircThread;
    @Override
    public void onEnable()
    {
        if ( !checkDependencies() || initialized ) return;

        ircThread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    mainChannel = SinkLibrary.getSettings().getIRCChannel();

                    Configuration.Builder<PircBotX> configBuilder = new Configuration.Builder<PircBotX>()
                            .setName(SinkLibrary.getSettings().getIRCBotNickname())
                            .setLogin("SinkIRC")
                            .setAutoNickChange(true)
                            .setAutoReconnect(true)
                            .setServer(SinkLibrary.getSettings().getIRCAddress(), SinkLibrary.getSettings().getIRCPort())
                            .addListener(new PircBotXLinkListener())
                            .setVersion("SinkIRC for Bukkit - visit http://dev.bukkit.org/bukkit-plugins/sink-plugins/");

                    if(SinkLibrary.getSettings().isIRCPasswordEnabled())
                    {
                        configBuilder = configBuilder.setServerPassword(SinkLibrary.getSettings().getIRCPassword());
                    }
                    if(!SinkLibrary.getSettings().isIRCAuthentificationEnabled())
                    {
                        configBuilder = configBuilder.addAutoJoinChannel(mainChannel);
                    }

                    ircBot = new PircBotX(configBuilder.buildConfiguration());
                    ircBot.startBot();
                    if(SinkLibrary.getSettings().isIRCAuthentificationEnabled())
                    {
                        ircBot.sendIRC().message(SinkLibrary.getSettings().getIRCAuthBot(), SinkLibrary.getSettings().getIRCAuthMessage());
                        try
                        {
                            Thread.sleep(1000); //Todo
                        }
                        catch ( InterruptedException e )
                        {
                            e.printStackTrace();
                        }
                        ircBot.sendIRC().joinChannel(SinkLibrary.getSettings().getIRCChannel());
                    }
                }
                catch ( IOException | IrcException e )
                {
                    e.printStackTrace();
                }
            }
        });
        ircThread.start();

        Bukkit.getPluginManager().registerEvents(new IrcListener(), this);

        getCommand("ircprivatemessage").setExecutor(new IrcPrivateMessageCommand());
        getCommand("irckick").setExecutor(new IrcKickCommand());

        SinkLibrary.registerCommand("help", new HelpCommand(this));
        SinkLibrary.registerCommand("exec", new ExecCommand(this));
        SinkLibrary.registerCommand("say", new SayCommand(this));
        SinkLibrary.registerCommand("kick", new KickCommand(this));
        SinkLibrary.registerCommand("msg", new MsgCommand(this));
        SinkLibrary.registerCommand("list", new ListCommand(this));
        SinkLibrary.registerCommand("set", new SetCommand(this));

        SinkLibrary.registerPlugin(this);
        initialized = true;
    }

    private boolean checkDependencies()
    {
        Plugin sinkLibrary = Bukkit.getPluginManager().getPlugin("SinkLibrary");
        if ( sinkLibrary == null )
        {
            Bukkit.getLogger().log(Level.WARNING, "This plugin requires SinkLibrary");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        if ( !SinkLibrary.initialized )
        {
            throw new NotInitializedException();
        }
        return true;
    }

    @Override
    public void onDisable()
    {
        if ( ircBot != null) ircBot.sendIRC().quitServer("Plugin is reloading or server is shutting down...");
        if(!ircThread.isInterrupted()) ircThread.interrupt();
        ircThread = null;
        ircBot = null;
        System.gc();
    }

    public static PircBotX getIrcBot()
    {
        return ircBot;
    }

    public static Channel getMainChannel()
    {
        return IrcUtil.getChannel(mainChannel);
    }
}
