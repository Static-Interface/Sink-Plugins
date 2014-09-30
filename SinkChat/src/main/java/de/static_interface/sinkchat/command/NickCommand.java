/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkchat.command;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.UserConfiguration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class NickCommand implements CommandExecutor {

    public static final String PREFIX = ChatColor.GREEN + "[Nick]" + ' ' + ChatColor.RESET;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z_0-9" + ChatColor.COLOR_CHAR + "]+$");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!SinkLibrary.getInstance().getSettings().isDisplayNamesEnabled()) {
            sender.sendMessage(PREFIX + "DisplayNames have been disabled in the config.");
            return true;
        }

        SinkUser user = SinkLibrary.getInstance().getUser(sender);
        String newDisplayName;

        if (args.length < 1) {
            return false;
        }

        if (args.length > 1) {
            if (!user.hasPermission("sinkchat.nick.others")) {
                sender.sendMessage(m("Permissions.SinkChat.Nick.Other"));
                return true;
            }

            String playerName = args[0];
            Player target = BukkitUtil.getPlayer(playerName);

            newDisplayName = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', args[1]) + ChatColor.RESET;
            if (target == null) {
                sender.sendMessage(PREFIX + m("General.NotOnline", args[0]));
                return true;
            }

            if (setDisplayName(target, newDisplayName, sender)) {
                user = SinkLibrary.getInstance().getUser(target);
                sender.sendMessage(PREFIX + m("SinkChat.Commands.Nick.OtherChanged", playerName, user.getDisplayName()));
            }
            return true;
        }
        if (user.isConsole()) {
            sender.sendMessage(m("General.ConsoleNotAvailable"));
            return true;
        }
        newDisplayName = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', args[0]) + ChatColor.RESET;
        Player player = user.getPlayer();
        if (setDisplayName(player, newDisplayName, sender)) {
            sender.sendMessage(PREFIX + m("SinkChat.Commands.Nick.SelfChanged", newDisplayName));
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    private boolean setDisplayName(Player target, String newDisplayName, CommandSender sender) {
        SinkUser user = SinkLibrary.getInstance().getUser(target);
        String cleanDisplayName = ChatColor.stripColor(newDisplayName);
        if (!NICKNAME_PATTERN.matcher(cleanDisplayName).matches()) {
            sender.sendMessage(PREFIX + m("SinkChat.Commands.Nick.IllegalNickname"));
            return false;
        }

        if (cleanDisplayName.length() > 16) {
            sender.sendMessage(PREFIX + m("SinkChat.Commands.Nick.TooLong"));
            return false;
        }

        if (cleanDisplayName.equals("off")) {
            newDisplayName = user.getDefaultDisplayName();
        }

        newDisplayName = user.getPrefix() + newDisplayName;

        for (Player onlinePlayer : BukkitUtil.getOnlinePlayers()) {
            if (target.equals(onlinePlayer)) {
                continue;
            }
            String onlinePlayerDisplayName = ChatColor.stripColor(onlinePlayer.getDisplayName().toLowerCase());
            String onlinePlayerName = ChatColor.stripColor(onlinePlayer.getName().toLowerCase());
            String displayName = ChatColor.stripColor(newDisplayName.toLowerCase());
            if (displayName.equals(onlinePlayerDisplayName) || displayName.equals(onlinePlayerName)) {
                target.sendMessage(PREFIX + m("SinkChat.Commands.Nick.Used"));
                return false;
            }
        }

        UserConfiguration config = user.getConfiguration();
        config.setDisplayName(newDisplayName);
        config.setHasDisplayName(true);

        SinkLibrary.getInstance().onRefreshDisplayName(user.getPlayer());
        return true;
    }
}
