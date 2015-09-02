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
import de.static_interface.sinklibrary.api.configuration.option.YamlI18nOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;

import java.io.File;

@SuppressWarnings(
        {"OverlyBroadCatchBlock", "InstanceMethodNamingConvention", "BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention",
         "StaticMethodNamingConvention"})
public class LanguageConfiguration extends Configuration {

    //TimeUnits
    public static YamlParentOption TIMEUNIT_PARENT = new YamlParentOption("TimeUnit");
    public static YamlI18nOption TIMEUNIT_YEARS = new YamlI18nOption(TIMEUNIT_PARENT, "Years", "Year(s)");
    public static YamlI18nOption TIMEUNIT_MONTHS = new YamlI18nOption(TIMEUNIT_PARENT, "Months", "Month(s)");
    public static YamlI18nOption TIMEUNIT_WEEKS = new YamlI18nOption(TIMEUNIT_PARENT, "Weeks", "Week(s)");
    public static YamlI18nOption TIMEUNIT_DAYS = new YamlI18nOption(TIMEUNIT_PARENT, "Days", "Day(s)");
    public static YamlI18nOption TIMEUNIT_HOURS = new YamlI18nOption(TIMEUNIT_PARENT, "Hours", "Hour(s)");
    public static YamlI18nOption TIMEUNIT_MINUTES = new YamlI18nOption(TIMEUNIT_PARENT, "Minutes", "Minute(s)");
    public static YamlI18nOption TIMEUNIT_SECONDS = new YamlI18nOption(TIMEUNIT_PARENT, "Seconds", "Second(s)");

    //General stuff
    public static YamlParentOption GENERAL_PARENT = new YamlParentOption("General");
    public static YamlI18nOption GENERAL_NOT_ONLINE = new YamlI18nOption(GENERAL_PARENT, "NotOnline", "&c{0} is not online!");
    public static YamlI18nOption GENERAL_USER_NOT_FOUND = new YamlI18nOption(GENERAL_PARENT, "UserNotFound", "&cUser not found: {0}");
    public static YamlI18nOption GENERAL_BANNED = new YamlI18nOption(GENERAL_PARENT, "Banned", "&4Banned: {0}&c");
    public static YamlI18nOption GENERAL_ERROR = new YamlI18nOption(GENERAL_PARENT, "Error", "&4Error: &c{0}");
    public static YamlI18nOption GENERAL_NOT_ENOUGH_ARGUMENTS = new YamlI18nOption(GENERAL_PARENT, "TooFewArguments", "Too few arguments!", true);
    public static YamlI18nOption GENERAL_TIME_LEFT = new YamlI18nOption(GENERAL_PARENT, "TimeLeft", "Time Left: {0}");
    public static YamlI18nOption GENERAL_UNKNOWN_VALUE = new YamlI18nOption(GENERAL_PARENT, "UnknownValue", "Unknown value: {0}", true);
    public static YamlI18nOption GENERAL_NOT_ENOUGH_MONEY = new YamlI18nOption(GENERAL_PARENT, "NotEnoughMoney", "You don't have enough money", true);
    public static YamlI18nOption GENERAL_INVALID_VALUE = new YamlI18nOption(GENERAL_PARENT, "InvalidValue", "Invalid value: {0}", true);
    public static YamlI18nOption GENERAL_SUCCESS = new YamlI18nOption(GENERAL_PARENT, "Success", "&aSuccess");
    public static YamlI18nOption
            GENERAL_SUCCESS_SET =
            new YamlI18nOption(GENERAL_PARENT, "SuccessSet", "&aSuccessfully set \"{0}\"&r&a to \"{1}\"&r&a!");


    //SinkChat
    public static YamlParentOption SC_PARENT = new YamlParentOption("SinkChat");
    public static YamlParentOption SC_COMMANDS_PARENT = new YamlParentOption(SC_PARENT, "Commands");

