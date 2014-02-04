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
