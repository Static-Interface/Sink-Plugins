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

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.Util;
import de.static_interface.sinklibrary.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class RawCommands
{
    public static class RawCommand extends Command
    {
        public RawCommand(Plugin plugin)
        {
            super(plugin);
        }

        @Override
        public boolean isIrcOpOnly()
        {
            return true;
        }

        @Override
        public boolean onExecute(CommandSender sender, String label, String[] args)
        {
            if ( args.length < 1 )
            {
                return false;
            }
            BukkitUtil.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Util.formatArrayToString(args, " ")));
            return true;
        }
    }

    public static class RawUserCommand extends Command
    {
        public RawUserCommand(Plugin plugin)
        {
            super(plugin);
        }

        @Override
        public boolean isIrcOpOnly()
        {
            return true;
        }

        @Override
        public boolean onExecute(CommandSender sender, String label, String[] args)
        {
            if ( args.length < 1 )
            {
                return false;
            }
            SinkUser target = SinkLibrary.getUser(args[0]);

            if ( !target.isOnline() )
            {
                sender.sendMessage(ChatColor.RED + "User ist nicht online!");
            }

            String message = "";

            for ( int i = 1; i < args.length; i++ )
            {
                message += args[i] + ' ';
            }
            message = message.trim();

            target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }
    }
}



