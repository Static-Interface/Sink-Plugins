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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Permissions {

    private List<Permission> permissions = new ArrayList<>();

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void add(Permission... newPermissions) {
        Collections.addAll(permissions, newPermissions);
    }

    public void merge(Permissions permissions) {
        for (Permission perm : permissions.getPermissions()) {
            add(perm);
        }
    }

    public void unmerge(Permissions permissions) {
        for (Permission perm : permissions.getPermissions()) {
            this.permissions.remove(perm);
        }
    }

    public Permission getPermission(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();

        for (Permission perm : getPermissions()) {
            if (perm.getPermissionString().trim().equalsIgnoreCase(s)) {
                return perm;
            }
        }

        return null;
    }
}
