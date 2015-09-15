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
import de.static_interface.sinklibrary.api.command.NativeCommand;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.SinkTabCompleter;
import de.static_interface.sinklibrary.api.exception.NotInitializedException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.provider.BanProvider;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.api.sender.ProxiedCommandSender;
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
import de.static_interface.sinklibrary.provider.SimpleBanProvider;
import de.static_interface.sinklibrary.provider.StringConvertProvider;
import de.static_interface.sinklibrary.sender.ProxiedConsoleCommandSender;
import de.static_interface.sinklibrary.sender.ProxiedPlayer;
import de.static_interface.sinklibrary.user.ConsoleUser;
import de.static_interface.sinklibrary.user.ConsoleUserProvider;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IngameUserProvider;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.user.IrcUserProvider;
import de.static_interface.sinklibrary.user.ProxiedIngameUserProvider;
import de.static_interface.sinklibrary.user.ProxiedUserProvider;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.SinkIrcReflection;
import de.static_interface.sinklibrary.util.StringUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Channel;
import org.pircbotx.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
public class SinkLibrary extends JavaPlugin {

    public static final int API_VERSION = 2;
    public static File LIB_FOLDER;
    private static SinkLibrary instance;
    private static HashMap<Class<?>, StringConvertProvider> stringConvertertProviders = new HashMap<>();
    private final Map<String, SinkCommand> commandAliases = new ConcurrentHashMap<>();
    private final Map<String, SinkCommand> commands = new ConcurrentHashMap<>();
    private TpsTimer timer;
    private Economy econ;
    private Permission perm;
    private Chat chat;
    private boolean economyAvailable = true;
    private boolean permissionsAvailable = true;
    private boolean chatAvailable = true;
    private boolean vaultAvailable = false;
    private Map<Class<?>, SinkUserProvider> userImplementations;
    private SinkTabCompleter defaultCompleter;
    private List<String> loadedLibs;
    private Settings settings;
    private Logger logger;
    private File customDataFolder;
    private ConsoleUserProvider consoleUserProvider;
    private IngameUserProvider ingameUserProvider;
    private IrcUserProvider ircUserProvider;
    private BanProvider banProvider;
    private boolean ircExceptionOccured = false;
    private SimpleBanProvider defaultBanProvider = new SimpleBanProvider();
    /**
     * Get the instance of this plugin
     * @return instance
     * @throws NotInitializedException if SinkLibrary did not initialize
     */
    public static SinkLibrary getInstance() {
        if (instance == null) {
            instance = (SinkLibrary) Bukkit.getPluginManager().getPlugin("SinkLibrary");
            //throw new NotInitializedException("SinkLibrary is not initalized");
        }
        return instance;
    }

    @SafeVarargs
    public static <T> void registerStringConvertProvider(StringConvertProvider<T> provider, Class<T>... clazzes) {
        if (clazzes == null || clazzes.length == 0) {
            throw new IllegalArgumentException("No implementation classes defined!");
        }
        for (Class<T> clazz : clazzes) {
            StringConvertProvider p = stringConvertertProviders.get(clazz);
            if (p != null) {
                throw new IllegalStateException(clazz.getName() + " already has a provider defined! (" + p.getClass().getName() + ")");
            }

            stringConvertertProviders.put(clazz, provider);
        }
    }

    public boolean registerBanProvider(BanProvider provider) {
        if (banProvider != null) {
            return false;
        }

        banProvider = provider;
        return true;
    }

    @Nonnull
    public BanProvider getBanProvider() {
        if (banProvider == null) {
            return defaultBanProvider;
        }

        return banProvider;
    }

    @Nullable
    public StringConvertProvider getStringConvertProvider(Class<?> clazz) {
        return stringConvertertProviders.get(clazz);
    }

    public Map<Class<?>, StringConvertProvider> getStringConvertProviders() {
        return Collections.unmodifiableMap(stringConvertertProviders);
    }

