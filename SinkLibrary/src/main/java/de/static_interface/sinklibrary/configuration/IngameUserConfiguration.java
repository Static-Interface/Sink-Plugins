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
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import org.bukkit.ChatColor;

import java.io.File;

public class IngameUserConfiguration extends Configuration {

    public static final int REQUIRED_VERSION = 1;

    private IngameUser user = null;

    /**
     * Stores Player Informations and Settings in PlayerConfiguration YAML Files.
     * Should be accessed via {@link IngameUser#getConfiguration()}
     *
     * @param user User
     */
    public IngameUserConfiguration(IngameUser user, File configuration) {
        super(configuration, false);

        File oldDirectory = new File(SinkLibrary.getInstance().getCustomDataFolder(), "Players");
        File newDirectory = new File(SinkLibrary.getInstance().getCustomDataFolder(), "players");

        if (oldDirectory.exists()) {
            oldDirectory.renameTo(newDirectory);
        }

        //Convert to UUID
        File oldFile = new File(new File(SinkLibrary.getInstance().getCustomDataFolder(), "players"), user.getName() + ".yml");
        File uniqueFile = getFile();

        if (oldFile.exists()) {
            oldFile.renameTo(uniqueFile);
        }

        this.user = user;
        init();
    }

    @Override
    public void addDefaults() {
        yamlConfiguration.options().header("This configuration saves and loads variables of players."
                                           + "\n You shouldn't edit it.");

        addDefault("ConfigVersion", REQUIRED_VERSION);
        addDefault("LastKnownName", user.getName());
        addDefault("StatsEnabled", true);
        addDefault("ExceptionTrackingEnabled", false);
        addDefault("SpyEnabled", true);
        addDefault("Nick.HasDisplayName", false);
        addDefault("Nick.DisplayName", user.getDefaultDisplayName());

        addDefault("BanData.IsBanned", false);
        addDefault("BanData.BanTime", 0);
        addDefault("BanData.Timeout", 0);
        addDefault("BanData.UnbanTime", 0);
        addDefault("BanData.Reason", "");
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
        Object configName = get("Nick.DisplayName");
        if (!getHasDisplayName() || configName == null) {
            return user.getDefaultDisplayName();
        }
        return (String) configName;
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

    public boolean isBanned() {
        return (boolean) get("BanInfo.IsBanned");
    }

    public void setBanned(boolean b) {
        set("BanInfo.IsBanned", b);
    }

    public String getBanReason() {
        return (String) get("BanInfo.Reason");
    }

    public void setBanReason(String reason) {
        set("BanInfo.Reason", reason);
    }

    public long getBanTime() {
        return Long.parseLong(String.valueOf(get("BanInfo.BanTime")));
    }

    public void setBanTime(long bantime) {
        set("BanInfo.BanTime", bantime);
    }

    public long getUnbanTime() {
        return Long.parseLong(String.valueOf(get("BanInfo.UnbanTime")));
    }

    public void setUnbanTime(long bantime) {
        set("BanInfo.UnbanTime", bantime);
    }

    public long getBanTimeOut() {
        return Long.parseLong(String.valueOf(get("BanInfo.Timeout")));
    }

    public void setBanTimeOut(long unbantime) {
        set("BanInfo.Timeout", unbantime);
    }
}
