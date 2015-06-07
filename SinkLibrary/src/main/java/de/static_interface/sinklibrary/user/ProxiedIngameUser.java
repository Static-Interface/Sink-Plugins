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
import de.static_interface.sinklibrary.sender.ProxiedObject;
import de.static_interface.sinklibrary.sender.ProxiedPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProxiedIngameUser extends IngameUser implements ProxiedObject<IngameUser, CommandSender> {

    private ProxiedPlayer proxiedPlayer;
    private IngameUser baseUser;
    private CommandSender proxy;
    ProxiedIngameUser(OfflinePlayer base, SinkUserProvider provider) {
        super(base, provider);
        proxiedPlayer = (ProxiedPlayer) base;
        proxy = proxiedPlayer.getProxy();
        baseUser = SinkLibrary.getInstance().getIngameUser(proxiedPlayer.getBaseObject());
    }

    @Override
    public int hashCode() {
        return baseUser.hashCode();
    }

    @Override
    public Player getPlayer() {
        return proxiedPlayer;
    }

    @Override
    public boolean equals(Object o) {
        return baseUser.equals(o);
    }

    @Override
    public IngameUser getBaseObject() {
        return baseUser;
    }

    @Override
    public CommandSender getProxy() {
        return proxy;
    }
}
