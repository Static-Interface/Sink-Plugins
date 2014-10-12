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

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.SinkTabCompleter;
import de.static_interface.sinklibrary.api.event.IrcSendMessageEvent;
import de.static_interface.sinklibrary.api.exception.NotInitializedException;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import de.static_interface.sinklibrary.command.SinkDebugCommand;
import de.static_interface.sinklibrary.command.SinkReloadCommand;
import de.static_interface.sinklibrary.command.SinkVersionCommand;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import de.static_interface.sinklibrary.configuration.Settings;
import de.static_interface.sinklibrary.listener.DisplayNameListener;
import de.static_interface.sinklibrary.listener.IrcCommandListener;
import de.static_interface.sinklibrary.listener.IrcLinkListener;
import de.static_interface.sinklibrary.listener.UserConfigurationListener;
import de.static_interface.sinklibrary.user.ConsoleUser;
import de.static_interface.sinklibrary.user.ConsoleUserProvider;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IngameUserProvider;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.user.IrcUserProvider;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nullable;

@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
public class SinkLibrary extends JavaPlugin {

    public static final int API_VERSION = 1;
    public static File LIB_FOLDER;
    private static SinkLibrary instance;
    public List<String> tmpBannedPlayers;
    private boolean sinkChatAvailable;
    private boolean ircAvailable;
    private Logger logger;
    private TpsTimer timer;
    private Economy econ;
    private Permission perm;
    private Chat chat;
    private String version;
    private Settings settings;
    private PluginDescriptionFile description;
    private boolean economyAvailable = true;
    private boolean permissionsAvailable = true;
    private boolean chatAvailable = true;
    private boolean vaultAvailable = false;
    private boolean initalized;
    private HashMap<String, SinkCommand> commandAliases;
    private HashMap<String, SinkCommand> commands;
    private HashMap<Class<? extends CommandSender>, SinkUserProvider> userImplementations;
    private IngameUserProvider ingameUserProvider;
    private IrcUserProvider ircUserProvider;
    private ConsoleUserProvider consoleUserProvider;

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

