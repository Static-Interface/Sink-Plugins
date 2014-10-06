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

package de.static_interface.sinklibrary;

import de.static_interface.sinklibrary.command.Command;
import de.static_interface.sinklibrary.command.SinkDebugCommand;
import de.static_interface.sinklibrary.command.SinkReloadCommand;
import de.static_interface.sinklibrary.command.SinkTabCompleter;
import de.static_interface.sinklibrary.command.SinkVersionCommand;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import de.static_interface.sinklibrary.configuration.Settings;
import de.static_interface.sinklibrary.configuration.UserConfiguration;
import de.static_interface.sinklibrary.event.IrcSendMessageEvent;
import de.static_interface.sinklibrary.exception.NotInitializedException;
import de.static_interface.sinklibrary.listener.DisplayNameListener;
import de.static_interface.sinklibrary.listener.IrcCommandListener;
import de.static_interface.sinklibrary.listener.IrcLinkListener;
import de.static_interface.sinklibrary.listener.UserConfigurationListener;
import de.static_interface.sinklibrary.util.BukkitUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
public class SinkLibrary extends JavaPlugin {

    public static final int API_VERSION = 1;
    private static SinkLibrary instance;
    public List<String> tmpBannedPlayers;
    private boolean sinkChatAvailable;
    private boolean ircAvailable;
    private Logger logger;
    private TpsTimer timer;
    private Economy econ;
    private Permission perm;
    private Chat chat;
    private File dataFolder;
    private String version;
    private Settings settings;
    private List<JavaPlugin> registeredPlugins;
    private ConcurrentHashMap<Player, SinkUser> onlineUsers;
    private PluginDescriptionFile description;
    private boolean economyAvailable = true;
    private boolean permissionsAvailable = true;
    private boolean chatAvailable = true;
    private boolean vaultAvailable = false;
    private boolean initalized;
    private HashMap<String, Command> commandAliases;
    private HashMap<String, Command> commands;
    private TabCompleter tabCompleter;

    /**
     * Get the instance of this plugin
     * @return instance
     * @throws NotInitializedException if SinkLibrary did not initialize
     */
    public static SinkLibrary getInstance() {
        if (instance == null) {
            throw new NotInitializedException("SinkLibrary is not initalized");
        }
        return instance;
    }

    public void onEnable() {
        // Init variables first to prevent NullPointerExceptions when other plugins try to access them
        instance = this;
        version = getDescription().getVersion();
        tmpBannedPlayers = new ArrayList<>();
        registeredPlugins = new ArrayList<>();
        onlineUsers = new ConcurrentHashMap<>();
        description = getDescription();
        dataFolder = getDataFolder();
        logger = new Logger();
        timer = new TpsTimer();
        commands = new HashMap<>();
        commandAliases = new HashMap<>();
        tabCompleter = new SinkTabCompleter();

        // Init language
        LanguageConfiguration languageConfiguration =
                new LanguageConfiguration();
        languageConfiguration.init();

        // Init Settings
        settings = new Settings();
        settings.init();

        getCustomLogger().log(Level.INFO, "Loading...");
        // Check optional dependencies
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getCustomLogger().warning("Vault Plugin not found. Disabling economy and some permission features.");
            permissionsAvailable = false;
            economyAvailable = false;
            chatAvailable = false;
            vaultAvailable = false;
        } else {
            vaultAvailable = true;

            if (!setupChat()) {
                chatAvailable = false;
            }

            if (!setupEcononmy()) {
                getCustomLogger().warning("Economy Plugin not found. Disabling economy features.");
                economyAvailable = false;
            }
            if (!setupPermissions()) {
                getCustomLogger().warning("Permissions Plugin not found. Disabling permissions features.");
                permissionsAvailable = false;
            }
        }