    public boolean hasStringConvertProvider(Class<?> clazz) {
        return stringConvertertProviders.get(clazz) != null;
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
        getLogger().info("Loading...");
        if (settings == null) {
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
            settings = new Settings();
            settings.init();
        }


        loadedLibs = new CopyOnWriteArrayList<>();

        ingameUserProvider = new IngameUserProvider();
        consoleUserProvider = new ConsoleUserProvider();
        ircUserProvider = new IrcUserProvider();

        registerUserImplementation(ConsoleCommandSender.class, consoleUserProvider);
        registerUserImplementation(User.class, ircUserProvider);
        registerUserImplementation(Player.class, ingameUserProvider);
        ProxiedUserProvider proxiedUserProvider = new ProxiedUserProvider();
        registerUserImplementation(ProxiedCommandSender.class, proxiedUserProvider);
        registerUserImplementation(ProxiedConsoleCommandSender.class, proxiedUserProvider);
        registerUserImplementation(ProxiedPlayer.class, new ProxiedIngameUserProvider());
        LIB_FOLDER = new File(getCustomDataFolder(), "libs");

        if ((!LIB_FOLDER.exists() && !LIB_FOLDER.mkdirs())) {
            getLogger().warning("Coudln't create lib directory");
        }

        LanguageConfiguration languageConfiguration =
                new LanguageConfiguration();
        languageConfiguration.init();

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
                getLogger().warning("Chat providing plugin not found. Disabling chat features.");
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

        if (chatAvailable && economyAvailable && permissionsAvailable) {
            getLogger().info("Successfully hooked into permissions, economy and chat.");
        }

        // Register Listeners and Commands
        registerListeners();
        registerCommands();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, getSinkTimer(), 1000, 50);

