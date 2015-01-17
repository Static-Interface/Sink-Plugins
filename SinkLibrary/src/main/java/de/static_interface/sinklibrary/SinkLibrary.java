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

package de.static_interface.sinklibrary;

import de.static_interface.sinklibrary.api.annotation.Unstable;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.SinkTabCompleter;
import de.static_interface.sinklibrary.api.event.IrcSendMessageEvent;
import de.static_interface.sinklibrary.api.exception.NotInitializedException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.sender.FakeSender;
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
import de.static_interface.sinklibrary.listener.IngameUserListener;
import de.static_interface.sinklibrary.listener.IrcCommandListener;
import de.static_interface.sinklibrary.listener.IrcLinkListener;
import de.static_interface.sinklibrary.user.ConsoleUser;
import de.static_interface.sinklibrary.user.ConsoleUserProvider;
import de.static_interface.sinklibrary.user.FakeUserProvider;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IngameUserProvider;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.user.IrcUserProvider;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.SinkIrcReflection;
import de.static_interface.sinklibrary.util.StringUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
public class SinkLibrary extends JavaPlugin {

    public static final int API_VERSION = 2;
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
    private HashMap<Class<?>, SinkUserProvider> userImplementations;
    private IngameUserProvider ingameUserProvider;
    private IrcUserProvider ircUserProvider;
    private ConsoleUserProvider consoleUserProvider;
    private FakeUserProvider fakeUserProvider;
    private SinkTabCompleter defaultCompleter;
    private List<String> loadedLibs = new ArrayList<>();

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
            getLogger().warning("Plugin: " + plugin.getName() + " is not up-to-date! (API version mismatch)");
            getLogger().warning("Please update " + plugin.getName() + " to the latest version or ask the author to update it");
            Debug.log(plugin.getName() + " API Version: " + compileVersion + ", current version: " + getApiVersion());
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        // Init variables first to prevent NullPointerExceptions when other plugins try to access them
        instance = this;
        // Init Settings
        description = getDescription();
        settings = new Settings();
        version = getDescription().getVersion();
        tmpBannedPlayers = new ArrayList<>();
        logger = new Logger();
        timer = new TpsTimer();
        commands = new HashMap<>();
        commandAliases = new HashMap<>();
        userImplementations = new HashMap<>();
        defaultCompleter = new SinkTabCompleter();

        ingameUserProvider = new IngameUserProvider();
        consoleUserProvider = new ConsoleUserProvider();
        ircUserProvider = new IrcUserProvider();
        fakeUserProvider = new FakeUserProvider();

        LIB_FOLDER = new File(getCustomDataFolder(), "libs");

        if ((!LIB_FOLDER.exists() && !LIB_FOLDER.mkdirs())) {
            getLogger().severe("Coudln't create lib directory");
        }

        registerUserImplementation(Player.class, ingameUserProvider);
        registerUserImplementation(ConsoleCommandSender.class, consoleUserProvider);
        registerUserImplementation(User.class, ircUserProvider);
        registerUserImplementation(FakeSender.class, fakeUserProvider);
        // Init language
        LanguageConfiguration languageConfiguration =
                new LanguageConfiguration();
        languageConfiguration.init();

