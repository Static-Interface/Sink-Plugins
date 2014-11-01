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

package de.static_interface.sinklibrary.user;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.exception.EconomyNotAvailableException;
import de.static_interface.sinklibrary.api.exception.PermissionsNotAvailableException;
import de.static_interface.sinklibrary.api.model.BanData;
import de.static_interface.sinklibrary.api.user.Bannable;
import de.static_interface.sinklibrary.api.user.Identifiable;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.VaultBridge;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;

public class IngameUser extends SinkUser<OfflinePlayer> implements Identifiable, Bannable {

    private Player player = null;
    private OfflinePlayer base = null;
    private String playerName = null;
    private IngameUserConfiguration config = null;
    private File configurationFile;
    IngameUser(OfflinePlayer base, SinkUserProvider provider) {
        super(base, provider);
        this.base = base;
        configurationFile = new File(new File(SinkLibrary.getInstance().getCustomDataFolder(), "players"), getUniqueId().toString() + ".yml");
        player = base.getPlayer();
        playerName = base.getName();

        if (playerName == null) {
            SinkLibrary.getInstance().getLogger().warning("Couldn't get player name from UUID: " + getUniqueId().toString());
        }
    }

    @Override
    public OfflinePlayer getBase() {
        return base;
    }

    /**
     * Get current money of player
     *
     * @return Money of player
     * @throws de.static_interface.sinklibrary.api.exception.EconomyNotAvailableException if economy is not available.
     */
    public double getBalance() {
        validateEconomy();
        return VaultBridge.getBalance(getBase());
    }

    /**
     * Allows to add or substract money from user.
     *
     * @param amount Amount to be added / substracted. May be negative for substracting
     * @return true if successful
     */
    public boolean addBalance(double amount) {
        validateEconomy();
        return VaultBridge.addBalance(getBase(), amount);
    }

    private void validateEconomy() {
        if (!SinkLibrary.getInstance().isEconomyAvailable()) {
            throw new EconomyNotAvailableException();
        }
    }

    /**
     * @return The PlayerConfiguration of the Player
     */
    public IngameUserConfiguration getConfiguration() {
        if (config == null) {
            config = new IngameUserConfiguration(this, configurationFile);
        }
        return config;
    }

    /**
     * @return CommandSender
     */
    public CommandSender getSender() {
        return getPlayer();
    }

    @Override
    public boolean hasPermission(SinkCommand command) {
        String permission = command.getPermission();
        return permission == null || hasPermission(command.getPermission());
    }

    public boolean hasPlayedBefore() {
        return base.hasPlayedBefore() && configurationFile.exists();
    }

    @Override
    public boolean isOp() {
        return getBase().isOp();
    }

    @Override
    public void setOp(boolean value) {
        getBase().setOp(value);
    }

    /**
     * Get Player instance
     *
     * @return Player
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * @param permission Permission required
     * @return True if the player has the permission specified by parameter.
     */
    public boolean hasPermission(String permission) {
        //Todo: fix this for offline usage?
        if (!isOnline()) {
            throw new RuntimeException("This may be only used for online players!");
        }
        //if (SinkLibrary.getInstance().permissionsAvailable())
        //{
        //    return SinkLibrary.getInstance().getPermissions().has(base, permission);
        //}
        //else
        //{
        return player.hasPermission(permission);
        //}
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return player.hasPermission(permission);
    }

    /**
     * Get user's primary group.
     *
     * @return Primary Group
     * @throws de.static_interface.sinklibrary.api.exception.PermissionsNotAvailableException if permissions are not available
     */
    public String getPrimaryGroup() {
        if (!SinkLibrary.getInstance().isPermissionsAvailable()) {
            throw new PermissionsNotAvailableException();
        }
        return SinkLibrary.getInstance().getPermissions().getPrimaryGroup(BukkitUtil.getMainWorld().getName(), base);
    }


    /**
     * @return Display Name with Permission Prefix or Op/non-Op Prefix
     */
    public String getDefaultDisplayName() {
        if (!isOnline()) {
            return playerName;
        }

        return getChatPrefix() + playerName + ChatColor.RESET;
    }

    /**
     * Get Chat prefix
     *
     * @return Player chat prefix
     * @deprecated use {@link #getChatPrefix()} instead
     * @throws de.static_interface.sinklibrary.api.exception.ChatNotAvailableException if chat is not available
     */
    @Deprecated
    public String getPrefix() {
        return getChatPrefix();
    }

    /**
     * Get Chat prefix
     *
     * @return Player chat prefix
     * @throws de.static_interface.sinklibrary.api.exception.ChatNotAvailableException if chat is not available
     */
    public String getChatPrefix() {
        if (!SinkLibrary.getInstance().isChatAvailable()) {
            return base.isOp() ? ChatColor.DARK_RED.toString() : ChatColor.WHITE.toString();
        }
        return ChatColor
                .translateAlternateColorCodes('&', SinkLibrary.getInstance().getChat().getPlayerPrefix(BukkitUtil.getMainWorld().getName(), base));
    }


    /**
     * Get players name (useful for offline player usage)
     *
     * @return Players name
     */
    public String getName() {
        return playerName;
    }

    /**
     * @return True if player is online and does not equals null
     */
    public boolean isOnline() {
        return base.isOnline();
    }

    /**
     * @return Player's custom displayname
     */
    public String getDisplayName() {
        if (!isOnline()) {
            return playerName;
        }
        if (!SinkLibrary.getInstance().getSettings().isDisplayNamesEnabled() || !getConfiguration().getHasDisplayName()) {
            String prefix = "";
            if (SinkLibrary.getInstance().isChatAvailable()) {
                prefix =
                        ChatColor.translateAlternateColorCodes('&', SinkLibrary.getInstance().getChat()
                                .getPlayerPrefix(BukkitUtil.getMainWorld().getName(), base));
            }
            return prefix + player.getDisplayName();
        } else {
            return getConfiguration().getDisplayName();
        }
    }

    /**
     * Sends message to user if online
     *
     * @param message Message to be displayed
     */
    public void sendMessage(String message) {
        if (isOnline()) {
            player.sendMessage(message);
        }
    }

    /**
     * @return The unique ID of the user
     */
    public UUID getUniqueId() {
        return base.getUniqueId();
    }

    public void ban(String reason, long timeout) {
        if (isOnline()) {
            getPlayer().kickPlayer(reason);
        }

        getConfiguration().setBanned(true);
        getConfiguration().setBanTime(System.currentTimeMillis());
        getConfiguration().setBanTimeOut(timeout);
        getConfiguration().setBanReason(reason);
    }

    public void unban() {
        getConfiguration().setBanned(false);
        getConfiguration().setUnbanTime(System.currentTimeMillis());
    }

    public BanData getBanData() {
        return new BanData(getConfiguration().isBanned(), getConfiguration().getBanTime(), getConfiguration().getBanTimeOut(),
                           getConfiguration().getUnbanTime(), getConfiguration().getBanReason());
    }
}
