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
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import org.bukkit.command.CommandSender;
import org.pircbotx.User;

import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;

public class IrcUserProvider extends SinkUserProvider {

    HashMap<User, SinkUser> instances = new HashMap<>();

    @Override
    public SinkUser newInstance(CommandSender sender) {
        return ((IrcCommandSender) sender).getUser();
    }

    @Override
    public String getCommandArgsSuffix() {
        return "_IRC";
    }

    @Override
    @Nullable
    public SinkUser getUserInstance(CommandSender sender) {
        for (SinkUser user : instances.values()) {
            if (user == ((IrcCommandSender) sender).getUser()) {
                return user;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public SinkUser getUserInstance(String nick) {
        for (SinkUser user : instances.values()) {
            if (user.getName().equals(nick)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Collection<SinkUser> getUserInstances() {
        return instances.values();
    }

    @Override
    public boolean loadUser(CommandSender sender) {
        loadUser(((IrcCommandSender) sender).getUser().getBase());
        return true; // todo
    }

    @Override
    public boolean unloadUser(SinkUser user) {
        unloadUser(((IrcUser) user).getBase());
        return true; // todo
    }

    public void loadUser(User user) {
        SinkLibrary.getInstance().getCustomLogger().debug("Loading user: " + user.toString());
        if (getUserInstance(user) != null) {
            return;
        }

        IrcUser sUser = new IrcUser(user, this);
        instances.put(user, sUser);
    }

    public void unloadUser(User user) {
        SinkLibrary.getInstance().getCustomLogger().debug("Unloading user: " + user.toString());
        IrcUser sUser = (IrcUser) getUserInstance(user);

        if (sUser == null) {
            return;
        }
        sUser.setOnline(false);
        //user.getConfiguration().save(); // Todo
        instances.remove(user);
    }

    @Nullable
    public SinkUser getUserInstance(User user) {
        for (User pircBotUser : instances.keySet()) {
            if (pircBotUser.getNick().equals(user.getNick()) && pircBotUser.getRealName().equals(user.getRealName())) {
                return instances.get(pircBotUser);
            }
        }
        return null;
    }
}