        getLogger().info("Loading...");
        // Check optional dependencies
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault Plugin not found. Disabling economy and some permission features.");
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
                getLogger().warning("Economy Plugin not found. Disabling economy features.");
                economyAvailable = false;
            }
            if (!setupPermissions()) {
                getLogger().warning("Permissions Plugin not found. Disabling permissions features.");
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
            getLogger().info("Successfully hooked into permissions, economy and chat.");
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
                Debug.log("SinkChat found. Skipping registration of IRCLinkListener");
            } else {
                Debug.log("SinkChat not found. Registering IRCLinkListener");
                Bukkit.getPluginManager().registerEvents(new IrcLinkListener(), this);
            }
        }

        loadLibs(getConsoleUser());
    }

    public SinkTabCompleter getDefaultTabCompleter() {
        return defaultCompleter;
    }

    public ClassLoader getClazzLoader() {
        return getClassLoader();
    }

    public void loadLibs() {
        loadLibs(null);
    }

    public void loadLibs(@Nullable SinkUser user) {
        if (user == null) {
            user = getConsoleUser();
        }

        File[] files = LIB_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    boolean loaded = true;
                    String path = file.getCanonicalPath();

                    // Don't load the same libs again (e.g. when using /sreload)
                    if (loadedLibs.contains(path)) {
                        continue;
                    }

                    if (file.getName().endsWith(".jar")) {
                        user.sendMessage(ChatColor.DARK_GREEN + "Loading jar library: " + file.getName());
                        addJarToClasspath(file.toURI().toURL());
                    } else if (file.getName().endsWith(".class")) {
                        addClassToClasspath(path);
                        user.sendMessage(ChatColor.DARK_GREEN + "Loading class file: " + file.getName());
                    } else if (file.getName().endsWith(".so") || file.getName().endsWith(".dll")) {
                        user.sendMessage(ChatColor.DARK_GREEN + "Loading native library: " + file.getName());
                        System.load(path);
                    } else {
                        user.sendMessage(ChatColor.RED + "Warning! Skipped unknown file: " + file.getName());
                        loaded = false;
                    }

                    if (loaded) {
                        loadedLibs.add(path);
                    }
                } catch (Throwable thr) {
                    user.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "An exception occurred while loading file: " + file.getName());
                }
            }
        }

        if (loadedLibs.size() > 0) {
            getLogger().info("Loaded " + loadedLibs.size() + " Libraries");
        }
    }

    @Unstable
    public void addClassToClasspath(String path) throws Exception {
        getClassLoader().loadClass(path);
    }

    public void addJarToClasspath(URL url) throws Exception {
        URLClassLoader classLoader
                = (URLClassLoader) getClassLoader();
        Class<URLClassLoader> clazz = URLClassLoader.class;

        // Use reflection to access protected "addURL" method
        Method method = clazz.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(classLoader, url);
    }

    public int getApiVersion() {
        return API_VERSION;
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving players...");
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            IngameUser user = getIngameUser(p);
            if (user.getConfiguration().exists()) {
                user.getConfiguration().save();
            }
        }
        try {
            if (SinkLibrary.getInstance().getSettings().isLogEnabled()) {
                Debug.getDebugLogFileWriter().close();
            }
        } catch (Exception ignored) {
        }
        instance = null;
        getLogger().info("Disabled.");
    }

    public HashMap<String, SinkCommand> getCommands() {
        return commands;
    }

    public boolean isIrcAvailable() {
        if (!ircAvailable) {
            return false;
        }

        try {
            SinkIrcReflection.getMainChannel();
        } catch (Throwable e) {
            Debug.log(e);
            return false;
        }
        return true;
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
    public boolean sendIrcMessage(@Nonnull String message) {
        return sendIrcMessage(message, SinkIrcReflection.getMainChannel().getName());
    }

    /**
     * Send Message to IRC via SinkIRC Plugin.
     *
     * @param message Message to send
     * @param target Target user/channel, use null for default channel
     * @return true if successfully sended, false if event got cancelled or irc is not available
     */
    public boolean sendIrcMessage(@Nonnull String message, @Nonnull String target) {
        if (!ircAvailable) {
            return false;
        }
        Debug.log("Firing new IRCSendMessageEvent(\"" + message + "\")");
        IrcSendMessageEvent event = new IrcSendMessageEvent(message, target);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
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
     * @return {@link IngameUser} instance of the player
     */
    @Nonnull
    public IngameUser getIngameUser(Player player) {
        return (IngameUser) getUser(player);
    }

    /**
     * @param partialPlayerName Partial name of the player
     * @return {@link IngameUser} instance of the player
     * @throws UserNotFoundException If user not found
     */
    @Nonnull
    public IngameUser getIngameUser(String partialPlayerName) {
        return getIngameUser(partialPlayerName, true);
    }

    /**
     * @param partialPlayerName Partial name of the player
     * @param throwExceptionIfNotFound If true, throw an exception if user was not found (never joined the server)
     * @return {@link IngameUser} instance of the player
     * @throws UserNotFoundException If user not found and throwExceptionIfNotFound equals true
     */
    @Nonnull
    public IngameUser getIngameUser(String partialPlayerName, boolean throwExceptionIfNotFound) {

        for (Player p : Bukkit.getOnlinePlayers()) {
            IngameUser onlineUser = getIngameUser(p);
            if (p.getName().startsWith(partialPlayerName) || onlineUser.getName().startsWith(partialPlayerName)
                || ChatColor.stripColor(onlineUser.getDisplayName()).startsWith(partialPlayerName)) {
                return onlineUser;
            }
        }

        IngameUser user = getIngameUser(BukkitUtil.getUniqueIdByName(partialPlayerName));
        if (throwExceptionIfNotFound && !user.hasPlayedBefore()) {
            throw new UserNotFoundException();
        }

        return user;
    }

    /**
     * @param playerName Exact name of the player
     * @return User instance
     * @throws UserNotFoundException If user not found
     */
    public IngameUser getIngameUserExact(String playerName) {
        return getIngameUserExact(playerName, true);
    }

    /**
     * @param playerName Exact name of the player
     * @param throwExceptionIfNotFound If true, throw an exception if user was not found (never joined the server)
     * @return {@link IngameUser} instance of the player
     * @throws UserNotFoundException If user not found and throwExceptionIfNotFound equals true
     */
    @Nonnull
    public IngameUser getIngameUserExact(String playerName, boolean throwExceptionIfNotFound) {
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null) {
            return getIngameUser(p);
        }

        IngameUser user = getIngameUser(BukkitUtil.getUniqueIdByName(playerName));
        if (throwExceptionIfNotFound && !user.hasPlayedBefore()) {
            throw new UserNotFoundException();
        }
        return user;
    }

    /**
     * @param partialName Name of the target user (e.g. use <name>_IRC suffix to target irc users)
     * @return {@link SinkUser} instance of the target
     */
    public SinkUser getUser(String partialName) {
        return getUser(partialName, true);
    }

    /**
     * @param partialName Name of the target user (e.g. use <name>_IRC suffix to target irc users)
     * @param throwExceptionIfNotFound If true, throw an exception if user was not found (never joined the server)
     * @return {@link SinkUser} instance of the target
     * @throws UserNotFoundException If user not found and throwExceptionIfNotFound equals true
     */
    public SinkUser getUser(String partialName, boolean throwExceptionIfNotFound) {
        boolean hasSuffix = false;

        if (partialName.equalsIgnoreCase("console")) {
            return getConsoleUser();
        }

        //Search for suffixes
        for (SinkUserProvider provider : userImplementations.values()) {
            String suffix = provider.getTabCompleterSuffix();
            if (suffix == null || suffix.equals("")) {
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

        //No user found with that suffix
        if (hasSuffix) {
            return null;
        }

        if (partialName.equalsIgnoreCase("console")) {
            return getConsoleUser();
        }

        return getIngameUser(partialName, throwExceptionIfNotFound);
    }

    /**
     * @param name Exact name of the target (e.g. use <name>_IRC prefix to target irc users)
     * @return {@link SinkUser} instance of the target
     */
    public SinkUser getUserExact(String name) {
        return getUserExact(name, true);
    }

    /**
     * @param name Exact name of the target (e.g. use <name>_IRC prefix to target irc users)
     * @param throwExceptionIfNotFound If true, throw an exception if user was not found (never joined the server)
     * @return {@link SinkUser} instance of the target
     * @throws UserNotFoundException If user not found and throwExceptionIfNotFound equals true
     */
    public SinkUser getUserExact(String name, boolean throwExceptionIfNotFound) {

        for (SinkUserProvider provider : userImplementations.values()) {
            String suffix = provider.getTabCompleterSuffix();
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

        return getIngameUserExact(name, throwExceptionIfNotFound);
    }

    /**
     * @param uuid UUID of the player
     * @return {@link SinkUser} instance of the target
     */
    public IngameUser getIngameUser(UUID uuid) {
        return ingameUserProvider.getUserInstance(uuid);
    }

    /**
     * @param sender Sender of the User
     * @deprecated Use {@link SinkLibrary#getUser(Object) instead}
     * @return {@link SinkUser} instance of the sender
     */
    @Deprecated
    public SinkUser getUser(CommandSender sender) {
        return getUser((Object) sender);
    }

    /**
     * @param base Base of the User
     * @return IUser instance of sender, e.g. if sender is {@link ConsoleCommandSender}, it will return {@link ConsoleUser}.
     *         <br/> May return null if there is no default implementation
     */
    @Nullable
    public SinkUser getUser(Object base) {
        if (base instanceof IrcCommandSender) {
            base = ((IrcCommandSender) base).getUser().getBase();
        }

        for (Class<?> implClass : userImplementations.keySet()) {
            if (implClass.isInstance(base)) {
                SinkUserProvider provider = userImplementations.get(implClass);

                if (provider instanceof IrcUserProvider) {
                    return ((IrcUserProvider) provider).getUserInstance(((User) base).getNick());
                }

                return userImplementations.get(implClass).getUserInstance(base);
            }
        }

        //Todo: add default implementation for not supported bases/CommandSenders
        return null;
    }

    /**
     * Register an own implementation of {@link SinkUser} for a {@link CommandSender}
     */
    public void registerUserImplementation(Class<?> base, SinkUserProvider provider) {
        Debug.log("Registering user provider for base: " + base.getName() + ": " + provider.getClass().getName());
        userImplementations.put(base, provider);
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
        return ircUserProvider.getUserInstance(nick);
    }

    public IrcUser getIrcUser(User user, String source) {
        return ircUserProvider.getUserInstance(user, source);
    }

    public void loadIrcUser(User user, String source) {
        ircUserProvider.loadUser(user, source);
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
        IngameUser user = ingameUserProvider.getUserInstance(uuid);
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
    @Unstable
    public Collection<IngameUser> getOnlineUsers() {
        Set<IngameUser> users = new HashSet<>();
        for (SinkUser user : ingameUserProvider.getUserInstances()) {
            users.add((IngameUser) user);
        }
        return users;
    }

    /**
     * @return Online IRC users as Users
     */
    @Unstable
    public Collection<IrcUser> getOnlineIrcUsers() {
        Set<IrcUser> users = new HashSet<>();
        for (SinkUser user : ircUserProvider.getUserInstances()) {
            users.add((IrcUser) user);
        }
        return users;
    }

    @Deprecated
    public Logger getCustomLogger() {
        return logger;
    }

    public void registerCommand(String name, SinkCommand command) {
        name = name.toLowerCase();
        Debug.logMethodCall(name, command);
        if (!command.getCommandOptions().isIrcOnly()) {
            try {
                PluginCommand cmd = Bukkit.getPluginCommand(name);
                if (cmd == null) {
                    Debug.log(Level.WARNING, "Command is not registered in plugin.yml file");
                    return;
                }

                Debug.log("Bukkit Command instance found: " + cmd.toString());

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
                command.setPermission(cmd.getPermission());
                command.setPlugin(cmd.getPlugin());
            } catch (NullPointerException ignored) {
                // do nothing because command may be an irc command which is not registered in the plugin.yml
            }
        } else {
            Debug.log("Command is ircOnly. Skipping search for Bukkit Command instance");
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
            getLogger().info(Updater.CONSOLEPREFIX + "No new updates found...");
        } else if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
            getLogger().info(Updater.CONSOLEPREFIX + "Updates downloaded, please restart or reload the server to take effect...");
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
        Bukkit.getPluginManager().registerEvents(new IngameUserListener(), this);
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
        return consoleUserProvider.getUserInstance(Bukkit.getConsoleSender());
    }
}
