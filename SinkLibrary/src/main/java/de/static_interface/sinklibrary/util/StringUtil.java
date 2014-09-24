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

import java.util.List;

import javax.annotation.Nullable;

public class StringUtil {

    /**
     * Format a string
     * @param str Input String
     * @param userMessage Message from user (e.g. /say <message> -> <message> would be userMessage)
     * @param paramValues Replace {0} {1}... {n} with these values
     * @return Formatted String
     */
    public static String format(String str, Player player, @Nullable String userMessage, @Nullable Object... paramValues) {
        return format(str, SinkLibrary.getInstance().getUser(player), userMessage, paramValues);
    }

    /**
     * Format a string
     * @param str Input String
     * @param paramValues Replace {0} {1}... {n} with these values
     * @return Formatted String
     */
    public static String format(String str, @Nullable Object... paramValues) {
        return format(str, (SinkUser) null, null, paramValues);
    }

    /**
     * Format a string
     * @param str Input String
     * @param user If there is any user who's calling this, you can use placeholders like
     *             {NAME} (some of the placeholders works with offline users too)
     * @param userMessage Message from user (e.g. /say <message> -> userMessage should be  <message>)
     * @param paramValues Replace {0} {1}... {n} with these values
     * @return Formatted String
     */
    public static String format(String str, @Nullable SinkUser user, @Nullable String userMessage, @Nullable Object... paramValues) {
        if (user != null) {
            str = str.replaceAll("(?i)\\{(PLAYER(NAME)?|NAME)\\}", user.getName());
            str = str.replaceAll("(?i)\\{(DISPLAYNAME|FORMATTEDNAME)\\}", user.getDisplayName());
            str = str.replaceAll("(?i)\\{(BALANCE|MONEY)\\}", String.valueOf(user.getBalance()));
            str = str.replaceAll("(?i)\\{(RANK|GROUP)\\}", user.getPrimaryGroup());
            str = str.replaceAll("(?i)\\{PREFIX\\}", user.getPrefix());
        }

        if (user.getPlayer() != null) {
            str = str.replaceAll("(?i)\\{X\\}", String.valueOf(user.getPlayer().getLocation().getX()));
            str = str.replaceAll("(?i)\\{Y\\}", String.valueOf(user.getPlayer().getLocation().getY()));
            str = str.replaceAll("(?i)\\{Z\\}", String.valueOf(user.getPlayer().getLocation().getZ()));
            str = str.replaceAll("(?i)\\{LEVEL\\}", String.valueOf(user.getPlayer().getLevel()));
            str = str.replaceAll("(?i)\\{(E)?XP(ERIENCE)?\\}", String.valueOf(user.getPlayer().getExp()));
            str = str.replaceAll("(?i)\\{TOTAL(E)?XP(ERIENCE)?\\}", String.valueOf(user.getPlayer().getTotalExperience()));
            str = str.replaceAll("(?i)\\{(FOOD|FOODLEVEL)\\}", String.valueOf(user.getPlayer().getFoodLevel()));
            str = str.replaceAll("(?i)\\{(GAMEMODE|GM)\\}", user.getPlayer().getGameMode().name());
            str = str.replaceAll("(?i)\\{HEALTH\\}", String.valueOf(user.getPlayer().getHealth()));
            str = str.replaceAll("(?i)\\{MAXHEALTH}", String.valueOf(user.getPlayer().getMaxHealth()));
            str = str.replaceAll("(?i)\\{WORLD\\}", user.getPlayer().getWorld().getName());
        }

        str = str.replaceAll("(?i)\\{CURRENCY\\}", VaultHelper.getCurrenyName());

        if (paramValues != null) {
            int i = 0;
            for (Object s : paramValues) {
                str = str.replaceAll("\\{" + i + "\\}", String.valueOf(s));
                i++;
            }
        }

        str = ChatColor.translateAlternateColorCodes('&', str);

        // Don't translate color on these placeholders:
        if (userMessage != null) {
            str = str.replaceAll("(?i)\\{MESSAGE\\}", userMessage);
        }

        return str;
    }

    /**
     * Format Array to String, useful if you want to format args to a message
     *
     * @param input     Array to format
     * @param character Character between input objects
     * @return e.g. If input = {"s1", "s2", "s3" } and character = " & " it will return "s1 & s2 & s3"
     */
    public static String formatArrayToString(Object[] input, @Nullable String character) {
        return formatArrayToString(input, character, 0, input.length);
    }

    /**
     * Format Array to String, useful if you want to format args to a message
     *
     * @param input     Array to format
     * @param character Character between input objects
     * @param startIndex Start Index
     * @return e.g. If input = {"s1", "s2", "s3" } and character = " & " it will return "s1 & s2 & s3"
     */
    public static String formatArrayToString(Object[] input, @Nullable String character, int startIndex) {
        return formatArrayToString(input, character, startIndex, input.length);
    }

    /**
     * Format Array to String, useful if you want to format args to a message
     *
     * @param input     Array to format
     * @param character Character between input objects
     * @param startIndex Start Index
     * @param endIndex End Index
     * @return e.g. If input = {"s1", "s2", "s3" } and character = " & " it will return "s1 & s2 & s3"
     */
    public static String formatArrayToString(Object[] input, @Nullable String character, int startIndex, int endIndex) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("startIndex can't be less than 0 !");
        }
        if (endIndex <= startIndex) {
            throw new IllegalArgumentException("endIndex can't be less or equal startIndex!");
        }

        if (character == null) {
            character = " ";
        }

        String tmp = "";
        for (int i = startIndex; i < endIndex; i++) {
            if (tmp.equals("")) {
                tmp = input[i].toString();
                continue;
            }
            tmp += character + input[i].toString();
        }

        return tmp;
    }

    /**
     * Formats a list with names to String.
     *
     * @param names Names
     * @return If names contains "user1", "user2", "user3", it will return "user1, user2 and user3".
     */
    public static String formatPlayerListToString(List<String> names) {
        String tmp = "";
        int i = 0;
        for (String s : names) {
            i++;
            if (tmp.isEmpty()) {
                tmp = s;
                continue;
            }
            if (i == names.toArray().length) {
                tmp = tmp + " and " + s;
                continue;
            }
            tmp = tmp + ", " + s;
        }
        return tmp;
    }

    public static boolean isStringEmptyOrNull(String s) {
        return s == null || s.trim().length() == 0;
    }
}
