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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkantispam.WarnUtil;
import de.static_interface.sinkantispam.database.row.PredefinedWarning;
import de.static_interface.sinkantispam.database.row.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.api.user.Identifiable;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.DateUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class WarnCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.RED + "[Warn] " + ChatColor.RESET;

    public WarnCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
        getCommandOptions().setCliOptions(buildOptions());
        getCommandOptions().setCmdLineSyntax("{PREFIX}{ALIAS} <options> <player> [WarningId]");
    }

    private Options buildOptions() {
        Options options = new Options();
        Option custom = Option.builder("c")
                .desc("Custom warning")
                .longOpt("custom")
                .build();
        options.addOption(custom);

        Option reason = Option.builder("r")
                .desc("Warning reason for custom warnings")
                .hasArg()
                .argName("reason")
                .type(String.class)
                .longOpt("reason")
                .build();

        options.addOption(reason);

        Option points = Option.builder("p")
                .desc("Warning points for custom warnings")
                .hasArg()
                .argName("points")
                .type(Integer.class)
                .longOpt("points")
                .build();
        options.addOption(points);

        Option expire = Option.builder("e")
                .desc("Expire time for custom warnings")
                .hasArg()
                .argName("time")
                .type(String.class)
                .longOpt("expire")
                .build();
        options.addOption(expire);
        return options;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + m("General.CommandMisused.Arguments.TooFew"));
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: " + getCommandPrefix() + "warn [Player] [Reason]");
            return false;
        }

        IngameUser target = SinkLibrary.getInstance().getIngameUser(args[0]);

        if (target.getName().equals(sender.getName())) {
            sender.sendMessage(PREFIX + m("SinkAntiSpam.WarnSelf"));
            return true;
        }

        SinkUser user = SinkLibrary.getInstance().getUser((Object) sender);
        UUID uuid = null;
        if (user instanceof Identifiable) {
            uuid = ((Identifiable) user).getUniqueId();
        }
        String name = (sender instanceof IrcCommandSender) ? user.getDisplayName() + " (IRC)" : user.getDisplayName();

        Warning warning = new Warning();
        warning.isDeleted = false;
        warning.isAutoWarning = false;
        warning.warnTime = System.currentTimeMillis();
        warning.deleter = null;
        warning.deleterUuid = null;
        warning.deleteTime = null;
        warning.warner = name;
        warning.warnerUuid = uuid == null ? null : uuid.toString();
        warning.playerName = target.getDisplayName();
        warning.player = target.getUniqueId().toString();
        warning.userWarningId = WarnUtil.getNextWarningId(target);

        if (getCommandLine().hasOption('c')) {
            if (!sender.hasPermission("sinkantispam.warnings.custom")) {
                sender.sendMessage(m("Permissions.General"));
                return true;
            }
            if (!getCommandLine().hasOption('r')) {
                throw new ParseException("Custom warnings require -r <reason> option");
            }
            if (!getCommandLine().hasOption('p')) {
                throw new ParseException("Custom warnings require -p <points> option");
            }

            if (getCommandLine().hasOption('e')) {
                try {
                    warning.expireTime = DateUtil.parseDateDiff(getCommandLine().getParsedOptionValue("e").toString(), true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Integer points = ((Number) getCommandLine().getParsedOptionValue("p")).intValue();

            warning.reason = StringUtil.formatArrayToString(getCommandLine().getOptionValues('r'), " ");
            warning.points = points;
            warning.predefinedId = null;
        } else {
            if (args.length < 1) {
                throw new NotEnoughArgumentsException();
            }
            PredefinedWarning pWarning = WarnUtil.getPredefinedWarning(args[1]);
            if (pWarning == null) {
                sender.sendMessage(m("SinkAntiSpam.UnknownWarning", args[1]));
                return true;
            }
            warning.reason = pWarning.reason;
            warning.points = pWarning.points;
            if (pWarning.expireTime != null) {
                warning.expireTime = System.currentTimeMillis() + pWarning.expireTime;
            }
            warning.predefinedId = pWarning.id;
        }

        WarnUtil.performWarning(warning);
        return true;
    }
}
