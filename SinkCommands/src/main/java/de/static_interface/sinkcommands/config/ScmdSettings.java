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

package de.static_interface.sinkcommands.config;

import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.configuration.option.YamlOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlParentOption;
import de.static_interface.sinklibrary.api.configuration.option.YamlStringOption;

import java.io.File;

public class ScmdSettings extends Configuration {

    public final static YamlParentOption SCMD_MESSAGE_PARENT = new YamlParentOption("Message");
    public final static YamlOption<String>
            SCMD_MESSAGE_RECEIVED_FORMAT =
            new YamlStringOption(SCMD_MESSAGE_PARENT, "MessageReceived", "&7[{DISPLAYNAME} &7-> {TARGETIRCPREFIX}&6Me&7]&r {USERMESSAGE}");
    public final static YamlOption<String>
            SCMD_MESSAGE_SEND_FORMAT =
            new YamlStringOption(SCMD_MESSAGE_PARENT, "MessageSend", "&7[&6Me &7-> {TARGETIRCPREFIX} {TARGETDISPLAYNAME}&7]&r {USERMESSAGE}");


    public ScmdSettings(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {

    }
}
