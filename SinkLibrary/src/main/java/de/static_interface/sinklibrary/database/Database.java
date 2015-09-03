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

package de.static_interface.sinklibrary.database;

import com.zaxxer.hikari.HikariDataSource;
import de.static_interface.sinklibrary.database.annotation.Column;
import de.static_interface.sinklibrary.database.annotation.ForeignKey;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.Nullable;

public abstract class Database {

    private final DatabaseConnectionInfo info;
    private final SQLDialect dialect;
    private final char backtick;
    protected HikariDataSource dataSource;
    protected Plugin plugin;
    protected Connection connection;

    /**
     *
     * @param info the connection info
     * @param plugin the plugin creating the database
     * @param dialect the sql dialect
     * @param backtick the backtick used by the database sql synrax
     */
    public Database(@Nullable DatabaseConnectionInfo info, Plugin plugin, SQLDialect dialect, char backtick) {
        this.plugin = plugin;
        this.info = info;
        this.dialect = dialect;
        this.backtick = backtick;
    }

    /**
     * @return The backtick used by the database sql syntax
     */
    public char getBacktick() {
        return backtick;
    }

    /**
     * Get the SQL type of a field
     * @param f the Field to convert
     * @return the SQL type
     * @throws RuntimeException if there is no native SQL type representation
     */
    public String toDatabaseType(Field f) {
        Class clazz = f.getType();
        Column column = FieldCache.getAnnotation(f, Column.class);
        boolean isForeignKey = FieldCache.getAnnotation(f, ForeignKey.class) != null;

        if (clazz == Date.class) {
            throw new RuntimeException("Date is not supported for now !");
        }
        if (clazz == java.sql.Date.class) {
            throw new RuntimeException("Date is not supported for now!");
        }
        if (clazz == Integer.class || clazz == int.class) {
            return "INT";
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return "TINYINT(1)";
        }
        if (clazz == Double.class || clazz == double.class) {
            return "DOUBLE";
        }
        if (clazz == Float.class || clazz == float.class) {
            return "FLOAT";
        }
        if (clazz == Long.class || clazz == long.class) {
            return "BIGINT";
        }
        if (clazz == Short.class || clazz == short.class) {
            return "SMALLINT";
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return "TINYINT";
        }
        if (clazz == String.class) {
            return column.primaryKey() || column.uniqueKey() || isForeignKey ? "VARCHAR(255)" : "TEXT(999)";
        }
        throw new RuntimeException("No database type available for: " + clazz.getName());
    }

    protected abstract void setupConfig();

    /**
     * Connect to the database
     * @throws SQLException
     */
    public abstract void connect() throws SQLException;

    /**
     * Close the connection of the database
     * @throws SQLException
     */
    public abstract void close() throws SQLException;

    /**
     * @return the {@link DatabaseConnectionInfo}
     */
    @Nullable
    public DatabaseConnectionInfo getConnectionInfo() {
        return info;
    }

    /**
     * @return the {@link Connection}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @return the {@link SQLDialect}
     */
    public SQLDialect getDialect() {
        return dialect;
    }
}
