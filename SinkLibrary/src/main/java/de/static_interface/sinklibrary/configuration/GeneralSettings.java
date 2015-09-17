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
import de.static_interface.sinklibrary.api.configuration.option.YamlOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;

import java.io.File;

public class GeneralSettings extends Configuration {

    public final static YamlParentOption GENERAL_PARENT = new YamlParentOption("General");
    public final static YamlOption<Boolean> GENERAL_DISPLAYNAMES = new YamlBooleanOption(GENERAL_PARENT, "DisplayNamesEnabled", true);
    public final static YamlOption<Boolean>
            GENERAL_DEBUG =
            new YamlBooleanOption(GENERAL_PARENT, "EnableDebug", false, "Provides more information, useful for bug reports etc");
    public final static YamlOption<Boolean>
            GENERAL_LOG =
            new YamlBooleanOption(GENERAL_PARENT, "EnableLog", false, "Log debug to Debug.log (useful for debugging)");

    public GeneralSettings() {
        super(new File(SinkLibrary.getInstance().getCustomDataFolder(), "Settings.yml"), true);
    }

    @Override
    public void addDefaults() {
        // do nothing
    }
}
