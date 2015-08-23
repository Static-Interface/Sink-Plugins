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

package de.static_interface.sinklibrary.database.exception;


import de.static_interface.sinklibrary.database.AbstractTable;

import java.lang.reflect.Field;

public class InvalidSqlColumnException extends RuntimeException {

    /**
     * This is thrown when an invalid SQL table is trying to be created.
     * Examples: A boolean which was annotated as UNSIGNED, Strings with ZEROFILL, etc...
     * @param table The parent table of the coulm which couldn't be created
     * @param columnField The wrapper Field of the column which couldn't be created
     * @param columName The name of the column which couldn't be created
     * @param reason The reason why it failed
     */
    public InvalidSqlColumnException(AbstractTable table, Field columnField, String columName, String reason) {
        super("Column \"" + columName + "\" " + "(wrapper: " + columnField.getType().getName() + ") on table \"" + table.getName() + "\" (wrapper: "
              + table.getClass().getName() + ") couldn't be created: " + reason);
    }
}
