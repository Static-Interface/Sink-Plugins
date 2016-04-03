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

package de.static_interface.sinkirc.config;

import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.configuration.option.YamlBooleanOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlIntegerOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringListOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringOption;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SiSettings extends Configuration {

    public final static YamlOption<String> SI_NICKNAME = new YamlStringOption("Nickname", "SinkIRCBot");
    public final static YamlParentOption SI_SERVER_PARENT = new YamlParentOption("Server");
    public final static YamlOption<String> SI_SERVER_ADDRESS = new YamlStringOption(SI_SERVER_PARENT, "Address", "irc.example.com");
    public final static YamlOption<String> SI_SERVER_PASSWORD = new YamlStringOption(SI_SERVER_PARENT, "Password", "");
    public final static YamlOption<Integer> SI_SERVER_PORT = new YamlIntegerOption(SI_SERVER_PARENT, "Port", 6667);
    public final static YamlOption<List<String>>
            SI_SERVER_CHANNELS =
            new YamlStringListOption(SI_SERVER_PARENT, "Channels", Arrays.asList("#SinkIRCBot"));
    public final static YamlParentOption SI_AUTHENTIFICATION_PARENT = new YamlParentOption("Authentification");
    public final static YamlOption<Boolean> SI_AUTHENTIFICATION_ENABLED = new YamlBooleanOption(SI_AUTHENTIFICATION_PARENT, "Enabled", false);
    public final static YamlOption<String> SI_AUTHENTIFICATION_AUTHBOT = new YamlStringOption(SI_AUTHENTIFICATION_PARENT, "AuthBot", "NickServ");
    public final static YamlOption<List<String>>
            SI_SERVER_STREAMS = new YamlStringListOption(SI_SERVER_PARENT, "Streams", Arrays.asList("event_asyncplayerchatevent >> #SinkIRCBot"));
    public final static YamlOption<String>
            SI_AUTHENTIFICATION_MESSAGE =
            new YamlStringOption(SI_AUTHENTIFICATION_PARENT, "AuthMessage", "identify <NickServPasswordHere>");

    public SiSettings(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {

    }
}
