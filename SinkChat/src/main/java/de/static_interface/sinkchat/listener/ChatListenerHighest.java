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

package de.static_interface.sinkchat.listener;

import de.static_interface.sinkchat.TownyBridge;
import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class ChatListenerHighest implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        try
        {
            onChat(event);
        }
        catch ( RuntimeException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Warning! Unexpected exception occurred:", e);
        }
    }

    private void onChat(AsyncPlayerChatEvent event)
    {
        if ( event.isCancelled() )
        {
            return;
        }

        for ( String callChar : ChannelHandler.getRegisteredCallChars().keySet() )
        {
            if ( event.getMessage().startsWith(callChar) && !event.getMessage().equalsIgnoreCase(callChar) )
            {
                if ( ChannelHandler.getRegisteredChannel(callChar).sendMessage(event.getPlayer(), event.getMessage()) )
                {
                    event.setCancelled(true);
                }
                return;
            }
        }

        User eventPlayer = SinkLibrary.getUser(event.getPlayer());

        String message = event.getMessage();
        int range = SinkLibrary.getSettings().getLocalChatRange();

        String nationPrefix = TownyBridge.getTownyPrefix(event.getPlayer());

        String formattedMessage = String.format(event.getFormat(), eventPlayer.getDisplayName(), message);

        formattedMessage = nationPrefix + formattedMessage;

        if ( !SinkLibrary.isPermissionsAvailable() )
        {
            formattedMessage = ChatColor.GRAY + _("SinkChat.Prefix.Chat.Local") + ChatColor.RESET + ' ' + formattedMessage;
        }

        String spyPrefix = ChatColor.GRAY + _("SinkChat.Prefix.Spy") + ' ' + ChatColor.RESET;
        double x = event.getPlayer().getLocation().getX();
        double y = event.getPlayer().getLocation().getY();
        double z = event.getPlayer().getLocation().getZ();

        for ( Player p : Bukkit.getOnlinePlayers() )
        {
            Location loc = p.getLocation();
            boolean isInRange = Math.abs(x - loc.getX()) <= range && Math.abs(y - loc.getY()) <= range && Math.abs(z - loc.getZ()) <= range;

            User onlineUser = SinkLibrary.getUser(p);

            // User has permission to read all spy chat
            boolean spyAll = onlineUser.hasPermission("sinkchat.spy.all");

            // User has permission to read spy, check for bypass
            boolean canSpy = onlineUser.hasPermission("sinkchat.spy") && !eventPlayer.hasPermission("sinkchat.spy.bypass");

            PlayerConfiguration config = onlineUser.getPlayerConfiguration();

            if ( isInRange )
            {
                p.sendMessage(formattedMessage);
            }
            else if ( (spyAll || canSpy) && config.isSpyEnabled() )
            {
                p.sendMessage(spyPrefix + formattedMessage);
            }
        }
        Bukkit.getConsoleSender().sendMessage(spyPrefix + formattedMessage);
        event.setCancelled(true);
    }
}