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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.configuration.UserConfiguration;
import de.static_interface.sinklibrary.api.model.BanData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserConfigurationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        SinkLibrary.getInstance().loadUser(event.getPlayer());

        IngameUser user = SinkLibrary.getInstance().getUser(event.getPlayer());
        UserConfiguration config = user.getConfiguration();

        if (!config.exists()) {
            config.init();
        }
        BanData result = user.getBanInfo();
        if (!result.isBanned()) {
            return;
        }

        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        String timeLeft = "";
        if (result.getUnbanTime() > 0) {
            long t = System.currentTimeMillis() - result.getUnbanTime();
            timeLeft = " " + m("General.TimeLeft", t / 1000 * 60);
        }
        event.setKickMessage(result.getReason() + timeLeft);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SinkLibrary.getInstance().unloadUser(event.getPlayer().getUniqueId());
    }
}
