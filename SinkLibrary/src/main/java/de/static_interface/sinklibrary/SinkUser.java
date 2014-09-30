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

import de.static_interface.sinklibrary.configuration.UserConfiguration;
import de.static_interface.sinklibrary.exception.EconomyNotAvailableException;
import de.static_interface.sinklibrary.exception.PermissionsNotAvailableException;
import de.static_interface.sinklibrary.model.BanInfo;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.VaultHelper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import javax.annotation.Nullable;

public class SinkUser implements Comparable<SinkUser> {

    private Player base = null;
    private Economy econ = null;
    private String playerName = null;
    private CommandSender sender = null;
    private UserConfiguration config = null;
    private UUID uuid = null;

    /**
     * Get User instance by player's name
     * <p>
     * <b>Use {@link #SinkUser(java.util.UUID)} for offline players</b>
     *
     * @param sender Sender
     */
    SinkUser(CommandSender sender) {
        this.sender = sender;
        initUser(sender.getName(), true);
    }

    /**
     * Get an user by UUID
     *
     * @param uuid UUID of user
     */
    SinkUser(UUID uuid) {
        this.uuid = uuid;
        initUser(Bukkit.getOfflinePlayer(uuid).getName(), false);
    }

    private void initUser(String player, boolean isConsole) {
        if (isConsole) {
            sender = Bukkit.getConsoleSender();
            base = null;
            econ = SinkLibrary.getInstance().getEconomy();
            playerName = "Console";
            return;
        }
        base = BukkitUtil.getPlayer(player);
        econ = SinkLibrary.getInstance().getEconomy();
        playerName = player;
        if (base == null) {
            uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
            return;
        }
        if (sender == null) {
            sender = base;
        }
        uuid = base.getUniqueId();
    }

    /**
     * Get current money of player
     *
     * @return Money of player
     * @throws de.static_interface.sinklibrary.exception.EconomyNotAvailableException if economy is not available.
     */
    public double getBalance() {
        validateEconomy();

        OfflinePlayer player = base;

        if (player == null) {
            if (player == null) {
                player = Bukkit.getOfflinePlayer(uuid);
            }
        }
        return VaultHelper.getBalance(player);
    }

    /**
     * Allows to add or substract money from user.
     *
     * @param amount Amount to be added / substracted. May be negative for substracting
     * @return true if successful
     */
    public boolean addBalance(double amount) {
        OfflinePlayer player = base;

        if (player == null) {
            if (player == null) {
                player = Bukkit.getOfflinePlayer(uuid);
            }
        }
        return VaultHelper.addBalance(player, amount);
    }

    private void validateEconomy() {
        if (isConsole()) {
            throw new NullPointerException("User is console, cannot get Player instance!");
        }

        if (!SinkLibrary.getInstance().isEconomyAvailable()) {
            throw new EconomyNotAvailableException();
        }

        assert econ != null;
        assert base != null;
    }

    /**
     * @return The PlayerConfiguration of the Player
     */
    public UserConfiguration getConfiguration() {
        if (!isPlayer()) {
            throw new IllegalStateException("User is not a player");
        }
        if (config == null) {
            config = new UserConfiguration(this);
        }
        return config;
    }

    /**
     * @return CommandSender
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Get Player instance
     *
     * @return Player
     */
    public Player getPlayer() {
        if (isConsole()) {
            throw new NullPointerException("User is console!");
        }
        return base;
    }

    /**
     * @param permission Permission required
     * @return True if the player has the permission specified by parameter.
     */
    public boolean hasPermission(String permission) {
        //Todo: fix this for offline usage
        if (isConsole()) {
            return true;
        }

        if (!isOnline()) {
            throw new RuntimeException("This may be only used for online players!");
        }
        //if (SinkLibrary.getInstance().permissionsAvailable())
        //{
        //    return SinkLibrary.getInstance().getPermissions().has(base, permission);
        //}
        //else
        //{
        return base.hasPermission(permission);
        //}
    }

