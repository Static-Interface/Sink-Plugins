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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUtil {

    public static Object parseValue(String[] args) {
        String arg = args[0].trim();

        if (arg.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            Long l = Long.parseLong(arg);
            if (l <= Byte.MAX_VALUE) {
                return Byte.parseByte(arg);
            } else if (l <= Short.MAX_VALUE) {
                return Short.parseShort(arg); // Value is a Short
            } else if (l <= Integer.MAX_VALUE) {
                return Integer.parseInt(arg); // Value is an Integer
            }
            return l; // Value is a Long
        } catch (NumberFormatException ignored) {
        }

        try {
            return Float.parseFloat(arg); // Value is Float
        } catch (NumberFormatException ignored) {
        }

        try {
            return Double.parseDouble(arg); // Value is Double
        } catch (NumberFormatException ignored) {
        }

        //Parse Booleans
        if (arg.equalsIgnoreCase("true")) {
            return true;
        } else if (arg.equalsIgnoreCase("false")) {
            return false;
        }

        if (arg.startsWith("'") && arg.endsWith("'") && arg.length() == 3) {
            return arg.toCharArray()[1]; // Value is char
        }

        String tmp = "";
        for (String s : args) {
            if (tmp.equals("")) {
                tmp = s;
            } else {
                tmp += " " + s;
            }
        }

        if (tmp.startsWith("[") && tmp.endsWith("]")) {
            List<String> list = Arrays.asList(tmp.substring(1, tmp.length() - 1).split(", "));
            List<Object> arrayList = new ArrayList<>();
            for (String s : list) {
                arrayList.add(parseValue(s.split(" ")));
            }
            return arrayList.toArray(new Object[arrayList.size()]);
        }

        return tmp; //is string
    }
}
