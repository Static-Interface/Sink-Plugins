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
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommands
{
    public static String PREFIX = ChatColor.DARK_GREEN + "[Statistiken] " + ChatColor.RESET;

    public static class EnableStatsCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            SinkUser user = SinkLibrary.getUser(sender);

            if ( user.isConsole() )
            {
                sender.sendMessage("This command is only ingame available.");
                return true;
            }
            Player player = user.getPlayer();

            PlayerConfiguration config = user.getPlayerConfiguration();

            if ( config.isStatsEnabled() )
            {
                player.sendMessage(PREFIX + ChatColor.RED + "Die Statistiken sind schon aktiviert!");
                return true;
            }

            config.setStatsEnabled(true);
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Die Statistiken wurden aktiviert.");
            SinkCommands.refreshScoreboard(player, -1);
            return true;
        }
    }

    public static class DisableStatsCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            SinkUser user = SinkLibrary.getUser(sender);

            if ( user.isConsole() )
            {
                sender.sendMessage("This command is only ingame available.");
                return true;
            }
            Player player = user.getPlayer();

            PlayerConfiguration config = user.getPlayerConfiguration();

            if ( !config.isStatsEnabled() )
            {
                player.sendMessage(PREFIX + ChatColor.RED + "Die Statistiken sind schon deaktiviert!");
                return true;
            }

            config.setStatsEnabled(false);
            sender.sendMessage(PREFIX + ChatColor.RED + "Die Statistiken wurden deaktiviert.");
            SinkCommands.refreshScoreboard(player, -1);
            return true;
        }
    }
}
