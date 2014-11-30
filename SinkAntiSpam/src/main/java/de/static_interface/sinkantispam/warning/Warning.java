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

package de.static_interface.sinkantispam.warning;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.SinkUser;
import org.bukkit.ChatColor;

import java.util.UUID;

import javax.annotation.Nullable;

public class Warning implements Comparable<Warning> {

    public static String SYSTEM = ChatColor.DARK_RED + "System";
    private final String reason;
    private final UUID warnerUuid;
    private final String warnedBy;
    private final boolean autoWarning;
    private final long warnTime;
    private int id;

    public Warning(String reason, String warnedBy, @Nullable UUID warnerUuid, int id, boolean autoWarning) {
        this.reason = reason;
        this.warnedBy = warnedBy;
        this.autoWarning = autoWarning;
        this.warnerUuid = warnerUuid;
        this.id = id;
        warnTime = System.currentTimeMillis();
    }

    public String getReason() {
        return reason;
    }

    public String getWarnerDisplayName() {
        if (getWarner() != null) {
            return getWarner().getDisplayName();
        }

        return warnedBy;
    }

    public boolean isAutoWarning() {
        return autoWarning;
    }

    @Nullable
    public SinkUser getWarner() {
        if (autoWarning) {
            return SinkLibrary.getInstance().getConsoleUser();
        }
        if (warnerUuid == null) {
            return null;
        }
        return SinkLibrary.getInstance().getIngameUser(warnerUuid);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Warning o) {
        if (o.getWarnTime() > warnTime) {
            return -1;
        } else if (o.getWarnTime() < warnTime) {
            return 1;
        }
        return 0;
    }

    public long getWarnTime() {
        return warnTime;
    }
}
