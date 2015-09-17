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
public class GeneralLanguage extends Configuration {

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


    //Permission messages
    public static YamlParentOption PERMISSIONS_PARENT = new YamlParentOption("Permissions");
    public static YamlI18nOption
            PERMISSIONS_GENERAL =
            new YamlI18nOption(PERMISSIONS_PARENT, "General", "You don't have permissions to do that.", true);

    public GeneralLanguage() {
        super(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Language.yml"));
    }

    public static String formatError(String msg) {
        return GENERAL_ERROR.format(msg);
    }

    @Override
    public void addDefaults() {

    }
}
