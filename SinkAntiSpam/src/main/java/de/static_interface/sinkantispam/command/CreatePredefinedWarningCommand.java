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
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Permission;
import de.static_interface.sinklibrary.api.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.util.DateUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

@Description("Create a predefined warning")
@Permission("SinkAntiSpam.Command.PredefinedWarnings")
@Aliases({"createpwarn", "createpwarning"})
public class CreatePredefinedWarningCommand extends SinkCommand {

    public CreatePredefinedWarningCommand(@Nonnull Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
        getCommandOptions().setCliOptions(buildOptions());
        getCommandOptions().setCmdLineSyntax("{PREFIX}{ALIAS} <options> <WarningName>");
    }

    private Options buildOptions() {
        Options options = new Options();
        Option reason = Option.builder("r")
                .desc("Warning reason")
                .hasArg()
                .argName("reason")
                .type(String.class)
                .longOpt("reason")
                .required()
                .build();

        options.addOption(reason);

        Option points = Option.builder("p")
                .desc("Warning points")
                .hasArg()
                .argName("points")
                .type(Integer.class)
                .longOpt("points")
                .required()
                .build();
        options.addOption(points);

        Option expire = Option.builder("e")
                .desc("Expire time")
                .hasArg()
                .argName("time")
                .type(String.class)
                .longOpt("expire")
                .optionalArg(true)
                .build();
        options.addOption(expire);
        return options;
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        PredefinedWarning warning = new PredefinedWarning();
        if (getCommandLine().hasOption('e')) {
            try {
                warning.expireTime = DateUtil.parseDateDiff(getCommandLine().getParsedOptionValue("e").toString(), true) - System.currentTimeMillis();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Integer points = Integer.valueOf(getCommandLine().getOptionValue('p'));

        warning.reason = StringUtil.formatArrayToString(getCommandLine().getOptionValues('r'), " ");
        warning.points = points;
        warning.nameId = args[0];
        SinkAntiSpam.getInstance().getPredefinedWarningsTable().insert(warning);
        sender.sendMessage(ChatColor.DARK_GREEN + "Success");
        return true;
    }
}
