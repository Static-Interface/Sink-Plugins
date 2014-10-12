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

package de.static_interface.sinklibrary.user;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.pircbotx.User;

import java.lang.reflect.Method;

public class IrcUser extends SinkUser {

    private User base;
    private boolean online;
    private IrcCommandSender sender;

    public IrcUser(User base, SinkUserProvider provider) {
        super(provider);
        this.base = base;
        online = true;
        sender = new IrcCommandSender(this, null);
    }

    public String getName() {
        return base.getNick();
    }

    @Override
    public String getDisplayName() {
        if (isOp()) {
            return ChatColor.DARK_RED + getName() + ChatColor.RESET;
        }
        return ChatColor.DARK_AQUA + getName() + ChatColor.RESET;
    }

    @Override
    public Configuration getConfiguration() {
        throw new RuntimeException("Not supported (yet?)"); //Todo
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean hasPermission(SinkCommand command) {
        return (command.getCommandOptions().isIrcOnly() && isOp()) || !command.getCommandOptions().isIrcOpOnly();
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
        boolean value;
        try {
            //Use reflection because we can't add SinkIRC as dependency
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("isOp", User.class);
            method.setAccessible(true);
            value = (boolean) method.invoke(null, getBase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        SinkLibrary.getInstance().getCustomLogger().debug("Reflection invoke test: isOp(): " + value + " for user: " + getName());
        return value;
    }

    @Override
    public void setOp(boolean value) {
        try {
            //Use reflection because we can't add SinkIRC as dependency
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("setOp", User.class, Boolean.class);
            method.setAccessible(true);
            method.invoke(null, getBase(), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void sendMessage(String msg) {
        SinkLibrary.getInstance().sendIrcMessage(msg, getName());
    }

    public void sendMessage(String msg, boolean privateMessage) {
        if (privateMessage) {
            sendMessage(msg);
        } else {
            SinkLibrary.getInstance().sendIrcMessage(getDisplayName() + ": " + msg);
        }
    }

    public void sendMessage(String msg, String target) {
        SinkLibrary.getInstance().sendIrcMessage(getName() + ": " + msg, target);
    }

    @Override
    public boolean isOnline() {
        return online; //??
    }

    public void setOnline(boolean value) {
        online = value;
    }

    public User getBase() {
        return base;
    }
}
