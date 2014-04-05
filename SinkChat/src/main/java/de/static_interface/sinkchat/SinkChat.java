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

package de.static_interface.sinkchat;

import com.palmergames.bukkit.towny.Towny;
import de.static_interface.sinkchat.channel.IChannel;
import de.static_interface.sinkchat.channel.channels.HelpChannel;
import de.static_interface.sinkchat.channel.channels.ShoutChannel;
import de.static_interface.sinkchat.channel.channels.TradeChannel;
import de.static_interface.sinkchat.command.ChannelCommand;
import de.static_interface.sinkchat.command.NickCommand;
import de.static_interface.sinkchat.command.PrivateChannelCommand;
import de.static_interface.sinkchat.command.SpyCommands;
import de.static_interface.sinkchat.listener.ChatListenerHighest;
import de.static_interface.sinkchat.listener.ChatListenerLowest;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SinkChat extends JavaPlugin
{
    private static boolean initialized = false;
    private static Towny towny;

    public void onEnable()
    {
        if ( !checkDependencies() )
        {
            return;
        }

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
            SinkLibrary.registerPlugin(this);
            initialized = true;
        }
    }

    public void onDisable()
    {
        System.gc();
    }

    public static boolean isTownyAvailable()
    {
        return towny != null;
    }

    public static Towny getTowny()
    {
        return towny;
    }

    private boolean checkDependencies()
    {
        if ( Bukkit.getPluginManager().getPlugin("SinkLibrary") == null )
        {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        Plugin tmp = Bukkit.getPluginManager().getPlugin("Towny");
        if ( tmp != null && tmp instanceof Towny )
        {
            towny = (Towny) tmp;
        }
        else
        {
            towny = null;
            SinkLibrary.getCustomLogger().log(Level.INFO, "Towny not found. Disabling Towny Chat features");
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
        getCommand("privatechannel").setExecutor(new PrivateChannelCommand());
    }
}