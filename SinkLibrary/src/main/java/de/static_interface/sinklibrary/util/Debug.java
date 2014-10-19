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

import de.static_interface.sinklibrary.SinkLibrary;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Debug {

    private static final int CLIENT_CODE_STACK_INDEX;
    private static boolean failed = false;
    private static FileWriter fileWriter = null;

    static {
        // Finds out the index of "this code" in the returned stack trace - funny but it differs in JDK 1.5 and 1.6
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(Debug.class.getName())) {
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }

    public static Class<?> getCallerClass() {
        return getCallerClass(1);
    }

    /**
     * @param depth Depth <br/>
     *        0 Class calling this method<br/>
     *        1 Class calling the class which is calling this method<br/>
     *        2 Class calling the class which is calling the class which is calling this method<br/>
     *        ...
     * @return {@link Class} instance of the caller class
     */
    public static Class<?> getCallerClass(int depth) {
        try {
            return Class.forName(Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + 1 + depth].getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null; // Shouldn't happen
    }

    public static String getCallerMethodName() {
        return getCallerMethodName(1);
    }

    /**
     * @param depth Depth <br/>
     *        0 Method calling this method<br/>
     *        1 Method calling the method which is calling this method<br/>
     *        2 Method calling the method which is calling the method which is calling this method<br/>
     *        ...
     * @return Name of the calling method
     */
    public static String getCallerMethodName(int depth) {
        return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + 1 + depth].getMethodName();
    }

    public static void log(String message) {
        logInternal(Level.INFO, message, null);
    }

    public static void log(Level level, String message) {
        logInternal(level, message, null);
    }

    public static void log(Throwable throwable) {
        logInternal(Level.SEVERE, "Unexpected Exception occurred: ", throwable);
    }

    public static void log(String message, Throwable throwable) {
        logInternal(Level.SEVERE, message, throwable);
    }

    public static void log(@Nonnull Level level, @Nonnull String message, @Nullable Throwable throwable) {
        logInternal(level, message, throwable);
    }

    private static void logInternal(@Nonnull Level level, @Nonnull String message, @Nullable Throwable throwable) {
        if (!SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
            return;
        }
        if (throwable != null) {
            String thr = ExceptionUtils.getStackTrace(throwable);
            logToFile(level, String.format(ChatColor.stripColor(message) + "%n%s", thr));
        } else {
            logToFile(level, String.format(ChatColor.stripColor(message)));
        }

        if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
            Bukkit.getLogger().log(level, "[Debug] " + Debug.getCallerClass(2).getSimpleName() + "#" + getCallerMethodName(2) + ": " + message);
        }
    }

    public static void logMethodCall(Object... arguments) {
        String args = StringUtil.formatArrayToString(arguments, ", ");
        args = "(" + args + ")";
        logInternal(Level.INFO, args, null);
    }

    public static void logToFile(Level level, String message) {
        boolean enabled;
        try {
            enabled = SinkLibrary.getInstance().getSettings().isLogEnabled();
        } catch (Exception ignored) {
            return;
        }
        if (!enabled) {
            return;
        }

        File logFile = new File(SinkLibrary.getInstance().getCustomDataFolder(), "Debug.log");
        if (!failed && !logFile.exists()) // Prevent creating/checking every time
        {
            if (!FileUtil.createFile(logFile)) {
                return;
            }
            failed = true;
            return;
        } else if (failed) {
            return;
        }
        if (fileWriter == null) {
            try {
                fileWriter = new FileWriter(logFile, true);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Couldn't create FileWriter: ", e);
                return;
            }
        }

        String newLine = System.getProperty("line.separator");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String date = format.format(new Date());

        try {
            fileWriter.write('[' + date + ' ' + level.getName() + "]: " + message + newLine);
        } catch (IOException ignored) {
            //Do nothing...

        }
    }

    public static FileWriter getDebugLogFileWriter() {
        return fileWriter;
    }
}
