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

package de.static_interface.sinklibrary.model;

public class BanInfo {

    private final boolean isBanned;
    private final long banTime;
    private final long unbanTime;
    private final String reason;

    public BanInfo(boolean isBanned, long banTime, long unbanTime, String reason) {
        this.isBanned = isBanned;
        this.banTime = banTime;
        this.unbanTime = unbanTime;
        this.reason = reason;
    }

    public boolean isBanned() {
        // -1 == perma banned
        return isBanned && (unbanTime == -1 || unbanTime < System.currentTimeMillis());
    }

    public long getBanTime() {
        return banTime;
    }

    public long getUnbanTime() {
        return unbanTime;
    }

    public String getReason() {
        return reason;
    }
}
