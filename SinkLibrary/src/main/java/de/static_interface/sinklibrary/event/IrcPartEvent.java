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

package de.static_interface.sinklibrary.event;

import org.pircbotx.PircBotX;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

public class IrcPartEvent extends IrcEventBase
{
    private final UserSnapshot user;
    private final ChannelSnapshot channel;
    private final String reason;
    private final UserChannelDaoSnapshot daoSnapshot;
    private final PircBotX bot;

    public IrcPartEvent(UserSnapshot user, ChannelSnapshot channel, String reason, UserChannelDaoSnapshot daoSnapshot, PircBotX bot)
    {
        this.user = user;
        this.channel = channel;
        this.reason = reason;
        this.daoSnapshot = daoSnapshot;
        this.bot = bot;
    }

    public UserSnapshot getUser() { return user; }

    public ChannelSnapshot getChannel() { return channel; }

    public String getReason() { return reason; }

    public UserChannelDaoSnapshot getDaoSnapshot() { return daoSnapshot; }

    @Override
    public PircBotX getBot()
    {
        return bot;
    }
}
