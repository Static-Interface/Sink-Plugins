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

package de.static_interface.sinklibrary.database.query;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.Row;

public abstract class SubQuery<T extends Row> extends Query<T> {

    public SubQuery(Query parent) {
        super(parent);
    }

    public MasterQuery<T> getMasterQuery() {
        return getParent().getMasterQuery();
    }

    @Override
    public AbstractTable<T> getTable() {
        return getParent().getTable();
    }

    @Override
    public void setTable(AbstractTable<T> table) {
        getParent().setTable(table);
    }
}
