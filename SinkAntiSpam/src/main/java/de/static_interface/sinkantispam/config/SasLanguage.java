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

package de.static_interface.sinkantispam.config;

import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.configuration.option.YamlI18nOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;

import java.io.File;

public class SasLanguage extends Configuration {

    public static YamlI18nOption SAS_PREFIX = new YamlI18nOption("Prefix", "&4[SinkAntiSpam]");
    public static YamlI18nOption
            SAS_WARN_MESSAGE =
            new YamlI18nOption("WarnMessage", "{TARGETDISPLAYNAME}&4 has been warned by {DISPLAYNAME}: &c{0} ({1} points)");
    public static YamlI18nOption SAS_REPLACE_DOMAIN = new YamlI18nOption("ReplaceDomain", "google.com");
    public static YamlI18nOption SAS_REPLACE_IP = new YamlI18nOption("ReplaceIP", "127.0.0.1");
    public static YamlI18nOption SAS_WARN_SELF = new YamlI18nOption("WarnSelf", "&4You can't warn yourself!", true);
    public static YamlI18nOption
            SAS_REPEATING_MESSAGE =
            new YamlI18nOption("RepeatingMessage", "Please don't spam the same message!", true);
    public static YamlI18nOption
            SAS_COOLDOWN_MESSAGE =
            new YamlI18nOption("CooldownMessage", "Please wait {0} ms before sending a message again", true);

    //SinkAntiSpam Auto-warn reasons
    public static YamlParentOption SAS_REASONS_PARENT = new YamlParentOption("Reasons");
    public static YamlI18nOption
            SAS_REASONS_BLACKLISED_WORDS =
            new YamlI18nOption(SAS_REASONS_PARENT, "BlacklistedWord", "Tried to write a blacklisted word: {0}");
    public static YamlI18nOption SAS_REASONS_IP = new YamlI18nOption(SAS_REASONS_PARENT, "IP", "Tried to write IP: &9&l&n{0}");
    public static YamlI18nOption
            SAS_REASONS_DOMAIN =
            new YamlI18nOption(SAS_REASONS_PARENT, "Domain", "Tried to write a not whitelisted domain: &9&l&n{0}");

    public SasLanguage(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {

    }
}
