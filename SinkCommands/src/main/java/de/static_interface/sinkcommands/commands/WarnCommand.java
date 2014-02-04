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
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;

public class WarnCommand implements CommandExecutor
{
    public static final String PREFIX = ChatColor.RED + "[Warn] " + ChatColor.RESET;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if ( args.length < 1 )
        {
            sender.sendMessage(PREFIX + ChatColor.RED + "Zu wenige Argumente!");
            sender.sendMessage(PREFIX + ChatColor.RED + "Benutzung: " + COMMAND_PREFIX + "warn [Spieler] (Grund)");
            return false;
        }
        Player target = (BukkitUtil.getPlayer(args[0]));
        if ( target == null )
        {
            sender.sendMessage(PREFIX + args[0] + " ist nicht online!");
            return true;
        }
        if ( target.getDisplayName().equals(BukkitUtil.getSenderName(sender)) )
        {
            sender.sendMessage(PREFIX + "Du kannst dich nicht selbst verwarnen!");
            return true;
        }
        if ( args.length == 1 )
        {
            sender.sendMessage(PREFIX + "Du musst einen Grund angeben!");
            return false;
        }

        String reason = "";

        for ( int i = 1; i < args.length; i++ )
        {
            if ( reason.isEmpty() )
            {
                reason = args[i];
                continue;
            }
            reason = reason + ' ' + args[i];
        }

        target.sendMessage(PREFIX + ChatColor.RED + "Du wurdest von " + BukkitUtil.getSenderName(sender) + ChatColor.RED + " verwarnt. Grund: " + reason);
        BukkitUtil.broadcast(PREFIX + SinkLibrary.getUser(target).getDisplayName() + " wurde von " + BukkitUtil.getSenderName(sender) + " verwarnt. Grund: " + reason, "sinkcommands.warn.message", true);
        return true;
    }
}
