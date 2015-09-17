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
import de.static_interface.sinklibrary.api.configuration.option.YamlBooleanOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlIntegerOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringListOption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SasSettings extends Configuration {

    public final static YamlParentOption SAS_BLACKLISED_PARENT = new YamlParentOption("BlacklistedWordsCheck");
    public final static YamlOption<Boolean> SAS_BLACKLIST_ENABLED = new YamlBooleanOption(SAS_BLACKLISED_PARENT, "Enabled", true);
    public final static YamlOption<List<String>> SAS_BLACKLISED_WORDS;
    public final static YamlParentOption SAS_WHITELISTED_DOMAINS_PARENT = new YamlParentOption("WhitelistedDomainsCheck");
    public final static YamlOption<Boolean> SAS_WHITELISTED_DOMAINS_ENABLED = new YamlBooleanOption(SAS_WHITELISTED_DOMAINS_PARENT, "Enabled", true);
    public final static YamlOption<List<String>> SAS_WHITELISTED_DOMAINS;
    public final static YamlOption<Boolean> SAS_IPFILTER_ENABLED = new YamlBooleanOption("IPFilterEnabled", true);
    public final static YamlOption<List<String>> SAS_EXCLUDED_COMMANDS;
    public final static YamlParentOption SAS_POINTS = new YamlParentOption("Points");
    public final static YamlOption<Integer> SAS_POINTS_DOMAIN = new YamlIntegerOption(SAS_POINTS, "Domain", 5);
    public final static YamlOption<Integer> SAS_POINTS_BLACKLIST = new YamlIntegerOption(SAS_POINTS, "Blacklist", 5);
    public final static YamlOption<Integer> SAS_POINTS_IP = new YamlIntegerOption(SAS_POINTS, "IP", 5);
    public final static YamlOption<List<String>> SAS_SANCTIONS;

    static {
        List<String> defaultSanctions = new ArrayList<>();
        defaultSanctions.add("80: BAN Too many warning points (80)");
        defaultSanctions.add("50: BAN Too many warning points (50) : 30d");
        defaultSanctions.add("10: CMD say {NAME} has got 10 warning points!");
        defaultSanctions.add("5: KICK You've got too many warning points! (5)");
        SAS_SANCTIONS = new YamlStringListOption("Sanctions", defaultSanctions);
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
        SAS_EXCLUDED_COMMANDS = new YamlStringListOption("ExcludedCommands", defaultExcludedCommands);
    }

    public SasSettings(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {

    }
}
