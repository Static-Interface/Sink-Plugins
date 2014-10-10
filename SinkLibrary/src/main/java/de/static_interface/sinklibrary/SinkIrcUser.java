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

package de.static_interface.sinklibrary;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.user.IUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.pircbotx.User;

public class SinkIrcUser implements IUser {

    private User base;

    public SinkIrcUser(User base) {
        this.base = base;
    }

    public String getName() {
        return base.getNick();
    }

    @Override
    public String getDisplayName() {
        throw new RuntimeException("Not supported yet"); //Todo
    }

    @Override
    public Configuration getConfiguration() {
        throw new RuntimeException("Not supported yet"); //Todo
    }

    @Override
    public String getIdentifierString() {
        return base.getNick() + ":" + base.getRealName();
    }

    @Override
    public CommandSender getSender() {
        return null; // todo
    }

    @Override
    public boolean hasPermission(SinkCommand command) {
        return (command.isIrcOnly() && isOp()) || !command.isIrcOpOnly();
    }

    @Override
    public boolean isOp() {
        throw new RuntimeException("Not supported yet"); //Todo
    }

    @Override
    public String getPrimaryGroup() {
        return isOp() ? "OP" : "Default";
    }

    @Override
    public String getChatPrefix() {
        return isOp() ? ChatColor.DARK_RED.toString() : ChatColor.DARK_AQUA.toString();
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void sendMessage(String msg) {
        SinkLibrary.getInstance().sendIrcMessage(base.getNick() + ": " + msg);
    }

    public void sendMessage(String msg, boolean privateMessage) {
        if (!privateMessage) {
            sendMessage(msg);
        } else {
            SinkLibrary.getInstance().sendIrcMessage(msg, getName());
        }
    }

    public void sendMessage(String msg, String target) {
        SinkLibrary.getInstance().sendIrcMessage(getName() + ": " + msg, target);
    }

    @Override
    public void sendDebugMessage(String msg) {
        if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
            sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Debug" + ChatColor.GRAY + "] " + ChatColor.RESET + msg);
        }
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    public User getBase() {
        return base;
    }
}
