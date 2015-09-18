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

package de.static_interface.sinkchat;

import com.palmergames.bukkit.towny.Towny;
import de.static_interface.sinkchat.channel.Channel;
import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinkchat.channel.ChannelValues;
import de.static_interface.sinkchat.command.NationChatCommand;
import de.static_interface.sinkchat.command.NickCommand;
import de.static_interface.sinkchat.command.SpyCommands;
import de.static_interface.sinkchat.command.TownChatCommand;
import de.static_interface.sinkchat.config.ScLanguage;
import de.static_interface.sinkchat.config.ScSettings;
import de.static_interface.sinkchat.listener.ChatListener;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class SinkChat extends JavaPlugin {

    private static SinkChat instance;
    private Towny towny;
    private ChannelConfiguration channelconfigs = null;

    public static SinkChat getInstance() {
        return instance;
    }

    public ChannelConfiguration getChannelConfigs() {
        return channelconfigs;
    }

    public boolean isTownyAvailable() {
        return towny != null;
    }

    public Towny getTowny() {
        return towny;
    }

    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            return;
        }

        File sinkChatDirectory = new File(SinkLibrary.getInstance().getCustomDataFolder(), "SinkChat");

        new ScSettings(new File(sinkChatDirectory, "Settings.yml")).init();
        new ScLanguage(new File(sinkChatDirectory, "Language.yml")).init();

        Configuration commandsConfig = new Configuration(new File(sinkChatDirectory, "Commands.yml")) {
            @Override
            public void addDefaults() {

            }
        };
        commandsConfig.init();

        instance = this;

        registerEvents();
        registerCommands(commandsConfig);
        registerChannels(sinkChatDirectory);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerChannels(File baseFolder) {
        channelconfigs = new ChannelConfiguration(baseFolder);
        YamlConfiguration yamlConfig = channelconfigs.getYamlConfiguration();
        ConfigurationSection section = yamlConfig.getConfigurationSection("Channels");
        Channel channel;
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

        if (!SinkLibrary.getInstance().validateApiVersion(SinkLibrary.API_VERSION, this)) {
            return false;
        }

        Plugin tmp = Bukkit.getPluginManager().getPlugin("Towny");
        if (tmp != null && tmp instanceof Towny) {
            towny = (Towny) tmp;
        } else {
            towny = null;
            getLogger().info("Towny not found. Disabling Towny Chat features");
        }

        return true;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }

    private void registerCommands(Configuration config) {
        SinkLibrary.getInstance().registerCommand("nick", new NickCommand(this, config));
        SinkLibrary.getInstance().registerCommand("enablespy", new SpyCommands.EnableSpyCommand(this, config));
        SinkLibrary.getInstance().registerCommand("disablespy", new SpyCommands.DisablSpyCommand(this, config));

        if (towny != null) {
            SinkLibrary.getInstance().registerCommand("nationchat", new NationChatCommand(this, config));
            SinkLibrary.getInstance().registerCommand("townchat", new TownChatCommand(this, config));
        }
    }
}