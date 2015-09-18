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

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

@DefaultPermission
@Usage("Usage: /<command> <Message>")
@Description("Chat for newbies")
@Aliases("nc")
public class NewbiechatCommand extends SinkCommand implements CommandExecutor {
    public static final String PREFIX = ChatColor.YELLOW + "[SupportChat] " + ChatColor.RESET;

    public NewbiechatCommand(@Nonnull Plugin plugin, Configuration config) {
        super(plugin, config);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        if (args.length < 1) {
            return false;
        }
        String message = StringUtil.formatArrayToString(args, " ");

        BukkitUtil.broadcast(PREFIX + BukkitUtil.getSenderName(sender) + ChatColor.WHITE + ": " + message, "sinkcommands.newbiechat", false);
        return true;
    }
}