    //SinkChat /nick
    public static YamlParentOption SC_NICK_COMMAND_PARENT = new YamlParentOption(SC_COMMANDS_PARENT, "Nick");
    public static YamlI18nOption SC_NICK_OTHER_CHANGED = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "OthersChanged", "{0}'s name is now {1}!)");
    public static YamlI18nOption SC_NICK_SELF_CHANGED = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "SelfChanged", "Your nickname is now {0}!");
    public static YamlI18nOption SC_NICK_ILLEGAL_NICKNAME = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "IllegalNickname", "Illegal nickname!", true);
    public static YamlI18nOption SC_NICK_TOO_LONG = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "TooLong", "Nickname is too long!", true);
    public static YamlI18nOption
            SC_NICK_ALREADY_USED =
            new YamlI18nOption(SC_NICK_COMMAND_PARENT, "AlreadyUsed", "Nickname is already used by someone!", true);

    //SinkChat /spy
    public static YamlParentOption SC_SPY_COMMAND_PARENT = new YamlParentOption(SC_COMMANDS_PARENT, "Spy");
    public static YamlI18nOption SC_SPY_ENABLED = new YamlI18nOption(SC_SPY_COMMAND_PARENT, "Enabled", "&aSpy chat has been enabled!");
    public static YamlI18nOption
            SC_SPY_ALREADY_ENABLED =
            new YamlI18nOption(SC_SPY_COMMAND_PARENT, "AlreadyEnabled", "Spy chat has already been enabled!", true);
    public static YamlI18nOption SC_SPY_DISABLED = new YamlI18nOption(SC_SPY_COMMAND_PARENT, "Disabled", "&4Spy chat has been disabled!");
    public static YamlI18nOption
            SC_SPY_ALREADY_DISABLED =
            new YamlI18nOption(SC_SPY_COMMAND_PARENT, "AlreadyDisabled", "Spy chat has been already disabled!", true);

    //SinkChat prefixes
    public static YamlParentOption SC_PREFIX_PARENT = new YamlParentOption(SC_PARENT, "Prefix");
    public static YamlI18nOption SC_PREFIX_SPY = new YamlI18nOption(SC_PREFIX_PARENT, "Spy", "&7[Spy]");
    public static YamlI18nOption SC_PREFIX_TOWN = new YamlI18nOption(SC_PREFIX_PARENT, "Town", "&7[&6{0}&7]");
    public static YamlI18nOption SC_PREFIX_LOCAL = new YamlI18nOption(SC_PREFIX_PARENT, "Local", "&7[Local]");
    public static YamlI18nOption SC_PREFIX_NATION = new YamlI18nOption(SC_PREFIX_PARENT, "Nation", "&7[&6{0}&7]");
    public static YamlParentOption SC_TOWNY_PARENT = new YamlParentOption(SC_PARENT, "Towny");
    public static YamlI18nOption SC_TOWNY_NOT_IN_TOWN = new YamlI18nOption(SC_TOWNY_PARENT, "NotInTown", "You are not a resident of any town", true);
    public static YamlI18nOption
            SC_TOWNY_NOT_IN_NATION =
            new YamlI18nOption(SC_TOWNY_PARENT, "NotInNation", "You or your town is not a member any nation", true);

    //Permission messages
    public static YamlParentOption PERMISSIONS_PARENT = new YamlParentOption("Permissions");
    public static YamlI18nOption
            PERMISSIONS_GENERAL =
            new YamlI18nOption(PERMISSIONS_PARENT, "General", "You don't have permissions to do that.", true);

    //SinkAntiSpam
    public static YamlParentOption SAS_PARENT = new YamlParentOption("SinkAntiSpam");
    public static YamlI18nOption SAS_PREFIX = new YamlI18nOption(SAS_PARENT, "Prefix", "&4[SinkAntiSpam]");
    public static YamlI18nOption
            SAS_WARN_MESSAGE =
            new YamlI18nOption(SAS_PARENT, "WarnMessage", "{TARGETDISPLAYNAME}&4 has been warned by {DISPLAYNAME}: &c{2} ({3} points)");
    public static YamlI18nOption SAS_REPLACE_DOMAIN = new YamlI18nOption(SAS_PARENT, "ReplaceDomain", "google.com");
    public static YamlI18nOption SAS_REPLACE_IP = new YamlI18nOption(SAS_PARENT, "ReplaceIP", "127.0.0.1");
    public static YamlI18nOption SAS_WARN_SELF = new YamlI18nOption(SAS_PARENT, "WarnSelf", "&4You can't warn yourself!", true);
    public static YamlI18nOption
            SAS_REPEATING_MESSAGE =
            new YamlI18nOption(SAS_PARENT, "RepeatingMessage", "Please don't spam the same message!", true);
    public static YamlI18nOption
            SAS_COOLDOWN_MESSAGE =
            new YamlI18nOption(SAS_PARENT, "CooldownMessage", "Please wait {0} ms before sending a message again", true);

    //SinkAntiSpam Auto-warn reasons
    public static YamlParentOption SAS_REASONS_PARENT = new YamlParentOption(SAS_PARENT, "Reasons");
    public static YamlI18nOption
            SAS_REASONS_BLACKLISED_WORDS =
            new YamlI18nOption(SAS_REASONS_PARENT, "BlacklistedWord", "Tried to write a blacklisted word: {0}");
    public static YamlI18nOption SAS_REASONS_IP = new YamlI18nOption(SAS_REASONS_PARENT, "IP", "Tried to write IP: &9&l&n{0}");
    public static YamlI18nOption
            SAS_REASONS_DOMAIN =
            new YamlI18nOption(SAS_REASONS_PARENT, "Domain", "Tried to write a not whitelisted domain: &9&l&n{0}");
    private static LanguageConfiguration instance;
    public LanguageConfiguration() {
        super(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Language.yml"));
    }

    @Deprecated
    public static LanguageConfiguration getInstance() {
        if (instance == null) {
            instance = new LanguageConfiguration();
            instance.init();
        }
        return instance;
    }

    public static String formatError(String msg) {
        return GENERAL_ERROR.format(msg);
    }

    @Override
    public void addDefaults() {

    }
}
