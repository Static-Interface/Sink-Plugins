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

package de.static_interface.sinklibrary.configuration;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;

import java.io.File;

import javax.annotation.Nullable;

@SuppressWarnings(
        {"OverlyBroadCatchBlock", "InstanceMethodNamingConvention", "BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention",
         "StaticMethodNamingConvention"})
public class LanguageConfiguration extends Configuration {

    public static final int REQUIRED_VERSION = 1;
    private static LanguageConfiguration instance;

    public LanguageConfiguration() {
        super(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Language.yml"));
    }

    public static LanguageConfiguration getInstance() {
        if (instance == null) {
            instance = new LanguageConfiguration();
            instance.init();
        }
        return instance;
    }

    /**
     * Get language as String from key
     *
     * @param path Path to language variable
     * @return Language String
     */
    public static String m(String path) {
        return m(path, null);
    }

    /**
     * Get language as String from key
     *
     * @param path Path to language variable
     * @param paramValues Varargs for {@link StringUtil#format(String, Object...)}
     * @return Language String
     */
    public static String m(String path, @Nullable Object... paramValues) {
        String s = (String) getInstance().get(path);
        if (paramValues != null) {
            s = StringUtil.format(s, paramValues);
        }
        s = s.replace("\\n", System.lineSeparator());
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void addDefaults() {
        addDefault("Main.ConfigVersion", REQUIRED_VERSION);
        addDefault("General.NotOnline", "&c{0} is not online!");
        addDefault("General.UserNotFound", "&cUser not found: {0}");
        addDefault("General.ConsoleNotAvailable", "&cThis command is only ingame available");
        addDefault("General.Banned", "&4Banned:&c ");
        addDefault("General.CommandMisused.Arguments.TooFew", "&4Too few arguments!"); // Deprecated
        addDefault("General.TooFewArguments", "&4Too few arguments!");

        addDefault("General.TimeLeft", "Time Left: {0}");
        addDefault("General.Me", "Me");

        addDefault("SinkChat.Commands.Nick.OtherChanged", "{0}'s name is now {1}!");
        addDefault("SinkChat.Commands.Nick.SelfChanged", "Your name is now {0}!");
        addDefault("SinkChat.Commands.Nick.IllegalNickname", "Illegal nickname!");
        addDefault("SinkChat.Commands.Nick.TooLong", "Nickname is too long!");
        addDefault("SinkChat.Commands.Nick.Used", "Nickname is already used by someone other!");

        addDefault("SinkChat.Commands.Channel.PlayerJoins", "You joined the {0} channel.");
        addDefault("SinkChat.Commands.Channel.PlayerLeaves", "You left the {0} channel.");
        addDefault("SinkChat.Commands.Channel.NoChannelGiven", "You must write the name of the channel!");
        addDefault("SinkChat.Commands.Channel.ChannelUnknown", "{0} is an unknown channel.");
        addDefault("SinkChat.Commands.Channel.Enabled", "&aenabled");
        addDefault("SinkChat.Commands.Channel.Disabled", "&cdisabled");
        addDefault("SinkChat.Commands.Channel.ChannelAlreadyExists", "The channel {0} already exists.");
        addDefault("SinkChat.Commands.Channel.SuccessfullyCreated", "The channel {0} was successfully created.");
        addDefault("SinkChat.Commands.Channel.SuccessfullyDeleted", "The channel {0} was successfully deleted.");
        addDefault("SinkChat.Commands.Channel.List", "These channels are available: {0}");
        addDefault("SinkChat.Commands.Channel.Part", "You have the following channels enabled:");
        addDefault("SinkChat.Commands.Channel.Help", "These commands are available:");

        addDefault("SinkChat.Commands.Spy.Enabled", "&aSpy chat has been enabled!");
        addDefault("SinkChat.Commands.Spy.AlreadyEnabled", "&cSpy chat has already been enabled!");

        addDefault("SinkChat.Commands.Spy.Disabled", "&4Spy chat has been disabled!");
        addDefault("SinkChat.Commands.Spy.AlreadyDisabled", "&cSpy chat has already been disabled!");

        /*
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
        addDefault("SinkChat.Channels.Private.AlreadyExstis", "&6Channel already exists! Use a different identifier.");
        addDefault("SinkChat.Channels.Private.Renamed", "&bChannel renamed to: %s");
        addDefault("SinkChat.Channels.Private.Users", "Users in channel \"%s\":");
        addDefault("SinkChat.Channels.Private.Channels", "&bAvailable channels: %s");
        */

        addDefault("SinkChat.Prefix.Spy", "&7[Spy]");
        addDefault("SinkChat.Prefix.Town", "&7[&6{0}&7]");
        addDefault("SinkChat.Prefix.Local", "&7Local");
        addDefault("SinkChat.Prefix.Channel", "&7[{0}]");
        addDefault("SinkChat.Prefix.Nation", "&7[&6{0}&7]");

        addDefault("SinkChat.DisabledChannel", "&4This channel has been disabled");
        addDefault("SinkChat.DeletedChannel", "The channel %{0} has been deleted.");
        addDefault("SinkChat.AlreadyDisabled", "&4This channel has already been disabled.");
        addDefault("SinkChat.EnabledChannel", "&4This channel has been enabled.");
        addDefault("SinkChat.AlreadyEnabled", "&4This channel has already been enabled.");
        addDefault("SinkChat.NoChannelJoined", "&4You don't take part in any channels.");
        addDefault("SinkChat.Towny.NotInTown", "&4Error:&c You are not a resident of any town");
        addDefault("SinkChat.Towny.NotInNation", "&4Error:&c You or your town is not a member any nation");
        addDefault("SinkChat.Towny.NoArguments", "&4Error:&c You need to specify a message!");

        addDefault("Permissions.General", "&4You don't have permissions to do that.");
        addDefault("Permissions.SinkChat.Nick.Other", "&4You may not change the nickname of other players!");

        addDefault("SinkAntiSpam.Prefix", "&4[SinkAntiSpam]");
        addDefault("SinkAntiSpam.Warn", "{0}&4 has been warned by {1}: {2} ({3}/{4})");
        addDefault("SinkAntiSpam.Reasons.BlacklistedWord", "&cTried to write a blacklisted word: {0}");
        addDefault("SinkAntiSpam.Reasons.IP", "&cTried to write IP: &9&l&n{0}");
        addDefault("SinkAntiSpam.Reasons.Domain", "&cTried to write a not whitelisted domain: &9&l&n{0}");
        addDefault("SinkAntiSpam.ReplaceDomain", "google.com");
        addDefault("SinkAntiSpam.ReplaceIP", "127.0.0.1");
        addDefault("SinkAntiSpam.WarnSelf", "&4You can't warn yourself!");
        addDefault("SinkAntiSpam.AutoBan", "&4You've been auto banned by SinkAntiSpam");
        addDefault("SinkAntiSpam.TooManyWarnings", "&4Too many warnings!");
        addDefault("SinkAntiSpam.RepeatingMessage", "&4Please don't spam the same message!");
        addDefault("SinkAntiSpam.SpamMessage", "&4Please wait {0} ms before sending a message again");

        addDefault("TimeUnit.Years", "Year(s)");
        addDefault("TimeUnit.Months", "Month(s)");
        addDefault("TimeUnit.Weeks", "Week(s)");
        addDefault("TimeUnit.Days", "Day(s)");
        addDefault("TimeUnit.Hours", "Hour(s)");
        addDefault("TimeUnit.Minutes", "Minute(s)");
        addDefault("TimeUnit.Seconds", "Second(s)");

        /*
        addDefault("SinkDuty.Time", "You have the following duty time: %s");
        addDefault("SinkDuty.Time.Others", "%s has the following duty time: %s");
        addDefault("SinkDuty.Time.Start", "Duty mode started.");
        addDefault("SinkDuty.Time.Finish", "Duty mode finished.");
        addDefault("SinkDuty.Time.NotInDuty", "You are not in duty mode !");
        addDefault("SinkDuty.Reload.ForceLeave", "You have been forced to leave duty mode because of a reload.");
        */
    }
}
