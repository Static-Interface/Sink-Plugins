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
        User user = SinkLibrary.getUser(event.getPlayer());

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
        User user = SinkLibrary.getUser(event.getPlayer());
        if ( user.hasPermission("sinkchat.color") )
        {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }
    }
}