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

package de.static_interface.sinklibrary.configuration;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings({"OverlyBroadCatchBlock", "InstanceMethodNamingConvention", "BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention"})
public abstract class ConfigurationBase
{
    private static boolean busy;

    /**
     * Create Configuration File
     */
    public abstract void create();

    /**
     * Save config file
     */
    public void save()
    {
        if ( getFile() == null )
        {
            return;
        }
        if ( !exists() )
        {
            return;
        }

        try
        {
            getYamlConfiguration().save(getFile());
        }
        catch ( IOException ignored )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't save configuration file: " + getFile() + '!');
        }
    }

    /**
     * @param path  Path to value
     * @param value Value of path
     */
    public void set(String path, Object value)
    {
        try
        {
            getYamlConfiguration().set(path, value);
            save();
        }
        catch ( RuntimeException e )
        {
            SinkLibrary.getCustomLogger().log(Level.WARNING, "Configuration:" + getFile() + ": Couldn't save " + value + " to path " + path, e);
        }
    }

    /**
     * Get value from configuration. If it doesn't exists, it will return the default value.
     *
     * @param path Path to value
     * @return Value of path
     */
    public Object get(String path)
    {
        try
        {
            Object value = getYamlConfiguration().get(path);
            if ( value == null || value == "" )
            {
                throw new NullPointerException("Path returned null!");
            }
            return value;
        }
        catch ( Exception e )
        {
            SinkLibrary.getCustomLogger().log(Level.WARNING, getFile() + ": Couldn't load value from path: " + path + ". Reason: " + e.getMessage() + " Using default value.");
            return getDefault(path);
        }
    }

    /**
     * Get YAML Configuration
     */
    public abstract YamlConfiguration getYamlConfiguration();

    /**
     * @return True if the config file exists
     */
    public boolean exists()
    {
        return getFile().exists();
    }

    /**
     * Load Configuration
     */
    public abstract void load();

    /**
     * Get default value for path
     *
     * @param path Path to value
     */
    public Object getDefault(String path)
    {
        if ( getDefaults() == null )
        {
            throw new RuntimeException("defaultValues are null! Couldn't read value from path: " + path);
        }
        return getDefaults().get(path);
    }

    /**
     * Add a default value
     *
     * @param path  Path to value
     * @param value Value of path
     */
    public void addDefault(String path, Object value)
    {
        if ( getDefaults() == null )
        {
            throw new RuntimeException("defaultValues are null! Couldn't add " + value + " to path: " + path);
        }
        if ( !getYamlConfiguration().isSet(path) || getYamlConfiguration().get(path) == null )
        {
            getYamlConfiguration().set(path, value);
            save();
        }
        getDefaults().put(path, value);
    }

    /**
     * Get Defaults
     *
     * @return Default values
     */
    public abstract HashMap<String, Object> getDefaults();

    /**
     * Backup Configuration.
     *
     * @throws IOException if backup fails
     */
    public void backup() throws IOException
    {
        Util.backupFile(getFile(), true);
    }

    /**
     * Delete Configuration
     */
    public void delete()
    {
        getFile().delete();
    }

    /**
     * Get configuration file
     *
     * @return configuration file
     */
    public abstract File getFile();

    /**
     * Recreate configuration
     */
    public void recreate()
    {
        if ( busy ) return;

        busy = true;

        SinkLibrary.getCustomLogger().log(Level.WARNING, "Recreating Configuration: " + getFile());
        try
        {
            backup();
        }
        catch ( IOException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't backup configuration: " + getFile(), e);
            return;
        }
        delete();
        create();

        busy = false;
    }

    /**
     * Reload a config
     */
    public void reload()
    {
        if ( !exists() ) return;
        load();
    }
}
