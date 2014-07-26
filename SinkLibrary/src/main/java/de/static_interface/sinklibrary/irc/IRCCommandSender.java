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

package de.static_interface.sinklibrary.irc;

import de.static_interface.sinklibrary.SinkLibrary;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.pircbotx.User;

import java.lang.reflect.Method;
import java.util.Set;

public class IrcCommandSender implements CommandSender
{
    User base;
    String source;
    public IrcCommandSender(User base, String source)
    {
        if (base == null) throw new IllegalArgumentException("base may not be null");
        if (source == null) throw new IllegalArgumentException("source may not be null");
        this.base = base;
        this.source = source;
    }

    public String getSource()
    {
        return source;
    }

    public User getUser()
    {
        return base;
    }

    @Override
    public void sendMessage(String msg)
    {
        SinkLibrary.sendIrcMessage(base.getNick() + ": " + msg, source);
    }

    @Override
    public void sendMessage(String[] msgs)
    {
        boolean first = true;
        for(String msg : msgs)
        {
            if (first)
            {
                msg = base.getNick() + ": " + msg;
                first = false;
            }
            SinkLibrary.sendIrcMessage(msg, source);
        }
    }

    @Override
    public Server getServer()
    {
        return Bukkit.getServer();
    }

    @Override
    public String getName()
    {
        return base.getNick();
    }

    @Override
    public boolean isPermissionSet(String s)
    {
        //Permission perm = new Permission(s);
        //return perm.getDefault().getValue(isOp());
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm)
    {
        //return perm.getDefault().getValue(isOp());
        return true;
    }

    @Override
    public boolean hasPermission(String s)
    {
        Permission perm = new Permission(s);
        return perm.getDefault().getValue(isOp());
    }

    @Override
    public boolean hasPermission(Permission perm)
    {
        return perm.getDefault().getValue(isOp());
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b)
    {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin)
    {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i)
    {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i)
    {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment)
    {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public void recalculatePermissions()
    {
        // do nothing?
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        throw new NotImplementedException("This is not available to IRC Users");
    }

    @Override
    public boolean isOp()
    {
        boolean value;
        try
        {
            //User reflection because we can't add SinkIRC as dependency
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("isOp", User.class);
            method.setAccessible(true);
            value = (boolean) method.invoke(null, base);
        }
        catch ( Exception e)
        {
            e.printStackTrace();
            return false;
        }

        SinkLibrary.getCustomLogger().debug("isOp(): " + value + " for user: " + base.getNick());
        return value;
    }

    @Override
    public void setOp(boolean value)
    {
        try
        {
            //User reflection because we can't add SinkIRC as dependency
            Class<?> c = Class.forName("de.static_interface.sinkirc.IrcUtil");
            Method method = c.getMethod("setOp", User.class, Boolean.class);
            method.setAccessible(true);
            method.invoke(null, base, value);
        }
        catch ( Exception e)
        {
            e.printStackTrace();
        }
    }
}
