/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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
import de.static_interface.sinklibrary.api.command.ConfigurableCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Permission;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class CommandUtil {
    public static Object parseValue(String[] args) {
        return parseValue(args, null, false);
    }

    public static <T> T parseValue(String[] args, @Nullable Class<T> returnType) {
        return parseValue(args, returnType, false);
    }

    public static <T> T parseValue(String[] args, @Nullable Class<T> returnType, boolean strict) {
        String arg = args[0].trim();

        if (strict && arg.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            Long l = Long.parseLong(arg);
            if (l <= Byte.MAX_VALUE && (returnType == null || returnType.isAssignableFrom(Byte.class) || returnType.isAssignableFrom(byte.class))) {
                return (T) Byte.valueOf(Byte.parseByte(arg));
            } else if (l <= Short.MAX_VALUE && (returnType == null || returnType.isAssignableFrom(Short.class) || returnType
                    .isAssignableFrom(short.class))) {
                return (T) Short.valueOf(Short.parseShort(arg)); // Value is a Short
            } else if (l <= Integer.MAX_VALUE && (returnType == null || returnType.isAssignableFrom(Integer.class) || returnType
                    .isAssignableFrom(int.class))) {
                return (T) Integer.valueOf(Integer.parseInt(arg)); // Value is an Integer
            }
            if (returnType == null || returnType.isAssignableFrom(Long.class) || returnType.isAssignableFrom(long.class)) {
                return (T) l; // Value is a Long
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            if (returnType == null || returnType.isAssignableFrom(Float.class) || returnType.isAssignableFrom(float.class)) {
                return (T) Float.valueOf(Float.parseFloat(arg)); // Value is Float
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            if (returnType == null || returnType.isAssignableFrom(Double.class) || returnType.isAssignableFrom(double.class)) {
                return (T) Double.valueOf(Double.parseDouble(arg)); // Value is Double
            }
        } catch (NumberFormatException ignored) {
        }

        //Parse Booleans
        if (returnType == null || returnType.isAssignableFrom(Boolean.class) || returnType.isAssignableFrom(boolean.class)) {
            if (arg.equalsIgnoreCase("true")) {
                return (T) Boolean.TRUE;
            } else if (arg.equalsIgnoreCase("false")) {
                return (T) Boolean.FALSE;
            }
        }

        if (returnType == null || returnType.isAssignableFrom(Character.class) || returnType.isAssignableFrom(char.class)) {
            if (arg.startsWith("'") && arg.endsWith("'") && arg.length() == 3) {
                return (T) Character.valueOf(arg.toCharArray()[1]); // Value is char
            }
        }

        String tmp = StringUtil.formatArrayToString(args, " ");

        if (returnType == null || returnType.isAssignableFrom(ArrayList.class)) {
            if (tmp.startsWith("[") && tmp.endsWith("]")) {
                List<String> list = Arrays.asList(tmp.substring(1, tmp.length() - 1).split(", "));
                List<Object> arrayList = new ArrayList<>();
                for (String s : list) {
                    arrayList.add(parseValue(s.split(" ")));
                }
                return (T) arrayList;
            }
        }

        if (returnType.isAssignableFrom(Player.class)) {
            return (T) Bukkit.getPlayer(args[0]);
        }

        if (returnType.isAssignableFrom(OfflinePlayer.class)) {
            return (T) Bukkit.getOfflinePlayer(args[0]);
        }

        if (returnType.isAssignableFrom(IngameUser.class)) {
            return (T) SinkLibrary.getInstance().getIngameUser(args[0]);
        }

        if (returnType.isAssignableFrom(IrcUser.class)) {
            return (T) SinkLibrary.getInstance().getIrcUser(args[0]);
        }

        if (returnType.isAssignableFrom(SinkUser.class)) {
            return (T) SinkLibrary.getInstance().getUser(args[0]);
        }

        if ((returnType == null || returnType.isAssignableFrom(String.class)) && (!strict || (strict && tmp.startsWith("\"") && tmp
                .endsWith("\"")))) {
            if (strict) {
                tmp = StringUtil.replaceFirst(tmp, "\"", "");
                tmp = StringUtil.replaceLast(tmp, "\"", "");
            }
            return (T) tmp; //is string
        }

        if (returnType != null && SinkLibrary.getInstance().hasStringConvertProvider(returnType)) {
            return (T) SinkLibrary.getInstance().getStringConvertProvider(returnType).convert(args, strict);
        }

        if (returnType == Object.class && !strict) {
            return null;
        }
        throw new IllegalArgumentException(
                "ReturnType " + returnType.getName() + " for args: [" + StringUtil.formatArrayToString(args, ", ") + "] is not supported!");
    }

    public static void initAnnotations(ConfigurableCommand command, AnnotatedElement annonatedElement) {
        Configuration config = command.getConfig();

        String commandPathPrefix = command.getConfigPath() + ".";

        boolean permSet = false;
        Permission perm = annonatedElement.getAnnotation(Permission.class);
        if (perm != null) {
            command.setCommandPermission(perm.value());
            permSet = true;
        } else if (annonatedElement.getAnnotation(DefaultPermission.class) != null) {
            if (command.getDefaultPermission() != null) {
                command.setCommandPermission(command.getDefaultPermission());
                permSet = true;
            }
        }
        if (config != null) {
            String value = (String) config.get(commandPathPrefix + "Permission");
            if (StringUtil.isEmptyOrNull(value)) {
                config.set(commandPathPrefix + "Permission", getDefaultValue(command.getCommandPermission()));
            }
            command.setCommandPermission((String) config.get(commandPathPrefix + "Permission", command.getCommandPermission()));
            permSet = true;
        }

        if(!permSet) {
            command.setCommandPermission(null);
        }

        Aliases aliases = annonatedElement.getAnnotation(Aliases.class);
        if (aliases != null) {
            command.setCommandAliases(Arrays.asList(aliases.value()));
        } else {
            command.setCommandAliases(new ArrayList<String>());
        }
        if (config != null) {
            Object value = config.get(commandPathPrefix + "Aliases");
            if (value == null) {
                List<String> defaultAliases = command.getCommandAliases();
                if (defaultAliases == null) {
                    defaultAliases = new ArrayList<>();
                }
                config.set(commandPathPrefix + "Aliases", defaultAliases);
            }
            command.setCommandAliases((List<String>) config.get(commandPathPrefix + "Aliases"));
        }

        Usage usage = annonatedElement.getAnnotation(Usage.class);
        if (usage != null) {
            command.setCommandUsage(usage.value());
        }

        //If the command doesn't support usages we don't need to make it configurable, so check first if an @Usage annotation exists
        if (usage != null && config != null) {
            String value = (String) config.get(commandPathPrefix + "Usage");
            if (StringUtil.isEmptyOrNull(value)) {
                config.set(commandPathPrefix + "Usage", getDefaultValue(command.getCommandUsage()));
            }
            command.setCommandUsage((String) config.get(commandPathPrefix + "Usage"));
        }

        Description description = annonatedElement.getAnnotation(Description.class);
        if (description != null) {
            command.setCommandDescription(description.value());
        }
        if (config != null) {
            String value = (String) config.get(commandPathPrefix + "Description");
            if (StringUtil.isEmptyOrNull(value)) {
                config.set(commandPathPrefix + "Description", getDefaultValue(command.getCommandDescription()));
            }
            command.setCommandDescription((String) config.get(commandPathPrefix + "Description"));
        }

        if (config != null) {
            config.save();
        }
    }

    private static String getDefaultValue(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
}
