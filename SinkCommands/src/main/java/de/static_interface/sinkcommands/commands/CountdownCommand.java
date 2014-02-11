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

package de.static_interface.sinkcommands.commands;

import de.static_interface.sinklibrary.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CountDownCommand implements CommandExecutor
{
    Plugin plugin;
    static long secondsLeft;

    public CountDownCommand(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public static final String PREFIX = ChatColor.DARK_RED + "[" + ChatColor.RED + "CountDown" + ChatColor.DARK_RED + "] " + ChatColor.RESET;

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
    {
        if ( args.length < 2 )
        {
            sender.sendMessage(PREFIX + ChatColor.RED + "Falsche Benutzung! /cd <Sekunden> <Grund>");
            return true;
        }

        if ( secondsLeft > 0 )
        {
            sender.sendMessage(PREFIX + ChatColor.RED + "Es läuft bereits ein Countdown!");
        }

        String secondsString = args[0];
        long seconds;
        try
        {
            seconds = Long.parseLong(secondsString);
        }
        catch ( NumberFormatException ignored )
        {
            sender.sendMessage(PREFIX + ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + '"' + secondsString + "\" ist keine gueltige Zahl!");
            return true;
        }

        if ( seconds < 0 )
        {
            sender.sendMessage(PREFIX + ChatColor.RED + "Countdown darf nicht negative sein!");
            return true;
        }

        if ( seconds > 60 )
        {
            sender.sendMessage(PREFIX + ChatColor.RED + "Max Zeit: 60 Sekunden");
            return true;
        }

        String message = "";
        for ( int i = 1; i < args.length; i++ )
        {
            message += ' ' + args[i];
        }
        message = message.trim();
        secondsLeft = seconds;
        BukkitUtil.broadcastMessage(PREFIX + ChatColor.GOLD + "Countdown für " + message + " gestartet!", true);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if ( secondsLeft <= 0 )
                {
                    secondsLeft = 0;
                    BukkitUtil.broadcastMessage(PREFIX + ChatColor.GREEN + "Los!", true);
                    cancel();
                    return;
                }
                if ( secondsLeft > 10 )
                {
                    if ( secondsLeft % 10 == 0 )
                    {
                        BukkitUtil.broadcastMessage(PREFIX + ChatColor.RED + secondsLeft + "...", true);
                    }
                }
                else
                {
                    BukkitUtil.broadcastMessage(PREFIX + ChatColor.DARK_RED + secondsLeft, true);
                }
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0, 20L);
        return true;
    }
}
