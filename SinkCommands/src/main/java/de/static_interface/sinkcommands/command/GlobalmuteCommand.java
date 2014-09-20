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

import de.static_interface.sinkcommands.SinkCommands;
import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class GlobalmuteCommand extends Command
{
    public static final String PREFIX = ChatColor.DARK_RED + "[GlobalMute] " + ChatColor.RESET;

    public GlobalmuteCommand(Plugin plugin)
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
        SinkCommands.globalmuteEnabled = !SinkCommands.globalmuteEnabled;

        if ( SinkCommands.globalmuteEnabled )
        {
            if ( args.length > 0 )
            {
                String reason = "";
                for ( String arg : args )
                {
                    if ( reason.isEmpty() )
                    {
                        reason = arg;
                        continue;
                    }
                    reason = reason + ' ' + arg;
                }
                BukkitUtil.broadcastMessage(PREFIX + "Der globale Mute wurde von " + BukkitUtil.getSenderName(sender) + " aktiviert. Grund: " + reason + ". Alle Spieler sind jetzt stumm.");
            }
            else
            {
                BukkitUtil.broadcastMessage(PREFIX + "Der global Mute wurde von " + BukkitUtil.getSenderName(sender) + " aktiviert. Alle Spieler sind jetzt stumm.");
            }
            SinkCommands.globalmuteEnabled = true;
            return true;
        }
        else
        {
            BukkitUtil.broadcastMessage(PREFIX + "Der global Mute wurde von " + BukkitUtil.getSenderName(sender) + " deaktiviert.");
        }
        return true;
    }
}
