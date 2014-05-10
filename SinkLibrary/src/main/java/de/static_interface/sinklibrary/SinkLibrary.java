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

import de.static_interface.sinklibrary.commands.ScriptCommand;
import de.static_interface.sinklibrary.commands.SinkDebugCommand;
import de.static_interface.sinklibrary.commands.SinkReloadCommand;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import de.static_interface.sinklibrary.configuration.Settings;
import de.static_interface.sinklibrary.events.IRCSendMessageEvent;
import de.static_interface.sinklibrary.listener.DisplayNameListener;
import de.static_interface.sinklibrary.listener.IRCLinkListener;
import de.static_interface.sinklibrary.listener.PlayerConfigurationListener;
import de.static_interface.sinklibrary.listener.ScriptChatListener;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
public class SinkLibrary extends JavaPlugin
{

    public static List<String> tmpBannedPlayers;
    private static TPSTimer timer;
    private static Economy econ;
    private static Permission perm;
    private static Chat chat;
    private static File dataFolder;
    private static String version;
    private static Settings settings;
    private static List<JavaPlugin> registeredPlugins;
    private static HashMap<UUID, User> onlineUsers;
    private static PluginDescriptionFile description;
    private static boolean economyAvailable = true;
    private static boolean permissionsAvailable = true;
    private static boolean chatAvailable = true;

    public static boolean initialized = false;
    public static boolean sinkChatAvailable;

    static Logger logger;


    public void onEnable()
    {
        // Init variables first to prevent NullPointerExceptions when other plugins try to access them
        version = getDescription().getVersion();
        tmpBannedPlayers = new ArrayList<>();
        registeredPlugins = new ArrayList<>();
        onlineUsers = new HashMap<>();
        description = getDescription();
        dataFolder = getDataFolder();
        logger = new Logger();
        timer = new TPSTimer();

        // Init language
        LanguageConfiguration languageConfiguration = new LanguageConfiguration();
        languageConfiguration.load();

        // Init Settings
        settings = new Settings();
        settings.load();

        logger.log(Level.INFO, "Loading...");
        // Check optional dependencies
        if ( !isVaultAvailable() )
        {
            SinkLibrary.getCustomLogger().warning("Vault Plugin not found. Disabling economy and some permission features.");
            permissionsAvailable = false;
            economyAvailable = false;
            chatAvailable = false;
        }
        else
        {
            if ( !setupChat() )
            {
                chatAvailable = false;
            }

            if ( !setupEcononmy() )
            {
                SinkLibrary.getCustomLogger().warning("Economy Plugin not found. Disabling economy features.");
                economyAvailable = false;
            }
            if ( !setupPermissions() )
            {
                SinkLibrary.getCustomLogger().warning("Permissions Plugin not found. Disabling permissions features.");
                permissionsAvailable = false;
            }

            for ( Player p : Bukkit.getOnlinePlayers() )
            {
                refreshDisplayName(p);
            }
        }

        if ( !getCustomDataFolder().exists() )
        {
            try
            {
                getCustomDataFolder().mkdirs();
            }
            catch ( Exception e )
            {
                Bukkit.getLogger().log(Level.SEVERE, "Couldn't create Data Folder!", e); // Log via Bukkits Logger, because Log File doesnt exists
            }
        }

        if ( chatAvailable && economyAvailable && permissionsAvailable )
        {
            SinkLibrary.getCustomLogger().info("Successfully hooked into permissions, economy and chat.");
        }

        // Register Listeners and Commands
        if ( !initialized )
        {
            registerListeners();
            registerCommands();
            initialized = true;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, timer, 1000, 50);

        // Check for updates
        update();

        // Init players (reload etc)
        for ( Player p : Bukkit.getOnlinePlayers() )
        {
            refreshDisplayName(p);
            loadUser(p.getUniqueId());
        }

        for ( Plugin plugin : Bukkit.getPluginManager().getPlugins() )
        {
            if ( plugin.getName().equalsIgnoreCase("SinkChat") )
            {
                sinkChatAvailable = true;
                SinkLibrary.getCustomLogger().debug("SinkChat found. Skipping registration of IRCLinkListener");
                break;
            }
        }
        if ( !sinkChatAvailable )
        {
            SinkLibrary.getCustomLogger().debug("SinkChat not found. Registering IRCLinkListener");
            Bukkit.getPluginManager().registerEvents(new IRCLinkListener(), this);
        }
    }

