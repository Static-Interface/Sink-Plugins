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
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.apache.commons.lang3.Validate;
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
        Validate.notNull(name);
        for (IngameUser u : instances.values()) {
            if (ChatColor.stripColor(u.getName().trim()).equals(ChatColor.stripColor(name.trim()))
                || (u.getDisplayName() != null && ChatColor.stripColor(u.getDisplayName().trim()).equals(name.trim()))) {
                return u;
            }
        }

        return getUserInstance(BukkitUtil.getUniqueIdByName(name));
    }

    @Override
    @Nullable
    public IngameUser newInstance(OfflinePlayer sender) {
        Player p = (Player) sender;
        return new IngameUser(p, this);
    }

    @Override
    public String getTabCompleterSuffix() {
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
