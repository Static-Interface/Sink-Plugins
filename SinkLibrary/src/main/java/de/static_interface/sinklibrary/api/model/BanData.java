/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.api.model;

import de.static_interface.sinklibrary.api.user.SinkUser;

import java.util.Date;

public class BanData {

    private final boolean isBanned;
    private final long banTime;
    private final long timeout;
    private final String reason;
    private final long unbantime;
    private final SinkUser user;

    public BanData(SinkUser user, boolean isBanned, long banTime, long timeout, long unbantime, String reason) {
        this.isBanned = isBanned;
        this.banTime = banTime;
        this.timeout = timeout;
        this.reason = reason;
        this.unbantime = unbantime;
        this.user = user;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public long getBanTime() {
        return banTime;
    }

    public long getTimeOut() {
        return timeout;
    }

    public long getUnbanTime() {
        return unbantime;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [" + "isbanned: " + isBanned + ", banTime:" + new Date(banTime)
                .toString() + "(" + banTime + ")" +
               ", timeout: " + new Date(timeout).toString() + "(" + timeout + ")" + ", reason: \"" + reason + "\", unbantime: " +
               new Date(unbantime).toString() + "(" + unbantime + ")" + "]";
    }

    public SinkUser getUser() {
        return user;
    }
}
