/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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

import de.static_interface.sinkantispam.SinkAntiSpam;
import de.static_interface.sinkantispam.database.row.PredefinedWarning;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

public class PredefinedWarningsListCommand extends SinkCommand {

    public PredefinedWarningsListCommand(@Nonnull Plugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        PredefinedWarning[] pWarnings = SinkAntiSpam.getInstance().getPredefinedWarningsTable().get("SELECT * FROM `{TABLE}`");
        if (pWarnings.length < 1) {
            sender.sendMessage(ChatColor.RED + "No predefined warnings found");
            return true;
        }

        for (PredefinedWarning warning : pWarnings) {
            sender.sendMessage(
                    "• " + ChatColor.GOLD + warning.nameId + ChatColor.GRAY + "(" + ChatColor.DARK_AQUA + warning.points + ChatColor.GRAY + "): "
                    + warning.reason);
        }
        return true;
    }
}
