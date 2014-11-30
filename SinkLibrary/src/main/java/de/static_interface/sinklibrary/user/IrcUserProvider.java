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

import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.ChatColor;
import org.pircbotx.User;

import javax.annotation.Nullable;

public class IrcUserProvider extends SinkUserProvider<User, IrcUser> {

    @Override
    public String getTabCompleterSuffix() {
        return "_IRC";
    }

    @Override
    @Nullable
    public IrcUser getUserInstance(String nick) {
        for (IrcUser user : instances.values()) {
            if (user.getName().equals(nick) || ChatColor.stripColor(user.getDisplayName()).equals(nick)) {
                return user;
            }
        }
        return null;
    }

    public IrcUser newInstance(User base, String source) {
        return new IrcUser(base, this, source);
    }


    public IrcUser getUserInstance(User base, String source) {
        if (instances.get(base) == null) {
            loadUser(base, source);
        }

        return instances.get(base);
    }

    @Override
    public IrcUser getUserInstance(User base) {
        throw new NotImplementedException("Use getUserInstance(User, Channel) instead");
    }

    /**
     * Load an user
     * @param base Base of User
     * @return True if successfully created a new instance, false if already loaded
     */
    @Override
    public boolean loadUser(User base) {
        throw new NotImplementedException("Use loadUser(User, Channel) instead");
    }

    public boolean loadUser(User base, String source) {
        if (instances.get(base) != null) {
            return false;
        }
        instances.put(base, newInstance(base, source));
        return true;
    }

    @Override
    public IrcUser newInstance(User base) {
        throw new NotImplementedException("Use newInstance(User, Channel) instead");
    }

    @Override
    public boolean unloadUser(IrcUser user) {
        return unloadUser(user.getBase());
    }
}
