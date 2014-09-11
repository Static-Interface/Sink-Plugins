/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.listener;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConfigurationListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        SinkLibrary.loadUser(event.getPlayer().getUniqueId());

        SinkUser user = SinkLibrary.getUser(event.getPlayer());
        PlayerConfiguration config = user.getPlayerConfiguration();

        if ( !config.exists() )
        {
            config.init();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        SinkLibrary.unloadUser(event.getPlayer().getUniqueId());
    }
}
