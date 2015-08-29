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

package de.static_interface.sinkantispam.command;

import de.static_interface.sinkantispam.WarnUtil;
import de.static_interface.sinkantispam.database.row.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ListWarnsCommand extends SinkCommand {

    public ListWarnsCommand(Plugin plugin) {
        super(plugin);

        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {

        SinkUser user = SinkLibrary.getInstance().getUser((Object) sender);

        IngameUser target;

        if (args.length < 1 && sender instanceof Player) {
            target = SinkLibrary.getInstance().getIngameUser((Player) sender);
        } else if (args.length < 1) {
            return false;
        } else {
            target = SinkLibrary.getInstance().getIngameUser(args[0]);
        }

        List<String> out = new ArrayList<>();
        out.add(ChatColor.RED + "Warnings: " + target.getDisplayName());

        List<Warning> warnings = WarnUtil.getWarnings(target, true);

        if (warnings.size() > 0) {
            for (Warning warning : warnings) {
                String hidden = ChatColor.GOLD + "#" + warning.id + " - " + ChatColor.GRAY + " [Hidden]";
                if (warning.isAutoWarning && !sender.hasPermission("sinkantispam.autowarnmessage")) {
                    sender.sendMessage(hidden);
                    continue;
                }
                if (warning.isDeleted) {
                    if (!sender.hasPermission("sinkantispam.canseedeleted")) {
                        out.add(hidden);
                        continue;
                    }

                    if (user instanceof IrcUser) {
                        out.add(ChatColor.GOLD + "#" + warning.id + " - " + ChatColor.GRAY + ChatColor.stripColor(warning.getWarnerDisplayName())
                                + ChatColor.GRAY + ": " + ChatColor.GRAY + ChatColor.stripColor(warning.reason) + "[Hidden]");
                    } else {
                        out.add(ChatColor.GOLD + "#" + warning.id + " - " + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.stripColor(
                                warning.getWarnerDisplayName())
                                + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ": " + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH
                                + ChatColor.stripColor(warning.reason));

                    }
                    continue;
                }
                out.add(ChatColor.GOLD + "#" + warning.id + " - " + warning.getWarnerDisplayName() + ChatColor.GOLD + ": "
                        + ChatColor.RED
                        + warning.reason);
            }
        } else {
            out.add(ChatColor.RED + "No warnings found");
        }

        sender.sendMessage(out.toArray(new String[out.size()]));
        return true;
    }
}
