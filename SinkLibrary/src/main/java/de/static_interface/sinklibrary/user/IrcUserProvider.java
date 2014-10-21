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

import de.static_interface.sinklibrary.api.user.SinkUserProvider;
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

    @Override
    public IrcUser newInstance(User base) {
        return new IrcUser(base, this);
    }

    @Override
    public boolean unloadUser(IrcUser user) {
        return unloadUser(user.getBase());
    }
}
