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

import de.static_interface.sinkirc.IrcUtil;
import de.static_interface.sinkirc.SinkIRC;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.sender.ProxiedCommandSender;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.sender.ProxiedCommandSenderConversable;
import de.static_interface.sinklibrary.sender.ProxiedConsoleCommandSender;
import de.static_interface.sinklibrary.sender.ProxiedPlayer;
import de.static_interface.sinklibrary.user.ConsoleUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Description("Execute commands as other players")
@Usage("<player> [*]<command> [command args] [-s]")
@DefaultPermission
public class SudoCommand extends SinkCommand {

    public SudoCommand(Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setIrcOpOnly(true);
        Options options = new Options();
        Option silent = Option.builder("s")
                .desc("Don't send any messages to target")
                .longOpt("silent")
                .build();
        options.addOption(silent);
        getCommandOptions().setCliOptions(options);
        getCommandOptions().setMinRequiredArgs(2);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        SinkUser user = SinkLibrary.getInstance().getUser((Object) sender);

        CommandSender fakeSender;

        SinkUser target = getArg(args, 0, SinkUser.class);

        if (!target.isOnline()) {
            throw new UserNotFoundException(args[0]);
        }

        if (sender instanceof Player && target instanceof IngameUser) {
            if (!((Player) sender).canSee(((IngameUser) target).getPlayer()) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                throw new UserNotFoundException(args[0]);
            }
        }

        boolean silent = getCommandLine().hasOption('s');
        if (target instanceof IrcUser && user instanceof IrcUser) {
            fakeSender = target.getSender();
        } else if (target.getBase() instanceof Player) {
            fakeSender = new ProxiedPlayer((Player) target.getBase(), sender, silent);
        }
        else if (target instanceof ConsoleUser) {
            fakeSender = new ProxiedConsoleCommandSender(Bukkit.getConsoleSender(), sender, silent);
        }
        else if (target.getSender() instanceof Conversable) {
            fakeSender = new ProxiedCommandSenderConversable(target.getSender(), sender, silent);
        }
        else {
            fakeSender = new ProxiedCommandSender(target.getSender(), sender, silent);
        }

        String commandLine = StringUtil.formatArrayToString(args, " ", 1);

        boolean shouldBeOp = false;
        if (commandLine.startsWith("*")) {
            commandLine = commandLine.replaceFirst("\\*", "");
            shouldBeOp = !target.isOp();
        }

        if (commandLine.startsWith("/")) {
            commandLine = commandLine.replaceFirst("/", "");
        }

        String cmd = args[1].toLowerCase().trim();
        List<String> tmp = new ArrayList<>(Arrays.asList(args));
        tmp.remove(0);
        tmp.remove(1);
        args = tmp.toArray(new String[tmp.size()]);

        if (shouldBeOp) {
            target.setOp(true);
        }

        try {
            if (target instanceof IrcUser) {
                IrcUtil.handleCommand(cmd, args, SinkIRC.getInstance().getMainChannel().getName(), ((IrcUser) target).getBase(), commandLine);
            } else {
                Bukkit.dispatchCommand(fakeSender, commandLine);
            }
        } catch (Throwable thr) {
            sender.sendMessage(ChatColor.DARK_RED + "An internal error occurred while trying to execute command on target");
            thr.printStackTrace();
        }
        if (shouldBeOp) {
            target.setOp(false);
        }
        return true;
    }
}

