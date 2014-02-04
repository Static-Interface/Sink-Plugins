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

import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import de.static_interface.sinklibrary.exceptions.ChatNotAvailableException;
import de.static_interface.sinklibrary.exceptions.EconomyNotAvailableException;
import de.static_interface.sinklibrary.exceptions.PermissionsNotAvailableException;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("NewExceptionWithoutArguments")
public class User
{
    private static Player base;
    private static Economy econ;
    private String playerName;
    private CommandSender sender;

    /**
     * Get User instance by player's name
     *
     * @param player Name of the player
     */
    User(String player)
    {
        if ( player.equals("CONSOLE") )
        {
            sender = Bukkit.getConsoleSender();
            base = null;
            econ = SinkLibrary.getEconomy();
            playerName = "Console";
            return;
        }
        base = BukkitUtil.getPlayer(player);
        econ = SinkLibrary.getEconomy();
        playerName = player;
        sender = base;
    }

    /**
     * Get current money of player
     *
     * @return Money of player
     * @throws de.static_interface.sinklibrary.exceptions.EconomyNotAvailableException if economy is not available.
     */
    public int getMoney()
    {
        if ( isConsole() )
        {
            throw new NullPointerException("User is console, cannot get Player instance!");
        }

        if ( !SinkLibrary.isEconomyAvailable() )
        {
            throw new EconomyNotAvailableException();
        }

        EconomyResponse response = econ.bankBalance(base.getName());
        return (int) response.balance;
    }

    /**
     * @return The PlayerConfiguration of the Player
     */
    public PlayerConfiguration getPlayerConfiguration()
    {
        return new PlayerConfiguration(this);
    }

    /**
     * Get Player instance
     *
     * @return Player
     */
    public Player getPlayer()
    {
        if ( isConsole() )
        {
            throw new NullPointerException("User is console!");
        }
        return base;
    }

    /**
     * @param permission Permission required
     * @return True if the player has the permission specified by parameter.
     */
    public boolean hasPermission(String permission)
    {
        //Todo: fix this for offline usage
        if ( isConsole() )
        {
            return true;
        }

        if ( !isOnline() )
        {
            throw new RuntimeException("This may be only used for online players!");
        }
        //if (SinkLibrary.permissionsAvailable())
        //{
        //    return SinkLibrary.getPermissions().has(base, permission);
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
     * @throws de.static_interface.sinklibrary.exceptions.PermissionsNotAvailableException if permissions are not available
     */
    public String getPrimaryGroup()
    {
        if ( isConsole() )
        {
            throw new NullPointerException("User is console!");
        }

        if ( !SinkLibrary.isPermissionsAvailable() )
        {
            throw new PermissionsNotAvailableException();
        }
        return SinkLibrary.getPermissions().getPrimaryGroup(base);
    }


    /**
     * @return Display Name with Permission Prefix or Op/non-Op Prefix
     */
    public String getDefaultDisplayName()
    {
        if ( isConsole() )
        {
            return playerName;
        }
        try
        {
            if ( SinkLibrary.isChatAvailable() )
            {
                String playerPrefix = getPrefix();
                return playerPrefix + base.getName() + ChatColor.RESET;
            }
        }
        catch ( Exception ignored ) {}

        String prefix = base.isOp() ? ChatColor.RED.toString() : ChatColor.WHITE.toString();
        return prefix + base.getName() + ChatColor.RESET;
    }

    /**
     * Get Prefix
     *
     * @return Player prefix
     * @throws de.static_interface.sinklibrary.exceptions.ChatNotAvailableException if chat is not available
     */
    public String getPrefix()
    {
        if ( !SinkLibrary.isChatAvailable() )
        {
            throw new ChatNotAvailableException();
        }
        return ChatColor.translateAlternateColorCodes('&', SinkLibrary.getChat().getPlayerPrefix(base));
    }

    /**
     * Get players name (useful for offline player usage)
     *
     * @return Players name
     */
    public String getName()
    {
        return playerName;
    }

    /**
     * @return True if player is online and does not equals null
     */
    public boolean isOnline()
    {
        if ( isConsole() )
        {
            return true;
        }
        base = Bukkit.getPlayerExact(playerName);
        if ( base != null )
        {
            if ( base.isOnline() )
            {
                return true;
            }
        }
        return false;

    }

    /**
     * @return True if User is Console
     */
    public boolean isConsole()
    {
        return (sender.equals(Bukkit.getConsoleSender()));
    }

    /**
     * @return If {@link org.bukkit.command.CommandSender CommandSnder} is instance of {@link org.bukkit.command.ConsoleCommandSender ConsoleCommandSender},
     * it will return "Console" in {@link org.bukkit.ChatColor#RED RED}, if sender is instance of
     * {@link org.bukkit.entity.Player Player}, it will return player's {@link org.bukkit.entity.Player#getDisplayName() DisplayName}
     */
    public String getDisplayName()
    {
        if ( isConsole() )
        {
            return ChatColor.RED + "Console" + ChatColor.RESET;
        }
        if ( !SinkLibrary.getSettings().isDisplayNamesEnabled() )
        {
            String prefix = "";
            if ( SinkLibrary.isChatAvailable() )
            {
                prefix = ChatColor.translateAlternateColorCodes('&', SinkLibrary.getChat().getPlayerPrefix(base));
            }
            return prefix + base.getDisplayName();
        }
        else
        {
            return getPlayerConfiguration().getDisplayName();
        }
    }
}
