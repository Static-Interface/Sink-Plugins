/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.util;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.annotation.Unstable;
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

/**
 * Does not work correctly when reloading server
 */
@Unstable
public class Debug {

    public static final String ANONYMOUS_CLASS = "<Anonymous Class>";
    private static boolean failed = false;
    private static FileWriter fileWriter = null;
    private static int STACK_INDEX = 4;

    /**
     * @return True if debug is enabled in the config
     */
    public static boolean isEnabled() {
        return ((SinkLibrary.getInstance().getSettings() != null) && (SinkLibrary.getInstance().getSettings().GENERAL_DEBUG.getValue()));
    }

    /**
     * @return Simple name of the class or {@link #ANONYMOUS_CLASS} if the class
     *         is an anonymous class
     */
    @Nonnull
    @Nullable
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        int index = STACK_INDEX;
        try {
            if (stElements[index].getClassName().equalsIgnoreCase("de.static_interface.sinklibrary.Logger")) {
                index++; // fix for old Logger#debug calls
            }
            String className = stElements[index].getClassName();
            if (StringUtil.isEmptyOrNull(className)) {
                return ANONYMOUS_CLASS;
            }
            return Class.forName(className).getSimpleName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Name of the calling method
     */
    @Nonnull
    @Nullable
    public static String getCallerMethodName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        int index = STACK_INDEX;

        if (stElements[index].getClassName().equalsIgnoreCase("de.static_interface.sinklibrary.Logger")) {
            index++; // fix for old Logger#debug calls
        }

        return stElements[index].getMethodName();
    }

    public static void logMethodCall(@Nullable Object... arguments) {
        String args = "";
        if (arguments != null) {
            args = StringUtil.formatArrayToString(arguments, ", ");
        }
        args = "(" + args + ")";
        logInternal(Level.INFO, args, null);
    }

    public static void log(@Nonnull String message) {
        logInternal(Level.INFO, message, null);
    }

    public static void log(@Nonnull Object o) {
        logInternal(Level.INFO, (o == null ? "null" : o.toString()), null);
    }

    public static void log(@Nonnull Level level, @Nonnull String message) {
        logInternal(level, message, null);
    }

    public static void log(@Nonnull Throwable throwable) {
        logInternal(Level.SEVERE, "Unexpected Exception occurred: ", throwable);
    }

    public static void log(@Nonnull String message, @Nonnull Throwable throwable) {
        logInternal(Level.SEVERE, message, throwable);
    }

    public static void log(@Nonnull Level level, @Nonnull String message, @Nullable Throwable throwable) {
        logInternal(level, message, throwable);
    }

    private static void logInternal(@Nonnull Level level, @Nonnull String message, @Nullable Throwable throwable) {
        boolean
                debugEnabled =
                ((SinkLibrary.getInstance().getSettings() != null) && (SinkLibrary.getInstance().getSettings().GENERAL_DEBUG.getValue()));
        /*
         * TODO: Fails on a first launch (endless loop)
         */
        if (debugEnabled) {
            return;
        }
        if (throwable != null) {
            String thr = ExceptionUtils.getStackTrace(throwable);
            logToFile(level, String.format(ChatColor.stripColor(message) + "%n%s", thr));
        } else {
            logToFile(level, ChatColor.stripColor(message));
        }

        if (debugEnabled) {
            Bukkit.getLogger().log(level, "[Debug] " + Debug.getCallerClassName() + " #" + getCallerMethodName() + ": " + message);
        }
    }

    public static void logToFile(@Nonnull Level level, @Nonnull String message) {
        /*
         * TODO: Fix endless loop
         */
        boolean enabled;
        try {
            enabled = ((SinkLibrary.getInstance().getSettings() != null) && (SinkLibrary.getInstance().getSettings().GENERAL_LOG.getValue()));
        } catch (Exception ignored) {
            return;
        }
        if (!enabled) {
            return;
        }

        File logFile = new File(SinkLibrary.getInstance().getCustomDataFolder(), "Debug.log");
        if (!failed && !logFile.exists()) // Prevent creating/checking every
                                          // time
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
            // Do nothing...
        }
    }

    @Nonnull
    public static FileWriter getDebugLogFileWriter() {
        return fileWriter;
    }
}
