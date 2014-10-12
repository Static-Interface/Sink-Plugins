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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SpyCommands {

    public static final String PREFIX = m("SinkChat.Prefix.Spy") + ' ' + ChatColor.RESET;

    public static class EnableSpyCommand extends SinkCommand {

        public EnableSpyCommand(Plugin plugin) {
            super(plugin);
            getCommandOptions().setPlayerOnly(true);
        }

        @Override
        protected boolean onExecute(CommandSender sender, String label, String[] args) {
            IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);

            Player player = user.getPlayer();

            IngameUserConfiguration config = user.getConfiguration();

            if (config.isSpyEnabled()) {
                player.sendMessage(PREFIX + m("SinkChat.Commands.Spy.AlreadyEnabled"));
                return true;
            }

            config.setSpyEnabled(true);
            sender.sendMessage(PREFIX + m("SinkChat.Commands.Spy.Enabled"));
            return true;
        }
    }

    public static class DisablSpyCommand extends SinkCommand {

        public DisablSpyCommand(Plugin plugin) {
            super(plugin);
            getCommandOptions().setPlayerOnly(true);
        }

        @Override
        public boolean onExecute(CommandSender sender, String label, String[] args) {
            IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);
            Player player = user.getPlayer();

            IngameUserConfiguration config = user.getConfiguration();

            if (!config.isSpyEnabled()) {
                player.sendMessage(PREFIX + m("SinkChat.Commands.Spy.AlreadyDisabled"));
                return true;
            }

            config.setSpyEnabled(false);
            player.sendMessage(PREFIX + m("SinkChat.Commands.Spy.Disabled"));
            return true;
        }
    }
}