    public void onDisable()
    {
        SinkLibrary.getCustomLogger().log(Level.INFO, "Disabling...");
        SinkLibrary.getCustomLogger().info("Saving players...");
        for ( Player p : Bukkit.getOnlinePlayers() )
        {
            User user = SinkLibrary.loadUser(p);
            if ( user.getPlayerConfiguration().exists() )
            {
                user.getPlayerConfiguration().save();
            }
        }
        SinkLibrary.getCustomLogger().debug("Disabled.");
        try
        {
            logger.getFileWriter().close();
        }
        catch ( Exception ignored ) { }
        System.gc();
    }

    private void update()
    {
        Updater updater = new Updater(settings.getUpdateType());
        String permission = "sinklibrary.updatenotification";
        String versionType = ' ' + updater.getLatestGameVersion() + ' ';
        if ( versionType.equalsIgnoreCase("release") ) versionType = " ";
        if ( updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE )
        {
            BukkitUtil.broadcast(Updater.CONSOLEPREFIX + "A new" + versionType + "update is available: " + updater.getLatestName(), permission, false);
        }
        else if ( updater.getResult() == Updater.UpdateResult.NO_UPDATE )
        {
            SinkLibrary.getCustomLogger().info(Updater.CONSOLEPREFIX + "No new updates found...");
        }
        else if ( updater.getResult() == Updater.UpdateResult.SUCCESS )
        {
            SinkLibrary.getCustomLogger().info(Updater.CONSOLEPREFIX + "Updates downloaded, please restart or reload the server to take effect...");
        }
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if ( chatProvider != null )
        {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    private boolean setupEcononmy()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if ( rsp == null )
        {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if ( permissionProvider != null )
        {
            perm = permissionProvider.getProvider();
        }
        return (perm != null);
    }

    private void registerListeners()
    {
        Bukkit.getPluginManager().registerEvents(new PlayerConfigurationListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayNameListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScriptChatListener(this), this);
    }

    private void registerCommands()
    {
        getCommand("sinkdebug").setExecutor(new SinkDebugCommand());
        getCommand("sinkreload").setExecutor(new SinkReloadCommand());
        getCommand("script").setExecutor(new ScriptCommand(this));
    }

    /**
     * @return True if chat is available
     */
    public static boolean isChatAvailable()
    {
        return chatAvailable;
    }

    /**
     * @return True if economy is available
     */
    public static boolean isEconomyAvailable()
    {
        return economyAvailable;
    }

    /**
     * @return True if permissions are available
     */
    public static boolean isPermissionsAvailable()
    {
        return permissionsAvailable;
    }

    /**
     * @return True if Vault available
     */
    public static boolean isVaultAvailable()
    {
        return Bukkit.getPluginManager().getPlugin("Vault") != null;
    }

    /**
     * Get SinkTimer
     *
     * @return SinkTimer
     */
    public static TPSTimer getSinkTimer()
    {
        return timer;
    }


    /**
     * Get Chat instance
     *
     * @return Chat instance
     * @see Chat
     */
    public static Chat getChat()
    {
        return chat;
    }

    /**
     * Get Economy instance from Vault
     *
     * @return Economy instace
     * @see Economy
     */
    public static Economy getEconomy()
    {
        return econ;
    }

    /**
     * Get Permissions instance
     *
     * @return Permissions
     * @see Permission
     */
    public static Permission getPermissions()
    {
        return perm;
    }

    /**
     * Get custom data folder
     *
     * @return Data Folder of Sink Plugins (.../plugins/SinkPlugins/)
     */
    public static File getCustomDataFolder()
    {
        String pluginName = getPluginName();
        if ( pluginName == null ) throw new NullPointerException("getPluginName() returned null");
        if ( dataFolder == null ) throw new NullPointerException("dataFolder is null");
        return new File(dataFolder.getAbsolutePath().replace(pluginName, "SinkPlugins"));
    }

    /**
     * Send Message to IRC via SinkIRC Plugin.
     *
     * @param message Message to send
     */
    public static boolean sendIRCMessage(String message)
    {
        boolean ircAvailable = false;
        for ( Plugin plugin : registeredPlugins )
        {
            if ( plugin.getName().equals("SinkIRC") )
            {
                ircAvailable = true;
                break;
            }
        }
        if ( !ircAvailable ) return false;
        SinkLibrary.getCustomLogger().debug("Firing new IRCSendMessageEvent(\"" + message + "\")");
        IRCSendMessageEvent event = new IRCSendMessageEvent(message);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    /**
     * Add Temp Ban to Player. Will be cleared on next server restart or plugin reload.
     *
     * @param username Player to ban
     */
    public static void addTempBan(String username)
    {
        tmpBannedPlayers.add(username);
    }

    /**
     * Remove Temp Ban
     *
     * @param username Player to unban
     */
    public static void removeTempBan(String username)
    {
        tmpBannedPlayers.remove(username);
    }

    /**
     * Get SinkPlugins Settings.
     *
     * @return Settings
     */
    public static Settings getSettings()
    {
        return settings;
    }

    /**
     * Register a plugin
     *
     * @param plugin Class which extends {@link org.bukkit.plugin.java.JavaPlugin}
     */
    public static void registerPlugin(JavaPlugin plugin)
    {
        registeredPlugins.add(plugin);
    }

    /**
     * @param player Player
     * @return User instance of player
     */
    public static User loadUser(Player player)
    {
        return loadUser(player.getUniqueId());
    }

    /**
     * @param playerName Name of Player
     * @return User instance
     * @deprecated use {@link #loadUser(java.util.UUID)} instead
     */
    @Deprecated
    public static User loadUser(String playerName)
    {
        UUID uuid = BukkitUtil.getUUIDByName(playerName);

        return loadUser(uuid);
    }

    public static User loadUser(UUID uuid)
    {
        Player player = Bukkit.getPlayer(uuid);
        if ( player == null || !player.isOnline() ) return new User(uuid);
        User user = onlineUsers.get(uuid);
        if ( user == null || (user.getPlayer() != null && !user.getPlayer().getUniqueId().equals(uuid)) )
        {
            user = new User(player.getUniqueId());
            onlineUsers.put(player.getUniqueId(), user);
        }
        return user;
    }

    /**
     * @param sender Command Sender
     * @return User instance
     */
    public static User loadUser(CommandSender sender)
    {
        if ( !(sender instanceof Player) ) return new User(sender);

        Player player = (Player) sender;
        return loadUser(player.getUniqueId());
    }

    /**
     * Get Version of SinkLibrary
     *
     * @return Version
     */
    public static String getVersion()
    {
        return version;
    }

    /**
     * Refresh a player's DisplayName
     *
     * @param player Player that needs to refresh DisplayName
     */
    public static void refreshDisplayName(Player player)
    {
        if ( !settings.isDisplayNamesEnabled() ) return;

        User user = loadUser(player);
        PlayerConfiguration config = user.getPlayerConfiguration();

        if ( !config.exists() )
        {
            return;
        }

        String displayName = user.getDisplayName();

        if ( displayName == null || displayName.equals("null") || displayName.isEmpty() || !config.getHasDisplayName() )
        {
            config.setDisplayName(user.getDefaultDisplayName());
            player.setDisplayName(user.getDefaultDisplayName());
            player.setPlayerListName(displayName.substring(0, 16));
            config.setDisplayName(user.getDefaultDisplayName());
            config.setHasDisplayName(false);
            return;
        }

        if ( displayName.equals(user.getDefaultDisplayName()) )
        {
            config.setDisplayName("");
            config.setHasDisplayName(false);
        }
        player.setDisplayName(displayName);
        player.setPlayerListName(displayName.substring(0, 15));
    }

    /**
     * @return Plugins which are registered through {@link SinkLibrary#registerPlugin(org.bukkit.plugin.java.JavaPlugin)}
     */
    public static List<JavaPlugin> getRegisteredPlugins()
    {
        return registeredPlugins;
    }

    /**
     * Unload an User
     * Do not call this, its handled internally
     * @param player Player which needs to be unloaded
     */
    public static void unloadUser(Player player)
    {
        User user = loadUser(player);
        user.getPlayerConfiguration().save();
        UUID uuid = user.getPlayer().getUniqueId();
        if ( uuid != null )
        {
            onlineUsers.remove(uuid);
        }
    }

    /**
     * Get the name of this plugin
     *
     * @return the name of this plugin
     */
    public static String getPluginName()
    {
        return description.getName();
    }

    /**
     * Get all online players
     *
     * @return Online players as Users
     */
    public static Collection<User> getOnlineUsers()
    {
        return onlineUsers.values();
    }

    public static Logger getCustomLogger()
    {
        return logger;
    }

    public static User getUserByUniqueId(UUID uuid)
    {
        for ( User user : getOnlineUsers() )
        {
            if ( user.getUniqueId().equals(uuid) ) return user;
        }
        return null;
    }
}
