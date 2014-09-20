/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkchat.channel;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyBridge;
import de.static_interface.sinkchat.Util;
import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Channel {

    String name;
    String callCode;
    boolean enabled;
    String permission;
    String prefix;
    boolean sendToIRC;
    int range;

    public Channel(String name, String callCode, boolean enabled, String permission, String prefix, boolean sendToIRC, int range) {
        this.name = name;
        this.callCode = callCode;
        this.enabled = enabled;
        this.permission = permission;
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        this.sendToIRC = sendToIRC;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the callChar
     * @deprecated use {@link #getCallCode()} instead.  
     */
    @Deprecated
    public String getCallChar() {
        return getCallCode();
    }

    /**
     * Returns the callCode.
     */
    public String getCallCode() {
        return callCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPermission() {
        return permission;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean sendToIRC() {
        return sendToIRC;
    }

    public int getRange() {
        return range;
    }

    public boolean enabledForPlayer(UUID uuid) {
        String enabledPath = "Channels." + getName() + ".Enabled";
        SinkUser user = SinkLibrary.getInstance().getUser(uuid);
        PlayerConfiguration config = user.getPlayerConfiguration();
        try {
            return (boolean) config.get(enabledPath, true);
        } catch (NullPointerException ignored) {
            return true;
        }
    }

    public void setEnabledForPlayer(UUID uuid, boolean setEnabled) {
        String enabledPath = "Channels." + getName() + ".Enabled";
        SinkUser user = SinkLibrary.getInstance().getUser(uuid);
        PlayerConfiguration config = user.getPlayerConfiguration();
        config.set(enabledPath, setEnabled);
    }

    public boolean sendMessage(SinkUser user, String message) {
        if (!isEnabled()) {
            user.sendMessage(m("SinkChat.DisabledChannel"));
            return true;
        }

        if (!enabledForPlayer(user.getUniqueId())) {
            return false;
        }

        String formattedMessage = message.substring(callCode.length());

        String townyPrefix = "";
        if (SinkChat.isTownyAvailable()) {
            townyPrefix = TownyBridge.getTownyPrefix(user.getPlayer());
        }

        if (SinkLibrary.getInstance().isPermissionsAvailable()) {
            formattedMessage =
                    getPrefix() + townyPrefix + ChatColor.GRAY + '[' + user.getPrimaryGroup() + ChatColor.GRAY + "] " + user.getDisplayName()
                    + ChatColor.GRAY + ": " + ChatColor.RESET + formattedMessage;
        } else {
            formattedMessage = getPrefix() + townyPrefix + user.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RESET + formattedMessage;
        }

        if (range <= 0) {
            for (Player target : BukkitUtil.getOnlinePlayers()) {
                if ((enabledForPlayer(target.getUniqueId())) && target.hasPermission(getPermission())) {
                    target.sendMessage(formattedMessage);
                }
            }
        } else {
            Util.sendMessage(user, formattedMessage, getRange());
        }

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
        if (sendToIRC()) {
            SinkLibrary.getInstance().sendIrcMessage(formattedMessage);
        }
        return true;
    }
}
