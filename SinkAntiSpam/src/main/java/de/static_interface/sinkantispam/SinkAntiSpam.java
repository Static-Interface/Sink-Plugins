/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
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

package de.static_interface.sinkantispam;

import de.static_interface.sinkantispam.command.CreatePredefinedWarningCommand;
import de.static_interface.sinkantispam.command.DeletePredefinedWarningCommand;
import de.static_interface.sinkantispam.command.DeleteWarnCommand;
import de.static_interface.sinkantispam.command.ListWarnsCommand;
import de.static_interface.sinkantispam.command.PredefinedWarningsListCommand;
import de.static_interface.sinkantispam.command.WarnCommand;
import de.static_interface.sinkantispam.config.SasLanguage;
import de.static_interface.sinkantispam.config.SasSettings;
import de.static_interface.sinkantispam.database.table.PredefinedWarningsTable;
import de.static_interface.sinkantispam.database.table.WarnedPlayersTable;
import de.static_interface.sinkantispam.database.table.WarningsTable;
import de.static_interface.sinkantispam.sanction.WarningSanction;
import de.static_interface.sinkantispam.sanction.impl.BanWarningSanction;
import de.static_interface.sinkantispam.sanction.impl.CommandWarningSanction;
import de.static_interface.sinkantispam.sanction.impl.KickWarningSanction;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.database.Database;
import de.static_interface.sinklibrary.database.DatabaseConfiguration;
import de.static_interface.sinklibrary.database.impl.database.MySqlDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nullable;

public class SinkAntiSpam extends JavaPlugin {

    private static SinkAntiSpam instance;
    private WarningsTable warningsTable;
    private PredefinedWarningsTable predefinedWarningsTable;
    private WarnedPlayersTable warnedPlayersTable;
    private Database db;
    private List<WarningSanction> registeredSanctions = new ArrayList<>();

    public static SinkAntiSpam getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            return;
        }
        File sinkAntiSpamDirectory = new File(SinkLibrary.getInstance().getCustomDataFolder(), "SinkAntiSpam");

        new SasSettings(new File(sinkAntiSpamDirectory, "Settings.yml")).init();
        new SasLanguage(new File(sinkAntiSpamDirectory, "Language.yml")).init();

        Configuration commandsConfig = new Configuration(new File(sinkAntiSpamDirectory, "Commands.yml")) {
            @Override
            public void addDefaults() {

            }
        };
        commandsConfig.init();

        db =
                new MySqlDatabase(
                        new DatabaseConfiguration(sinkAntiSpamDirectory, "Database.yml", "SAS_", "SinkPlugins"),
                        this);
        try {
            db.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        warnedPlayersTable = new WarnedPlayersTable(db);
        try {
            warnedPlayersTable.create();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        predefinedWarningsTable = new PredefinedWarningsTable(db);
        try {
            predefinedWarningsTable.create();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        warningsTable = new WarningsTable(db);
        try {
            warningsTable.create();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerWarningSanction(new BanWarningSanction());
        registerWarningSanction(new KickWarningSanction());
        registerWarningSanction(new CommandWarningSanction());

        instance = this;

        Bukkit.getPluginManager().registerEvents(new SinkAntiSpamListener(), this);
        SinkLibrary.getInstance().registerCommand("warn", new WarnCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("listwarnings", new ListWarnsCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("deletewarning", new DeleteWarnCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("createpredefinedwarning", new CreatePredefinedWarningCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("deletepredefinedwarning", new DeletePredefinedWarningCommand(this, commandsConfig));
        SinkLibrary.getInstance().registerCommand("predefinedwarninglist", new PredefinedWarningsListCommand(this, commandsConfig));
    }

    public void registerWarningSanction(WarningSanction sanction) {
        if (sanction.getId() == null) {
            throw new RuntimeException("WarningSanction ID equals null!");
        }
        if (getWarningSanction(sanction.getId()) != null) {
            throw new IllegalArgumentException(sanction.getId() + " is already registered!");
        }

        registeredSanctions.add(sanction);
    }

    @Nullable
    public WarningSanction getWarningSanction(String id) {
        id = id.trim();
        for (WarningSanction sanction : registeredSanctions) {
            if (sanction.getId().equalsIgnoreCase(id)) {
                return sanction;
            }
        }

        return null;
    }

    @Override
    public void onDisable() {
        instance = null;
        registeredSanctions.clear();

        if (db != null) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("SinkLibrary") == null) {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        return SinkLibrary.getInstance().validateApiVersion(SinkLibrary.API_VERSION, this);
    }

    public WarningsTable getWarningsTable() {
        return warningsTable;
    }

    public PredefinedWarningsTable getPredefinedWarningsTable() {
        return predefinedWarningsTable;
    }

    public WarnedPlayersTable getWarnedPlayersTable() {
        return warnedPlayersTable;
    }
}
