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

package de.static_interface.sinklibrary.database.query.impl;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.query.Query;

public class FromQuery<T> extends Query<T> {

    private AbstractTable table;

    public FromQuery(AbstractTable table) {
        super(null);
        this.table = table;
    }

    public AbstractTable getTable() {
        return table;
    }

    /**
     * Start a select query. Calls {@link #select(String...)} with "*" as argument
     */
    public SelectQuery<T> select() {
        return select("*");
    }

    /**
     * Start a select query
     @param columns the columns which should be queried or * for all columns
     */
    public SelectQuery<T> select(String... columns) {
        SelectQuery<T> query = new SelectQuery(this, columns);
        setChild(query);
        return query;
    }
}
