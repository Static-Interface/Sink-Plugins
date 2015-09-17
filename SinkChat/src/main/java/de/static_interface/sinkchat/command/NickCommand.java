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

package de.static_interface.sinkchat.command;

import de.static_interface.sinkchat.config.ScLanguage;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.exception.NotEnoughPermissionsException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.configuration.GeneralSettings;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

@Description("Change your nickname")
@DefaultPermission
@Usage("/<command> [Player] <New Name|off>")
public class NickCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.GREEN + "[Nick]" + ' ' + ChatColor.RESET;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z_0-9" + ChatColor.COLOR_CHAR + "]+$");

    public NickCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setPlayerOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        if (!GeneralSettings.GENERAL_DISPLAYNAMES.getValue()) {
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
                throw new NotEnoughPermissionsException();
            }

            String playerName = args[0];
            Player target = BukkitUtil.getPlayer(playerName);

            newDisplayName = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', args[1]) + ChatColor.RESET;
            if (target == null) {
                throw new UserNotFoundException(args[0]);
            }

            if (sender instanceof Player) {
                if (!((Player) sender).canSee(target) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                    throw new UserNotFoundException(args[0]);
                }
            }

            if (setDisplayName(target, newDisplayName, sender)) {
                user = SinkLibrary.getInstance().getIngameUser(target);
                sender.sendMessage(PREFIX + ScLanguage.SC_NICK_OTHER_CHANGED.format(playerName, user.getDisplayName()));
            }
            return true;
        }

        newDisplayName = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', args[0]) + ChatColor.RESET;
        Player player = ((IngameUser) user).getPlayer();
        if (setDisplayName(player, newDisplayName, sender)) {
            sender.sendMessage(PREFIX + ScLanguage.SC_NICK_SELF_CHANGED.format(newDisplayName));
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    private boolean setDisplayName(Player target, String newDisplayName, CommandSender sender) {
        IngameUser user = SinkLibrary.getInstance().getIngameUser(target);
        String cleanDisplayName = ChatColor.stripColor(newDisplayName);
        if (!NICKNAME_PATTERN.matcher(cleanDisplayName).matches()) {
            sender.sendMessage(PREFIX + ScLanguage.SC_NICK_ILLEGAL_NICKNAME.format());
            return false;
        }

        if (cleanDisplayName.length() > 16) {
            sender.sendMessage(PREFIX + ScLanguage.SC_NICK_TOO_LONG.format());
            return false;
        }

        if (cleanDisplayName.equals("off")) {
            newDisplayName = user.getDefaultDisplayName();
        }

        newDisplayName = user.getChatPrefix() + newDisplayName;

        for (Player onlinePlayer : BukkitUtil.getOnlinePlayers()) {
            if (target.equals(onlinePlayer)) {
                continue;
            }
            String onlinePlayerDisplayName = ChatColor.stripColor(onlinePlayer.getDisplayName().toLowerCase());
            String onlinePlayerName = ChatColor.stripColor(onlinePlayer.getName().toLowerCase());
            String displayName = ChatColor.stripColor(newDisplayName.toLowerCase());
            if (displayName.equals(onlinePlayerDisplayName) || displayName.equals(onlinePlayerName)) {
                target.sendMessage(PREFIX + ScLanguage.SC_NICK_ALREADY_USED.format());
                return false;
            }
        }

        IngameUserConfiguration config = user.getConfiguration();
        config.setDisplayName(newDisplayName);
        config.setHasDisplayName(true);

        SinkLibrary.getInstance().onRefreshDisplayName(user.getPlayer());
        return true;
    }
}
