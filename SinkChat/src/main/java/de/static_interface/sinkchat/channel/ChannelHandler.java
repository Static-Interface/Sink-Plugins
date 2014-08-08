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

public class ChannelHandler
{
    private static HashMap<String, Channel> registeredChannels = new HashMap<>();

    public static void registerChannel(Channel channel)
    {
        registeredChannels.put(channel.getCallChar(), channel);
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
    		if ( channel.getName().equals(channelname) || channel.getPrefix().equals(channelname) ) return channel;
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

    /**
     * Removes a channel by it's call code.
     */
    public static void removeRegisteredChannel(String callCode)
    {
    	registeredChannels.remove(callCode);
    }
}
