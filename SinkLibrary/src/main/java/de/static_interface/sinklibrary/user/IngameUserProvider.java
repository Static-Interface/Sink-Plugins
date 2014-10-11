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

import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import javax.annotation.Nullable;

public class IngameUserProvider extends SinkUserProvider {

    @Nullable
    @Override
    public SinkUser getUserInstance(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null) {
            return getUserInstance(p);
        }

        OfflinePlayer of = Bukkit.getOfflinePlayer(name);
        return getUserInstance(of.getUniqueId());
    }

    @Override
    @Nullable
    public SinkUser newInstance(CommandSender sender) {
        Player p = (Player) sender;
        return new IngameUser(p.getUniqueId(), this);
    }

    @Override
    public String getCommandArgsSuffix() {
        return "";
    }

    public SinkUser getUserInstance(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return getUserInstance(p);
        }

        return new IngameUser(uuid, this);
    }
}