    /**
     * Get user's primary group.
     *
     * @return Primary Group
     * @throws de.static_interface.sinklibrary.exception.PermissionsNotAvailableException if permissions are not available
     */
    public String getPrimaryGroup() {
        if (isConsole()) {
            throw new IllegalArgumentException("User is console!");
        }

        if (!SinkLibrary.getInstance().isPermissionsAvailable()) {
            throw new PermissionsNotAvailableException();
        }
        return SinkLibrary.getInstance().getPermissions().getPrimaryGroup(base);
    }


    /**
     * @return Display Name with Permission Prefix or Op/non-Op Prefix
     */
    public String getDefaultDisplayName() {
        if (isConsole() || !isOnline()) {
            return playerName;
        }

        return getPrefix() + playerName + ChatColor.RESET;
    }

    /**
     * Get Prefix
     *
     * @return Player prefix
     * @throws de.static_interface.sinklibrary.exception.ChatNotAvailableException if chat is not available
     */
    public String getPrefix() {
        if (!SinkLibrary.getInstance().isChatAvailable()) {
            return base.isOp() ? ChatColor.DARK_RED.toString() : ChatColor.WHITE.toString();
        }
        return ChatColor.translateAlternateColorCodes('&', SinkLibrary.getInstance().getChat().getPlayerPrefix(base));
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
        if (isConsole()) {
            return true;
        }
        if (base == null) {
            base = Bukkit.getPlayer(uuid);
        }
        return base != null && base.isOnline();

    }

    /**
     * @return True if User is Console
     */
    public boolean isConsole() {
        return sender != null && (sender.equals(Bukkit.getConsoleSender()));
    }

    /**
     * @return If {@link org.bukkit.command.CommandSender CommandSnder} is instance of {@link org.bukkit.command.ConsoleCommandSender ConsoleCommandSender},
     * it will return "Console" in {@link org.bukkit.ChatColor#RED RED}, if sender is instance of
     * {@link org.bukkit.entity.Player Player}, it will return player's {@link org.bukkit.entity.Player#getDisplayName() DisplayName}
     */
    public String getDisplayName() {
        if (isConsole()) {
            return ChatColor.RED + "Console" + ChatColor.RESET;
        }
        if (!isOnline()) {
            return playerName;
        }
        if (!SinkLibrary.getInstance().getSettings().isDisplayNamesEnabled() || !getConfiguration().getHasDisplayName()) {
            String prefix = "";
            if (SinkLibrary.getInstance().isChatAvailable()) {
                prefix = ChatColor.translateAlternateColorCodes('&', SinkLibrary.getInstance().getChat().getPlayerPrefix(base));
            }
            return prefix + base.getDisplayName();
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
            if (!isPlayer()) {
                sender.sendMessage(message);
            } else {
                base.sendMessage(message);
            }
        }
    }

    /**
     * @return The unique ID of the user
     */
    @Nullable
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Sends the message if debugging is enabled in the config
     *
     * @param message Message to be displayed
     */
    public void sendDebugMessage(String message) {
        if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
            sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Debug" + ChatColor.GRAY + "] " + ChatColor.RESET + message);
        }
    }

    public boolean isPlayer() {
        return base != null || Bukkit.getOfflinePlayer(uuid).hasPlayedBefore();
    }

    @Override
    public int compareTo(SinkUser o) {
        return getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public void ban(String reason, long unbantime) {
        getConfiguration().setBanned(true);
        getConfiguration().setBanTime(System.currentTimeMillis());
        getConfiguration().setUnbanTime(unbantime);
        getConfiguration().setBanReason(reason);
    }

    public void unban() {
        getConfiguration().setBanned(false);
    }

    public BanInfo getBanInfo() {
        return new BanInfo(getConfiguration().isBanned(), getConfiguration().getBanTime(), getConfiguration().getUnbanTime(),
                           getConfiguration().getBanReason());
    }
}
