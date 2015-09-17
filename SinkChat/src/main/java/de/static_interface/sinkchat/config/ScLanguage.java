/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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

package de.static_interface.sinkchat.config;

import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.configuration.option.YamlI18nOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;

import java.io.File;

public class ScLanguage extends Configuration {

    public static YamlParentOption SC_COMMANDS_PARENT = new YamlParentOption("Commands");

    public static YamlParentOption SC_NICK_COMMAND_PARENT = new YamlParentOption(SC_COMMANDS_PARENT, "Nick");
    public static YamlI18nOption SC_NICK_OTHER_CHANGED = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "OthersChanged", "{0}'s name is now {1}!)");
    public static YamlI18nOption SC_NICK_SELF_CHANGED = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "SelfChanged", "Your nickname is now {0}!");
    public static YamlI18nOption SC_NICK_ILLEGAL_NICKNAME = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "IllegalNickname", "Illegal nickname!", true);
    public static YamlI18nOption SC_NICK_TOO_LONG = new YamlI18nOption(SC_NICK_COMMAND_PARENT, "TooLong", "Nickname is too long!", true);
    public static YamlI18nOption
            SC_NICK_ALREADY_USED =
            new YamlI18nOption(SC_NICK_COMMAND_PARENT, "AlreadyUsed", "Nickname is already used by someone!", true);

    public static YamlParentOption SC_SPY_COMMAND_PARENT = new YamlParentOption(SC_COMMANDS_PARENT, "Spy");
    public static YamlI18nOption SC_SPY_ENABLED = new YamlI18nOption(SC_SPY_COMMAND_PARENT, "Enabled", "&aSpy chat has been enabled!");
    public static YamlI18nOption
            SC_SPY_ALREADY_ENABLED =
            new YamlI18nOption(SC_SPY_COMMAND_PARENT, "AlreadyEnabled", "Spy chat has already been enabled!", true);
    public static YamlI18nOption SC_SPY_DISABLED = new YamlI18nOption(SC_SPY_COMMAND_PARENT, "Disabled", "&4Spy chat has been disabled!");
    public static YamlI18nOption
            SC_SPY_ALREADY_DISABLED =
            new YamlI18nOption(SC_SPY_COMMAND_PARENT, "AlreadyDisabled", "Spy chat has been already disabled!", true);

    public static YamlParentOption SC_TOWNY_PARENT = new YamlParentOption("Towny");
    public static YamlI18nOption SC_TOWNY_NOT_IN_TOWN = new YamlI18nOption(SC_TOWNY_PARENT, "NotInTown", "You are not a resident of any town", true);
    public static YamlI18nOption
            SC_TOWNY_NOT_IN_NATION =
            new YamlI18nOption(SC_TOWNY_PARENT, "NotInNation", "You or your town is not a member any nation", true);

    public ScLanguage(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {

    }
}
