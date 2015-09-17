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

package de.static_interface.sinklibrary.user;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import de.static_interface.sinklibrary.configuration.GeneralSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;

public class ConsoleUser extends SinkUser<ConsoleCommandSender> {

    private ConsoleCommandSender sender;

    public ConsoleUser(ConsoleCommandSender sender, SinkUserProvider provider) {
        super(sender, provider);
        this.sender = sender;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED + "Console" + ChatColor.RESET;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    @Deprecated
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean hasPermission(SinkCommand command) {
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return true;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        // do nothing
    }

    @Override
    public String getPrimaryGroup() {
        throw new UnsupportedOperationException("Console can't have a group");
    }

    @Override
    public String getChatPrefix() {
        return ChatColor.DARK_RED.toString();
    }

    @Override
    public void sendMessage(String msg) {
        getSender().sendMessage(msg);
    }

    @Override
    public void sendDebugMessage(String msg) {
        if (GeneralSettings.GENERAL_DEBUG.getValue()) {
            sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Debug" + ChatColor.GRAY + "] " + ChatColor.RESET + msg);
        }
    }

    @Override
    public boolean isOnline() {
        return true;
    }
}
