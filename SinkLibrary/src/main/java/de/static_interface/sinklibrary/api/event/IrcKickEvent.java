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

package de.static_interface.sinklibrary.api.event;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class IrcKickEvent extends IrcEvent {

    private final User user;
    private final User recipient;
    private final Channel channel;
    private final String reason;
    private final PircBotX bot;

    public IrcKickEvent(User user, User recipient, Channel channel, String reason, PircBotX bot) {
        this.user = user;
        this.recipient = recipient;
        this.channel = channel;
        this.reason = reason;
        this.bot = bot;
    }

    public User getUser() {
        return user;
    }

    public User getRecipient() {
        return recipient;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public PircBotX getBot() {
        return bot;
    }
}
