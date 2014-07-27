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

import de.static_interface.sinkirc.IrcUtil;
import de.static_interface.sinkirc.SinkIRC;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;

public class IrcKickCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        SinkUser user = SinkLibrary.getUser(sender);

        if ( args.length < 1 )
        {
            return false;
        }

        String reason = "Kicked by " + user.getDisplayName() + " ingame";

        String target = args[0];

        if ( args.length > 1 )
        {
            reason = reason + ", reason: ";
            for ( int i = 1; i < args.length; i++ )
            {
                reason += args[i] + ' ';
            }
        }

        Channel channel = SinkIRC.getIrcBot().getUserBot().getChannels().first();
        channel.send().kick(IrcUtil.getUser(channel, target), reason);

        return true;
    }
}
