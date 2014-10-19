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

import de.static_interface.sinklibrary.api.logger.SinkLogger;
import de.static_interface.sinklibrary.util.Debug;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

@SuppressWarnings("InstanceMethodNamingConvention")
public class Logger implements SinkLogger {

    /**
     * Protected constructor
     * Use {@link de.static_interface.sinklibrary.SinkLibrary#getCustomLogger()} instead
     */
    protected Logger() {
    }

    @Override
    public void log(Level level, String message) {
        Bukkit.getLogger().log(level, ChatColor.translateAlternateColorCodes('§', message));
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        try {
            String thr = ExceptionUtils.getStackTrace(throwable);
            logToFile(level, String.format(ChatColor.stripColor(message) + "%n%s", thr));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }
        Bukkit.getLogger().log(level, ChatColor.translateAlternateColorCodes('§', message), throwable);
    }

    @Deprecated
    public void logToFile(Level level, String message) {
        Debug.logToFile(level, message);
    }

    @Override
    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    @Override
    public void info(String message) {
        log(Level.INFO, message);
    }

    @Override
    public void warn(String message) {
        log(Level.WARNING, message);
    }

    @Override
    @Deprecated
    public void debug(String message) {
        Debug.log(message);
    }
}
