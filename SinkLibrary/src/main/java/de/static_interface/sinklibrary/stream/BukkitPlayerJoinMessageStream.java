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

package de.static_interface.sinklibrary.stream;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.stream.EventMessageStream;
import de.static_interface.sinklibrary.user.IngameUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BukkitPlayerJoinMessageStream extends EventMessageStream<IngameUser, PlayerJoinEvent> {

    public BukkitPlayerJoinMessageStream(Plugin plugin) {
        super(plugin);
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(PlayerJoinEvent event) {
        super.onEvent(event);
    }

    @Nullable
    @Override
    public IngameUser getUser(PlayerJoinEvent event) {
        return SinkLibrary.getInstance().getIngameUser(event.getPlayer());
    }

    @Nonnull
    @Override
    public String getMessage(PlayerJoinEvent event) {
        return event.getJoinMessage();
    }
}
