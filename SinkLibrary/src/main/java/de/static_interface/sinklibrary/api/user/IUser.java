/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.api.user;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import org.bukkit.command.CommandSender;

public interface IUser {

    String getName();

    String getDisplayName();

    Configuration getConfiguration();

    String getIdentifierString();

    CommandSender getSender();

    boolean hasPermission(SinkCommand command);

    boolean isOp();

    String getPrimaryGroup();

    String getChatPrefix();

    boolean isConsole();

    void sendMessage(String msg);

    void sendDebugMessage(String msg);

    boolean isPlayer();
}
