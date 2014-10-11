/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkchat;

import com.palmergames.bukkit.towny.Towny;
import de.static_interface.sinkchat.channel.Channel;
import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinkchat.channel.ChannelValues;
import de.static_interface.sinkchat.command.ChannelCommand;
import de.static_interface.sinkchat.command.NationChatCommand;
import de.static_interface.sinkchat.command.NickCommand;
import de.static_interface.sinkchat.command.SpyCommands;
import de.static_interface.sinkchat.command.TownChatCommand;
import de.static_interface.sinkchat.listener.ChatListener;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SinkChat extends JavaPlugin {

    private static boolean initialized = false;
    private static Towny towny;
    private static ChannelConfiguration channelconfigs = null;

    public static ChannelConfiguration getChannelConfigs() {
        return channelconfigs;
    }

    public static boolean isTownyAvailable() {
        return towny != null;
    }

    public static Towny getTowny() {
        return towny;
    }

    public void onEnable() {
        if (!checkDependencies()) {
            return;
        }

        if (!initialized) {
            registerEvents();
            registerCommands();
            registerChannels();
            initialized = true;
        }
    }

    private void registerChannels() {
        channelconfigs = new ChannelConfiguration();
        YamlConfiguration yamlConfig = channelconfigs.getYamlConfiguration();
        ConfigurationSection section = yamlConfig.getConfigurationSection("Channels");
        Channel channel = null;
        for (String key : section.getKeys(false)) {
            String pathPrefix = "Channels." + key + ".";

            String callChar = (String) channelconfigs.get(pathPrefix + ChannelValues.CALLCHAR);
            boolean enabled = (boolean) channelconfigs.get(pathPrefix + ChannelValues.ENABLED);
            String permission = (String) channelconfigs.get(pathPrefix + ChannelValues.PERMISSION);
            boolean sendToIRC = (boolean) channelconfigs.get(pathPrefix + ChannelValues.SEND_TO_IRC);
            int range = (int) channelconfigs.get(pathPrefix + ChannelValues.RANGE);
            String format = (String) channelconfigs.get(pathPrefix + ChannelValues.FORMAT);

            channel = new Channel(key, callChar, enabled, permission, sendToIRC, range, format);
            ChannelHandler.registerChannel(channel);
        }
    }

    private boolean checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("SinkLibrary") == null) {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        Plugin tmp = Bukkit.getPluginManager().getPlugin("Towny");
        if (tmp != null && tmp instanceof Towny) {
            towny = (Towny) tmp;
        } else {
            towny = null;
            SinkLibrary.getInstance().getCustomLogger().log(Level.INFO, "Towny not found. Disabling Towny Chat features");
        }

        return true;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }

    private void registerCommands() {
        SinkLibrary.getInstance().registerCommand("nick", new NickCommand(this));
        SinkLibrary.getInstance().registerCommand("enablespy", new SpyCommands.EnableSpyCommand(this));
        SinkLibrary.getInstance().registerCommand("disablespy", new SpyCommands.DisablSpyCommand(this));
        SinkLibrary.getInstance().registerCommand("channel", new ChannelCommand(this));

        if (towny != null) {
            getCommand("nationchat").setExecutor(new NationChatCommand());
            getCommand("townchat").setExecutor(new TownChatCommand());
        }
    }
}