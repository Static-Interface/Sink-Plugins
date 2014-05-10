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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Used for setting chat format
 */
public class ChatListenerLowest implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        User user = SinkLibrary.loadUser(event.getPlayer());

        String groupPrefix = SinkLibrary.isPermissionsAvailable() ? ChatColor.RESET.toString() + ChatColor.GRAY + '[' + user.getPrimaryGroup() + ChatColor.RESET + ChatColor.GRAY + "] " : "";

        event.setFormat(groupPrefix + "%1$s" + ChatColor.GRAY + ':' + ChatColor.WHITE + " %2$s");

        if ( user.hasPermission("sinkchat.color") )
        {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        User user = SinkLibrary.loadUser(event.getPlayer());
        if ( user.hasPermission("sinkchat.color") )
        {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }
    }
}