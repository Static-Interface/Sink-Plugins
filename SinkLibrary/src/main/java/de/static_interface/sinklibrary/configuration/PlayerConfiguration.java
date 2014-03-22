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

import com.google.common.io.Files;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class PlayerConfiguration extends ConfigurationBase
{
    public static final int REQUIRED_VERSION = 1;

    private Player player;
    private File yamlFile = null;
    private YamlConfiguration yamlConfiguration = null;
    private User user;

    HashMap<String, Object> defaultValues = null;

    /**
     * Stores Player Informations and Settings in PlayerConfiguration YAML Files.
     * Should be accessed via {@link de.static_interface.sinklibrary.User#getPlayerConfiguration()}
     *
     * @param user User
     */
    public PlayerConfiguration(User user)
    {
        if ( user.isConsole() )
        {
            throw new RuntimeException("User is Console, cannot create PlayerConfiguration.");
        }

        this.user = user;
        player = user.getPlayer();

        load();
    }

    @Override
    public YamlConfiguration getYamlConfiguration()
    {
        return yamlConfiguration;
    }

    @Override
    public void create()
    {
        try
        {
            File playersPath = new File(SinkLibrary.getCustomDataFolder() + File.separator + "Players");
            yamlFile = new File(playersPath, user.getName() + ".yml");

            boolean createNewConfiguration = !exists();

            if ( createNewConfiguration )
            {
                SinkLibrary.getCustomLogger().log(Level.INFO, "Creating new player configuration: " + yamlFile);
            }

            Files.createParentDirs(yamlFile);

            if ( createNewConfiguration && !yamlFile.createNewFile() )
            {
                SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't create player configuration: " + yamlFile);
                return;
            }

            yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(yamlFile);

            /*
            if ( !createNewConfiguration )
            {
                int version;
                try
                {
                    version = (int) get("ConfigVersion");
                }
                catch(NullPointerException e)
                {
                    version = 0;
                }
                if ( version < REQUIRED_VERSION )
                {
                    SinkLibrary.getCustomLogger().log(Level.WARNING, "***************");
                    SinkLibrary.getCustomLogger().log(Level.WARNING, "Configuration: " + yamlFile + " is too old! Current Version: " + version + ", required Version: " + REQUIRED_VERSION);
                    recreate();
                    SinkLibrary.getCustomLogger().log(Level.WARNING, "***************");
                    return;
                }
            }
            */

            yamlConfiguration.options().header(String.format("This configuration saves and loads variables of players.%nDon't edit it."));

            addDefault("ConfigVersion", REQUIRED_VERSION);
            addDefault("StatsEnabled", true);
            addDefault("ExceptionTrackingEnabled", false);
            addDefault("SpyEnabled", true);
            addDefault("Nick.HasDisplayName", false);
            addDefault("Nick.DisplayName", user.getDefaultDisplayName());
            addDefault("DutyTime", 0);

            save();
        }
        catch ( IOException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't create player config file: " + yamlFile.getAbsolutePath());
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }
        catch ( InvalidConfigurationException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "***************");
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Invalid configuration file detected: " + yamlFile);
            SinkLibrary.getCustomLogger().log(Level.SEVERE, e.getMessage());
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "***************");
            recreate();
        }
    }

    @Override
    public void load()
    {
        defaultValues = new HashMap<>();
        create();
    }

    @Override
    public HashMap<String, Object> getDefaults()
    {
        return defaultValues;
    }

    @Override
    public File getFile()
    {
        return yamlFile;
    }

    /**
     * @return True if spy is enabled
     */
    public boolean isSpyEnabled()
    {
        return (boolean) get("SpyEnabled");
    }

    /**
     * Enable or disable Spy
     *
     * @param value True will enable it, false disable
     */
    public void setSpyEnabled(boolean value)
    {
        set("SpyEnabled", value);
    }

    /**
     * @return True if stats are enabled
     */
    public boolean isStatsEnabled()
    {
        return (boolean) get("StatsEnabled");
    }

    /**
     * Set stats Enabled
     *
     * @param value Value
     */
    public void setStatsEnabled(boolean value)
    {
        set("StatsEnabled", value);
    }

    /**
     * Set DisplayName for player
     *
     * @param displayName New Display Name
     */
    public void setDisplayName(String displayName)
    {
        player.setDisplayName(displayName);
        player.setCustomName(displayName);
        set("Nick.DisplayName", displayName);
        if ( ChatColor.stripColor(displayName).equals(ChatColor.stripColor(user.getDefaultDisplayName())) )
        {
            setHasDisplayName(false);
        }
    }

    /**
     * @return Custom Display Name of Player
     */
    public String getDisplayName()
    {
        if ( !getHasDisplayName() )
        {
            return user.getDefaultDisplayName();
        }
        return (String) get("Nick.DisplayName");
    }

    /**
     * @return True if player has an custom Display Name
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean getHasDisplayName()
    {
        return (boolean) get("Nick.HasDisplayName");
    }

    /**
     * @param value If set to true, player will use custom displayname instead of default
     */
    public void setHasDisplayName(boolean value)
    {
        set("Nick.HasDisplayName", value);
    }

    /**
     * @return True if exception tracking is enabled, false if not.
     */
    public boolean getHasExceptionTrackingEnabled()
    {
        return (boolean) get("ExceptionTrackingEnabled");
    }

    public void setHasExceptionTrackingEnabled(boolean value)
    {
        set("ExceptionTrackingEnabled", value);
    }

    public void setDutyTime(long millis)
    {
        set("DutyTime", millis);
    }

    public long getDutyTime()
    {
        return Long.valueOf(String.valueOf(get("DutyTime")));
    }
}
