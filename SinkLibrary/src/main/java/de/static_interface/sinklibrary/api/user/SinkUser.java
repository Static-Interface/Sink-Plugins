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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public abstract class SinkUser {

    public abstract String getName();

    public abstract String getDisplayName();

    public abstract Configuration getConfiguration();

    public abstract CommandSender getSender();

    public abstract boolean hasPermission(SinkCommand command);

    public abstract boolean hasPermission(String permission);

    public abstract boolean hasPermission(Permission permission);

    public abstract boolean isOp();

    public abstract void setOp(boolean value);

    public abstract String getPrimaryGroup();

    public abstract String getChatPrefix();

    public abstract void sendMessage(String msg);

    public void sendDebugMessage(String msg) {
        if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
            sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Debug" + ChatColor.GRAY + "] " + ChatColor.RESET + msg);
        }
    }

    public abstract boolean isOnline();
}
