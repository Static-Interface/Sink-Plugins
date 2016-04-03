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

package de.static_interface.sinkirc;

import de.static_interface.sinkirc.queue.IrcQueue;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.event.IrcCommandEvent;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import javax.annotation.Nullable;

public class IrcUtil {

    private static String commandPrefix = "~";
    private static List<String> loadedChannels = new CopyOnWriteArrayList<>();

    public static boolean isOp(User user) {
        for (Channel channel : SinkIRC.getInstance().getJoinedChannels()) {
            if (channel.isOp(user)) {
                return true;
            }
        }
        return false;
    }

    public static void setOp(User user, Channel channel, Boolean value) {
        if (value && isOp(user)) {
            return;
        }
        if (!value && !isOp(user)) {
            return;
        }

        if (value) {
            channel.send().op(user);
        } else {
            channel.send().deOp(user);
        }
    }

    public static String getCommandPrefix() {
        return commandPrefix;
    }

    public static void setCommandPrefix(String commandPrefix) {
        //Todo: make configurable
        IrcUtil.commandPrefix = commandPrefix;
    }

    public static String replaceColorCodes(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        input = input.replace(ChatColor.BLACK.toString(), Colors.BLACK);
        input = input.replace(ChatColor.DARK_BLUE.toString(), Colors.DARK_BLUE);
        input = input.replace(ChatColor.DARK_GREEN.toString(), Colors.DARK_GREEN);
        input = input.replace(ChatColor.DARK_AQUA.toString(), Colors.TEAL);
        input = input.replace(ChatColor.DARK_RED.toString(), Colors.RED);
        input = input.replace(ChatColor.DARK_PURPLE.toString(), Colors.PURPLE);
        input = input.replace(ChatColor.GOLD.toString(), Colors.OLIVE);
        input = input.replace(ChatColor.GRAY.toString(), Colors.LIGHT_GRAY);
        input = input.replace(ChatColor.DARK_GRAY.toString(), Colors.DARK_GRAY);
        input = input.replace(ChatColor.BLUE.toString(), Colors.BLUE);
        input = input.replace(ChatColor.GREEN.toString(), Colors.GREEN);
        input = input.replace(ChatColor.AQUA.toString(), Colors.CYAN);
        input = input.replace(ChatColor.RED.toString(), Colors.RED);
        input = input.replace(ChatColor.LIGHT_PURPLE.toString(), Colors.PURPLE);
        input = input.replace(ChatColor.YELLOW.toString(), Colors.YELLOW);
        input = input.replace(ChatColor.WHITE.toString(), Colors.NORMAL);
        input = input.replace("§k", "");
        input = input.replace(ChatColor.BOLD.toString(), Colors.BOLD);
        input = input.replace(ChatColor.STRIKETHROUGH.toString(), "");
        input = input.replace(ChatColor.UNDERLINE.toString(), Colors.UNDERLINE);
        input = input.replace(ChatColor.ITALIC.toString(), "");
        input = input.replace(ChatColor.RESET.toString(), Colors.NORMAL);
        return input;
    }

    @Nullable
    public static User getUser(String name) {
        Debug.logMethodCall(name);
        List<User> matchedUsers = new ArrayList<>();
        for (Channel channel : SinkIRC.getInstance().getJoinedChannels()) {
            for (User user : channel.getUsers()) {
                if (matchedUsers.contains(user)) {
                    continue;
                }
                if (user.getNick().startsWith(name)) {
                    matchedUsers.add(user);
                }
                if (user.getNick().equalsIgnoreCase(name)) {
                    return user;
                }
            }
        }

        if (matchedUsers.size() > 0) {
            return matchedUsers.get(0);
        }
        Debug.log("Couldn't find IRC user: " + name);
        return null;
    }

    @Nullable
    public static Channel getChannel(String name) {
        for (Channel channel : SinkIRC.getInstance().getJoinedChannels()) {
            if (channel.getName().equalsIgnoreCase(name)) {
                if (loadedChannels.contains(channel.getName())) {
                    return channel;
                }

                for (User user : channel.getUsers()) {
                    SinkLibrary.getInstance().loadIrcUser(user, channel.getName());
                }

                loadedChannels.add(channel.getName());
                return channel;
            }
        }
        return null;
    }

    public static boolean sendMessage(String target, String message) {
        //SinkLibrary.getInstance().getCustomLogger().debug("sendCleanMessage(\"" + target + "\", \"" + message + "\")");
        try {
            if (target.startsWith("#")) {
                IrcQueue.addToQueue(message, target);
            } else {
                String user = getUser(target).getNick();
                IrcQueue.addToQueue(message, user);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendMessage(Channel target, String message) {
        //SinkLibrary.getInstance().getCustomLogger().debug("sendCleanMessage(\"" + target.getName() + "\", \"" + message + "\")");
        try {
            IrcQueue.addToQueue(message, target.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Trigger an IRC command. It's thread-safe.
     * @param command The command to be executed
     * @param args Command args
     * @param source Command source (e.g. channel or username if private message)
     * @param user The user who executes the command
     * @param label Command label
     */
    public static void handleCommand(String command, final String[] args, String source, User user, String label) {
        if (StringUtil.isEmptyOrNull(command)) {
            return;
        }

        command = command.toLowerCase().trim();

        if (user.getNick().equals(SinkIRC.getInstance().getIrcBot().getNick())) {
            return; // Bot has send a message to itself, this causes an ininite loop on this method and the server will crash after that
        }

        Bukkit.getLogger().log(Level.INFO, user.getNick() + " issued IRC command: " + label);

        Debug.logMethodCall(user.getNick(), label);
        if (IrcUtil.isOp(user)) {
            label = ChatColor.translateAlternateColorCodes('&', label);
        } else {
            label = ChatColor.stripColor(label);
        }
        final IrcCommandSender sender = new IrcCommandSender(SinkLibrary.getInstance().getIrcUser(user, source), source);

        final SinkCommand cmd = SinkLibrary.getInstance().getSinkCommand(SinkLibrary.IRC_COMMAND_INTERNAL_PREFIX + command);

        boolean isQueryCommand = !source.startsWith("#");

        if (cmd != null && (!isQueryCommand && cmd.getCommandOptions().isIrcQueryOnly())) {
            sender.sendMessage("This command is only available via query");
            return;
        }

        final String finalLabel = label;
        Bukkit.getScheduler().runTaskAsynchronously(SinkIRC.getInstance(), () -> {
            Debug.log("Firing event...");
            IrcCommandEvent event = new IrcCommandEvent(sender, cmd, finalLabel, args, SinkIRC.getInstance().getIrcBot());
            Bukkit.getPluginManager().callEvent(event);
        });

    }

    public static String getFormattedName(User user) {
        if (isOp(user)) {
            return ChatColor.DARK_RED + user.getNick() + ChatColor.RESET;
        } else {
            return ChatColor.DARK_AQUA + user.getNick() + ChatColor.RESET;
        }
    }
}
