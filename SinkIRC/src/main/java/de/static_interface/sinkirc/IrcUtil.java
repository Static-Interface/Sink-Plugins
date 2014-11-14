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

package de.static_interface.sinkirc;

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

import javax.annotation.Nullable;

public class IrcUtil {

    private static String commandPrefix = "~";
    private static List<String> loadedChannels = new ArrayList<>();

    public static boolean isOp(User user) {
        return SinkIRC.getInstance().getMainChannel().isOp(user);
    }

    public static void setOp(User user, boolean value) {
        if (value && isOp(user)) {
            return;
        }
        if (!value && !isOp(user)) {
            return;
        }

        if (value) {
            SinkIRC.getInstance().getMainChannel().send().op(user);
        } else {
            SinkIRC.getInstance().getMainChannel().send().deOp(user);
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
    public static User getUser(Channel channel, String name) {
        Debug.logMethodCall(channel.getName(), name);
        List<User> matchedUsers = new ArrayList<>();
        for (User user : channel.getUsers()) {
            if (user.getNick().startsWith(name)) {
                matchedUsers.add(user);
            }
            if (user.getNick().equalsIgnoreCase(name)) {
                return user;
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
        for (Channel channel : SinkIRC.getInstance().getIrcBot().getUserBot().getChannels()) {
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
        message = replaceColorCodes(message);

        try {
            if (target.startsWith("#")) {
                SinkIRC.getInstance().getIrcBot().sendIRC().message(target, message);
            } else {
                getUser(SinkIRC.getInstance().getMainChannel(), target).send().message(message);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendMessage(Channel target, String message) {
        //SinkLibrary.getInstance().getCustomLogger().debug("sendCleanMessage(\"" + target.getName() + "\", \"" + message + "\")");
        message = replaceColorCodes(message);
        try {
            target.send().message(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void handleCommand(String command, String[] args, String source, User user, String label) {
        if (StringUtil.isEmptyOrNull(command)) {
            return;
        }

        Debug.logMethodCall(label);
        if (IrcUtil.isOp(user)) {
            label = ChatColor.translateAlternateColorCodes('&', label);
        } else {
            label = ChatColor.stripColor(label);
        }
        IrcCommandSender sender = new IrcCommandSender(SinkLibrary.getInstance().getIrcUser(user, source), source);

        SinkCommand cmd = SinkLibrary.getInstance().getCustomCommand(command);

        boolean isQueryCommand = !source.startsWith("#");

        if (cmd != null && (!isQueryCommand && cmd.getCommandOptions().isIrcQueryOnly())) {
            sender.sendMessage("This command is only available via query");
            return;
        }

        IrcCommandEvent event = new IrcCommandEvent(sender, cmd, label, args, SinkIRC.getInstance().getIrcBot());
        Bukkit.getPluginManager().callEvent(event);
    }

    public static String getFormattedName(User user) {
        if (isOp(user)) {
            return ChatColor.DARK_RED + user.getNick() + ChatColor.RESET;
        } else {
            return ChatColor.DARK_AQUA + user.getNick() + ChatColor.RESET;
        }
    }
}
