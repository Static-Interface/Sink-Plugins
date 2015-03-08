/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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
import de.static_interface.sinklibrary.api.user.SinkUserProvider;
import de.static_interface.sinklibrary.sender.ProxiedPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ProxiedIngameUser extends IngameUser {

    private ProxiedPlayer proxiedPlayer;
    private IngameUser baseUser;

    ProxiedIngameUser(OfflinePlayer base, SinkUserProvider provider) {
        super(base, provider);
        proxiedPlayer = (ProxiedPlayer) base;
        baseUser = SinkLibrary.getInstance().getIngameUser((Player) proxiedPlayer.getCallee());
    }

    @Override
    public int hashCode() {
        return baseUser.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return proxiedPlayer.equals(o);
    }
}
