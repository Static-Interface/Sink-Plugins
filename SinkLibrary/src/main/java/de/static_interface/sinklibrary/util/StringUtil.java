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

package de.static_interface.sinklibrary.util;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.ConsoleUser;
import de.static_interface.sinklibrary.user.IngameUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
        return format(str, SinkLibrary.getInstance().getIngameUser(player), userMessage, null, paramValues);
    }

    /**
     * Format a string
     * @param str Input String
     * @param user If there is any user who's calling this, you can use placeholders like
     *             {NAME} (some of the placeholders works with offline users too)
     * @param target If there is a target player (e.g. /msg <target>), you can use placeholders for that target too
     * @param userMessage Message from user (e.g. /say <message> -> <message> would be userMessage)
     * @return Formatted String
     */
    public static String format(String str, @Nullable String userMessage, @Nullable SinkUser user, @Nullable SinkUser target) {
        return format(str, user, target, userMessage, null, null);
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
     * @param customPlaceholders Custom placeholders values (keys are the placeholders), dont use brackets on keys.
     *                           Example: if equals "TEST" and value equals "Hello", all {TEST} instances will be replaced
     *                           with "Hello"
     * @return Formatted String
     */
    public static String format(String str, @Nullable HashMap<String, Object> customPlaceholders) {
        return format(str, (SinkUser) null, null, customPlaceholders, null, null);
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
        return format(str, user, null, userMessage, null, paramValues);
    }

    /**
     * Format a string
     * @param str Input String
     * @param user If there is any user who's calling this, you can use placeholders like
     *             {NAME} (some of the placeholders works with offline users too)
     * @param userMessage Message from user (e.g. /say <message> -> userMessage should be  <message>)
     * @param paramValues Replace {0} {1}... {n} with these values
     * @param customPlaceholders Custom placeholders values (keys are the placeholders), dont use brackets on keys.
     *                           Example: if equals "TEST" and value equals "Hello", all {TEST} instances will be replaced
     *                           with "Hello"
     * @return Formatted String
     */
    public static String format(String str, @Nullable SinkUser user, @Nullable String userMessage,
                                @Nullable HashMap<String, Object> customPlaceholders, @Nullable Object... paramValues) {
        return format(str, user, null, userMessage, customPlaceholders, paramValues);
    }

    /**
     * Format a string
     * @param str Input String
     * @param user If there is any user who's calling this, you can use placeholders like
     *             {NAME} (some of the placeholders works with offline users too)
     * @param target If there is a target player (e.g. /msg <target>), you can use placeholders for that target too
     * @param userMessage Message from user (e.g. /say <message> -> userMessage should be  <message>)
     * @param paramValues Replace {0} {1}... {n} with these values
     * @param customPlaceholders Custom placeholders values (keys are the placeholders), dont use brackets on keys.
     *                           Example: if equals "TEST" and value equals "Hello", all {TEST} instances will be replaced
     *                           with "Hello"
     * @return Formatted String
     */
    public static String format(String str, @Nullable SinkUser user, @Nullable SinkUser target, @Nullable String userMessage,
                                @Nullable HashMap<String, Object> customPlaceholders, @Nullable Object... paramValues) {
        return format(str, user, target, userMessage, customPlaceholders, true, paramValues);
    }

    /**
     * Format a string
     * @param str Input String
     * @param user If there is any user who's calling this, you can use placeholders like
     *             {NAME} (some of the placeholders works with offline users too)
     * @param target If there is a target player (e.g. /msg <target>), you can use placeholders for that target too
     * @param userMessage Message from user (e.g. /say <message> -> userMessage should be  <message>)
     * @param paramValues Replace {0} {1}... {n} with these values
     * @param customPlaceholders Custom placeholders values (keys are the placeholders), dont use brackets on keys.
     *                           Example: if equals "TEST" and value equals "Hello", all {TEST} instances will be replaced
     *                           with "Hello"
     * @return Formatted String
     */
    public static String format(String str, @Nullable SinkUser user, @Nullable SinkUser target, @Nullable String userMessage,
                                @Nullable HashMap<String, Object> customPlaceholders, boolean formatS, @Nullable Object... paramValues) {
        if (user != null) {
            str = str.replaceAll("(?i)\\{(PLAYER(NAME)?|NAME)\\}", user.getName());
            str = str.replaceAll("(?i)\\{(DISPLAYNAME|FORMATTEDNAME)\\}", user.getDisplayName());
            if (user instanceof IngameUser) {
                str = str.replaceAll("(?i)\\{(BALANCE|MONEY)\\}", String.valueOf(((IngameUser) user).getBalance()));
            }
            if (!(user instanceof ConsoleUser)) {
                str = str.replaceAll("(?i)\\{(RANK|GROUP)\\}", user.getPrimaryGroup());
            } else {
                str = str.replaceAll("(?i)\\{(RANK|GROUP)\\}", "");
            }
            str = str.replaceAll("(?i)\\{PREFIX\\}", user.getChatPrefix());
        }

        if (user != null && user instanceof IngameUser && ((IngameUser) user).getPlayer() != null) {
            Player p = ((IngameUser) user).getPlayer();
            str = str.replaceAll("(?i)\\{X\\}", String.valueOf(p.getLocation().getX()));
            str = str.replaceAll("(?i)\\{Y\\}", String.valueOf(p.getLocation().getY()));
            str = str.replaceAll("(?i)\\{Z\\}", String.valueOf(p.getLocation().getZ()));
            str = str.replaceAll("(?i)\\{LEVEL\\}", String.valueOf(p.getLevel()));
            str = str.replaceAll("(?i)\\{(E)?XP(ERIENCE)?\\}", String.valueOf(p.getExp()));
            str = str.replaceAll("(?i)\\{TOTAL(E)?XP(ERIENCE)?\\}", String.valueOf(p.getTotalExperience()));
            str = str.replaceAll("(?i)\\{(FOOD|FOODLEVEL)\\}", String.valueOf(p.getFoodLevel()));
            str = str.replaceAll("(?i)\\{(GAMEMODE|GM)\\}", p.getGameMode().name());
            str = str.replaceAll("(?i)\\{HEALTH\\}", String.valueOf(p.getHealth()));
            str = str.replaceAll("(?i)\\{MAXHEALTH}", String.valueOf(p.getMaxHealth()));
            str = str.replaceAll("(?i)\\{WORLD\\}", p.getWorld().getName());
        }

        if (target != null) {
            str = str.replaceAll("(?i)\\{(T(ARGET)?(NAME)|TARGET)\\}", target.getName());
            str = str.replaceAll("(?i)\\{T(ARGET)?(DISPLAYNAME|FORMATTEDNAME)\\}", target.getDisplayName());
            if (target instanceof IngameUser) {
                str = str.replaceAll("(?i)\\{T(ARGET)?(BALANCE|MONEY)\\}", String.valueOf(((IngameUser) target).getBalance()));
            }

            if (!(target instanceof ConsoleUser)) {
                str = str.replaceAll("(?i)\\{T(ARGET)?(RANK|GROUP)\\}", target.getPrimaryGroup());
            } else {
                str = str.replaceAll("(?i)\\{T(ARGET)?(RANK|GROUP)\\}", "");
            }

            str = str.replaceAll("(?i)\\{T(ARGET)?PREFIX\\}", target.getChatPrefix());
        }

        if (target != null && target instanceof IngameUser && ((IngameUser) target).getPlayer() != null) {
            Player p = ((IngameUser) target).getPlayer();
            str = str.replaceAll("(?i)\\{T(ARGET)?X\\}", String.valueOf(p.getLocation().getX()));
            str = str.replaceAll("(?i)\\{T(ARGET)?Y\\}", String.valueOf(p.getLocation().getY()));
            str = str.replaceAll("(?i)\\{T(ARGET)?Z\\}", String.valueOf(p.getLocation().getZ()));
            str = str.replaceAll("(?i)\\{T(ARGET)?LEVEL\\}", String.valueOf(p.getLevel()));
            str = str.replaceAll("(?i)\\{T(ARGET)?(E)?XP(ERIENCE)?\\}", String.valueOf(p.getExp()));
            str = str.replaceAll("(?i)\\{T(ARGET)?TOTAL(E)?XP(ERIENCE)?\\}", String.valueOf(p.getTotalExperience()));
            str = str.replaceAll("(?i)\\{T(ARGET)?(FOOD|FOODLEVEL)\\}", String.valueOf(p.getFoodLevel()));
            str = str.replaceAll("(?i)\\{T(ARGET)?(GAMEMODE|GM)\\}", p.getGameMode().name());
            str = str.replaceAll("(?i)\\{T(ARGET)?HEALTH\\}", String.valueOf(p.getHealth()));
            str = str.replaceAll("(?i)\\{T(ARGET)?MAXHEALTH}", String.valueOf(p.getMaxHealth()));
            str = str.replaceAll("(?i)\\{T(ARGET)?WORLD\\}", p.getWorld().getName());
        }

        if (SinkLibrary.getInstance().isEconomyAvailable()) {
            str = str.replaceAll("(?i)\\{CURRENCY\\}", VaultBridge.getCurrenyName());
        }

        str = str.replaceAll("(?i)\\{AQUA\\}", ChatColor.AQUA.toString());
        str = str.replaceAll("(?i)\\{BLACK\\}", ChatColor.BLACK.toString());
        str = str.replaceAll("(?i)\\{BOLD\\}", ChatColor.BOLD.toString());
        str = str.replaceAll("(?i)\\{BLUE\\}", ChatColor.BLUE.toString());
        str = str.replaceAll("(?i)\\{DARK(_|\\s)AQUA\\}", ChatColor.DARK_AQUA.toString());
        str = str.replaceAll("(?i)\\{DARK(_|\\s)BLUE\\}", ChatColor.DARK_BLUE.toString());
        str = str.replaceAll("(?i)\\{DARK(_|\\s)GRAY\\}", ChatColor.DARK_GRAY.toString());
        str = str.replaceAll("(?i)\\{DARK(_|\\s)GREEN\\}", ChatColor.DARK_GREEN.toString());
        str = str.replaceAll("(?i)\\{DARK(_|\\s)PURPLE\\}", ChatColor.DARK_PURPLE.toString());
        str = str.replaceAll("(?i)\\{DARK(_|\\s)RED\\}", ChatColor.DARK_RED.toString());
        str = str.replaceAll("(?i)\\{GOLD\\}", ChatColor.GOLD.toString());
        str = str.replaceAll("(?i)\\{GRAY\\}", ChatColor.GRAY.toString());
        str = str.replaceAll("(?i)\\{GREEN\\}", ChatColor.GREEN.toString());
        str = str.replaceAll("(?i)\\{ITALIC\\}", ChatColor.ITALIC.toString());
        str = str.replaceAll("(?i)\\{LIGHT(_|\\s)PURPLE\\}", ChatColor.LIGHT_PURPLE.toString());
        str = str.replaceAll("(?i)\\{MAGIC\\}", ChatColor.MAGIC.toString());
        str = str.replaceAll("(?i)\\{RED\\}", ChatColor.RED.toString());
        str = str.replaceAll("(?i)\\{RESET\\}", ChatColor.RESET.toString());
        str = str.replaceAll("(?i)\\{STRIKETHROUGH\\}", ChatColor.STRIKETHROUGH.toString());
        str = str.replaceAll("(?i)\\{UNDERLINE\\}", ChatColor.UNDERLINE.toString());
        str = str.replaceAll("(?i)\\{WHITE\\}", ChatColor.WHITE.toString());
        str = str.replaceAll("(?i)\\{YELLOW\\}", ChatColor.YELLOW.toString());

        if (customPlaceholders != null) {
            for (String placeHolder : customPlaceholders.keySet()) {
                str = str.replaceAll("(?i)(\\{" + placeHolder.toUpperCase() + "\\})",
                                     String.valueOf(customPlaceholders.get(placeHolder)));
            }
        }

        if (paramValues != null) {
            int i = 0;
            for (Object s : paramValues) {
                str = str.replaceAll("\\{" + i + "\\}", String.valueOf(s));
                i++;
                if (formatS) {
                    str = str.replaceAll("%" + i + "\\$\\s", String.valueOf(s));
                }
            }
        }

        str = ChatColor.translateAlternateColorCodes('&', str);

        // Don't translate color on these placeholders:
        if (userMessage != null) {
            str = str.replaceAll("(?i)\\{(USER)?MESSAGE\\}", stripRegex(userMessage));
        }

        return str;
    }

    /**
     * Strip regex from a string
     * @param s String to strip
     * @return Stripped string
     */
    public static String stripRegex(String s) {
        s = s.replace("\\", "\\\\");
        s = s.replace(".", "\\.");
        s = s.replace("^", "\\^");
        s = s.replace("$", "\\$");
        s = s.replace("*", "\\*");
        s = s.replace("+", "\\+");
        s = s.replace("?", "\\?");
        s = s.replace("(", "\\(");
        s = s.replace(")", "\\)");
        s = s.replace("[", "\\[");
        s = s.replace("{", "\\{");
        s = s.replace("|", "\\|");
        s = s.replace("-", "\\-");
        s = s.replace("]", "\\]");
        s = s.replace("}", "\\}");

        return s;
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
        if (input == null || input.length == 0) {
            return "";
        }

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
            if (input[i] == null) {
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

    /**
     * @param s String to check
     * @return True if a string is null or if the string is empty (length == 0)
     */
    public static boolean isEmptyOrNull(@Nullable String s) {
        return s == null || s.trim().length() == 0 || s.trim().isEmpty();
    }
}
