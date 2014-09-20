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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyBridge;
import de.static_interface.sinkchat.Util;
import de.static_interface.sinkchat.channel.Channel;
import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;

public class ChatListenerHighest implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        try {
            onChat(event);
        } catch (RuntimeException e) {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Warning! Unexpected exception occurred:", e);
        }
    }

    private void onChat(AsyncPlayerChatEvent event) {
        SinkUser user = SinkLibrary.getUser(event.getPlayer());

        if (event.isCancelled()) {
            return;
        }

        Channel target = null;
        for (String callChar : ChannelHandler.getRegisteredChannels().keySet()) {
            target = ChannelHandler.getRegisteredChannel(callChar);
            if (event.getMessage().startsWith(callChar) && !event.getMessage().equalsIgnoreCase(callChar)) {
                if (!target.enabledForPlayer(event.getPlayer().getUniqueId())) {
                    break;
                }
                if (target.sendMessage(user, event.getMessage())) {
                    event.setCancelled(true);
                    return;
                }
                break;
            }
        }

        String message = event.getMessage();
        int range = SinkLibrary.getSettings().getLocalChatRange();

        String townyPrefix = "";
        if (SinkChat.isTownyAvailable()) {
            townyPrefix = TownyBridge.getTownyPrefix(event.getPlayer());
        }

        String formattedMessage = String.format(event.getFormat(), user.getDisplayName(), message);

        formattedMessage = townyPrefix + formattedMessage;

        if (!SinkLibrary.isPermissionsAvailable()) {
            formattedMessage = ChatColor.GRAY + m("SinkChat.Prefix.Local") + ChatColor.RESET + ' ' + formattedMessage;
        }

        Util.sendMessage(user, formattedMessage, range);

        Bukkit.getConsoleSender().sendMessage(Util.getSpyPrefix() + formattedMessage);
        event.setCancelled(true);
    }
}