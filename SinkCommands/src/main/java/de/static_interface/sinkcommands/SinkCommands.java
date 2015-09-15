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

package de.static_interface.sinkcommands;

import com.earth2me.essentials.Essentials;
import de.static_interface.sinkcommands.command.ChatCommand;
import de.static_interface.sinkcommands.command.ClearCommand;
import de.static_interface.sinkcommands.command.CountdownCommand;
import de.static_interface.sinkcommands.command.DrugCommand;
import de.static_interface.sinkcommands.command.GlobalmuteCommand;
import de.static_interface.sinkcommands.command.GupCommand;
import de.static_interface.sinkcommands.command.LagCommand;
import de.static_interface.sinkcommands.command.ListCommand;
import de.static_interface.sinkcommands.command.MessageCommands;
import de.static_interface.sinkcommands.command.MilkCommand;
import de.static_interface.sinkcommands.command.NewbiechatCommand;
import de.static_interface.sinkcommands.command.RawCommands;
import de.static_interface.sinkcommands.command.RenameCommand;
import de.static_interface.sinkcommands.command.SudoCommand;
import de.static_interface.sinkcommands.command.TeamchatCommand;
import de.static_interface.sinkcommands.listener.DrugDeadListener;
import de.static_interface.sinkcommands.listener.GlobalMuteListener;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SinkCommands extends JavaPlugin {

    private static SinkCommands instance;
    private boolean globalmuteEnabled;
    private boolean initialized = false;
    private com.earth2me.essentials.Essentials essentialsPlugin;

    public static SinkCommands getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!checkDependencies()) {
            return;
        }

        LagTimer lagTimer = new LagTimer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lagTimer, 15000, 15000);

        if (!initialized) {
            registerEvents();
            registerCommands();
            initialized = true;
        }

    }

    private boolean checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("SinkLibrary") == null) {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        Plugin tmp = Bukkit.getPluginManager().getPlugin("Essentials");
        if (tmp != null && tmp instanceof com.earth2me.essentials.Essentials) {
            essentialsPlugin = (com.earth2me.essentials.Essentials) tmp;
        } else {
            essentialsPlugin = null;
            SinkCommands.getInstance().getLogger().info("Essentials not found. Disabling Essentials features");
        }

        return SinkLibrary.getInstance().validateApiVersion(SinkLibrary.API_VERSION, this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GlobalMuteListener(), this);
        pm.registerEvents(new DrugDeadListener(), this);
    }

    private void registerCommands() {

        getCommand("teamchat").setExecutor(new TeamchatCommand());
        getCommand("newbiechat").setExecutor(new NewbiechatCommand());
        getCommand("list").setExecutor(new ListCommand());

        SinkLibrary.getInstance().registerCommand("milk", new MilkCommand(this));
        SinkLibrary.getInstance().registerCommand("rename", new RenameCommand(this));
        SinkLibrary.getInstance().registerCommand("clearinventory", new ClearCommand(this));
        SinkLibrary.getInstance().registerCommand("drug", new DrugCommand(this));
        SinkLibrary.getInstance().registerCommand("countdown", new CountdownCommand(this));
        SinkLibrary.getInstance().registerCommand("globalmute", new GlobalmuteCommand(this));
        SinkLibrary.getInstance().registerCommand("lag", new LagCommand(this));
        SinkLibrary.getInstance().registerCommand("raw", new RawCommands.RawCommand(this));
        SinkLibrary.getInstance().registerCommand("rawuser", new RawCommands.RawUserCommand(this));
        SinkLibrary.getInstance().registerCommand("gup", new GupCommand(this));
        SinkLibrary.getInstance().registerCommand("sudo", new SudoCommand(this));
        SinkLibrary.getInstance().registerCommand("chat", new ChatCommand(this));
        SinkLibrary.getInstance().registerCommand("message", new MessageCommands.MessageCommand(this));
        SinkLibrary.getInstance().registerCommand("reply", new MessageCommands.ReplyCommand(this));
    }

    public Essentials getEssentialsPlugin() {
        return essentialsPlugin;
    }

    public boolean isGlobalmuteEnabled() {
        return globalmuteEnabled;
    }

    public void setGlobalmuteEnabled(boolean globalmuteEnabled) {
        this.globalmuteEnabled = globalmuteEnabled;
    }
}
