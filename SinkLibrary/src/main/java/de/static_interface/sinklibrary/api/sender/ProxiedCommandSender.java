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

package de.static_interface.sinklibrary.api.sender;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R2.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class ProxiedCommandSender extends ProxiedNativeCommandSender implements CommandSender {

    public ProxiedCommandSender(CommandSender base, CommandSender faker) {
        super(((CraftPlayer) base).getHandle(), base, faker);
    }

    @Override
    public void sendMessage(String s) {
        getCallee().sendMessage(s);
        getCaller().sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        getCallee().sendMessage(strings);
        getCaller().sendMessage(strings);
    }

    @Override
    public Server getServer() {
        return getCallee().getServer();
    }

    @Override
    public String getName() {
        return getCallee().getName();
    }

    @Override
    public boolean isPermissionSet(String s) {
        return getCallee().isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return getCallee().isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s) {
        return getCallee().hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return getCallee().hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return getCallee().addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return getCallee().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return getCallee().addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return getCallee().addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        getCallee().removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        getCallee().recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getCallee().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return getCallee().isOp();
    }

    @Override
    public void setOp(boolean b) {
        getCallee().setOp(b);
    }

    @Override
    public int hashCode() {
        return getCallee().hashCode(); //???
    }

    @Override
    public boolean equals(Object o) {
        return getCallee().equals(o); //???
    }
}
