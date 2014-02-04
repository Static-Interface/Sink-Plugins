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
import java.util.Set;
import java.util.TreeMap;

public class PrivateChannelHandler
{

    private static TreeMap<String, IPrivateChannel> registeredConversations = new TreeMap<>();

    public static void registerChannel(IPrivateChannel channel)
    {
        registeredConversations.put(channel.getChannelIdentifier(), channel);
    }

    public static IPrivateChannel getChannel(String channelIdentifier)
    {
        return (registeredConversations.get(channelIdentifier));
    }

    public static boolean isChannelIdentTaken(String channelIdentifier)
    {
        Set<String> keys = registeredConversations.keySet();
        for ( String s : keys )
        {
            if ( s.equals(channelIdentifier) ) return true;
        }

        return false;
    }

    public static Collection<IPrivateChannel> getRegisteredChannels()
    {
        return registeredConversations.values();
    }
}
