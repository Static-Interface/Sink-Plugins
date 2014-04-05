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

package de.static_interface.sinkchat.channel;

import de.static_interface.sinkchat.TownyBridge;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChannelUtil
{
    public static boolean sendMessage(Player player, String message, IChannel channel, String prefix, String callByChar)
    {

        //SinkLibrary.getCustomLogger().debug("sendMessage(\"" + player.getName() + "\", \"" + message + "\", \"" + channel.getChannelName() + "\", \"" + prefix + "\", \'" + callByChar + "\');");

        if ( channel.contains(player) )
        {
            return false;
        }

        if ( message.equals(callByChar) )
        {
            return false;
        }

        String formattedMessage = message.substring(1);
        User user = SinkLibrary.getUser(player);

        String nationPrefix = TownyBridge.getTownyPrefix(player);

        prefix = nationPrefix + prefix;

        if ( SinkLibrary.isPermissionsAvailable() )
        {
            formattedMessage = prefix + ChatColor.GRAY + '[' + user.getPrimaryGroup() + ChatColor.GRAY + "] " + user.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RESET + formattedMessage;
        }
        else
        {
            formattedMessage = prefix + user.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RESET + formattedMessage;
        }

        for ( Player target : Bukkit.getOnlinePlayers() )
        {
            if ( !(channel.contains(target)) && target.hasPermission(channel.getPermission()) )
            {
                target.sendMessage(formattedMessage);
            }
        }
        Bukkit.getConsoleSender().sendMessage(formattedMessage);
        SinkLibrary.sendIRCMessage(formattedMessage);
        return true;
    }
}
