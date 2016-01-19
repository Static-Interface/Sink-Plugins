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
import de.static_interface.sinkantispam.WarnUtil;
import de.static_interface.sinkantispam.database.row.PredefinedWarning;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Permission;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.configuration.GeneralLanguage;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

@Description("Delete a predefined warning")
@Permission("SinkAntiSpam.Command.PredefinedWarnings")
@Aliases({"delpwarn", "deletepwarn"})
@Usage("<warningname>")
public class DeletePredefinedWarningCommand extends SinkCommand {

    public DeletePredefinedWarningCommand(@Nonnull Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setIrcOpOnly(true);
        getCommandOptions().setCliOptions(new Options());
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        if (args.length < 0) {
            return false;
        }

        PredefinedWarning pWarning = WarnUtil.getPredefinedWarning(args[0]);
        if (pWarning == null) {
            sender.sendMessage(GeneralLanguage.GENERAL_UNKNOWN_VALUE.format(args[0]));
            return true;
        }

        SinkAntiSpam.getInstance().getPredefinedWarningsTable().executeUpdate("DELETE FROM `{TABLE}` WHERE `id` = ?", pWarning.id);
        sender.sendMessage(ChatColor.DARK_GREEN + "Success");
        return true;
    }
}
