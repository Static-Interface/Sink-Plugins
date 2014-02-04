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

import java.util.Collection;
import java.util.TreeMap;

public class ChannelHandler
{

    private static String registeredChannelNames = "";

    private static TreeMap<String, IChannel> registeredChannelsMap = new TreeMap<>();
    private static TreeMap<String, IChannel> callChars = new TreeMap<>();

    /**
     * @param channel  Instance of Channel to be registered.
     * @param name     Name of given instance to be registered.
     * @param callChar Char you use to call the channel in game.
     */
    public static void registerChannel(IChannel channel, String name, String callChar)
    {
        if ( registeredChannelsMap.containsValue(name) )
        {
            return;
        }

        registeredChannelsMap.put(name, channel);
        callChars.put(callChar, channel);
        registeredChannelNames = registeredChannelNames + name + ' ';
    }

    /**
     * @param callChar Char you use to call the channel in game.
     * @return Returns the instance of Channel with the given name.
     */

    public static IChannel getRegisteredChannel(String callChar)
    {
        return callChars.get(callChar);
    }

    /**
     * @param name Name of the channel
     * @return IChannel instance
     */
    public static IChannel getChannelByName(String name)
    {
        for ( String channel : registeredChannelsMap.keySet() )
        {
            if ( channel.equalsIgnoreCase(name) ) return registeredChannelsMap.get(channel);
        }
        return null;
    }

    /**
     * @return Returns regiteredChannelNames, a String containing the names of all registered instances.
     */

    public static String getChannelNames()
    {
        return registeredChannelNames;
    }

    /**
     * @return Returns a collection containing all registered channels.
     */

    public static Collection<IChannel> getRegisteredChannels()
    {
        return registeredChannelsMap.values();
    }

    /**
     * @return Returns the registered callChars.
     */

    public static TreeMap<String, IChannel> getRegisteredCallChars()
    {
        return callChars;
    }

}
