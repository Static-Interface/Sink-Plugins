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

import de.static_interface.sinkantispam.command.DeleteWarnCommand;
import de.static_interface.sinkantispam.command.ListWarnsCommand;
import de.static_interface.sinkantispam.command.WarnCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SinkAntiSpam extends JavaPlugin {

    private static SinkAntiSpam instance;

    public static SinkAntiSpam getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            return;
        }
        instance = this;

        Bukkit.getPluginManager().registerEvents(new SinkAntiSpamListener(), this);
        SinkLibrary.getInstance().registerCommand("warn", new WarnCommand(this));
        SinkLibrary.getInstance().registerCommand("listwarns", new ListWarnsCommand(this));
        SinkLibrary.getInstance().registerCommand("deletewarn", new DeleteWarnCommand(this));
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private boolean checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("SinkLibrary") == null) {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        return SinkLibrary.getInstance().validateApiVersion(SinkLibrary.API_VERSION, this);
    }
}
