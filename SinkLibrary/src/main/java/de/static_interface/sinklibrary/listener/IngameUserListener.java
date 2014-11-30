/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.listener;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.model.BanData;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class IngameUserListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Debug.logMethodCall(event);
        SinkLibrary.getInstance().loadUser(event.getPlayer());

        IngameUser user = SinkLibrary.getInstance().getIngameUser(event.getPlayer());
        IngameUserConfiguration config = user.getConfiguration();

        if (!config.exists()) {
            config.init();
        }

        BanData result = user.getBanData();
        Debug.log(result);
        if (!result.isBanned()) {
            Debug.log("Player is not banned.");
            return;
        }

        String timeLeft = "";

        if (result.getTimeOut() < System.currentTimeMillis()) {
            Debug.log("Bantimeout occurred. Unbanning player.");
            user.unban();
            return;
        }

        Debug.log("User is banned, calculating time left...");

        if (result.getTimeOut() > 0) {
            long t = result.getTimeOut() - System.currentTimeMillis();
            timeLeft = ", " + ChatColor.GOLD + m("General.TimeLeft", StringUtil.timeLeftDateToString(new Date(t)));
        }
        String kickMessage = m("General.Banned") + result.getReason() + timeLeft;
        Debug.log("Denying user: " + user.getName() + "(KICK_BANNED, kickMessage: " + kickMessage + ")");
        event.setKickMessage(kickMessage);
        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SinkLibrary.getInstance().unloadUser(event.getPlayer().getUniqueId());
    }
}
