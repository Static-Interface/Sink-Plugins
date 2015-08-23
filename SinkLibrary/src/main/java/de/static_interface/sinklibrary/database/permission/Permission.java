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

package de.static_interface.sinklibrary.database.permission;

import javax.annotation.Nonnull;

public final class Permission {

    private final String description;
    private final Class valueType;
    private final boolean includeInAllPerms;
    private String permissionString;
    private Object defaultValue;

    public Permission(String permissionString, String description, @Nonnull Object defaultValue) {
        this.permissionString = permissionString;
        this.description = description;
        this.defaultValue = defaultValue;
        this.valueType = defaultValue.getClass();
        includeInAllPerms = true;
    }

    public Permission(String permissionString, String description, Class valueType, Object defaultValue, boolean includeInAllPerms) {
        this.permissionString = permissionString;
        this.description = description;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
        this.includeInAllPerms = includeInAllPerms;
    }

    public String getDescription() {
        return description;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class getValueType() {
        return valueType;
    }

    public boolean includeInAllPermission() {
        return includeInAllPerms;
    }
}
