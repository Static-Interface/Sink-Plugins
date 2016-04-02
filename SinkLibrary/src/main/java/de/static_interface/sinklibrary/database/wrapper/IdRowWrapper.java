/*
 * Copyright (c) 2013 - 2016 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.database.wrapper;

import static de.static_interface.sinklibrary.database.query.Query.eq;
import static de.static_interface.sinklibrary.database.query.Query.from;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.impl.row.IdRow;

public abstract class IdRowWrapper<T extends IdRow> {

    private int id;
    private String idColumn;
    private AbstractTable<T> table;

    public IdRowWrapper(AbstractTable<T> table, T row) {
        id = row.getId();
        idColumn = row.getIdColumn();
        this.table = table;
    }

    public T getBase() {
        return from(table).select().where(idColumn, eq("?")).get(id);
    }

    public int getId() {
        return id;
    }

    public AbstractTable<T> getTable() {
        return table;
    }
}
