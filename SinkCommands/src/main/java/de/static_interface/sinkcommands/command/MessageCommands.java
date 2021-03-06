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

import de.static_interface.sinkcommands.config.ScmdSettings;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.sender.ProxiedPlayer;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.user.ProxiedUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCommands {

    private static Map<SinkUser, SinkUser> lastReplies = new ConcurrentHashMap<>();

    private static void sendMessage(SinkUser executor, SinkUser target, String message) {
        for (SinkUser user : lastReplies.keySet()) {
            if (user instanceof ProxiedUser && !(executor instanceof ProxiedUser) && ((ProxiedPlayer) user.getBase()).getBaseObject()
                    .equals(executor.getSender())) {
                executor = user;
                break;
            }
        }
        //Todo: add @Name Message Support
        String format = ScmdSettings.SCMD_MESSAGE_SEND_FORMAT.getValue();
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

        if (executor instanceof IrcUser) {
            ((IrcUser) executor).sendMessage(StringUtil.format(format, executor, target, message, customValues), true);
        } else {
            executor.sendMessage(StringUtil.format(format, executor, target, message, customValues));
        }

        format = ScmdSettings.SCMD_MESSAGE_RECEIVED_FORMAT.getValue();
        if (target instanceof IrcUser) {
            ((IrcUser) target).sendMessage(StringUtil.format(format, executor, target, message, customValues), true);
        } else {
            target.sendMessage(StringUtil.format(format, executor, target, message, customValues));
        }

        lastReplies.put(executor, target);
        lastReplies.put(target, executor);
    }

    @DefaultPermission
    @Description("Send messages to other users")
    @Aliases({"m", "msg", "tell", "whisper"})
    public static class MessageCommand extends SinkCommand {

        public MessageCommand(Plugin plugin, Configuration config) {
            super(plugin, config);
            getTabCompleterOptions().setIncludeIrcUsers(true);
            getTabCompleterOptions().setIncludeSuffix(true);
            getCommandOptions().setIrcQueryOnly(true);
        }

        @Override
        protected boolean onExecute(CommandSender sender, String label, String[] args) {
            if (args.length < 2 || args[0].trim().length() < 2 || args[1].trim().isEmpty()) {
                return false;
            }

            SinkUser executor = SinkLibrary.getInstance().getUser((Object) sender);
            SinkUser target = SinkLibrary.getInstance().getUser(args[0]);

            if (!target.isOnline()) {
                throw new UserNotFoundException(args[0]);
            }

            if (sender instanceof Player && target instanceof IngameUser) {
                if (!((Player) sender).canSee(((IngameUser) target).getPlayer()) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                    throw new UserNotFoundException(args[0]);
                }
            }

            if (target == null || !target.isOnline()) {
                throw new UserNotFoundException(args[0]);
            }

            String message = StringUtil.formatArrayToString(args, " ", 1);

            sendMessage(executor, target, message);

            return true;
        }
    }

    @DefaultPermission
    @Description("Send a reply to the last message")
    @Aliases("r")
    public static class ReplyCommand extends SinkCommand {

        public ReplyCommand(Plugin plugin, Configuration config) {
            super(plugin, config);
            getCommandOptions().setIrcQueryOnly(true);
        }

        @Override
        protected boolean onExecute(CommandSender sender, String label, String[] args) {
            if (args.length < 1 || StringUtil.isEmptyOrNull(args[0])) {
                return false;
            }

            SinkUser executor = SinkLibrary.getInstance().getUser((Object) sender);
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
