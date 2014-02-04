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

package de.static_interface.sinkchat.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class SpyCommands
{
    public static final String PREFIX = _("SinkChat.Prefix.Spy") + ' ' + ChatColor.RESET;

    public static class EnableSpyCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            User user = SinkLibrary.getUser(sender);

            if ( user.isConsole() )
            {
                sender.sendMessage(_("General.ConsoleNotAvailable"));
                return true;
            }
            Player player = user.getPlayer();

            PlayerConfiguration config = user.getPlayerConfiguration();

            if ( config.isSpyEnabled() )
            {
                player.sendMessage(PREFIX + _("SinkChat.Commands.Spy.AlreadyEnabled"));
                return true;
            }

            config.setSpyEnabled(true);
            sender.sendMessage(PREFIX + _("SinkChat.Commands.Spy.Enabled"));
            return true;
        }
    }

    public static class DisablSpyCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            User user = SinkLibrary.getUser(sender);
            if ( user.isConsole() )
            {
                sender.sendMessage(_("General.ConsoleNotAvailable"));
                return true;
            }
            Player player = user.getPlayer();

            PlayerConfiguration config = user.getPlayerConfiguration();

            if ( !config.isSpyEnabled() )
            {
                player.sendMessage(PREFIX + _("SinkChat.Commands.Spy.AlreadyDisabled"));
                return true;
            }

            config.setSpyEnabled(false);
            player.sendMessage(PREFIX + _("SinkChat.Commands.Spy.Disabled"));
            return true;
        }
    }
}