    public boolean validateApiVersion(int compileVersion, Plugin plugin) {
        if (compileVersion < getApiVersion()) {
            getCustomLogger().warn("Plugin: " + plugin.getName() + " is not up-to-date! (API version mismatch)");
            getCustomLogger().warn("Please update " + plugin.getName() + " to the latest version or ask the author to update it");
            getCustomLogger().debug(plugin.getName() + " API Version: " + compileVersion + ", current version: " + getApiVersion());
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
        return true;
    }

    public void onEnable() {
        // Init variables first to prevent NullPointerExceptions when other plugins try to access them
        instance = this;
        version = getDescription().getVersion();
        tmpBannedPlayers = new ArrayList<>();
        description = getDescription();
        logger = new Logger();
        timer = new TpsTimer();
        commands = new HashMap<>();
        commandAliases = new HashMap<>();
        userImplementations = new HashMap<>();
        ingameUserProvider = new IngameUserProvider();
        consoleUserProvider = new ConsoleUserProvider();
        ircUserProvider = new IrcUserProvider();

        LIB_FOLDER = new File(getCustomDataFolder(), "libs");

        if ((!LIB_FOLDER.exists() && !LIB_FOLDER.mkdirs())) {
            SinkLibrary.getInstance().getCustomLogger().severe("Coudln't create lib directory");
        }

        registerUserImplementation(Player.class, ingameUserProvider);
        registerUserImplementation(ConsoleCommandSender.class, consoleUserProvider);
        registerUserImplementation(IrcCommandSender.class, ircUserProvider);

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
            getCustomLogger().warn("Vault Plugin not found. Disabling economy and some permission features.");
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
                getCustomLogger().warn("Economy Plugin not found. Disabling economy features.");
                economyAvailable = false;
            }
            if (!setupPermissions()) {
                getCustomLogger().warn("Permissions Plugin not found. Disabling permissions features.");
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

        loadLibs();
    }

    public ClassLoader getClazzLoader() {
        return getClassLoader();
    }

    private void loadLibs() {
        try {
            File[] files = LIB_FOLDER.listFiles();
            if (files != null) {
                int i = 0;
                for (File file : files) {
                    if (file.getName().endsWith(".jar")) {
                        getLogger().info("SinkLibrary: Loading java library: " + file.getName());
                        addUrlToClasspath(file.toURL());
                        i++;
                    }

                    //if(file.getName().endsWith(".so") ||file.getName().endsWith("dll"))
                    //{
                    //    getLogger().info("SinkScripts: Loading native library: " + file.getName());
                    //    System.loadLibrary(file.getCanonicalPath());
                    //    i++;
                    //}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUrlToClasspath(URL url) throws Exception {
        URLClassLoader classLoader
                = (URLClassLoader) getClassLoader();
        Class clazz = URLClassLoader.class;

        // Use reflection
        Method method = clazz.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(classLoader, url);
    }

    public int getApiVersion() {
        return API_VERSION;
    }

    public void onDisable() {
        getCustomLogger().log(Level.INFO, "Disabling...");
        getCustomLogger().info("Saving players...");
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            IngameUser user = getIngameUser(p);
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

    public HashMap<String, SinkCommand> getCommands() {
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

        return new File(getDataFolder().getAbsolutePath().replace(pluginName, "SinkPlugins"));
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
     * @param player Player
     * @return User instance of the player
     */
    public IngameUser getIngameUser(Player player) {
        return (IngameUser) getUser(player);
    }

    /**
     * @param partialPlayerName Name of the player
     * @return User instance
     */
    public IngameUser getIngameUser(String partialPlayerName) {
        Player p = BukkitUtil.getPlayer(partialPlayerName);
        if (p != null) {
            return getIngameUser(p);
        }
        UUID uuid = BukkitUtil.getUniqueIdByName(partialPlayerName);
        return getIngameUser(uuid);
    }

    /**
     * @param playerName Exact name of the player
     * @return User instance
     */
    public IngameUser getIngameUserExact(String playerName) {
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null) {
            return getIngameUser(p);
        }
        UUID uuid = BukkitUtil.getUniqueIdByName(playerName);
        return getIngameUser(uuid);
    }

    /**
     * @param partialName Name of the target user (e.g. use <name>_IRC suffix to target irc users)
     * @return User instance of the target
     */
    @Nullable
    public SinkUser getUser(String partialName) {
        boolean hasSuffix = false;
        for (SinkUserProvider provider : userImplementations.values()) {
            String suffix = provider.getCommandArgsSuffix();
            if (suffix.equals("")) {
                continue;
            }
            if (partialName.endsWith(suffix)) {
                hasSuffix = true;
            }

            for (IrcUser user : getOnlineIrcUsers()) {
                if (user.getName().startsWith(partialName.replaceFirst(StringUtil.stripRegex(suffix), "")) && hasSuffix) {
                    return user;
                }
            }
        }
        if (hasSuffix) {
            return null;
        }

        if (partialName.equalsIgnoreCase("console")) {
            return getConsoleUser();
        }

        return getIngameUser(partialName);
    }

    /**
     * @param name Exact name of the target (e.g. use <name>_IRC prefix to target irc users)
     * @return User instance of the target
     */
    @Nullable
    public SinkUser getUserExact(String name) {

        for (SinkUserProvider provider : userImplementations.values()) {
            String suffix = provider.getCommandArgsSuffix();
            if (suffix.equals("")) {
                continue;
            }
            if (name.endsWith(suffix)) {
                name = name.replaceFirst(StringUtil.stripRegex(suffix), "");
                return provider.getUserInstance(name);
            }
        }

        if (name.equalsIgnoreCase("console")) {
            return getConsoleUser();
        }

        return getIngameUserExact(name);
    }

    public IngameUser getIngameUser(UUID uuid) {
        return (IngameUser) ingameUserProvider.getUserInstance(uuid);
    }

    /**
     * @param sender Command Sender
     * @return IUser instance of sender, e.g. if sender is {@link ConsoleCommandSender}, it will return {@link ConsoleUser}.
     *         <br/> May return null if there is no default implementation
     */
    @Nullable
    public SinkUser getUser(CommandSender sender) {
        //Todo: Replace with SinkUserProviders

        for (Class<? extends CommandSender> implClass : userImplementations.keySet()) {
            if (implClass.isInstance(sender)) {
                return userImplementations.get(implClass).getUserInstance(sender);
            }
        }

        //Todo: add default implementation for not supported CommandSenders
        return null;
    }

    /**
     * Register an own implementation of {@link SinkUser} for a {@link CommandSender}
     */
    public void registerUserImplementation(Class<? extends CommandSender> sender, SinkUserProvider provider) {
        userImplementations.put(sender, provider);
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

        IngameUser user = getIngameUser(player);
        IngameUserConfiguration config = user.getConfiguration();

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

    @Nullable
    public IrcUser getIrcUser(String nick) {
        return (IrcUser) ircUserProvider.getUserInstance(nick);
    }

    public IrcUser getIrcUser(User user) {
        return (IrcUser) ircUserProvider.getUserInstance(user);
    }

    public void loadIrcUser(User user) {
        ircUserProvider.loadUser(user);
    }

    public void unloadIrcUser(User user) {
        ircUserProvider.unloadUser(user);
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
        if (p == null || !p.isOnline()) {
            return;
        }

        ingameUserProvider.loadUser(p);
    }

    /**
     * Unload an User
     * Do not call this, its handled internally
     *
     * @param uuid UUID of the User who needs to be unloaded
     */
    public void unloadUser(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        IngameUser user = null;
        if (p != null) {
            user = (IngameUser) ingameUserProvider.getUserInstance(uuid);
        }
        if (user == null) {
            return;
        }
        user.getConfiguration().save();
        ingameUserProvider.unloadUser(user);
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
     * @return Online players as Users
     */
    public Collection<IngameUser> getOnlineUsers() {
        Collection<IngameUser> users = new ArrayList<>();
        for (SinkUser user : ingameUserProvider.getUserInstances()) {
            users.add((IngameUser) user);
        }
        return users;
    }

    /**
     * @return Online IRC users as Users
     */
    public Collection<IrcUser> getOnlineIrcUsers() {
        Collection<IrcUser> users = new ArrayList<>();
        for (SinkUser user : ircUserProvider.getUserInstances()) {
            users.add((IrcUser) user);
        }
        return users;
    }

    public Logger getCustomLogger() {
        return logger;
    }

    public void registerCommand(String name, SinkCommand command) {
        name = name.toLowerCase();
        if (!command.isIrcOnly()) {
            try {
                PluginCommand cmd = Bukkit.getPluginCommand(name);
                if (cmd != null) {
                    cmd.setExecutor(command);
                    cmd.setTabCompleter(new SinkTabCompleter(command.includeIrcUsersInTabCompleter()));
                    for (String alias : cmd.getAliases()) // Register alias commands
                    {
                        alias = alias.toLowerCase();
                        if (alias.equals(name)) {
                            continue;
                        }
                        commandAliases.put(alias, command);
                    }
                    command.setUsage(cmd.getUsage());
                    command.setPermission(cmd.getPermission());
                    command.setPlugin(cmd.getPlugin());
                }
            } catch (NullPointerException ignored) {
                // do nothing because command may be an irc command which is not registered in the plugin.yml
            }
        }

        commandAliases.put(name, command);
        commands.put(name, command);
    }

    public SinkCommand getCustomCommand(String name) {
        SinkCommand cmd;
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

    public ConsoleUser getConsoleUser() {
        return (ConsoleUser) consoleUserProvider.getUserInstance(Bukkit.getConsoleSender());
    }
}
