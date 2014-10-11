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

package de.static_interface.sinkchat.channel;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkchat.ChannelConfiguration;
import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.command.ChannelCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChannelHandler {

    private static HashMap<String, Channel> registeredChannels = new HashMap<>();

    public static void registerChannel(Channel channel) {
        registeredChannels.put(channel.getCallCode(), channel);
        saveChannel(channel);
    }

    /**
     * @return All registered channels. HashMap<String, Channel> where String is the call code, Channel is the channel instance.
     */
    public static HashMap<String, Channel> getRegisteredChannels() {
        return registeredChannels;
    }

    /**
     * Returns a channel using a given name or null, if the channel can't be found.
     */
    public static Channel getChannelByName(String channelname) {
        for (Channel channel : registeredChannels.values()) {
            if (channel.getName().equalsIgnoreCase(channelname)) {
                return channel;
            }
        }
        return null;
    }

    /**
     * Gets a registered channel by it's call code.
     */
    public static Channel getRegisteredChannel(String callCode) {
        return registeredChannels.get(callCode);
    }

    private static boolean deleteChannel(Channel channel) {
        if (channel == null) {
            return false;
        }

        for (Player p : BukkitUtil.getOnlinePlayers()) {
            IngameUser user = SinkLibrary.getInstance().getIngameUser(p);
            if ((channel != null) && (user.getUniqueId() != null) && channel.enabledForPlayer(user.getUniqueId())) {
                user.sendMessage(ChannelCommand.PREFIX + m("SinkChat.DeletedChannel", channel.getName()));
            }
        }

        SinkChat.getChannelConfigs().set("Channels." + channel.getName(), null);

        return true;
    }

    private static void saveChannel(Channel channel) {
        String pathPrefix = "Channels." + channel.getName() + ".";
        ChannelConfiguration config = SinkChat.getChannelConfigs();

        config.set(pathPrefix + ChannelValues.DEFAULT, true);
        config.set(pathPrefix + ChannelValues.CALLCHAR, channel.getCallCode());
        config.set(pathPrefix + ChannelValues.ENABLED, channel.isEnabled());
        config.set(pathPrefix + ChannelValues.PERMISSION, channel.getPermission());
        config.set(pathPrefix + ChannelValues.SEND_TO_IRC, channel.sendToIRC());
        config.set(pathPrefix + ChannelValues.RANGE, channel.getRange());
    }

    /**
     * Removes a channel by it's call code.
     */
    public static boolean removeRegisteredChannel(String channelName) {
        Channel channel = getChannelByName(channelName);
        if (channel == null) {
            channel = registeredChannels.get(channelName);
        }
        if (channel == null) {
            return false;
        }

        registeredChannels.remove(channelName);
        return deleteChannel(channel);
    }
}
