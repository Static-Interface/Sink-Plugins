/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.exception.UserNotFoundException;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class MessageCommands {

    private static HashMap<SinkUser, SinkUser> lastReplies = new HashMap<>();

    private static void sendMessage(SinkUser executor, SinkUser target, String message) {
        String format = SinkLibrary.getInstance().getSettings().getMessageSendFormat();
        HashMap<String, Object> customValues = new HashMap<>();
        if (target instanceof IrcUser) {
            customValues.put("T(ARGET)?IRC(PREFIX)?", ChatColor.GRAY + "(IRC)");
        } else {
            customValues.put("T(ARGET)?IRC(PREFIX)?", "");
        }

        if (executor instanceof IrcUser) {
            customValues.put("IRC(PREFIX)?", ChatColor.GRAY + "(IRC)");
        } else {
            customValues.put("IRC(PREFIX)?", "");
        }

        executor.sendMessage(StringUtil.format(format, executor, target, message, customValues, null));

        format = SinkLibrary.getInstance().getSettings().getMessageReceivedFormat();
        target.sendMessage(StringUtil.format(format, executor, target, message, customValues, null));

        lastReplies.put(executor, target);
        lastReplies.put(target, executor);
    }

    public static class MessageCommand extends SinkCommand {

        public MessageCommand(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean onExecute(CommandSender sender, String label, String[] args) {
            if (args.length < 2 || args[0].trim().length() < 2 || args[1].trim().isEmpty()) {
                throw new NotEnoughArgumentsException();
            }

            SinkUser executor = SinkLibrary.getInstance().getUser(sender);
            SinkUser target = SinkLibrary.getInstance().getUser(args[0]);

            if (target == null || !target.isOnline()) {
                throw new UserNotFoundException(args[0]);
            }

            String message = StringUtil.formatArrayToString(args, " ", 1);

            sendMessage(executor, target, message);

            return true;
        }
    }

    public static class ReplyCommand extends SinkCommand {

        public ReplyCommand(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean onExecute(CommandSender sender, String label, String[] args) {
            if (args.length < 1 || StringUtil.isStringEmptyOrNull(args[0])) {
                throw new NotEnoughArgumentsException();
            }

            SinkUser executor = SinkLibrary.getInstance().getUser(sender);
            SinkUser target = lastReplies.get(executor);

            if (target == null || !target.isOnline()) {
                throw new UserNotFoundException();
            }

            String message = StringUtil.formatArrayToString(args, " ", 0);
            sendMessage(executor, target, message);
            return true;
        }
    }
}
