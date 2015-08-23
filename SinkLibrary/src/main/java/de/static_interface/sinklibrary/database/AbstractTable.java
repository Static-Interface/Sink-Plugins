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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.database.annotation.Column;
import de.static_interface.sinklibrary.database.annotation.ForeignKey;
import de.static_interface.sinklibrary.database.annotation.Index;
import de.static_interface.sinklibrary.database.exception.InvalidSqlColumnException;
import de.static_interface.sinklibrary.database.impl.table.OptionsTable;
import de.static_interface.sinklibrary.util.ReflectionUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.lang.Validate;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public abstract class AbstractTable<T extends Row> {

    private final String name;
    protected Database db;
    private DSLContext context;

    public AbstractTable(String name, Database db) {
        this.name = name;
        this.db = db;
        context = DSL.using(db.getConnection(), db.getDialect());
    }

    public static boolean hasColumn(Record r, String columnName) throws SQLException {
        return r.field(columnName) != null;
    }

    public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    public final String getName() {
        return db.getConnectionInfo().getTablePrefix() + name;
    }

    public void create() throws SQLException {
        char bt = db.getBacktick();
        String sql = "CREATE TABLE IF NOT EXISTS " + bt + getName() + bt + " (";

        List<Field> foreignKeys = new ArrayList<>();
        List<Field> indexes = new ArrayList<>();
        Class foreignOptionsTable = null;

        if (this instanceof OptionsTable) {
            foreignOptionsTable = ((OptionsTable) this).getForeignTable();
        }

        boolean primarySet = false;
        for (Field f : getRowClass().getFields()) {
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();

            sql += bt + name + bt + " " + db.toDatabaseType(f.getType(), column);

            if (column.zerofill()) {
                if (!ReflectionUtil.isNumber(f.getType())) {
                    throw new InvalidSqlColumnException(this, f, name, "column was annotated as ZEROFILL but wrapper type is not a number");
                }
                sql += " ZEROFILL";
            }

            if (column.unsigned()) {
                if (!ReflectionUtil.isNumber(f.getType())) {
                    throw new InvalidSqlColumnException(this, f, name,
                                                        "column was annotated as UNSIGNED but wrapper type is not a number");
                }
                sql += " UNSIGNED";
            }

            if (column.autoIncrement()) {
                if (!ReflectionUtil.isNumber(f.getType())) {
                    throw new InvalidSqlColumnException(this, f, name,
                                                        "column was annotated as AUTO_INCREMENT but wrapper type is not a number");
                }
                sql += " AUTO_INCREMENT";
            }

            if (column.uniqueKey()) {
                sql += " UNIQUE KEY";
            }

            if (column.primaryKey()) {
                if (primarySet) {
                    throw new SQLException("Duplicate primary key");
                }
                primarySet = true;
                sql += " PRIMARY KEY";
            }

            if (f.getAnnotation(Nullable.class) == null) {
                sql += " NOT NULL";
            } else if (ReflectionUtil.isPrimitiveClass(f.getType())) {
                // The column is nullable but the wrapper type is a primitive value, which can't be null
                throw new InvalidSqlColumnException(this, f, name,
                                                    "column was annotated as NULLABLE but wrapper type is a primitive type");
            }

            if (!StringUtil.isEmptyOrNull(column.defaultValue())) {
                sql += " DEFAULT " + column.defaultValue();
            }

            if (!StringUtil.isEmptyOrNull(column.comment())) {
                sql += " COMMENT '" + column.comment() + "'";
            }

            if (f.getAnnotation(ForeignKey.class) != null) {
                foreignKeys.add(f);
            }

            if (f.getAnnotation(Index.class) != null) {
                indexes.add(f);
            }

            sql += ",";
        }

        for (Field f : foreignKeys) {
            Column column = f.getAnnotation(Column.class);
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();
            ForeignKey foreignKey = f.getAnnotation(ForeignKey.class);

            sql = addForeignKey(sql, name, foreignKey.table(), foreignKey.column(), foreignKey.onUpdate(), foreignKey.onDelete());
        }

        if (foreignOptionsTable != null) {
            String column = ((OptionsTable) this).getForeignColumn();
            CascadeAction onUpdate = ((OptionsTable) this).getForeignOnUpdateAction();
            CascadeAction onDelete = ((OptionsTable) this).getForeignOnDeleteAction();
            sql = addForeignKey(sql, "foreignTarget", foreignOptionsTable, column, onUpdate, onDelete);
        }

        for (Field f : indexes) {
            if (getEngine().equalsIgnoreCase("InnoDB") && foreignKeys.contains(f)) {
                continue; //InnoDB already creates indexes for foreign keys, so skip these...
            }

            Column column = f.getAnnotation(Column.class);
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();

            Index index = f.getAnnotation(Index.class);
            String indexName = StringUtil.isEmptyOrNull(index.name()) ? name + "_I" : index.name();

            sql += "INDEX " + bt + indexName + bt + " (" + bt + name + bt + ")";

            sql += ",";
        }

        if (sql.endsWith(",")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        sql += ")";
        if (db.getDialect() == SQLDialect.MYSQL || db.getDialect() == SQLDialect.MARIADB) {
            //Todo: do other SQL databases support engines?
            sql += " ENGINE=" + getEngine();
        }
        sql += ";";

        executeUpdate(sql);
    }

    protected String addForeignKey(String sql, String name, Class<? extends AbstractTable> targetClass, String columnName, CascadeAction onUpdate,
                                   CascadeAction onDelete) {
        char bt = db.getBacktick();

        String tablename;
        try {
            tablename = targetClass.getField("TABLE_NAME").get(null).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Static String Field TABLE_NAME was not declared in table wrapper class " + targetClass.getName() + "!", e);
        }

        sql +=
                "FOREIGN KEY (" + bt + name + bt + ") REFERENCES " + db.getConnectionInfo().getTablePrefix() + tablename + " (" + bt + columnName
                + bt + ")";
        sql += " ON UPDATE " + onUpdate.toSql() + " ON DELETE " + onDelete.toSql();

        sql += ",";

        return sql;
    }

    public String getEngine() {
        return "InnoDB"; // Table implemetations may override this
    }

    public ResultSet serialize(T row) {
        String columns = "";
        char bt = db.getBacktick();
        int i = 0;
        List<Field> fields = ReflectionUtil.getAllFields(getRowClass());
        for (Field f : fields) {
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();
            name = bt + name + bt;
            if (i == 0) {
                columns = name;
                i++;
                continue;
            }
            columns += ", " + name;
            i++;
        }

        if (i == 0) {
            throw new IllegalStateException(getRowClass().getName() + " doesn't have any public fields!");
        }

        String valuesPlaceholders = "";
        for (int k = 0; k < i; k++) {
            if (k == 0) {
                valuesPlaceholders = "?";
                continue;
            }
            valuesPlaceholders += ",?";
        }

        String sql = "INSERT INTO `{TABLE}` (" + columns + ") " + "VALUES(" + valuesPlaceholders + ")";
        List<Object> values = new ArrayList<>();
        for (Field f : fields) {
            try {
                values.add(f.get(row));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        executeUpdate(sql, values.toArray(new Object[values.size()]));
        return executeQuery("SELECT * FROM `{TABLE}` ORDER BY id DESC LIMIT 1");
    }

    protected T[] deserialize(ResultSet rs) {
        List<T> result = deserializeResultSet(rs);
        T[] array = (T[]) Array.newInstance(getRowClass(), result.size());
        return result.toArray(array);
    }

    public T[] get(String query, Object... paramObjects) {
        try {
            query = query.replaceAll("\\Q{TABLE}\\E", getName());
            PreparedStatement statement = db.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (paramObjects != null && paramObjects.length > 0) {
                int i = 1;
                for (Object s : paramObjects) {
                    statement.setObject(i, s);
                    i++;
                }
            }

            return deserialize(statement.executeQuery());
        } catch (SQLException e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't execute SQL query: " + sqlToString(query, paramObjects));
            throw new RuntimeException(e);
        }
    }

    public T insert(T row) {
        Validate.notNull(row);
        return deserialize(serialize(row))[0];
    }

    public SelectJoinStep select(SelectField... selectFields) {
        return context.select(selectFields).from(name);
    }

    public T fetch(ResultQuery query) {
        List<T> result = fetchList(query);
        if (result == null || result.size() < 1) {
            return null;
        }
        return result.get(0);
    }

    public List<T> fetchList(ResultQuery query) {
        List<T> result = new ArrayList<>();
        Result<Record> queryResult = query.fetch();
        if (queryResult == null || queryResult.size() < 1) {
            return null;
        }
        for (Record r : queryResult) {
            result.add(deserializeRecord(r));
        }
        return result;
    }

    protected T deserializeRecord(Record r) {
        Constructor<?> ctor;
        Object instance;
        try {
            ctor = getRowClass().getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid row class: " + getRowClass().getName() + ": Constructor shouldn't accept arguments!");
        }
        try {
            instance = ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Deserializing failed: ", e);
        }

        List<Field> fields = ReflectionUtil.getAllFields(getRowClass());
        for (Field f : fields) {
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();
            Object value = null;
            try {
                if (!hasColumn(r, name)) {
                    //Select query may not include this column
                    continue;
                }
                value = r.getValue(name);
                if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                    value = ((int) value) != 0; // for some reason this is returned as int on TINYINT(1)..
                }
                if (value == null && f.getAnnotation(Nullable.class) == null && !column.autoIncrement()) {
                    throw new RuntimeException("Trying to set null value on a not nullable and not autoincrement column");
                }
                f.set(instance, value);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Couldn't set value \"" + (value == null ? "null" : value.toString()) + "\" for field: " + getRowClass().getName() + "." + f
                                .getName() + ": ", e);
            }
        }
        return (T) instance;
    }

    protected List<T> deserializeResultSet(ResultSet r) {
        List<T> result = new ArrayList<>();
        Constructor<?> ctor;
        Object instance;
        try {
            ctor = getRowClass().getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid row class: " + getRowClass().getName() + ": Constructor shouldn't accept arguments!");
        }
        try {
            while (r.next()) {
                try {
                    instance = ctor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Deserializing failed: ", e);
                }

                List<Field> fields = ReflectionUtil.getAllFields(getRowClass());
                for (Field f : fields) {
                    Column column = f.getAnnotation(Column.class);
                    if (column == null) {
                        continue;
                    }
                    Object value = null;
                    try {
                        String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();

                        if (!hasColumn(r, name)) {
                            //Select query may not include this column
                            continue;
                        }

                        value = r.getObject(name);
                        if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                            value = ((int) value) != 0; // for some reason this is returned as int on TINYINT(1)..
                        }

                        if (value == null && f.getAnnotation(Nullable.class) == null && !column.autoIncrement()) {
                            SinkLibrary.getInstance().getLogger().warning(
                                    "Trying to set null value on a not nullable and not autoincrement column: " + getRowClass().getName() + "." + f
                                            .getName());
                        }

                        f.set(instance, value);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Couldn't set value \"" + (value == null ? "null" : value.toString()) + "\" for field: " + getRowClass().getName()
                                + "." + f.getName() + ": ", e);
                    }
                }
                result.add((T) instance);
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while deserializing " + getRowClass().getName() + ": ", e);
        }
        return result;
    }

    public Class<T> getRowClass() {
        Object superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            return (Class<T>) superclass;
        }
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = genericSuperclass.getActualTypeArguments()[0];
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        }
        throw new IllegalStateException("Unknown type: " + type.getTypeName());
    }

    public ResultSet executeQuery(String sql, @Nullable Object... paramObjects) {
        sql = sql.replaceAll("\\Q{TABLE}\\E", getName());
        try {
            PreparedStatement statment = db.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                             ResultSet.CONCUR_UPDATABLE);
            if (paramObjects != null) {
                int i = 1;
                for (Object s : paramObjects) {
                    statment.setObject(i, s);
                    i++;
                }
            }
            return statment.executeQuery();
        } catch (SQLException e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't execute SQL query: " + sqlToString(sql, paramObjects));
            throw new RuntimeException(e);
        }
    }

    public void executeUpdate(String sql, @Nullable Object... paramObjects) {
        sql = sql.replaceAll("\\Q{TABLE}\\E", getName());
        try {
            PreparedStatement statment = db.getConnection().prepareStatement(sql);
            if (paramObjects != null) {
                int i = 1;
                for (Object s : paramObjects) {
                    statment.setObject(i, s);
                    i++;
                }
            }
            statment.executeUpdate();
        } catch (SQLException e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't execute SQL update: " + sqlToString(sql, paramObjects));
            throw new RuntimeException(e);
        }
    }

    protected String sqlToString(String sql, Object... paramObjects) {
        if (sql == null || paramObjects == null || paramObjects.length < 1) {
            return sql;
        }

        for (Object paramObject : paramObjects) {
            sql = sql.replaceFirst("\\Q?\\E", paramObject == null ? "NULL" : paramObject.toString());
        }

        return sql;
    }

    public boolean exists() {
        try {
            DatabaseMetaData dbm = db.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, name, null);
            return tables.next();
        } catch (Exception e) {
            return false;
        }
    }
}
