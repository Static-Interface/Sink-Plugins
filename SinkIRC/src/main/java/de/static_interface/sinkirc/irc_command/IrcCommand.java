/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc.irc_command;

import de.static_interface.sinkirc.IrcListener;
import de.static_interface.sinklibrary.command.Command;
import de.static_interface.sinklibrary.sender.IrcCommandSender;
import org.bukkit.plugin.Plugin;

public abstract class IrcCommand extends Command {

    protected static final String IRC_PREFIX = IrcListener.IRC_PREFIX;

    public IrcCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isIrcOnly() {
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    public boolean isQueryCommand() {
        assert sender instanceof IrcCommandSender;

        IrcCommandSender ircSender = (IrcCommandSender) sender;

        return !ircSender.getSource().startsWith("#");
    }
}

/*

            //if ( command.equals("debug") )
            //{
            //    if ( !isOp ) throw new UnauthorizedAccessException();
            //    sender.sendMessage(Colors.BLUE + "Debug Output: ");
            //    String values = "";
            //    for ( de.static_interface.sinklibrary.User u : SinkLibrary.getOnlineUsers() )
            //    {
            //        String user = u.getName();
            //        String tmp = '<' + user + ',' + ChatColor.stripColor(u.getDisplayName()) + '>';
            //        if ( values.isEmpty() )
            //        {
            //            values = tmp;
            //            continue;
            //        }
            //        values = values + ", " + tmp;
            //    }
            //    sender.sendMessage("HashMap Values: " + values);
            //}

 */
