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

package de.static_interface.sinklibrary.util;

import org.bukkit.Bukkit;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import java.lang.reflect.Method;

/**
 * As we can't use SinkIRC as dependency (it would cause a cyclic reference), we need to call the methods with reflection
 */
public class SinkIrcReflection {

    public static PircBotX getPircBotX() {
        try {
            Class<?> c = Class.forName("de.static_interface.sinkirc.SinkIRC");
            Method method = c.getMethod("getIrcBot");
            method.setAccessible(true);
            return (PircBotX) method.invoke(Bukkit.getPluginManager().getPlugin("SinkIRC"));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't access SinkIRC! Did you forgot to install it?", e);
        }
    }

    public static boolean isIrcOp(User user) {
        boolean value;
        try {
            //Use reflection because we can't add SinkIRC as dependency
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("isOp", User.class);
            method.setAccessible(true);
            value = (boolean) method.invoke(null, user);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't access SinkIRC! Did you forgot to install it?", e);
        }
        return value;
    }

    public static void setOp(User user, boolean value) {
        try {
            //Use reflection because we can't add SinkIRC as dependency
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("setOp", User.class, Boolean.class);
            method.setAccessible(true);
            method.invoke(null, user, value);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't access SinkIRC! Did you forgot to install it?", e);
        }
    }

    public static String getIrcCommandPrefix() {
        try {
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("getCommandPrefix", null);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return (String) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't access SinkIRC! Did you forgot to install it?", e);
        }
    }

    public static Channel getMainChannel() {
        try {
            Class<?> c = Class.forName("de.static_interface.sinkirc.SinkIRC");
            Method method = c.getMethod("getMainChannel");
            method.setAccessible(true);
            return (Channel) method.invoke(Bukkit.getPluginManager().getPlugin("SinkIRC"));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't access SinkIRC! Did you forgot to install it?", e);
        }
    }
}
