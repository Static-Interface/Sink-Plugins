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

package de.static_interface.sinklibrary.api.user;

import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashMap;

public abstract class SinkUserProvider {

    public HashMap<CommandSender, SinkUser> instances;

    public SinkUserProvider() {
        instances = new HashMap<>();
    }

    public SinkUser getUserInstance(CommandSender sender) {
        if (instances.get(sender) == null) {
            loadUser(sender);
        }

        return instances.get(sender);
    }

    public Collection<SinkUser> getUserInstances() {
        return instances.values();
    }

    /**
     * Load an user
     * @param sender Base of User
     * @return True if successfully created a new instance, false if already loaded
     */
    public boolean loadUser(CommandSender sender) {
        if (instances.get(sender) != null) {
            return false;
        }
        instances.put(sender, newInstance(sender));
        return true;
    }

    public abstract SinkUser newInstance(CommandSender sender);

    public boolean unloadUser(SinkUser user) {
        for (CommandSender sender : instances.keySet()) {
            SinkUser value = instances.get(sender);

            if (value == user) {
                instances.remove(sender);
                return true;
            }
        }
        return false;
    }
}
