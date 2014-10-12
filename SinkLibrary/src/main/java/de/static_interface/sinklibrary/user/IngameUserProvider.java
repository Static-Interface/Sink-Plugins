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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import javax.annotation.Nullable;

public class IngameUserProvider extends SinkUserProvider<OfflinePlayer, IngameUser> {

    @Nullable
    @Override
    public IngameUser getUserInstance(String name) {
        for (IngameUser u : instances.values()) {
            if (u.getName().equals(name) || ChatColor.stripColor(u.getDisplayName()).equals(name)) {
                return u;
            }
        }

        OfflinePlayer of = Bukkit.getOfflinePlayer(name);
        return getUserInstance(of.getUniqueId());
    }

    @Override
    @Nullable
    public IngameUser newInstance(OfflinePlayer sender) {
        Player p = (Player) sender;
        return new IngameUser(p, this);
    }

    @Override
    public String getCommandArgsSuffix() {
        return "";
    }

    public IngameUser getUserInstance(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return getUserInstance(p);
        }

        return new IngameUser(Bukkit.getOfflinePlayer(uuid), this);
    }
}
