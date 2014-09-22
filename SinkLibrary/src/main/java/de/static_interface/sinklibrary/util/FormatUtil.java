/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.util;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FormatUtil {

    public static String formatMessage(String message, Player player, String userMessage) {
        return formatMessage(message, SinkLibrary.getInstance().getUser(player), userMessage);
    }

    public static String formatMessage(String message, Player player) {
        return formatMessage(message, SinkLibrary.getInstance().getUser(player));
    }

    public static String formatMessage(String message, SinkUser user, String userMessage) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = message.replaceAll("(?i)\\{(PLAYER|NAME)\\}", user.getName());
        message = message.replaceAll("(?i)\\{(DISPLAYNAME|FORMATTEDNAME)\\}", user.getDisplayName());
        message = message.replaceAll("(?i)\\{(BALANCE|MONEY)\\}", String.valueOf(MathUtil.round(VaultHelper.getBalance(user.getPlayer()))));
        message = message.replaceAll("(?i)\\{WORLD\\}", user.getPlayer().getWorld().getName());
        message = message.replaceAll("(?i)\\{(RANK|GROUP)\\}", user.getPrimaryGroup());
        message = message.replaceAll("(?i)\\{(GAMEMODE|GM)\\}", user.getPlayer().getGameMode().name());
        message = message.replaceAll("(?i)\\{HEALTH\\}", String.valueOf(user.getPlayer().getHealth()));
        message = message.replaceAll("(?i)\\{MAXHEALTH}", String.valueOf(user.getPlayer().getMaxHealth()));
        message = message.replaceAll("(?i)\\{PREFIX\\}", user.getPrefix());
        message = message.replaceAll("(?i)\\{LEVEL\\}", String.valueOf(user.getPlayer().getLevel()));
        message = message.replaceAll("(?i)\\{(E)?XP(ERIENCE)?\\}", String.valueOf(user.getPlayer().getExp()));
        message = message.replaceAll("(?i)\\{TOTAL(E)?XP(ERIENCE)?\\}", String.valueOf(user.getPlayer().getTotalExperience()));
        message = message.replaceAll("(?i)\\{(FOOD|FOODLEVEL)\\}", String.valueOf(user.getPlayer().getFoodLevel()));
        message = message.replaceAll("(?i)\\{X\\}", String.valueOf(user.getPlayer().getLocation().getX()));
        message = message.replaceAll("(?i)\\{Y\\}", String.valueOf(user.getPlayer().getLocation().getY()));
        message = message.replaceAll("(?i)\\{Z\\}", String.valueOf(user.getPlayer().getLocation().getZ()));
        if (userMessage != null) {
            message = message.replaceAll("(?i)\\{MESSAGE\\}", userMessage);
        }
        return message;
    }

    public static String formatMessage(String message, SinkUser user) {
        return formatMessage(message, user, null);
    }
}
