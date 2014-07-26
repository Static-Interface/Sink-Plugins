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

package de.static_interface.sinklibrary.events;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class IrcReceiveMessageEvent extends IrcEventBase
{
    private final User user;
    private final Channel channel;
    private final String message;
    private final PircBotX bot;

    public IrcReceiveMessageEvent(User user, Channel channel, String message, PircBotX bot)
    {
        this.user = user;
        this.channel = channel;
        this.message = message;
        this.bot = bot;
    }

    public User getUser()
    {
        return user;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public PircBotX getBot()
    {
        return bot;
    }
}
