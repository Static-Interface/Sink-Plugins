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

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class FakeConsoleCommandSender implements ConsoleCommandSender, FakeSender {

    private final ConsoleCommandSender base;
    private final CommandSender faker;

    public FakeConsoleCommandSender(ConsoleCommandSender base, CommandSender faker) {
        this.base = base;
        this.faker = faker;
    }

    @Override
    public void sendMessage(String s) {
        base.sendMessage(s);
        faker.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        base.sendMessage(strings);
        faker.sendMessage(strings);
    }

    @Override
    public Server getServer() {
        return base.getServer();
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public boolean isPermissionSet(String s) {
        return base.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return base.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s) {
        return base.hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return base.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return base.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return base.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return base.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return base.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        base.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        base.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return base.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return base.isOp();
    }

    @Override
    public void setOp(boolean b) {
        base.setOp(b);
    }

    @Override
    public boolean isConversing() {
        return base.isConversing();
    }

    @Override
    public void acceptConversationInput(String s) {
        base.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return base.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        base.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
        base.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public void sendRawMessage(String s) {
        base.sendRawMessage(s);
        faker.sendMessage(s);
    }

    @Override
    public CommandSender getBase() {
        return base;
    }

    @Override
    public CommandSender getFaker() {
        return faker;
    }
}
