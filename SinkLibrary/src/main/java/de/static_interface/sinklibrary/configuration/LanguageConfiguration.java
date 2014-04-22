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
import org.bukkit.ChatColor;

import java.io.File;

@SuppressWarnings({"OverlyBroadCatchBlock", "InstanceMethodNamingConvention", "BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention", "StaticMethodNamingConvention"})
public class LanguageConfiguration extends ConfigurationBase
{
    public static final int REQUIRED_VERSION = 1;
    private static LanguageConfiguration instance;

    public LanguageConfiguration()
    {
        super(new File(SinkLibrary.getCustomDataFolder(), "Language.yml"));
    }

    public static LanguageConfiguration getInstance()
    {
        if ( instance == null )
        {
            instance = new LanguageConfiguration();
        }
        return instance;
    }

    @Override
    public void addDefaults()
    {
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

        addDefault("SinkChat.Commands.Spy.Disabled", "&4Spy chat has been disabled!");
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
    }

    /**
     * Get language as String from key
     *
     * @param path Path to language variable
     * @return Language String
     */
    public static String _(String path)
    {
        return ChatColor.translateAlternateColorCodes('&', (String) getInstance().get(path));
    }
}
