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

import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyBridge;
import de.static_interface.sinkchat.Util;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class Channel
{
    String name;
    String callChar;
    boolean enabled;
    String permission;
    String prefix;
    boolean sendToIRC;
    int range;

    public Channel(String name, String callChar, boolean enabled, String permission, String prefix,
            boolean sendToIRC, int range)
    {
        this.name = name;
        this.callChar = callChar;
        this.enabled = enabled;
        this.permission = permission;
        this.prefix =  ChatColor.translateAlternateColorCodes('&', prefix);
        this.sendToIRC = sendToIRC;
        this.range = range;
    }

    public String getName()
    {
        return name;
    }

    public String getCallChar()
    {
        return callChar;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public String getPermission()
    {
        return permission;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public boolean sendToIRC()
    {
        return sendToIRC;
    }

    public int getRange()
    {
        return range;
    }

    String enabledPath = "Channels." + getName() + ".Enabled";
    public boolean enabledForPlayer(UUID uuid)
    {
        User user = SinkLibrary.getUser(uuid);
        PlayerConfiguration config = user.getPlayerConfiguration();
        return (boolean) config.get(enabledPath, true);
    }

    public boolean sendMessage(User user, String message)
    {
        if(!isEnabled())
        {
            user.sendMessage(_("SinkChat.DisabledChannel"));
            return true;
        }

        if ( !enabledForPlayer(user.getUniqueId()) )
        {
            return false;
        }

        String formattedMessage = message.substring(1);

        String townyPrefix = "";
        if ( SinkChat.isTownyAvailable() )
        {
            townyPrefix = TownyBridge.getTownyPrefix(user.getPlayer());
        }

        if ( SinkLibrary.isPermissionsAvailable() )
        {
            formattedMessage = getPrefix() + townyPrefix + ChatColor.GRAY + '[' + user.getPrimaryGroup() + ChatColor.GRAY + "] " + user.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RESET + formattedMessage;
        }
        else
        {
            formattedMessage = getPrefix() + townyPrefix + user.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RESET + formattedMessage;
        }

        if (range <= 0)
        {
            for ( Player target : Bukkit.getOnlinePlayers() )
            {
                if ( (enabledForPlayer(target.getUniqueId())) && target.hasPermission(getPermission()) )
                {
                    target.sendMessage(formattedMessage);
                }
            }
        }
        else
        {
            Util.sendMessage(user, formattedMessage, getRange());
        }

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
        if (sendToIRC()) SinkLibrary.sendIRCMessage(formattedMessage);
        return true;
    }
}
