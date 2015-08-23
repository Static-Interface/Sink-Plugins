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

package de.static_interface.sinklibrary.database.impl.table;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.CascadeAction;
import de.static_interface.sinklibrary.database.Database;
import de.static_interface.sinklibrary.database.impl.row.OptionsRow;
import de.static_interface.sinklibrary.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javax.annotation.Nullable;

public abstract class OptionsTable extends AbstractTable<OptionsRow> {

    public OptionsTable(String name, Database db) {
        super(name, db);
    }

    public void setOption(String key, Object value) {
        setOption(key, value, null);
    }

    public void setOption(String key, Object value, @Nullable Integer foreignTarget) {
        if (value != null && value.equals("null")) {
            value = null;
        }

        OptionsRow row;
        String parsedValue;
        try {
            row = getRowClass().newInstance();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.close();
            parsedValue = Base64.getEncoder().encodeToString(baos.toByteArray());
            row.key = key;
            row.value = parsedValue;
            row.foreignTarget = foreignTarget;
            insert(row);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getOption(String key) {
        return getOptionInternal("SELECT * FROM {TABLE} WHERE `key`=?", Object.class, false, key);
    }

    public Object getOption(String key, Integer foreignId) {
        return getOptionInternal("SELECT * FROM `{TABLE}` WHERE `key`=? AND `foreignTarget`=?", Object.class, false, key, foreignId);
    }

    @Override
    public Class<OptionsRow> getRowClass() {
        return OptionsRow.class;
    }

    public <K> K getOption(String key, Class<K> clazz, K defaultValue) {
        try {
            return getOptionInternal("SELECT * FROM {TABLE} WHERE `key`=?", clazz, true, key);
        } catch (NullPointerException ignored) {
            return defaultValue;
        }
    }

    public <K> K getOption(String key, @Nullable Integer foreignId, Class<K> clazz, K defaultValue) {
        try {
            return getOptionInternal("SELECT * FROM {TABLE} WHERE `key`=? AND `foreignTarget`=?", clazz, true, key, foreignId);
        } catch (NullPointerException ignored) {
            return defaultValue;
        }
    }

    private <K> K getOptionInternal(String query, Class<K> clazz, boolean throwExceptionOnNull, Object... bindings) {
        String s;
        OptionsRow[] result = get(query, bindings);
        if (result == null || result.length < 1) {
            if (throwExceptionOnNull) {
                throw new NullPointerException();
            }
            return null;
        }
        s = result[0].value;
        if (s == null) {
            return null;
        }
        try {
            byte[] data = Base64.getDecoder().decode(s);
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return (K) o;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error deserializing \"" + s + "\" on query: " + query + ", params: [" + StringUtil.formatArrayToString(bindings, ", ") + "]", e);
        }
    }

    @Nullable
    public abstract Class<?> getForeignTable();

    public abstract String getForeignColumn();

    public abstract CascadeAction getForeignOnUpdateAction();

    public abstract CascadeAction getForeignOnDeleteAction();
}
