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

package de.static_interface.sinkchat;

import de.static_interface.sinkchat.channel.IChannel;
import de.static_interface.sinkchat.channel.channels.HelpChannel;
import de.static_interface.sinkchat.channel.channels.ShoutChannel;
import de.static_interface.sinkchat.channel.channels.TradeChannel;
import de.static_interface.sinkchat.command.ChannelCommand;
import de.static_interface.sinkchat.command.NickCommand;
import de.static_interface.sinkchat.command.SpyCommands;
import de.static_interface.sinkchat.listener.ChatListenerHighest;
import de.static_interface.sinkchat.listener.ChatListenerLowest;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SinkChat extends JavaPlugin
{
    private static boolean initialized = false;

    public void onEnable()
    {
        if ( !checkDependencies() )
        {
            return;
        }

        SinkLibrary.registerPlugin(this);

        IChannel fc = new HelpChannel((String) SinkLibrary.getSettings().get("SinkChat.Channels.Help.Prefix"));
        IChannel sc = new ShoutChannel((String) SinkLibrary.getSettings().get("SinkChat.Channels.Shout.Prefix"));
        IChannel hc = new TradeChannel((String) SinkLibrary.getSettings().get("SinkChat.Channels.Trade.Prefix"));

        sc.registerChannel();
        hc.registerChannel();
        fc.registerChannel();

        if ( !initialized )
        {
            registerEvents();
            registerCommands();
            initialized = true;
        }

    }

    public void onDisable()
    {
        System.gc();
    }

    private boolean checkDependencies()
    {
        if ( Bukkit.getPluginManager().getPlugin("SinkLibrary") == null )
        {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new ChatListenerLowest(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListenerHighest(), this);
    }

    private void registerCommands()
    {
        getCommand("nick").setExecutor(new NickCommand());
        getCommand("channel").setExecutor(new ChannelCommand());
        getCommand("enablespy").setExecutor(new SpyCommands.EnableSpyCommand());
        getCommand("disablespy").setExecutor(new SpyCommands.DisablSpyCommand());
        //getCommand("privatechannel").setExecutor(new PrivateChannelCommand());
    }
}