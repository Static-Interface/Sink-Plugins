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

package de.static_interface.sinklibrary.database.impl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.static_interface.sinklibrary.api.annotation.Unstable;
import de.static_interface.sinklibrary.database.Database;
import de.static_interface.sinklibrary.database.DatabaseConnectionInfo;
import de.static_interface.sinklibrary.database.SQLDialect;

import java.io.File;
import java.sql.SQLException;

/**
 * H2 database implementation<br/>
 * See <a href="http://www.h2database.com/html/main.html">H2 homepage</a> for more information about H2
 */
@Unstable
public class H2Database extends Database {

    private final File dbFile;

    /**
     * @param file the file to be used for storage
     * @param prefix the prefix for tables
     */
    public H2Database(File file, final String prefix) {
        super(new DatabaseConnectionInfo() {
            @Override
            public String getAddress() {
                return null;
            }

            @Override
            public int getPort() {
                return 0;
            }

            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getTablePrefix() {
                return prefix;
            }

            @Override
            public String getDatabaseName() {
                return null;
            }
        }, SQLDialect.H2, '\0');
        dbFile = file;
    }

    @Override
    protected void setupConfig() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("user", "sa");
        config.addDataSourceProperty("url", "jdbc:h2:file:" + dbFile.getAbsolutePath()
                                            + ";MV_STORE=FALSE;MODE=MySQL;IGNORECASE=TRUE");
        config.setConnectionTimeout(5000);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void connect() throws SQLException {
        setupConfig();
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            dataSource.close();
            throw e;
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }

        if (dataSource != null) {
            dataSource.close();
        }
    }
}
