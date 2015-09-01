/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkchat.listener;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyHelper;
import de.static_interface.sinkchat.Util;
import de.static_interface.sinkchat.channel.Channel;
import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        try {
            handleChat(event);
        } catch (RuntimeException e) {
            SinkChat.getInstance().getLogger().log(Level.SEVERE, "Warning! Unexpected exception occurred", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleChatMonitor(AsyncPlayerChatEvent event) {
        //for (Player p : new HashSet<>(event.getRecipients())) {
        //    p.sendMessage(event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage()));
        //}
        //
        //event.setCancelled(true);
    }

    private void handleChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        IngameUser user = SinkLibrary.getInstance().getIngameUser(event.getPlayer());
        String message = event.getMessage();

        if (user.hasPermission("sinkchat.color")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        Channel channel;
        for (String callChar : ChannelHandler.getRegisteredChannels().keySet()) {
            channel = ChannelHandler.getRegisteredChannel(callChar);
            if (!StringUtil.isEmptyOrNull(callChar) &&
                event.getMessage().startsWith(callChar) &&
                !event.getMessage().equalsIgnoreCase(callChar)) {

                if (!channel.enabledForPlayer(event.getPlayer().getUniqueId())) {
                    continue;
                }

                event.setFormat(channel.formatEventFormat(user, message));
                channel.handleRecipients(user, event.getRecipients(), message);

                if (channel.sendToIRC()) {
                    SinkLibrary.getInstance().sendIrcMessage(
                            event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage()));
                }
                return;
            }
        }

        int range = SinkLibrary.getInstance().getSettings().SC_LOCAL_CHAT_RANGE.getValue();

        HashMap<String, Object> customParams = new HashMap<>();
        if (SinkChat.getInstance().isTownyAvailable()) {
            customParams.put("NATIONTAG", TownyHelper.getNationTag(event.getPlayer()));
            customParams.put("TOWN(Y)?TAG", TownyHelper.getTownTag(event.getPlayer()));
            customParams.put("TOWN(Y)?", TownyHelper.getTown(event.getPlayer()));
            customParams.put("NATION", TownyHelper.getNation(event.getPlayer()));
        }
        customParams.put("CHANNEL", m("SinkChat.Prefix.Channel", m("SinkChat.Prefix.Local")));

        String eventFormat = SinkLibrary.getInstance().getSettings().SC_DEFAULT_CHAT_FORMAT.getValue();
        //String eventFormat = format.replaceAll("\\{((PLAYER(NAME)?)|DISPLAYNAME|NAME|FORMATTEDNAME)\\}", "\\$1\\%s");
        //eventFormat = eventFormat.replaceAll("\\{MESSAGE\\}", "\\$2\\%s");
        eventFormat = StringUtil.format(eventFormat, user, null, message, customParams, false, null);

        if (!SinkLibrary.getInstance().isPermissionsAvailable()) {
            eventFormat = m("SinkChat.Prefix.Local") + ' ' + ChatColor.RESET + eventFormat;
        }

        event.setFormat(eventFormat);

        String spyMessage = Util.getSpyPrefix() + eventFormat.replace("%1$s", user.getDisplayName()).replace("%2$s", event.getMessage());
        //String spyMessage = Util.getSpyPrefix() + eventFormat;

        for (Player p : new HashSet<>(event.getRecipients())) {
            if (!Util.isInRange(user, p, range)) {
                event.getRecipients().remove(p);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (event.getRecipients().contains(p)) {
                continue;
            }

            IngameUser target = SinkLibrary.getInstance().getIngameUser(p);

            if (!Util.isInRange(user, p, range) && Util.canSpySender(target, user)) {
                p.sendMessage(spyMessage);
            }
        }

        //Bukkit.getConsoleSender().sendMessage(spyMessage);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        IngameUser user = SinkLibrary.getInstance().getIngameUser(event.getPlayer());
        if (user.hasPermission("sinkchat.color")) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }
    }
}