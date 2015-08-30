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
import de.static_interface.sinklibrary.Updater;
import de.static_interface.sinklibrary.api.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Settings extends Configuration {

    public static final int REQUIRED_VERSION = 1;

    public Settings() {
        super(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Settings.yml"), true);
    }

    @Override
    public void addDefaults() {
        addDefault("Main.ConfigVersion", REQUIRED_VERSION);

        addDefault("General.DisplayNamesEnabled", false, "Broken");
        addDefault("General.EnableDebug", false, "Provides more information, useful for bug reports etc");
        addDefault("General.EnableLog", true, "Log debug to Debug.log (useful for debugging)?");

        addDefault("Updater.Enabled", true, "Enable Updater");
        addDefault("Updater.UpdateType", "default");

        addDefault("SinkAntiSpam.BlacklistedWordsCheck.Enabled", true);

        List<String> defaultBlackList = new ArrayList<>();
        defaultBlackList.add("BlacklistedWord");
        defaultBlackList.add("BlackListedWord2");
        addDefault("SinkAntiSpam.BlacklistedWordsCheck.Words", defaultBlackList);

        addDefault("SinkAntiSpam.WhitelistedDomainsCheck.Enabled", true);

        List<String> defaultDomainWiteList = new ArrayList<>();
        defaultDomainWiteList.add("google.com");
        defaultDomainWiteList.add("youtube.com");
        defaultDomainWiteList.add("yourhomepagehere.com");
        addDefault("SinkAntiSpam.WhitelistedDomainsCheck.Domains", defaultDomainWiteList);

        addDefault("SinkAntiSpam.IPCheck.Enabled", true);

        List<String> defaultExcludedCommands = new ArrayList<>();
        defaultExcludedCommands.add("msg");
        defaultExcludedCommands.add("tell");
        defaultExcludedCommands.add("m");
        defaultExcludedCommands.add("whisper");
        defaultExcludedCommands.add("t");

        addDefault("SinkAntiSpam.ExcludedCommands.Commands", defaultExcludedCommands);

        addDefault("SinkAntiSpam.Points.Domain", 5);
        addDefault("SinkAntiSpam.Points.IP", 5);
        addDefault("SinkAntiSpam.Points.Blacklist", 5);

        addDefault("SinkChat.LocalChatRange", 50);
        addDefault("SinkChat.DefaultChatFormat", "&7{CHANNEL} [{RANK}] {DISPLAYNAME}&7:&f {MESSAGE}");

        addDefault("SinkIRC.Username", "SinkIRCBot", "IRC Bot Nickname");
        addDefault("SinkIRC.Server.Address", "irc.example.com");
        addDefault("SinkIRC.Server.PasswordEnabled", false);
        addDefault("SinkIRC.Server.Password", "", "Server password, leave empty if there no password");
        addDefault("SinkIRC.Server.Port", 6667, "IRC Server port");
        addDefault("SinkIRC.Channel", "#ServerName", "IRC Channel");
        addDefault("SinkIRC.Authentification.Enabled", false, "Enable Authentification");
        addDefault("SinkIRC.Authentification.AuthBot", "NickServ", "Name of the authbot");
        addDefault("SinkIRC.Authentification.AuthMessage", "identify NickServPasswordHere",
                   "Message you send for identification with /msg <AuthBot> <message>. If you e.g. NickServ, use identify <password>");
        addDefault("SinkIRC.JoinLeaveMaxUsers", 20, "Max users until join leaves won't show in IRC. Use 0 to disable");
        addDefault("SinkCommands.Message.MessageReceived", "&7[{TARGETIRCPREFIX} {DISPLAYNAME} &7-> &6Me&7]&r {USERMESSAGE}");
        addDefault("SinkCommands.Message.MessageSend", "&7[&6Me &7-> {TARGETIRCPREFIX} {TARGETDISPLAYNAME}&7]&r {USERMESSAGE}");
    }

    public Updater.UpdateType getUpdateType() {
        switch (((String) get("Updater.UpdateType")).toLowerCase()) {
            case "default":
            case "download":
                return Updater.UpdateType.DEFAULT;
            case "no_download":
            case "no_version_check":
            case "off":
            case "disabled":
                return Updater.UpdateType.NO_VERSION_CHECK;
            default:
                return Updater.UpdateType.NO_DOWNLOAD;
        }
    }

    public boolean isUpdaterEnabled() {
        return (boolean) get("Updater.Enabled");
    }

    public boolean isDisplayNamesEnabled() {
        return (boolean) get("General.DisplayNamesEnabled"); //TODO: Fix this
        //return false;
    }

    public List<String> getBlackListedWords() {
        return getStringList("SinkAntiSpam.BlacklistedWordsCheck.Words");
    }

    public List<String> getWhitelistedWords() {
        return getStringList("SinkAntiSpam.WhitelistedDomainsCheck.Domains");
    }

    public List<String> getExcludedCommands() {
        return getStringList("SinkAntiSpam.ExcludedCommands.Commands");
    }

    public List<String> getStringList(String path) {
        try {
            List<String> value = yamlConfiguration.getStringList(path);
            if (value == null) {
                throw new NullPointerException("Path returned null!");
            }
            return value;
        } catch (Exception e) {
            SinkLibrary.getInstance().getLogger()
                    .log(Level.WARNING, yamlFile + ": Couldn't load value for path: " + path, e);
            return new ArrayList<>();
        }
    }

    public boolean isBlacklistedWordsEnabled() {
        return (boolean) get("SinkAntiSpam.BlacklistedWordsCheck.Enabled");
    }

    public boolean isIPCheckEnabled() {
        return (boolean) get("SinkAntiSpam.IPCheck.Enabled");
    }

    public boolean isWhitelistedDomainCheckEnabled() {
        return (boolean) get("SinkAntiSpam.WhitelistedDomainsCheck.Enabled");
    }

    public int getLocalChatRange() {
        return (int) get("SinkChat.LocalChatRange");
    }

    public String getIrcBotUsername() {
        return (String) get("SinkIRC.Username");
    }

    public String getIrcAddress() {
        return (String) get("SinkIRC.Server.Address");
    }

    public boolean isIrcPasswordEnabled() {
        return (boolean) get("SinkIRC.Server.PasswordEnabled");
    }

    public String getIrcPassword() {
        return (String) get("SinkIRC.Server.Password");
    }

    public int getIrcPort() {
        return (int) get("SinkIRC.Server.Port");
    }

    public String getIrcChannel() {
        return (String) get("SinkIRC.Channel");
    }

    public boolean isIrcAuthentificationEnabled() {
        return (boolean) get("SinkIRC.Authentification.Enabled");
    }

    public String getIrcAuthBot() {
        return (String) get("SinkIRC.Authentification.AuthBot");
    }

    public String getIrcAuthMessage() {
        return (String) get("SinkIRC.Authentification.AuthMessage");
    }

    public int getIrcJoinLeaveMaxUsers() {
        return (int) get("SinkIRC.JoinLeaveMaxUsers");
    }

    public boolean isLogEnabled() {
        return (boolean) get("General.EnableLog");
    }

    public boolean isDebugEnabled() {
        return (boolean) get("General.EnableDebug");
    }

    public String getDefaultChatFormat() {
        return (String) get("SinkChat.DefaultChatFormat");
    }

    public int getWarnAutoBanTime() {
        return (int) get("SinkAntiSpam.AutoBanTime");
    }

    public int getMaxWarnings() {
        return (int) get("SinkAntiSpam.MaxWarnings");
    }

    public String getMessageReceivedFormat() {
        return (String) get("SinkCommands.Message.MessageReceived");
    }

    public String getMessageSendFormat() {
        return (String) get("SinkCommands.Message.MessageSend");
    }
}
