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

import java.util.HashMap;

import de.static_interface.sinkchat.ChannelConfigurations;
import de.static_interface.sinkchat.SinkChat;

public class ChannelHandler
{
    private static HashMap<String, Channel> registeredChannels = new HashMap<>();

    public static void registerChannel(Channel channel)
    {
        registeredChannels.put(channel.getCallCode(), channel);
        saveChannel(channel);
    }

    /**
     * @return All registered channels. HashMap<String, Channel> where String is the call code, Channel is the channel instance.
     */
    public static HashMap<String, Channel> getRegisteredChannels()
    {
        return registeredChannels;
    }

    /**
     * Returns a channel using a given name or null, if the channel can't be found.
     */
    public static Channel getChannelByName(String channelname)
    {
    	for ( Channel channel : registeredChannels.values() )
    	{
    		if ( channel.getName().equalsIgnoreCase(channelname) || channel.getPrefix().equalsIgnoreCase(channelname) ) return channel;
    	}
    	return null;
    }

    /**
     * Gets a registered channel by it's call code.
     */
    public static Channel getRegisteredChannel(String callCode)
    {
        return registeredChannels.get(callCode);
    }

    private static void deleteChannel(Channel channel)
    {
    	ChannelConfigurations config = SinkChat.getChannelConfigs();
    	config.set("Channels."+channel.getName(), null);
    }

    private static void saveChannel(Channel channel)
    {
    	String pathPrefix = "Channels." + channel.getName() + ".";
    	ChannelConfigurations config = SinkChat.getChannelConfigs();

    	config.set(pathPrefix + ChannelValues.DEFAULT, true);
    	config.set(pathPrefix + ChannelValues.CALLCHAR, channel.getCallCode());
    	config.set(pathPrefix + ChannelValues.ENABLED, channel.enabled);
    	config.set(pathPrefix + ChannelValues.PERMISSION, channel.getPermission());
    	config.set(pathPrefix + ChannelValues.PREFIX, channel.getPrefix());
    	config.set(pathPrefix + ChannelValues.SEND_TO_IRC, channel.sendToIRC);
    	config.set(pathPrefix + ChannelValues.RANGE, channel.getRange());;
    }

    /**
     * Removes a channel by it's call code.
     */
    public static void removeRegisteredChannel(String callCode)
    {
    	Channel channel = registeredChannels.get(callCode);
    	registeredChannels.remove(callCode);
    	deleteChannel(channel);
    }
}
