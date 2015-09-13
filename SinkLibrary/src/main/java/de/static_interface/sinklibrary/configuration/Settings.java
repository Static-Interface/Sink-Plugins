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
import de.static_interface.sinklibrary.api.configuration.option.YamlBooleanOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlIntegerOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringListOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringOption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Settings extends Configuration {

    public final static YamlParentOption GENERAL_PARENT = new YamlParentOption("General");
    public final static YamlOption<Boolean> GENERAL_DISPLAYNAMES = new YamlBooleanOption(GENERAL_PARENT, "DisplayNamesEnabled", true);
    public final static YamlOption<Boolean>
            GENERAL_DEBUG =
            new YamlBooleanOption(GENERAL_PARENT, "EnableDebug", false, "Provides more information, useful for bug reports etc");
    public final static YamlOption<Boolean>
            GENERAL_LOG =
            new YamlBooleanOption(GENERAL_PARENT, "EnableLog", false, "Log debug to Debug.log (useful for debugging)");

    public final static YamlParentOption SAS_PARENT = new YamlParentOption("SinkAntiSpam");
    public final static YamlParentOption SAS_BLACKLISED_PARENT = new YamlParentOption(SAS_PARENT, "BlacklistedWordsCheck");
    public final static YamlOption<Boolean> SAS_BLACKLIST_ENABLED = new YamlBooleanOption(SAS_BLACKLISED_PARENT, "Enabled", true);
    public final static YamlOption<List<String>> SAS_BLACKLISED_WORDS;
    public final static YamlParentOption SAS_WHITELISTED_DOMAINS_PARENT = new YamlParentOption(SAS_PARENT, "WhitelistedDomainsCheck");
    public final static YamlOption<Boolean> SAS_WHITELISTED_DOMAINS_ENABLED = new YamlBooleanOption(SAS_WHITELISTED_DOMAINS_PARENT, "Enabled", true);
    public final static YamlOption<List<String>> SAS_WHITELISTED_DOMAINS;
    public final static YamlOption<Boolean> SAS_IPFILTER_ENABLED = new YamlBooleanOption(SAS_PARENT, "IPFilterEnabled", true);
    public final static YamlOption<List<String>> SAS_EXCLUDED_COMMANDS;
    public final static YamlParentOption SAS_POINTS = new YamlParentOption(SAS_PARENT, "Points");
    public final static YamlOption<Integer> SAS_POINTS_DOMAIN = new YamlIntegerOption(SAS_POINTS, "Domain", 5);
    public final static YamlOption<Integer> SAS_POINTS_BLACKLIST = new YamlIntegerOption(SAS_POINTS, "Blacklist", 5);
    public final static YamlOption<Integer> SAS_POINTS_IP = new YamlIntegerOption(SAS_POINTS, "IP", 5);
    public final static YamlOption<List<String>> SAS_SANCTIONS;
    public final static YamlParentOption SC_PARENT = new YamlParentOption("SinkChat");
    public final static YamlOption<Integer> SC_LOCAL_CHAT_RANGE = new YamlIntegerOption(SC_PARENT, "LocalChatRange", 50);
    public final static YamlOption<String>
            SC_DEFAULT_CHAT_FORMAT =
            new YamlStringOption(SC_PARENT, "DefaultChatFormat", "&7{CHANNEL} [{RANK}] {DISPLAYNAME}&7:&f {MESSAGE}");
    public final static YamlParentOption SI_PARENT = new YamlParentOption("SinkIRC");
    public final static YamlOption<String> SI_NICKNAME = new YamlStringOption(SI_PARENT, "Nickname", "SinkIRCBot");
    public final static YamlOption<Integer> SI_MAX_JOIN_LEAVE_USERS = new YamlIntegerOption(SI_PARENT, "JoinLeaveMaxUsers", 20);
    public final static YamlParentOption SI_SERVER_PARENT = new YamlParentOption(SI_PARENT, "Server");
    public final static YamlOption<String> SI_SERVER_ADDRESS = new YamlStringOption(SI_SERVER_PARENT, "Address", "irc.example.com");
    public final static YamlOption<String> SI_SERVER_PASSWORD = new YamlStringOption(SI_SERVER_PARENT, "Password", "");
    public final static YamlOption<Integer> SI_SERVER_PORT = new YamlIntegerOption(SI_SERVER_PARENT, "Port", 6667);
    public final static YamlOption<String> SI_SERVER_CHANNEL = new YamlStringOption(SI_SERVER_PARENT, "Channel", "#SinkIRCBot");
    public final static YamlParentOption SI_AUTHENTIFICATION_PARENT = new YamlParentOption(SI_PARENT, "Authentification");
    public final static YamlOption<Boolean> SI_AUTHENTIFICATION_ENABLED = new YamlBooleanOption(SI_AUTHENTIFICATION_PARENT, "Enabled", false);
    public final static YamlOption<String> SI_AUTHENTIFICATION_AUTHBOT = new YamlStringOption(SI_AUTHENTIFICATION_PARENT, "AuthBot", "NickServ");
    public final static YamlOption<String>
            SI_AUTHENTIFICATION_MESSAGE =
            new YamlStringOption(SI_AUTHENTIFICATION_PARENT, "AuthMessage", "identify <NickServPasswordHere>");
    public final static YamlParentOption SCMD_PARENT = new YamlParentOption("SinkCommands");
    public final static YamlParentOption SCMD_MESSAGE_PARENT = new YamlParentOption(SCMD_PARENT, "Message");
    public final static YamlOption<String>
            SCMD_MESSAGE_RECEIVED_FORMAT =
            new YamlStringOption(SCMD_MESSAGE_PARENT, "MessageReceived", "&7[{TARGETIRCPREFIX} {DISPLAYNAME} &7-> &6Me&7]&r {USERMESSAGE}");
    public final static YamlOption<String>
            SCMD_MESSAGE_SEND_FORMAT =
            new YamlStringOption(SCMD_MESSAGE_PARENT, "MessageSend", "&7[&6Me &7-> {TARGETIRCPREFIX} {TARGETDISPLAYNAME}&7]&r {USERMESSAGE}");

    static {
        List<String> defaultSanctions = new ArrayList<>();
        defaultSanctions.add("80: BAN Too many warning points (80)");
        defaultSanctions.add("50: BAN Too many warning points (50) : 30d");
        defaultSanctions.add("10: CMD say {NAME} has got 10 warning points!");
        defaultSanctions.add("5: KICK You've got too many warning points! (5)");
        SAS_SANCTIONS = new YamlStringListOption(SAS_PARENT, "Sanctions", defaultSanctions);
    }
    static {
        List<String> defaultBlackList = new ArrayList<>();
        defaultBlackList.add("BlacklistedWord");
        defaultBlackList.add("BlackListedWord2");
        SAS_BLACKLISED_WORDS = new YamlStringListOption(SAS_BLACKLISED_PARENT, "Words", defaultBlackList);
    }

    static {
        List<String> defaultDomainWhiteList = new ArrayList<>();
        defaultDomainWhiteList.add("google.com");
        defaultDomainWhiteList.add("youtube.com");
        defaultDomainWhiteList.add("yourhomepagehere.com");
        SAS_WHITELISTED_DOMAINS = new YamlStringListOption(SAS_WHITELISTED_DOMAINS_PARENT, "Domains", defaultDomainWhiteList);
    }

    static {
        List<String> defaultExcludedCommands = new ArrayList<>();
        defaultExcludedCommands.add("msg");
        defaultExcludedCommands.add("tell");
        defaultExcludedCommands.add("m");
        defaultExcludedCommands.add("whisper");
        defaultExcludedCommands.add("t");
        SAS_EXCLUDED_COMMANDS = new YamlStringListOption(SAS_PARENT, "ExcludedCommands", defaultExcludedCommands);
    }
    public Settings() {
        super(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Settings.yml"), true);
    }

    @Override
    public void addDefaults() {
        // do nothing
    }
}
