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

package de.static_interface.sinklibrary.api.event;

import org.pircbotx.PircBotX;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

public class IrcQuitEvent extends IrcEvent {

    private final UserSnapshot user;
    private final String reason;
    private final UserChannelDaoSnapshot daoSnapshot;
    private final PircBotX bot;

    public IrcQuitEvent(UserSnapshot user, String reason, UserChannelDaoSnapshot daoSnapshot, PircBotX bot) {
        this.user = user;
        this.reason = reason;
        this.daoSnapshot = daoSnapshot;
        this.bot = bot;
    }

    public UserSnapshot getUser() {
        return user;
    }

    public String getReason() {
        return reason;
    }

    public UserChannelDaoSnapshot getDaoSnapshot() {
        return daoSnapshot;
    }

    @Override
    public PircBotX getBot() {
        return bot;
    }
}
