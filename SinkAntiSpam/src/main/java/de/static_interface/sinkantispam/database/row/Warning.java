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

package de.static_interface.sinkantispam.database.row;

import de.static_interface.sinkantispam.database.table.PredefinedWarningsTable;
import de.static_interface.sinkantispam.database.table.WarnedPlayersTable;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.database.CascadeAction;
import de.static_interface.sinklibrary.database.Row;
import de.static_interface.sinklibrary.database.annotation.Column;
import de.static_interface.sinklibrary.database.annotation.ForeignKey;

import java.util.UUID;

import javax.annotation.Nullable;

public class Warning implements Row, Comparable<Warning> {

    @Column(primaryKey = true, autoIncrement = true)
    public Integer id;

    @Column
    public int userWarningId;

    @Column
    public String reason;

    @Column(name = "is_auto_warning")
    public boolean isAutoWarning;

    @Column(name = "warn_time")
    public long warnTime;

    @Column(name = "warner")
    @Nullable
    public String warner;

    @Column(name = "warner_uuid")
    @Nullable
    public String warnerUuid;

    @Column
    @Nullable
    public String deleter;

    @Column(name = "deleter_uuid")
    @Nullable
    public String deleterUuid;

    @Column(name = "delete_time")
    @Nullable
    public Long deleteTime;

    @Column(name = "is_deleted")
    public boolean isDeleted;

    @Column
    public int points;

    @Column(name = "expire_time")
    @Nullable
    public Long expireTime;

    @Column(name = "user_id")
    @ForeignKey(table = WarnedPlayersTable.class, column = "id", onUpdate = CascadeAction.CASCADE, onDelete = CascadeAction.CASCADE)
    public int userId;

    @Column(name = "predefined_id")
    @ForeignKey(table = PredefinedWarningsTable.class, column = "id", onDelete = CascadeAction.SET_NULL, onUpdate = CascadeAction.CASCADE)
    @Nullable
    public Integer predefinedId;

    public boolean isValid() {
        return !isDeleted && expireTime > System.currentTimeMillis();
    }

    public String getWarnerDisplayName() {
        if (getWarner() != null) {
            return getWarner().getDisplayName();
        }

        return warner;
    }

    @Nullable
    public SinkUser getWarner() {
        if (isAutoWarning) {
            return SinkLibrary.getInstance().getConsoleUser();
        }
        if (warnerUuid == null) {
            return null;
        }
        return SinkLibrary.getInstance().getIngameUser(UUID.fromString(warnerUuid));
    }

    @Override
    public int compareTo(Warning o) {
        if (o.warnTime > warnTime) {
            return -1;
        } else if (o.warnTime < warnTime) {
            return 1;
        }
        return 0;
    }
}
