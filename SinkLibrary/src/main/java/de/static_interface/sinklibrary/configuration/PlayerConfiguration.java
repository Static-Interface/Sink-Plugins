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
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.ChatColor;

import java.io.File;

public class PlayerConfiguration extends ConfigurationBase {

    public static final int REQUIRED_VERSION = 1;

    private SinkUser user = null;

    /**
     * Stores Player Informations and Settings in PlayerConfiguration YAML Files.
     * Should be accessed via {@link de.static_interface.sinklibrary.SinkUser#getPlayerConfiguration()}
     *
     * @param user User
     */
    public PlayerConfiguration(SinkUser user) {
        super(new File(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Players"), user.getUniqueId().toString() + ".yml"), false);
        if (user.isConsole()) {
            throw new RuntimeException("User is Console, cannot create PlayerConfiguration!");
        }

        //Convert to UUID
        File oldFile = new File(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Players"), user.getName() + ".yml");
        File uniqueFile = getFile();

        if (oldFile.exists()) {
            oldFile.renameTo(uniqueFile);
        }

        this.user = user;
        init();
    }

    @Override
    public void addDefaults() {
        yamlConfiguration.options().header(String.format("This configuration saves and loads variables of players.%nDon't edit it."));

        addDefault("ConfigVersion", REQUIRED_VERSION);
        addDefault("LastKnownName", user.getName());
        addDefault("StatsEnabled", true);
        addDefault("ExceptionTrackingEnabled", false);
        addDefault("SpyEnabled", true);
        addDefault("Nick.HasDisplayName", false);
        addDefault("Nick.DisplayName", user.getDefaultDisplayName());
    }

    /**
     * @return True if spy is enabled
     */
    public boolean isSpyEnabled() {
        return (boolean) get("SpyEnabled");
    }

    /**
     * Enable or disable Spy
     *
     * @param value True will enable it, false disable
     */
    public void setSpyEnabled(boolean value) {
        set("SpyEnabled", value);
    }

    /**
     * @return True if stats are enabled
     */
    public boolean isStatsEnabled() {
        return (boolean) get("StatsEnabled");
    }

    /**
     * Set stats Enabled
     *
     * @param value Value
     */
    public void setStatsEnabled(boolean value) {
        set("StatsEnabled", value);
    }

    /**
     * @return Custom Display Name of Player
     */
    public String getDisplayName() {
        if (!getHasDisplayName()) {
            return user.getDefaultDisplayName();
        }
        return (String) get("Nick.DisplayName");
    }

    /**
     * Set DisplayName for player
     *
     * @param displayName New Display Name
     */
    public void setDisplayName(String displayName) {
        set("Nick.DisplayName", displayName);
        if (ChatColor.stripColor(displayName).equals(ChatColor.stripColor(user.getDefaultDisplayName()))) {
            setHasDisplayName(false);
        }
    }

    /**
     * @return True if player has an custom Display Name
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean getHasDisplayName() {
        return (boolean) get("Nick.HasDisplayName");
    }

    /**
     * @param value If set to true, player will use custom displayname instead of default
     */
    public void setHasDisplayName(boolean value) {
        set("Nick.HasDisplayName", value);
    }
}
