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
import de.static_interface.sinklibrary.api.configuration.option.YamlIntegerOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringOption;

import java.io.File;

public class ScSettings extends Configuration {

    public final static YamlOption<Integer> SC_LOCAL_CHAT_RANGE = new YamlIntegerOption("LocalChatRange", 50);
    public final static YamlOption<String>
            SC_DEFAULT_CHAT_FORMAT =
            new YamlStringOption("DefaultChatFormat", "&7{CHANNEL} [{RANK}] {DISPLAYNAME}&7:&f {MESSAGE}");

    public static YamlParentOption SC_PREFIX_PARENT = new YamlParentOption("Prefix");
    public static YamlI18nOption SC_PREFIX_SPY = new YamlI18nOption(SC_PREFIX_PARENT, "Spy", "&7[Spy]");
    public static YamlI18nOption SC_PREFIX_TOWN = new YamlI18nOption(SC_PREFIX_PARENT, "Town", "&7[&6{0}&7]");
    public static YamlI18nOption SC_PREFIX_LOCAL = new YamlI18nOption(SC_PREFIX_PARENT, "Local", "&7[Local]");
    public static YamlI18nOption SC_PREFIX_NATION = new YamlI18nOption(SC_PREFIX_PARENT, "Nation", "&7[&6{0}&7]");

    public ScSettings(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {

    }
}