        // Init players (reload etc)
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            onRefreshDisplayName(p);
        }

        Bukkit.getPluginManager().registerEvents(new IrcCommandListener(), this);

        if (!isSinkChatAvailable()) {
            Bukkit.getPluginManager().registerEvents(new IrcLinkListener(), this);
        }

        loadLibs(getConsoleUser());
    }

    public SinkTabCompleter getDefaultTabCompleter() {
        if (defaultCompleter == null) {
            defaultCompleter = new SinkTabCompleter();
        }
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
            if (Settings.GENERAL_LOG.getValue()) {
                Debug.getDebugLogFileWriter().close();
            }
        } catch (Exception ignored) {
        }
        instance = null;
        getLogger().info("Disabled.");
    }

    public Map<String, SinkCommand> getCommands() {
        return commands;
    }

    public boolean isIrcAvailable() {
        if (Bukkit.getPluginManager().getPlugin("SinkIRC") == null || ircExceptionOccured) {
            return false;
        }

        try {
            SinkIrcReflection.getMainChannel();
        } catch (Throwable e) {
            Debug.log(e);
            ircExceptionOccured = true;
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
        if (timer == null) {
            timer = new TpsTimer();
        }
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
        if (customDataFolder == null) {
            customDataFolder = new File(getDataFolder().getAbsolutePath().replace(pluginName, "SinkPlugins"));
        }

        return customDataFolder;
    }

    /**
     * Send Message to IRC via SinkIRC Plugin to the default channel.
     *
     * @param message Message to send
     */
    public boolean sendIrcMessage(@Nonnull String message) {
        Channel channel = SinkIrcReflection.getMainChannel();
        if (channel != null) {
            return sendIrcMessage(message, channel.getName());
        }
        Debug.log("channel == null!");
        return false;
    }

    /**
     * Send Message to IRC via SinkIRC Plugin.
     *
     * @param message Message to send
     * @param target Target user/channel, use null for default channel
     * @return true if successfully added to queue, false if exception happened or irc not available
     */
    public boolean sendIrcMessage(@Nonnull String message, @Nonnull String target) {
        if (!isIrcAvailable()) {
            return false;
        }
        try {
            SinkIrcReflection.addToQueue(message, target);
            return true;
        } catch (Throwable tr) {
            tr.printStackTrace();
            ircExceptionOccured = true;
            return false;
        }
    }

    /**
     * Get SinkPlugins Settings.
     *
     * @return Settings
     */
    @Deprecated
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
        if (throwExceptionIfNotFound && !user.hasPlayedBefore() && !user.getConfigurationFile().exists()) {
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
        throwExceptionIfNotFound = false;
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
        throwExceptionIfNotFound = false;

        if (partialName.equalsIgnoreCase("console")) {
            return getConsoleUser();
        }

        //Search for suffixes
        for (SinkUserProvider provider : getUserImplementations().values()) {
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
            if (throwExceptionIfNotFound) {
                throw new UserNotFoundException();
            }
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

        for (SinkUserProvider provider : getUserImplementations().values()) {
            String suffix = provider.getTabCompleterSuffix();
            if (suffix.equals("")) {
                continue;
            }
            if (name.endsWith(suffix)) {
                name = name.replaceFirst(StringUtil.stripRegex(suffix), "");
                SinkUser user = provider.getUserInstance(name);
                if (user != null) {
                    return user;
                }
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
        return getIngameUserProvider().getUserInstance(uuid);
    }

    /**
     * @param sender Sender of the User
     * @return {@link SinkUser} instance of the sender
     */
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
        Debug.logMethodCall(base.toString());

        if (base instanceof IrcCommandSender) {
            base = ((IrcCommandSender) base).getUser().getBase();
        }

        for (Class<?> implClass : getUserImplementations().keySet()) {
            if (implClass.isInstance(base)) {
                SinkUserProvider provider = getUserImplementations().get(implClass);

                if (provider instanceof IngameUserProvider && implClass == ProxiedPlayer.class) {
                    continue;
                }

                if (provider instanceof ProxiedIngameUserProvider && implClass != ProxiedPlayer.class) {
                    continue;
                }

                if (provider instanceof ProxiedUserProvider && implClass == ProxiedPlayer.class) {
                    continue;
                }

                if (provider instanceof IrcUserProvider) {
                    return ((IrcUserProvider) provider).getUserInstance(((User) base).getNick());
                }

                SinkUser tmp = getUserImplementations().get(implClass).getUserInstance(base);
                if (tmp != null) {
                    return tmp;
                }
            }
        }

        //Todo: add default implementation for not supported bases/CommandSenders
        return null;
    }

    /**
     * Register an own implementation of {@link SinkUser} for a {@link CommandSender}
     */
    public void registerUserImplementation(Class<?> base, SinkUserProvider provider) {
        Validate.notNull(base);
        Validate.notNull(provider);
        Debug.log("Registering user provider for base: " + base.getName() + ": " + provider.getClass().getName());
        getUserImplementations().put(base, provider);
    }

    private Map<Class<?>, SinkUserProvider> getUserImplementations() {
        if (userImplementations == null) {
            userImplementations = new ConcurrentHashMap<>();
        }

        return userImplementations;
    }

    /**
     * Get Version of SinkLibrary
     *
     * @return Version
     */
    public String getVersion() {
        return getInstance().getDescription().getVersion();
    }

    /**
     * Refresh a player's DisplayName
     *
     * @param player Player that needs to refresh DisplayName
     */
    public void onRefreshDisplayName(Player player) {
        if (!Settings.GENERAL_DISPLAYNAMES.getValue()) {
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
        return getIrcUserProvider().getUserInstance(nick);
    }

    public IrcUser getIrcUser(User user, String source) {
        return getIrcUserProvider().getUserInstance(user, source);
    }

    public void loadIrcUser(User user, String source) {
        getIrcUserProvider().loadUser(user, source);
    }

    public void unloadIrcUser(User user) {
        getIrcUserProvider().unloadUser(user);
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
        IngameUser user = getIngameUserProvider().getUserInstance(uuid);
        if (user == null) {
            return;
        }
        user.getConfiguration().save();
        getIngameUserProvider().unloadUser(user);
    }

    /**
     * Get the name of this plugin
     *
     * @return the name of this plugin
     */
    public String getPluginName() {
        return getInstance().getDescription().getName();
    }

    /**
     * @return Online players as Users
     * @deprecated Unstable implementation
     */
    @Unstable
    @Deprecated
    public Collection<IngameUser> getOnlineUsers() {
        Set<IngameUser> users = new HashSet<>();
        for (SinkUser user : getIngameUserProvider().getUserInstances()) {
            users.add((IngameUser) user);
        }
        return users;
    }

    /**
     * @return Online IRC users as Users
     */
    @Unstable
    public Collection<IrcUser> getOnlineIrcUsers() {
        return getIrcUserProvider().getUserInstances();
    }

    @Deprecated
    public Logger getCustomLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void registerCommand(@Nonnull String name, @Nonnull final SinkCommand impl) {
        Debug.logMethodCall(name, impl);
        final Plugin p = impl.getPlugin();
        Command cmd = Bukkit.getPluginCommand((p == null ? "" : p.getName() + ":") + name);
        if (!impl.getCommandOptions().isIrcOnly()) {
            if (cmd == null) {
                Debug.log("Bukkit Command instance not found: " + p.getName() + ":" + name + ", registering a custom one...");
                cmd = new NativeCommand(name, p, impl);
                getCommandMap().register(name, cmd);
            } else {
                Debug.log("Bukkit Command instance found: " + p.getName() + ":" + cmd.toString());
                ((PluginCommand) cmd).setExecutor(impl);
                ((PluginCommand) cmd).setTabCompleter(getDefaultTabCompleter());
                impl.setPlugin(((PluginIdentifiableCommand) cmd).getPlugin());
            }

            for (String alias : cmd.getAliases()) {
                // Register alias commands
                if (alias.equalsIgnoreCase(name)) {
                    continue;
                }
                commandAliases.put(alias.toLowerCase(), impl);
            }

            impl.setUsage(cmd.getUsage());
            impl.setPermission(cmd.getPermission());
        } else {
            Debug.log("Command is ircOnly. Skipping search for Bukkit Command instance");
        }

        commandAliases.put(name.toLowerCase(), impl);
        commands.put(name.toLowerCase(), impl);
    }

    private CommandMap getCommandMap() {
        try {
            Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterCommand(@Nonnull String name, @Nonnull Plugin plugin) {
        unregisterCommand(name, plugin, true);
    }

    public void unregisterCommand(@Nonnull String name, @Nonnull Plugin plugin, boolean unregisterBukkit) {
        Validate.notNull(name);
        Validate.notNull(plugin);

        name = name.toLowerCase();

        SinkCommand sinkCommand = commands.get(name);
        if (sinkCommand != null && sinkCommand.getCommandOptions().isIrcOnly()) {
            commands.remove(name);
            return;
        }

        PluginCommand cmd = Bukkit.getPluginCommand(plugin.getName() + ":" + name);
        if (cmd == null) {
            return;
        }

        for (String alias : cmd.getAliases()) // Register alias commands
        {
            alias = alias.toLowerCase();
            if (alias.equals(name)) {
                continue;
            }
            commandAliases.remove(alias);
        }

        commands.remove(name.toLowerCase());
        commandAliases.remove(name.toLowerCase());

        if (unregisterBukkit) {
            unregisterCommandBukkit(cmd);
        }
    }

    private Object getPrivateField(Object object, String field) throws SecurityException,
                                                                       NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    private void unregisterCommandBukkit(PluginCommand cmd) {
        try {
            Object result = getPrivateField(this.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (String alias : cmd.getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName())) {
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public SinkCommand getCustomCommand(String name) {
        SinkCommand cmd = commands.get(name.toLowerCase());
        if (cmd == null) {
            cmd = commandAliases.get(name.toLowerCase());
        }
        return cmd;
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
        registerCommand("sinkdebug", new SinkDebugCommand(this));
        registerCommand("sinkreload", new SinkReloadCommand(this));
        registerCommand("sinkversion", new SinkVersionCommand(this));
    }

    public boolean isSinkChatAvailable() {
        return Bukkit.getPluginManager().getPlugin("SinkChat") != null;
    }

    public ConsoleUser getConsoleUser() {
        return getConsoleUserProvider().getUserInstance(Bukkit.getConsoleSender());
    }

    private ConsoleUserProvider getConsoleUserProvider() {
        return consoleUserProvider;
    }

    private IngameUserProvider getIngameUserProvider() {
        return ingameUserProvider;
    }

    private IrcUserProvider getIrcUserProvider() {
        return ircUserProvider;
    }
}
