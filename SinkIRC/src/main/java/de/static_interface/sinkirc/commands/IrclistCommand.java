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

package de.static_interface.sinkirc.commands;

import de.static_interface.sinkirc.SinkIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jibble.pircbot.User;

public class IrclistCommand implements CommandExecutor
{
    public static final String PREFIX = ChatColor.YELLOW + "[IRC] " + ChatColor.RESET;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        User[] users = SinkIRC.getIRCBot().getUsers(SinkIRC.getMainChannel());
        String message = "";
        for ( User user : users )
        {
            String name = user.getNick();
            if ( name.equals(SinkIRC.getIRCBot().getNick()) )
            {
                continue;
            }
            if ( user.isOp() )
            {
                name = ChatColor.RED + name + ChatColor.RESET;
            }
            if ( message.isEmpty() )
            {
                message = name;
            }
            else
            {
                message = message + ", " + name;
            }
        }
        if ( users.length <= 1 )
        {
            message = "Zur Zeit sind keine Benutzer im IRC.";
        }
        sender.sendMessage(PREFIX + "Online IRC Benutzer: " + message);
        return true;
    }
}
