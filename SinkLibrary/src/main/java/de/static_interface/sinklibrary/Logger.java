/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary;

import de.static_interface.sinklibrary.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

@SuppressWarnings("InstanceMethodNamingConvention")
public class Logger {

    boolean failed = false;
    FileWriter fileWriter = null;

    /**
     * Protected constructor
     * Use {@link de.static_interface.sinklibrary.SinkLibrary#getCustomLogger()} instead
     */
    protected Logger() {
    }

    public void log(Level level, String message) {
        try {
            logToFile(level, ChatColor.stripColor(message));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }

        Bukkit.getLogger().log(level, ChatColor.translateAlternateColorCodes('§', message));
    }

    public void log(Level level, String message, Throwable throwable) {
        try {
            logToFile(level, String.format(ChatColor.stripColor(message) + "%n%s", throwable));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }
        Bukkit.getLogger().log(level, ChatColor.translateAlternateColorCodes('§', message), throwable);
    }

    public void logToFile(Level level, String message) {

        boolean enabled;
        try {
            enabled = SinkLibrary.getInstance().getSettings().isLogEnabled();
        } catch (Exception ignored) {
            return;
        }
        if (!enabled) {
            return;
        }

        File logFile = new File(SinkLibrary.getInstance().getCustomDataFolder(), "SinkPlugins.log");
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

    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void warning(String message) {
        log(Level.WARNING, message);
    }

    public void debug(String message) {
        if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
            log(Level.INFO, "[DEBUG] " + message);
        }
    }

    FileWriter getFileWriter() {
        return fileWriter;
    }
}
