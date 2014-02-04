/*
 * Copyright (c) 2014 adventuria.eu / static-interface.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.static_interface.sinklibrary;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

@SuppressWarnings("InstanceMethodNamingConvention")
public class Logger
{

    /**
     * Protected constructor
     * Use {@link de.static_interface.sinklibrary.SinkLibrary#getLogger()} instead
     */
    protected Logger()
    {}

    boolean failed = false;
    FileWriter fileWriter = null;

    public void log(Level level, String message)
    {
        try
        {
            logToFile(level, ChatColor.stripColor(message));
        }
        catch ( Exception e )
        {
            Bukkit.getLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }

        Bukkit.getLogger().log(level, ChatColor.translateAlternateColorCodes('§', message));
    }

    public void log(Level level, String message, Throwable throwable)
    {
        try
        {
            logToFile(level, String.format(ChatColor.stripColor(message) + "%n%s", throwable));
        }
        catch ( Exception e )
        {
            Bukkit.getLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }
        Bukkit.getLogger().log(level, ChatColor.translateAlternateColorCodes('§', message), throwable);
    }

    public void logToFile(Level level, String message)
    {

        boolean enabled;
        try
        {
            enabled = SinkLibrary.getSettings().isLogEnabled();
        }
        catch ( Exception ignored )
        {
            return;
        }
        if ( !enabled ) return;

        File logFile = new File(SinkLibrary.getCustomDataFolder() + File.separator + "SinkPlugins.log");
        if ( !failed && !logFile.exists() ) // Prevent creating/checking every time
        {
            if ( !Util.createFile(logFile) ) return;
            failed = true;
            return;
        }
        else if ( failed ) return;
        if ( fileWriter == null ) try
        {
            fileWriter = new FileWriter(logFile, true);
        }
        catch ( IOException e )
        {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't create FileWriter: ", e);
            return;
        }

        String newLine = System.getProperty("line.separator");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String date = format.format(new Date());

        try
        {
            fileWriter.write('[' + date + ' ' + level.getName() + "]: " + message + newLine);
        }
        catch ( IOException e )
        {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't write to log file: ", e);
            e.printStackTrace();
        }
    }

    public void severe(String message)
    {
        log(Level.SEVERE, message);
    }

    public void info(String message)
    {
        log(Level.INFO, message);
    }

    public void warning(String message)
    {
        log(Level.WARNING, message);
    }

    public void debug(String message)
    {
        if ( SinkLibrary.getSettings().isDebugEnabled() )
        {
            log(Level.INFO, "[DEBUG] " + message);
        }
    }

    FileWriter getFileWriter()
    {
        return fileWriter;
    }
}
