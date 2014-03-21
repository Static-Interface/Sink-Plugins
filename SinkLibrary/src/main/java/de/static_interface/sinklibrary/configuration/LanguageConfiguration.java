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

package de.static_interface.sinklibrary.configuration;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.Util;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings({"OverlyBroadCatchBlock", "InstanceMethodNamingConvention", "BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention", "StaticMethodNamingConvention"})
public class LanguageConfiguration
{
    public static final int REQUIRED_VERSION = 1;

    private static YamlConfiguration yamlConfiguration = new YamlConfiguration();
    private static HashMap<String, Object> defaultValues;
    private static File yamlFile;
    private static boolean busy;

    /**
     * Initialize
     */
    public static void load()
    {
        defaultValues = new HashMap<>();
        create();
    }

    /**
     * Create Configuration
     */
    public static void create()
    {
        try
        {
            yamlFile = new File(SinkLibrary.getCustomDataFolder(), "Language.yml");

            boolean createNewConfiguration = !exists();

            if ( createNewConfiguration )
            {
                SinkLibrary.getCustomLogger().log(Level.INFO, "Creating new configuration: " + yamlFile);
            }

            if ( createNewConfiguration && !yamlFile.createNewFile() )
            {
                SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't create configuration: " + yamlFile);
                return;
            }

            yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(yamlFile);

            yamlConfiguration.options().header(String.format("This configuration saves and loads variables for language.%n%%s will be replaced with the value by the plugin."));

            addDefault("Main.ConfigVersion", REQUIRED_VERSION);
            addDefault("General.NotOnline", "&c%s is not online!");
            addDefault("General.ConsoleNotAvailable", "&cThis command is only ingame available");
            addDefault("General.CommandMisused.Arguments.TooFew", "You have missed arguments");

            addDefault("SinkChat.Commands.Nick.OtherChanged", "%s's name is now %s!");
            addDefault("SinkChat.Commands.Nick.SelfChanged", "Your name is now %s!");
            addDefault("SinkChat.Commands.Nick.IllegalNickname", "Illegal nickname!");
            addDefault("SinkChat.Commands.Nick.TooLong", "Nickname is too long!");
            addDefault("SinkChat.Commands.Nick.Used", "Nickname is already used by someone other!");

            addDefault("SinkChat.Commands.Channel.PlayerJoins", "You joined the %s channel.");
            addDefault("SinkChat.Commands.Channel.PlayerLeaves", "You left the %s channel.");
            addDefault("SinkChat.Commands.Channel.NoChannelGiven", "You must write the name of the channel!");
            addDefault("SinkChat.Commands.Channel.ChannelUnknown", "%s is an unknown channel.");
            addDefault("SinkChat.Commands.Channel.List", "These channels are available: %s");
            addDefault("SinkChat.Commands.Channel.Part", "You have the following channels enabled:");
            addDefault("SinkChat.Commands.Channel.Help", "These commands are available:");

            addDefault("SinkChat.Commands.Spy.Enabled", "&aSpy chat has been enabled!");
            addDefault("SinkChat.Commands.Spy.AlreadyEnabled", "&cSpy chat has been already enabled!");

            addDefault("SinkChat.Commands.Spy.Disabled", "&cSpy chat has been disabled!");
            addDefault("SinkChat.Commands.Spy.AlreadyDisabled", "&cSpy chat has been already disabled!");

            addDefault("SinkChat.Channels.Help", "Help");
            addDefault("SinkChat.Channels.Shout", "Shout");
            addDefault("SinkChat.Channels.Trade", "Trade");

            addDefault("SinkChat.Channels.Private.InvitedToChat", "%s invited you to a chat. Use \"%s <text>\" to chat with him!");
            addDefault("SinkChat.Channels.Private.HasInvitedToChat", "You have invited %s to chat.");
            addDefault("SinkChat.Channels.Private.HasInvitedToChat.ErrorAlreadyInChat", "%s already takes part in that conversation !");
            addDefault("SinkChat.Channels.Private.LeftChat", "You have left the private conversation %s!");
            addDefault("SinkChat.Channels.Private.PlayerLeftCon", "%s has left conversation %s");
            addDefault("SinkChat.Channels.Private.PlayerKicked", "%s has been kicked: %s");
            addDefault("SinkChat.Channels.Private.PlayerKicked.ErrorNotInChannel", "%s is not in that conversation!");
            addDefault("SinkChat.Channels.Private.Invites", "&2Invited players: %s");
            addDefault("SinkChat.Channels.Private.DoesntExists", "&cCoudln't find channel: \"%s\"");
            addDefault("SinkChat.Channels.Private.WrongUsage", "&cWrong Usage!");
            addDefault("SinkChat.Channels.Private.Created", "&2Created successfully channel: \"%s\"!");
            addDefault("SinkChat.Channels.Private.IdentifierUsed", "&cIdentifier is already used!");
            addDefault("SinkChat.Channels.Private.AlreadyExstis", "&6Channel already exists! Use a diffrent identifier.");
            addDefault("SinkChat.Channels.Private.Renamed", "&bChannel renamed to: %s");
            addDefault("SinkChat.Channels.Private.Users", "Users in channel \"%s\":");
            addDefault("SinkChat.Channels.Private.Channels", "&bAvailable channels: %s");

            addDefault("SinkChat.Prefix.Channel", "&a[Channel]");
            addDefault("SinkChat.Prefix.Nick", "&2[Nick]");
            addDefault("SinkChat.Prefix.Spy", "&7[Spy]");
            addDefault("SinkChat.Prefix.Local", "&7[Local]");

            addDefault("Permissions.General", "&4You don't have permissions to do that.");
            addDefault("Permissions.SinkChat.Channel", "&4You may not use the %s channel.");
            addDefault("Permissions.SinkChat.Nick.Other", "&4You may not change the nickname of other players!");

            addDefault("SinkAntiSpam.Prefix", "&4[SinkAntiSpam]");
            addDefault("SinkAntiSpam.Warn", "&l&n%s&r has been automatically warned: %s");
            addDefault("SinkAntiSpam.Reasons.BlacklistedWord", "&cTried to write a blacklisted word: &9&l&n%s");
            addDefault("SinkAntiSpam.Reasons.IP", "&cTried to write IP: &9&l&n%s");
            addDefault("SinkAntiSpam.Reasons.Domain", "&cTried to write a not whitelisted domain: &9&l&n%s");
            addDefault("SinkAntiSpam.ReplaceDomain", "google.com");
            addDefault("SinkAntiSpam.ReplaceIP", "127.0.0.1");
            
            addDefault("SinkDuty.Time", "You have the following duty time: %s");
            addDefault("SinkDuty.Time.Others", "%s has the following duty time: %s");
            addDefault("SinkDuty.Time.Start", "Duty mode started.");
            addDefault("SinkDuty.Time.Finish", "Duty mode finished.");
            addDefault("SinkDuty.Time.NotInDuty", "You are not in duty mode !");
            addDefault("SinkDuty.Reload.ForceLeave", "You have been forced to leave duty mode because of a reload.");
            save();
        }
        catch ( IOException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't create configuration file: " + yamlFile.getName());
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Exception occurred: ", e);
        }
        catch ( InvalidConfigurationException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Invalid configuration file detected: " + yamlFile);
            SinkLibrary.getCustomLogger().log(Level.SEVERE, e.getMessage());
            recreate();
        }
    }

