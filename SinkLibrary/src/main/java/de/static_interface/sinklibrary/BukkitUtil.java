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

import de.static_interface.sinklibrary.sender.IrcCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class BukkitUtil {

    /** Since Bukkit 1.7.9 R0.3, the return type of {@link org.bukkit.Bukkit#getOnlinePlayers()} changed, so
     *  plugins will possibly break when using this method on older bukkit versions
     *
     * @return the current online players
     */
    public static List<Player> getOnlinePlayers() {
        List<Player> tmp = new ArrayList<>();
        try {
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) // 1.7.9 R0.3 and newer
            {
                tmp = (List<Player>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null);
            } else {
                Collections.addAll(tmp, ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null)));
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return tmp;
    }


    /**
     * Get online Player by name.
     *
     * @param name Name of the
     * @return If more than one matches, it will return the player with exact name.
     */
    @Nullable
    public static Player getPlayer(String name) {
        List<Player> matchedPlayers = new ArrayList<>();
        for (Player player : BukkitUtil.getOnlinePlayers()) {
            if (player.getName().toLowerCase().contains(name.toLowerCase())) {
                matchedPlayers.add(player);
            }
        }

        Player exactPlayer = Bukkit.getPlayerExact(name);
        if (matchedPlayers.toArray().length > 1 && exactPlayer != null) {
            return exactPlayer;
        } else {
            try {
                return matchedPlayers.get(0);
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public static UUID getUUIDByName(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    /**
     * @param sender Command Sender
     * @return If {@link org.bukkit.command.CommandSender CommandSnder} is instance of {@link org.bukkit.command.ConsoleCommandSender ConsoleCommandSender},
     * it will return "Console" in {@link org.bukkit.ChatColor#RED RED}, if sender is instance of
     * {@link org.bukkit.entity.Player Player}, it will return player's {@link org.bukkit.entity.Player#getDisplayName() DisplayName}
     */
    public static String getSenderName(CommandSender sender) {
        if (sender instanceof IrcCommandSender) {
            ChatColor prefix = sender.isOp() ? ChatColor.DARK_RED : ChatColor.DARK_AQUA;
            return prefix + sender.getName() + ChatColor.RESET;
        }
        if (sender instanceof ConsoleCommandSender) {
            return ChatColor.DARK_RED + "Console" + ChatColor.RESET;
        }
        SinkUser user = SinkLibrary.getInstance().getUser(sender);
        return user.getDisplayName() + ChatColor.RESET;
    }

    /**
     * Use this instead of {@link org.bukkit.Bukkit#broadcast(String, String).}.
     * Send message to all players with specified permission.
     * It will also send the message also to IRC
     *
     * @param message Message to send
     * @param sendIRC If true, message will be broadcasted to IRC if available
     */
    public static void broadcastMessage(String message, boolean sendIRC) {
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            p.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(message);
        if (!sendIRC) {
            return;
        }
        SinkLibrary.getInstance().sendIrcMessage(message);
    }

    /**
     * @param message Message to send
     */
    public static void broadcastMessage(String message) {
        broadcastMessage(message, true);
    }

    /**
     * Use this instead of {@link org.bukkit.Bukkit#broadcast(String message, String permission).
     * Send message to all players with specified permission.
     * It will also send the message to IRC if the default permission is true}.
     *
     * @param message    Message to send
     * @param permission Permission needed to receive the message
     * @param sendIRC    If true, message will broadcasted to IRC if available
     */
    public static void broadcast(String message, String permission, boolean sendIRC) {
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            SinkUser user = SinkLibrary.getInstance().getUser(p);
            if (!user.hasPermission(permission)) {
                continue;
            }
            p.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(message);
        if (sendIRC) {
            SinkLibrary.getInstance().sendIrcMessage(message);
        }
    }
}
