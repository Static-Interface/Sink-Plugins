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

package de.static_interface.sinkchat;

import de.static_interface.sinkchat.channel.ChannelValues;
import de.static_interface.sinklibrary.api.configuration.Configuration;

import java.io.File;

public class ChannelConfiguration extends Configuration {

    public ChannelConfiguration(File baseFolder) {
        super(new File(baseFolder, "ChatChannels.yml"), true);
    }

    @Override
    public void onCreate() {
        set("Channels.Help." + ChannelValues.DEFAULT, true);
        set("Channels.Help." + ChannelValues.CALLCHAR, "?");
        set("Channels.Help." + ChannelValues.ENABLED, true);
        set("Channels.Help." + ChannelValues.PERMISSION, "sinkchat.channel.help");
        set("Channels.Help." + ChannelValues.SEND_TO_IRC, true);
        set("Channels.Help." + ChannelValues.RANGE, 0);
        set("Channels.Help." + ChannelValues.FORMAT, "&7[&aHelp&7] &7[{RANK}] {DISPLAYNAME}&7:&f {MESSAGE}");

        set("Channels.Shout." + ChannelValues.DEFAULT, true);
        set("Channels.Shout." + ChannelValues.CALLCHAR, "!");
        set("Channels.Shout." + ChannelValues.ENABLED, true);
        set("Channels.Shout." + ChannelValues.PERMISSION, "sinkchat.channel.shout");
        set("Channels.Shout." + ChannelValues.SEND_TO_IRC, true);
        set("Channels.Shout." + ChannelValues.RANGE, 0);
        set("Channels.Shout." + ChannelValues.FORMAT, "&7[Shout] [{RANK}] {DISPLAYNAME}&7:&f {MESSAGE}");

        set("Channels.Trade." + ChannelValues.DEFAULT, true);
        set("Channels.Trade." + ChannelValues.CALLCHAR, "$");
        set("Channels.Trade." + ChannelValues.ENABLED, true);
        set("Channels.Trade." + ChannelValues.PERMISSION, "sinkchat.channel.trade");
        set("Channels.Trade." + ChannelValues.SEND_TO_IRC, true);
        set("Channels.Trade." + ChannelValues.RANGE, 0);
        set("Channels.Trade." + ChannelValues.FORMAT, "&7[&6Trade&7] &7[{RANK}] {DISPLAYNAME}&7:&f {MESSAGE}");
    }

    @Override
    public void addDefaults() {
        // Do nothing
    }
}
