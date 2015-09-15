/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RawCommands {

    @DefaultPermission
    @Description("Send raw messages")
    public static class RawCommand extends SinkCommand {
        public RawCommand(Plugin plugin) {
            super(plugin);
            getCommandOptions().setIrcOpOnly(true);
        }

        @Override
        public boolean onExecute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) {
                return false;
            }
            BukkitUtil.broadcastMessage(ChatColor.translateAlternateColorCodes('&', StringUtil.formatArrayToString(args, " ")));
            return true;
        }
    }

    @DefaultPermission
    @Description("Send raw messages to an user")
    public static class RawUserCommand extends SinkCommand {

        public RawUserCommand(Plugin plugin) {
            super(plugin);
            getCommandOptions().setIrcOpOnly(true);
        }

        @Override
        public boolean onExecute(CommandSender sender, String label, String[] args) {
            if (args.length < 1) {
                return false;
            }
            SinkUser target = SinkLibrary.getInstance().getUser(args[0]);

            if (!target.isOnline()) {
                throw new UserNotFoundException(args[0]);
            }

            if (sender instanceof Player && target instanceof IngameUser) {
                if (!((Player) sender).canSee(((IngameUser) target).getPlayer()) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                    throw new UserNotFoundException(args[0]);
                }
            }

            String message = "";

            for (int i = 1; i < args.length; i++) {
                message += args[i] + ' ';
            }
            message = message.trim();

            target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }
    }
}



