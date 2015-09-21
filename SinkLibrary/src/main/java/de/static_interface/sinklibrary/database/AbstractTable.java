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
import de.static_interface.sinklibrary.database.annotation.UniqueKey;
import de.static_interface.sinklibrary.database.exception.InvalidSqlColumnException;
import de.static_interface.sinklibrary.database.impl.table.OptionsTable;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.ReflectionUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.lang.Validate;

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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * A class which allows interaction with a SQL table
 * @param <T> See {@link Row}
 */
public abstract class AbstractTable<T extends Row> {

    private final String name;
    protected Database db;

    /**
     * @param name the name of the table
     * @param db the database of this table
     */
    public AbstractTable(String name, Database db) {
        this.name = name;
        this.db = db;
    }

    /**
     * @param rs the ResulSet to check
     * @param columnName the name of the column to check
     * @return thrue if the ResultSet contains the columnNmae
     * @throws SQLException
     */
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

    /**
     * @return the prefixed name of the table
     */
    public final String getName() {
        return db.getConnectionInfo().getTablePrefix() + name;
    }

    /**
     * Get the database of this table
     * @return the database of this table
     */
    public final Database getDatabase() {
        return db;
    }

    /**
     * Create the table
     * @see ForeignKey
     * @see Column
     * @see Index
     * @throws SQLException if the {@link Row} class is malformed
     */
    public void create() throws SQLException {
        char bt = db.getBacktick();
        String sql = "CREATE TABLE IF NOT EXISTS " + bt + getName() + bt + " (";

        List<String> primaryKeys = new ArrayList<>();
        List<String> uniqueKeys = new ArrayList<>();
        List<Field> foreignKeys = new ArrayList<>();
        List<Field> indexes = new ArrayList<>();
        HashMap<Integer, List<String>> combinedUniqueKeys = new HashMap<>();

        Class foreignOptionsTable = null;

        if (this instanceof OptionsTable) {
            foreignOptionsTable = ((OptionsTable) this).getForeignTable();
        }

        for (Field f : getRowClass().getFields()) {
            Column column = FieldCache.getAnnotation(f, Column.class);
            if (column == null) {
                continue;
            }
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();

            sql += bt + name + bt + " " + db.toDatabaseType(f);

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
                uniqueKeys.add(name);
            }

            UniqueKey uniqueKey = FieldCache.getAnnotation(f, UniqueKey.class);
            if (uniqueKey != null) {
                if (uniqueKey.combinationId() == Integer.MAX_VALUE) {
                    uniqueKeys.add(name);
                } else {
                    List<String> keys = combinedUniqueKeys.get(uniqueKey.combinationId());
                    if (keys == null) {
                        keys = new ArrayList<>();
                    }
                    keys.add(name);
                    combinedUniqueKeys.put(uniqueKey.combinationId(), keys);
                }
            }

            if (column.primaryKey()) {
                primaryKeys.add(name);
            }

            if (FieldCache.getAnnotation(f, Nullable.class) == null) {
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

            if (FieldCache.getAnnotation(f, ForeignKey.class) != null) {
                foreignKeys.add(f);
            }

            if (FieldCache.getAnnotation(f, Index.class) != null) {
                indexes.add(f);
            }

            sql += ",";
        }

        if (primaryKeys.size() > 0) {
            String columns = "";
            for (String f : primaryKeys) {
                if (!columns.equals("")) {
                    columns += ", ";
                }
                columns += bt + f + bt;
            }
            sql += "PRIMARY KEY (" + columns + "),";
        }

        if (uniqueKeys.size() > 0) {
            for (String s : uniqueKeys) {
                sql += "UNIQUE KEY " + bt + s + bt + ",";
            }
        }

        if (combinedUniqueKeys.size() > 0) {
            for (List<String> columnsList : combinedUniqueKeys.values()) {
                String columns = "";
                String first = null;
                for (String f : columnsList) {
                    if (!columns.equals("")) {
                        columns += ", ";
                    }
                    if (first == null) {
                        first = f;
                    }
                    columns += bt + f + bt;
                }
                sql += "UNIQUE KEY " + bt + first + "_uk" + bt + " (" + columns + "),";
            }
        }

        for (Field f : foreignKeys) {
            Column column = FieldCache.getAnnotation(f, Column.class);
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();
            ForeignKey foreignKey = FieldCache.getAnnotation(f, ForeignKey.class);

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

            Column column = FieldCache.getAnnotation(f, Column.class);
            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();

            Index index = FieldCache.getAnnotation(f, Index.class);
            String indexName = StringUtil.isEmptyOrNull(index.name()) ? name + "_I" : index.name();

            sql += "INDEX " + bt + indexName + bt + " (" + bt + name + bt + ")";

            sql += ",";
        }

        if (sql.endsWith(",")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        sql += ")";
        if (db.getDialect() == SQLDialect.MySQL || db.getDialect() == SQLDialect.MariaDB) {
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

    /**
     * @return the SQL storage engine
     */
    public String getEngine() {
        return "InnoDB"; // Table implemetations may override this
    }

    /**
     * Get the result as deserialized {@link T}[] from the given query
     * @param query The SQL query, <code>{TABLE}</code> will be replaced with {@link #getName()}
     * @param bindings the {@link PreparedStatement} bindings
     * @return the {@link ResultSet} deserialized as {@link T}
     */
    public T[] get(String query, Object... bindings) {
        try {
            query = query.replaceAll("\\Q{TABLE}\\E", getName());
            PreparedStatement statement = db.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (bindings != null && bindings.length > 0) {
                int i = 1;
                for (Object s : bindings) {
                    statement.setObject(i, s);
                    i++;
                }
            }

            ResultSet rs = statement.executeQuery();
            List<T> result = deserializeResultSet(rs);
            rs.close();
            T[] array = (T[]) Array.newInstance(getRowClass(), result.size());
            return result.toArray(array);
        } catch (SQLException e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't execute SQL query: " + sqlToString(query, bindings));
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert a row to the table
     * @param row the row to insert
     * @return the {@link T} object with auto-incremented fields
     */
    public T insert(T row) {
        Validate.notNull(row);
        String columns = "";
        char bt = db.getBacktick();
        int i = 0;
        List<Field> fields = ReflectionUtil.getAllFields(getRowClass());
        Map<Field, String> autoIncrements = new HashMap<>();
        for (Field f : fields) {
            Column column = FieldCache.getAnnotation(f, Column.class);
            if (column == null) {
                continue;
            }

            String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();

            if (column.autoIncrement()) {
                autoIncrements.put(f, name);
            }

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

        PreparedStatement ps = createPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS, values.toArray(new Object[values.size()]));
        try {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ResultSet rs;
        try {
            rs = ps.getGeneratedKeys();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Field f : autoIncrements.keySet()) {
            setFieldFromResultSet(row, rs, f, autoIncrements.get(f));
        }

        try {
            rs.close();
        } catch (SQLException e) {
            Debug.log(e);
        }

        return row;
    }

    protected T setFieldFromResultSet(T instance, ResultSet rs, Field f, String columnName) {
        Column column = FieldCache.getAnnotation(f, Column.class);
        Object value;
        try {
            value = rs.getObject(columnName, f.getType());
            if (value == null) {
                value = rs.getObject(columnName);
            }

            if (value != null && ReflectionUtil.isWrapperClass(f.getType()) && ReflectionUtil.isPrimitiveClass(value.getClass())) {
                value = ReflectionUtil.primitiveToWrapper(value);
            } else if (value != null && ReflectionUtil.isWrapperClass(value.getClass()) && ReflectionUtil.isPrimitiveClass(f.getType())) {
                value = ReflectionUtil.wrapperToPrimitive(value);
            }

            if (value != null && f.getType().isAssignableFrom(value.getClass())) {
                value = f.getType().cast(value);
            }

            if (value instanceof Long && !f.getType().isAssignableFrom(Long.class)) {
                if (f.getType().isAssignableFrom(Byte.class)) {
                    value = ((Number) value).byteValue();
                } else if (f.getType().isAssignableFrom(Short.class)) {
                    value = ((Number) value).shortValue();
                } else if (f.getType().isAssignableFrom(Integer.class)) {
                    value = ((Number) value).intValue();
                }
            } else if (value instanceof Double && !f.getType().isAssignableFrom(Double.class)) {
                if (f.getType().isAssignableFrom(Float.class)) {
                    value = ((Number) value).floatValue();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if ((f.getType() == boolean.class || f.getType() == Boolean.class) && ReflectionUtil.isNumber(value.getClass())
            && value != (Object) false && value != (Object) true && value != Boolean.TRUE
            && value != Boolean.FALSE) {
            value = ((byte) value) != 0; // for some reason this is returned as int on TINYINT(1)..
        }

        if (value == null && (ReflectionUtil.isPrimitiveClass(f.getType()) || (FieldCache.getAnnotation(f, Nullable.class) == null && !column
                .autoIncrement()))) {
            SinkLibrary.getInstance().getLogger().warning(
                    "Trying to set null value on a not nullable and not autoincrement column: " + getRowClass().getName() + "." + f
                            .getName());
        }

        try {
            f.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    protected T setFieldsFromResultSet(T instance, ResultSet rs) {
        List<Field> fields = ReflectionUtil.getAllFields(getRowClass());
        for (Field f : fields) {
            Column column = FieldCache.getAnnotation(f, Column.class);
            if (column == null) {
                continue;
            }
            Object value = null;
            try {
                String name = StringUtil.isEmptyOrNull(column.name()) ? f.getName() : column.name();
                if (!hasColumn(rs, name)) {
                    //Select query may not include this column
                    continue;
                }
                setFieldFromResultSet(instance, rs, f, name);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Couldn't set value \"" + (value == null ? "null" : value.toString()) + "\" for field: " + getRowClass().getName()
                        + "." + f.getName() + ": ", e);
            }
        }
        return instance;
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

                setFieldsFromResultSet((T) instance, r);

                result.add((T) instance);
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while deserializing " + getRowClass().getName() + ": ", e);
        }
        return result;
    }

    /**
     * @return the {@link Class}&lt;{@link T}&gt; representation of {@link T}
     */
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

    /**
     * Execute a native query without auto deserialisation<br/>
     * @param sql the sql query, <code>{TABLE}</code> will be replaced with {@link #getName()}
     * @param bindings the {@link PreparedStatement} bindings
     * @return the {@link ResultSet} of the query
     * @deprecated Use {@link #get(String, Object...)} instead
     */
    @Deprecated
    public ResultSet executeQuery(String sql, @Nullable Object... bindings) {
        sql = sql.replaceAll("\\Q{TABLE}\\E", getName());
        try {
            PreparedStatement statment = db.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                             ResultSet.CONCUR_UPDATABLE);
            if (bindings != null) {
                int i = 1;
                for (Object s : bindings) {
                    statment.setObject(i, s);
                    i++;
                }
            }
            return statment.executeQuery();
        } catch (SQLException e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't execute SQL query: " + sqlToString(sql, bindings));
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement createPreparedStatement(String sql, @Nullable Object... bindings) {
        return createPreparedStatement(sql, null, bindings);
    }

    public PreparedStatement createPreparedStatement(String sql, Integer flags, @Nullable Object... bindings) {
        sql = sql.replaceAll("\\Q{TABLE}\\E", getName());
        try {
            PreparedStatement statement;
            if (flags != null) {
                statement = db.getConnection().prepareStatement(sql, flags);
            } else {
                statement = db.getConnection().prepareStatement(sql);
            }
            if (bindings != null) {
                int i = 1;
                for (Object s : bindings) {
                    statement.setObject(i, s);
                    i++;
                }
            }
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute a native SQL update without auto deserialisation<br/>
     * @param sql the sql query, <code>{TABLE}</code> will be replaced with {@link #getName()}
     * @param bindings the {@link PreparedStatement} bindings
     */
    public void executeUpdate(String sql, @Nullable Object... bindings) {
        try {
            PreparedStatement statment = createPreparedStatement(sql, bindings);
            statment.executeUpdate();
        } catch (Exception e) {
            SinkLibrary.getInstance().getLogger().severe("Couldn't execute SQL update: " + sqlToString(sql, bindings));
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

    /**
     * @return true if the table exists
     */
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