        if (!getCustomDataFolder().exists()) {
            try {
                boolean success = getCustomDataFolder().mkdirs();
                if (!success) {
                    throw new IOException("Couldn't create directories!");
                }
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "Couldn't create Data Folder!", e); // Log via Bukkits Logger, because Log File doesnt exists
            }
        }

        if (chatAvailable && economyAvailable && permissionsAvailable) {
            getCustomLogger().info("Successfully hooked into permissions, economy and chat.");
        }

        // Register Listeners and Commands
        if (!initalized) {
            registerListeners();
            registerCommands();
            initalized = true;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, timer, 1000, 50);

        // Check for updates
        update();

        // Init players (reload etc)
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            onRefreshDisplayName(p);
        }

        sinkChatAvailable = Bukkit.getPluginManager().getPlugin("SinkChat") != null;

        if (Bukkit.getPluginManager().getPlugin("SinkIRC") != null) {
            ircAvailable = true;
            Bukkit.getPluginManager().registerEvents(new IrcCommandListener(), this);

            if (Bukkit.getPluginManager().getPlugin("SinkChat") != null) {
                getCustomLogger().debug("SinkChat found. Skipping registration of IRCLinkListener");
            } else {
                getCustomLogger().debug("SinkChat not found. Registering IRCLinkListener");
                Bukkit.getPluginManager().registerEvents(new IrcLinkListener(), this);
            }
        }
    }

    public void onDisable() {
        getCustomLogger().log(Level.INFO, "Disabling...");
        getCustomLogger().info("Saving players...");
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            SinkUser user = getUser(p);
            if (user.getConfiguration().exists()) {
                user.getConfiguration().save();
            }
        }
        getCustomLogger().debug("Disabled.");
        try {
            getCustomLogger().getFileWriter().close();
        } catch (Exception ignored) {
        }
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public boolean isIrcAvailable() {
        return ircAvailable;
    }

    /**
     * @return True if chat is available
     */
    public boolean isChatAvailable() {
        return chatAvailable;
    }

    /**
     * @return True if economy is available
     */
    public boolean isEconomyAvailable() {
        return economyAvailable;
    }

    /**
     * @return True if permissions are available
     */
    public boolean isPermissionsAvailable() {
        return permissionsAvailable;
    }

    /**
     * @return True if Vault available
     */
    public boolean isVaultAvailable() {
        return vaultAvailable;
    }

    /**
     * Get SinkTimer
     *
     * @return SinkTimer
     */
    public TpsTimer getSinkTimer() {
        return timer;
    }

    /**
     * Get Chat instance
     *
     * @return Chat instance
     * @see Chat
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Get Economy instance from Vault
     *
     * @return Economy instace
     * @see Economy
     */
    public Economy getEconomy() {
        return econ;
    }

    /**
     * Get Permissions instance
     *
     * @return Permissions
     * @see Permission
     */
    public Permission getPermissions() {
        return perm;
    }

    /**
     * Get custom data folder
     *
     * @return Data Folder of Sink Plugins (.../plugins/SinkPlugins/)
     */
    public File getCustomDataFolder() {
        String pluginName = getPluginName();
        if (pluginName == null) {
            throw new NullPointerException("getPluginName() returned null");
        }
        if (dataFolder == null) {
            throw new NullPointerException("dataFolder is null");
        }
        return new File(dataFolder.getAbsolutePath().replace(pluginName, "SinkPlugins"));
    }

    /**
     * Send Message to IRC via SinkIRC Plugin to the default channel.
     *
     * @param message Message to send
     * @deprecated use {@link #sendIrcMessage(String)} instead
     */
    @Deprecated
    public boolean sendIRCMessage(String message) {
        return sendIrcMessage(message);
    }

    /**
     * Send Message to IRC via SinkIRC Plugin to the default channel.
     *
     * @param message Message to send
     */
    public boolean sendIrcMessage(String message) {
        return sendIrcMessage(message, null);
    }

    /**
     * Send Message to IRC via SinkIRC Plugin.
     *
     * @param message Message to send
     * @param target Target user/channel, use null for default channel
     * @return true if successfully sended, false if event got cancelled or irc is not available
     */
    public boolean sendIrcMessage(String message, String target) {
        if (!ircAvailable) {
            return false;
        }
        getCustomLogger().debug("Firing new IRCSendMessageEvent(\"" + message + "\")");
        IrcSendMessageEvent event = new IrcSendMessageEvent(message, target);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    /**
     * Add Temp Ban to Player. Will be cleared on next server restart or plugin reload.
     *
     * @param username Player to ban
     */
    public void addTempBan(String username) {
        tmpBannedPlayers.add(username);
    }

    /**
     * Remove Temp Ban
     *
     * @param username Player to unban
     */
    public void removeTempBan(String username) {
        tmpBannedPlayers.remove(username);
    }

    /**
     * Get SinkPlugins Settings.
     *
     * @return Settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Register a plugin
     *
     * @param plugin Class which extends {@link org.bukkit.plugin.java.JavaPlugin}
     */
    public void registerPlugin(JavaPlugin plugin) {
        registeredPlugins.add(plugin);
    }

    /**
     * @param player Player
     * @return User instance of player
     */
    public SinkUser getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    /**
     * @param playerName Name of Player
     * @return User instance
     * @deprecated use {@link #getUser(java.util.UUID)} instead
     */
    @Deprecated
    public SinkUser getUser(String playerName) {
        Player p = Bukkit.getPlayer(playerName);
        if (p != null && p.isOnline()) {
            return getUser(p);
        }
        UUID uuid = BukkitUtil.getUniqueIdByName(playerName);
        return getUser(uuid);
    }

    public SinkUser getUser(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        SinkUser user = null;
        if (p != null) {
            user = onlineUsers.get(p);
        }
        if (user == null) {
            user = new SinkUser(uuid);
        }
        return user;
    }

    /**
     * @param sender Command Sender
     * @return User instance
     */
    public SinkUser getUser(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return new SinkUser(sender);
        }

        Player player = (Player) sender;
        return getUser(player.getUniqueId());
    }

    /**
     * Get Version of SinkLibrary
     *
     * @return Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Refresh a player's DisplayName
     *
     * @param player Player that needs to refresh DisplayName
     */
    public void onRefreshDisplayName(Player player) {
        if (!settings.isDisplayNamesEnabled()) {
            return;
        }

        SinkUser user = getUser(player);
        UserConfiguration config = user.getConfiguration();

        if (!config.exists()) {
            return;
        }

        String displayName = user.getDisplayName();

        if (displayName == null || (displayName.equals(user.getDefaultDisplayName()) && config.getHasDisplayName())
            || displayName.equals("null") || displayName.isEmpty() || !
                config.getHasDisplayName()) {
            displayName = user.getDefaultDisplayName();
            config.setDisplayName(displayName);
            config.setHasDisplayName(false);
        }

        player.setCustomName(displayName);
        config.setDisplayName(displayName);

        if (displayName.length() > 16) {
            displayName = displayName.substring(0, 16);
        }
        player.setPlayerListName(displayName);
    }

    /**
     * Unload an User
     * INTERNAL METHOD
     * Do not call this, its handled internally
     *
     * @param p Player instance of the user who needs to be loaded
     */
    public void loadUser(Player p) {
        // If user is already loaded or offline, return
        if (p == null || !p.isOnline() || onlineUsers.get(p) != null) {
            return;
        }
        onlineUsers.put(p, new SinkUser(p));
    }

    /**
     * Unload an User
     * Do not call this, its handled internally
     *
     * @param uuid UUID of the User who needs to be unloaded
     */
    public void unloadUser(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        SinkUser user = null;
        if (p != null) {
            user = onlineUsers.get(p);
        }
        if (user == null) {
            return;
        }
        user.getConfiguration().save();
        onlineUsers.remove(p);
    }

    /**
     * Get the name of this plugin
     *
     * @return the name of this plugin
     */
    public String getPluginName() {
        return description.getName();
    }

    /**
     * Get all online players
     *
     * @return Online players as Users
     * @deprecated Use {@link Bukkit#getOnlinePlayers()} and {@link #getUser(Player)} intead
     */
    @Deprecated
    public Collection<SinkUser> getOnlineUsers() {
        return Collections.unmodifiableCollection(onlineUsers.values());
    }

    public Logger getCustomLogger() {
        return logger;
    }

    public void registerCommand(String name, Command command) {
        name = name.toLowerCase();
        if (!command.isIrcOnly()) {
            try {
                PluginCommand cmd = Bukkit.getPluginCommand(name);
                if (cmd != null) {
                    cmd.setExecutor(command);
                    cmd.setTabCompleter(getDefaultTabCompleter());
                    for (String alias : cmd.getAliases()) // Register alias commands
                    {
                        alias = alias.toLowerCase();
                        if (alias.equals(name)) {
                            continue;
                        }
                        commandAliases.put(alias, command);
                    }
                    command.setUsage(cmd.getUsage());
                }
            } catch (NullPointerException ignored) {
                // do nothing because command may be an irc command which is not registered in the plugin.yml
            }
        }

        commandAliases.put(name, command);
        commands.put(name, command);
    }

    public TabCompleter getDefaultTabCompleter() {
        return tabCompleter;
    }

    public Command getCustomCommand(String name) {
        Command cmd;
        cmd = commands.get(name.toLowerCase());
        if (cmd == null) {
            cmd = commandAliases.get(name.toLowerCase());
        }
        return cmd;
    }

    private void update() {
        Updater updater = new Updater(settings.getUpdateType());
        String permission = "sinklibrary.updatenotification";
        String versionType = ' ' + updater.getLatestGameVersion() + ' ';
        if (versionType.equalsIgnoreCase("release")) {
            versionType = " ";
        }
        if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
            BukkitUtil
                    .broadcast(Updater.CONSOLEPREFIX + "A new" + versionType + "update is available: " + updater.getLatestName(), permission, false);
        } else if (updater.getResult() == Updater.UpdateResult.NO_UPDATE) {
            getCustomLogger().info(Updater.CONSOLEPREFIX + "No new updates found...");
        } else if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
            getCustomLogger().info(Updater.CONSOLEPREFIX + "Updates downloaded, please restart or reload the server to take effect...");
        }
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    private boolean setupEcononmy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            perm = permissionProvider.getProvider();
        }
        return (perm != null);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new UserConfigurationListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayNameListener(), this);
    }

    private void registerCommands() {
        registerCommand("sdebug", new SinkDebugCommand(this));
        registerCommand("sinkreload", new SinkReloadCommand(this));
        registerCommand("sinkversion", new SinkVersionCommand(this));
    }

    public boolean isSinkChatAvailable() {
        return sinkChatAvailable;
    }
}
