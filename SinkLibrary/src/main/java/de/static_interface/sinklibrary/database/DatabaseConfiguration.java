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

import de.static_interface.sinklibrary.api.configuration.Configuration;

import java.io.File;

public class DatabaseConfiguration extends Configuration implements DatabaseConnectionInfo {

    public DatabaseConfiguration(File baseFolder) {
        super(new File(baseFolder, "Database.yml"), true);
    }

    public DatabaseConfiguration(File baseFolder, String fileName) {
        super(new File(baseFolder, fileName), true);
    }

    @Override
    public void addDefaults() {
        addDefault("Type", "H2");
        addDefault("Address", "localhost");
        addDefault("Port", 3306);
        addDefault("Username", "root");
        addDefault("Password", "");
        addDefault("TablePrefix", "RP_");
        addDefault("DatabaseName", "ReallifePlugin");
    }

    @Override
    public SQLDialect getDatabaseType() {
        try {
            return SQLDialect.valueOf(((String) get("Type")).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAddress() {
        return (String) get("Address");
    }

    @Override
    public int getPort() {
        return Integer.valueOf(String.valueOf(get("Port")));
    }

    @Override
    public String getUsername() {
        return (String) get("Username");
    }

    @Override
    public String getPassword() {
        return (String) get("Password");
    }

    @Override
    public String getTablePrefix() {
        return (String) get("TablePrefix");
    }

    @Override
    public String getDatabaseName() {
        return (String) get("DatabaseName");
    }
}