    /**
     * Save configuration
     */
    public static void save()
    {
        if ( yamlFile == null )
        {
            return;
        }
        if ( !exists() )
        {
            return;
        }

        try
        {
            yamlConfiguration.save(yamlFile);
        }
        catch ( IOException ignored )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't save configuration file: " + yamlFile + '!');
        }
    }

    /**
     * Get Value from path
     *
     * @param path Path to value
     * @return Value of Path
     */
    public static Object get(String path)
    {
        try
        {
            Object value = yamlConfiguration.get(path);
            if ( value == null || value == "" )
            {
                throw new NullPointerException("Path returned null!");
            }
            return value;
        }
        catch ( Exception e )
        {
            SinkLibrary.getCustomLogger().log(Level.WARNING, yamlFile + ": Couldn't load value from path: " + path + ". Reason: " + e.getMessage() + " Using default value.");
            return getDefault(path);
        }
    }

    /**
     * @return True if file exists
     */
    public static boolean exists()
    {
        return yamlFile.exists();
    }

    /**
     * Delete configuration
     */
    public static void delete()
    {
        yamlFile.delete();
    }

    /**
     * Backup configuration
     */
    public static void backup() throws IOException
    {
        Util.backupFile(yamlFile, true);
    }

    /**
     * Get default values
     *
     * @return Default Values
     */
    public static Map<String, Object> getDefaults()
    {
        return defaultValues;
    }

    /**
     * Get language as String from key
     *
     * @param path Path to language variable
     * @return Language String
     */
    public static String _(String path)
    {
        String value;
        try
        {
            String rawValue = yamlConfiguration.getRoot().getString(path);
            if ( rawValue == null || rawValue.equals("null") )
            {
                throw new NullPointerException("Path returned null.");
            }
            value = ChatColor.translateAlternateColorCodes('&', rawValue);
        }
        catch ( Exception e )
        {
            SinkLibrary.getCustomLogger().log(Level.WARNING, yamlFile.getName() + ": Couldn't load value from path: " + path + ". Reason: " + e.getMessage() + " Using default value.");
            value = (String) getDefault(path);
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    /**
     * Get Default
     *
     * @param path Path to default value
     * @return Default value
     */
    public static Object getDefault(String path)
    {
        return getDefaults().get(path);
    }

    /**
     * Add default value
     *
     * @param path  Path to default value
     * @param value Value of Path
     */
    public static void addDefault(String path, Object value)
    {
        if ( getDefaults() == null )
        {
            throw new RuntimeException("defaultValues are null! Couldn't add " + value + " to path: " + path);
        }

        if ( !yamlConfiguration.isSet(path) || yamlConfiguration.get(path) == null )
        {
            yamlConfiguration.set(path, value);
            save();
        }
        getDefaults().put(path, value);
    }

    /**
     * Get File
     *
     * @return Configuration File
     */
    public static File getFile()
    {
        return yamlFile;
    }

    /**
     * Recreate configuration
     */
    public static void recreate()
    {
        if ( busy ) return;

        busy = true;

        SinkLibrary.getCustomLogger().log(Level.WARNING, "Recreating Configuration: " + yamlFile);
        try
        {
            backup();
        }
        catch ( IOException e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Coudln't backup configuration: " + yamlFile, e);
            return;
        }
        delete();
        create();

        busy = false;
    }

    /**
     * Reload Config
     */
    public static void reload()
    {
        if ( !exists() ) return;
        load();
    }
}
