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

import de.static_interface.sinkafk.AFKCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListCommand implements CommandExecutor
{
    public static boolean activateAwayListening = false;

    @Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
    {
        String msg = "";

        for ( User user : SinkLibrary.getOnlineUsers() )
        {
            if ( activateAwayListening && AFKCommand.isUserAfk(user) )
            {
                msg = String.format("%s, [AFK] [%s] %s%s", msg, user.getPrimaryGroup(), user.getPrefix(), ChatColor.RESET);
                continue;
            }

            msg = String.format("%s, [%s] %s%s", msg, user.getPrimaryGroup(), user.getPrefix(), ChatColor.RESET);
        }
        return true;
    }
}
