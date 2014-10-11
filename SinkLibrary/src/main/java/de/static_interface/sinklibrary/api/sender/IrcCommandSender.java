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

package de.static_interface.sinklibrary.api.sender;

import de.static_interface.sinklibrary.user.IrcUser;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.pircbotx.PircBotX;

import java.lang.reflect.Method;
import java.util.Set;

public class IrcCommandSender implements CommandSender {

    IrcUser user;
    String source;
    volatile boolean useNotice = false;

    /**
     * INTERNAL CONSTRUCTOR
     */
    public IrcCommandSender(IrcUser user, String source) {
        this.user = user;
        this.source = source;
        this.useNotice = false;
    }

    public synchronized boolean getUseNotice() {
        return useNotice;
    }

    public synchronized void setUseNotice(boolean value) {
        this.useNotice = value;
    }

    public String getSource() {
        return source;
    }

    public IrcUser getUser() {
        return user;
    }

    private void sendNotice(String msg) {
        try {
            PircBotX bot;
            Class<?> c = Class.forName("de.static_interface.sinkirc.SinkIRC");
            Method method = c.getMethod("getIrcBot");
            method.setAccessible(true);
            bot = (PircBotX) method.invoke(null);
            bot.sendIRC().notice(user.getName(), msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void sendMessage(String msg) {
        if (useNotice) {
            sendNotice(msg);
            return;
        }
        if (source == null) {
            user.sendMessage(msg);
        } else {
            user.sendMessage(msg, source);
        }
    }

    @Override
    public synchronized void sendMessage(String[] msgs) {
        boolean first = true;
        for (String msg : msgs) {
            if (useNotice) {
                sendNotice(msg);
                continue;
            }

            if (first) {
                msg = user.getName() + ": " + msg;
                first = false;
            }
            sendMessage(msg);
        }
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public boolean isPermissionSet(String s) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String s) {
        Permission perm = new Permission(s);
        return perm.getDefault().getValue(isOp());
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return perm.getDefault().getValue(isOp());
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        // do nothing
    }

    @Override
    public void recalculatePermissions() {
        // do nothing?
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public boolean isOp() {
        return user.isOp();
    }

    @Override
    public void setOp(boolean value) {
        user.setOp(value);
    }
}
