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

    private String defaultDatabase;
    private String defaultTablePrefix;

    /**
     * @param baseFolder the parent directory for the configuration
     * @param defaultDatabase the default database name
     * @param defaultTablePrefix the default table prefix
     */
    public DatabaseConfiguration(File baseFolder, String defaultDatabase, String defaultTablePrefix) {
        this(baseFolder, "Database.yml", defaultDatabase, defaultTablePrefix);
    }

    /**
     * @param baseFolder the parent directory for the configuration
     * @param fileName the name of the configuration file
     * @param defaultDatabase the default database name
     * @param defaultTablePrefix the default table prefix
     */
    public DatabaseConfiguration(File baseFolder, String fileName, String defaultDatabase, String defaultTablePrefix) {
        super(new File(baseFolder, fileName), true);
        this.defaultDatabase = defaultDatabase;
        this.defaultTablePrefix = defaultTablePrefix;
    }

    @Override
    public void onCreate() {
        set("TablePrefix", defaultTablePrefix);
        set("DatabaseName", defaultDatabase);
    }

    @Override
    public void addDefaults() {
        addDefault("Type", "H2");
        addDefault("Address", "localhost");
        addDefault("Port", 3306);
        addDefault("Username", "root");
        addDefault("Password", "");
        addDefault("TablePrefix", defaultTablePrefix);
        addDefault("DatabaseName", defaultDatabase);
    }

    /**
     * @return the database type
     */
    public String getDatabaseType() {
        try {
            return (((String) get("Type")));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the address to connect to
     */
    @Override
    public String getAddress() {
        return (String) get("Address");
    }

    /**
     * @return the port of the connection
     */
    @Override
    public int getPort() {
        return Integer.valueOf(String.valueOf(get("Port")));
    }

    /**
     * @return the username for authentification
     */
    @Override
    public String getUsername() {
        return (String) get("Username");
    }

    /**
     * @return the password for authentification
     */
    @Override
    public String getPassword() {
        return (String) get("Password");
    }

    /**
     * @return the prefix for the tables
     */
    @Override
    public String getTablePrefix() {
        return (String) get("TablePrefix");
    }

    /**
     * @return the name of the database
     */
    @Override
    public String getDatabaseName() {
        return (String) get("DatabaseName");
    }
}
