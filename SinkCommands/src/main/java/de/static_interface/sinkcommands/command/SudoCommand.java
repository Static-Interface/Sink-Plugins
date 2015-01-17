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
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.sender.FakeCommandSender;
import de.static_interface.sinklibrary.api.sender.FakePlayerCommandSender;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudoCommand extends SinkCommand {

    public SudoCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        SinkUser user = SinkLibrary.getInstance().getUser((Object) sender);

        CommandSender fakeSender;

        SinkUser target = SinkLibrary.getInstance().getUser(args[0]);

        if (sender instanceof Player && target instanceof IngameUser) {
            if (!target.isOnline()) {
                throw new UserNotFoundException(args[0]);
            }

            if (!((Player) sender).canSee(((IngameUser) target).getPlayer()) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                throw new UserNotFoundException(args[0]);
            }
        }

        if (target instanceof IrcUser && user instanceof IrcUser) {
            fakeSender = target.getSender();
        } else if (target.getBase() instanceof Player) {
            fakeSender = new FakePlayerCommandSender((Player) target.getBase(), sender);
        } else {
            fakeSender = new FakeCommandSender(target.getSender(), sender);
        }

        String commandLine = StringUtil.formatArrayToString(args, " ", 1);

        if (target instanceof IrcUser) {
            String cmd = args[0];
            List<String> tmp = new ArrayList<>(Arrays.asList(args));
            tmp.remove(cmd);
            args = tmp.toArray(new String[tmp.size()]);

            IrcUtil.handleCommand(cmd, args, SinkIRC.getInstance().getMainChannel().getName(), ((IrcUser) target).getBase(), commandLine);
            return true;
        }

        Bukkit.dispatchCommand(fakeSender, commandLine);
        return true;
    }
}

