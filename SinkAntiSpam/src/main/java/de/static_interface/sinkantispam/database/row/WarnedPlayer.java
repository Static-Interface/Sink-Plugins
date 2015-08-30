/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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

import de.static_interface.sinklibrary.database.Row;
import de.static_interface.sinklibrary.database.annotation.Column;

import javax.annotation.Nullable;

public class WarnedPlayer implements Row {

    @Column(primaryKey = true, autoIncrement = true)
    public Integer id;

    @Column(name = "player_uuid", uniqueKey = true)
    @Nullable
    public String playerUuid;

    @Column(name = "player_name", uniqueKey = true)
    public String playerName;

    @Column
    public int deleted_points;
}
