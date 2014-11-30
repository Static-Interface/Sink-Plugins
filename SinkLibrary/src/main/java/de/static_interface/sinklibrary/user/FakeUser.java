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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.sender.FakeSender;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class FakeUser extends SinkUser<FakeSender> {

    SinkUser base;
    SinkUser faker;

    public FakeUser(FakeSender base, SinkUserProvider provider) {
        super(base, provider);
        this.base = SinkLibrary.getInstance().getUser((Object) base.getBase());
        this.faker = SinkLibrary.getInstance().getUser((Object) base.getFaker());
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public String getDisplayName() {
        return base.getDisplayName();
    }

    @Override
    public Configuration getConfiguration() {
        if (base instanceof IngameUser) {
            return base.getConfiguration();
        }
        return null;
    }

    @Override
    public CommandSender getSender() {
        return base.getSender();
    }

    @Override
    public boolean hasPermission(SinkCommand command) {
        return base.hasPermission(command.getPermission());
    }

    @Override
    public boolean hasPermission(String permission) {
        return base.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return base.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return base.isOp();
    }

    @Override
    public void setOp(boolean value) {
        base.setOp(value);
    }

    @Override
    public String getPrimaryGroup() {
        return base.getPrimaryGroup();
    }

    @Override
    public String getChatPrefix() {
        return base.getChatPrefix();
    }

    @Override
    public void sendMessage(String msg) {
        base.sendMessage(msg);
        faker.sendMessage(msg);
    }

    @Override
    public boolean isOnline() {
        return base.isOnline();
    }
}
