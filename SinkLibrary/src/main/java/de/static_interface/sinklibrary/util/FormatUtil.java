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

    public static String formatString(String str, Player player, String userMessage) {
        return formatString(str, SinkLibrary.getInstance().getUser(player), userMessage);
    }

    public static String formatString(String str, Player player) {
        return formatString(str, SinkLibrary.getInstance().getUser(player));
    }

    public static String formatString(String str, SinkUser user, String userMessage) {
        str = str.replaceAll("(?i)\\{(PLAYER|NAME)\\}", user.getName());
        str = str.replaceAll("(?i)\\{(DISPLAYNAME|FORMATTEDNAME)\\}", user.getDisplayName());
        str = str.replaceAll("(?i)\\{(BALANCE|MONEY)\\}", String.valueOf(MathUtil.round(VaultHelper.getBalance(user.getPlayer()))));
        str = str.replaceAll("(?i)\\{WORLD\\}", user.getPlayer().getWorld().getName());
        str = str.replaceAll("(?i)\\{(RANK|GROUP)\\}", user.getPrimaryGroup());
        str = str.replaceAll("(?i)\\{(GAMEMODE|GM)\\}", user.getPlayer().getGameMode().name());
        str = str.replaceAll("(?i)\\{HEALTH\\}", String.valueOf(user.getPlayer().getHealth()));
        str = str.replaceAll("(?i)\\{MAXHEALTH}", String.valueOf(user.getPlayer().getMaxHealth()));
        str = str.replaceAll("(?i)\\{PREFIX\\}", user.getPrefix());
        str = str.replaceAll("(?i)\\{LEVEL\\}", String.valueOf(user.getPlayer().getLevel()));
        str = str.replaceAll("(?i)\\{(E)?XP(ERIENCE)?\\}", String.valueOf(user.getPlayer().getExp()));
        str = str.replaceAll("(?i)\\{TOTAL(E)?XP(ERIENCE)?\\}", String.valueOf(user.getPlayer().getTotalExperience()));
        str = str.replaceAll("(?i)\\{(FOOD|FOODLEVEL)\\}", String.valueOf(user.getPlayer().getFoodLevel()));
        str = str.replaceAll("(?i)\\{X\\}", String.valueOf(user.getPlayer().getLocation().getX()));
        str = str.replaceAll("(?i)\\{Y\\}", String.valueOf(user.getPlayer().getLocation().getY()));
        str = str.replaceAll("(?i)\\{Z\\}", String.valueOf(user.getPlayer().getLocation().getZ()));
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (userMessage != null) {
            str = str.replaceAll("(?i)\\{MESSAGE\\}", userMessage);
        }
        return str;
    }

    public static String formatString(String str, SinkUser user) {
        return formatString(str, user, null);
    }
}
