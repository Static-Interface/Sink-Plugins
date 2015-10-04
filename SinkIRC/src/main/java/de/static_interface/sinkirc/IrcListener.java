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

package de.static_interface.sinkirc;

import de.static_interface.sinkirc.queue.IrcQueue;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.event.IrcJoinEvent;
import de.static_interface.sinklibrary.api.event.IrcKickEvent;
import de.static_interface.sinklibrary.api.event.IrcNickChangeEvent;
import de.static_interface.sinklibrary.api.event.IrcPartEvent;
import de.static_interface.sinklibrary.api.event.IrcPrivateMessageEvent;
import de.static_interface.sinklibrary.api.event.IrcQuitEvent;
import de.static_interface.sinklibrary.api.event.IrcReceiveMessageEvent;
import de.static_interface.sinklibrary.api.event.MessageStreamEvent;
import de.static_interface.sinklibrary.api.stream.MessageStream;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.pircbotx.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class IrcListener implements Listener {

    public static final String IRC_PREFIX = ChatColor.GRAY + "[IRC] " + ChatColor.RESET;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessageStream(MessageStreamEvent event) {
        String name = event.getMessageStream().getName().toLowerCase().trim();
        Debug.logMethodCall(event.getEventName() + "(" + name + ")", event.getUser() != null ? event.getUser().getName() : null, event.getMessage());
        Validate.notNull(name);
        Map<String, List<String>> redirects = SinkIRC.getInstance().getStreamRedirects();
        if (redirects.size() == 0 || !redirects.containsKey(name)) {
            return;
        }

        List<String> targetChannels = redirects.get(name);
        if (targetChannels == null) {
            Debug.log("targetchannels == null");
            return;
        }

        Debug.log("targetChannels.size() = " + targetChannels.size());
        for (String channelName : targetChannels) {
            Channel channel = IrcUtil.getChannel(channelName);
            if (channel == null) {
                Debug.log(channelName + "->channel == null");
                continue;
            }
            SinkUser sender = event.getUser();
            String msg = event.getMessage();
            msg = event.getMessageStream().formatMessage(sender, msg);
            channel.send().message(IrcUtil.replaceColorCodes(msg));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIrcMessage(IrcReceiveMessageEvent event) {
        String name = event.getChannel().getName();
        Debug.logMethodCall(event.getEventName() + "(" + name + ")", event.getUser().getNick(), event.getMessage());
        Map<String, List<String>> redirects = SinkIRC.getInstance().getStreamRedirects();
        if (redirects.size() == 0 || !redirects.containsKey(name)) {
            return;
        }

        List<String> targetStreams = redirects.get(name);
        if (targetStreams == null) {
            return;
        }

        for (String streamName : targetStreams) {
            Validate.notNull(streamName);
            MessageStream stream = SinkLibrary.getInstance().getMessageStream(streamName.toLowerCase().trim());
            SinkUser user = SinkLibrary.getInstance().getIrcUser(event.getUser(), event.getChannel().getName());
            String message = event.getMessage();
            stream.sendMessage(user, message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIrcJoin(IrcJoinEvent event) {
        if (event.getUser().equals(SinkIRC.getInstance().getIrcBot().getUserBot())) {
            Debug.log("Bot joined, skipping");
            return;
        }

        IrcQueue.addToQueue("Willkommen, " + event.getUser().getNick() + '!', event.getChannel().getName());
        BukkitUtil.broadcastMessage(
                IRC_PREFIX + ChatColor.GRAY + '[' + event.getChannel().getName() + "] " + ChatColor.DARK_AQUA + event.getUser().getNick()
                + ChatColor.WHITE + " ist dem Kanal beigetreten.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIrcKick(IrcKickEvent event) {
        String reason = event.getReason();
        if (reason.equals(event.getRecipient().getNick()) || reason.equals(event.getUser().getNick())) {
            reason = "";
        }
        String formattedReason = "Grund: " + reason;
        if (reason.isEmpty() || reason.equals("\"\"")) {
            formattedReason = "";
        }
        BukkitUtil.broadcastMessage(
                IRC_PREFIX + ChatColor.GRAY + '[' + event.getChannel().getName() + "] " + ChatColor.DARK_AQUA + event.getRecipient().getNick() + ChatColor.WHITE
                + " wurde von " + event.getUser().getNick() + " aus dem Kanal geworfen. " + formattedReason);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIrcNickChange(IrcNickChangeEvent event) {
        BukkitUtil.broadcastMessage(
                IRC_PREFIX + ChatColor.GRAY + '[' + "Server" + "] " + ChatColor.DARK_AQUA + event.getOldNick() + ChatColor.WHITE + " ist jetzt als "
                + ChatColor.DARK_AQUA + event.getNewNick() + ChatColor.WHITE + " bekannt.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIrcPart(IrcPartEvent event) {
        BukkitUtil.broadcastMessage(
                IRC_PREFIX + ChatColor.GRAY + '[' + event.getChannel().getName() + "] " + ChatColor.DARK_AQUA + event.getUser().getNick()
                + ChatColor.WHITE + " hat den Kanal verlassen (" + event.getReason() + ").");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIrcPrivateMessage(IrcPrivateMessageEvent event) {
        String[] args = event.getMessage().split(" ");
        String cmd = args[0];
        List<String> tmp = new ArrayList<>(Arrays.asList(args));
        tmp.remove(cmd);
        args = tmp.toArray(new String[tmp.size()]);

        if (event.getMessage().trim().equals(IrcUtil.getCommandPrefix())) {
            return; // no command
        }
        IrcUtil.handleCommand(cmd, args, event.getUser().getNick(), event.getUser(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIrcQuit(IrcQuitEvent event) {
        String formattedReason = " (" + event.getReason() + ')';
        if (event.getReason().isEmpty() || event.getReason().equals("\"\"")) {
            formattedReason = "";
        }
        BukkitUtil.broadcastMessage(
                IRC_PREFIX + ChatColor.GRAY + '[' + "Server" + "] " + ChatColor.DARK_AQUA + event.getUser().getNick() + ChatColor.WHITE
                + " hat den IRC Server verlassen." + formattedReason);
    }
}
