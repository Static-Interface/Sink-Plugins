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

package de.static_interface.sinkantispam;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarnUtil {

    static HashMap<UUID, Long> bannedPlayers = new HashMap<>();
    static HashMap<UUID, List<Warning>> warnings = new HashMap<>();
    static String prefix = m("SinkAntiSpam.Prefix") + ' ' + ChatColor.RESET;

    public static void warnPlayer(Player player, Warning warning) {
        List<Warning> tmp = warnings.get(player.getUniqueId());
        if (tmp == null) {
            tmp = new ArrayList<>();
        }
        tmp.add(warning);
        warnings.put(player.getUniqueId(), tmp);

        String message = prefix + m("SinkAntiSpam.Warn", SinkLibrary.getInstance().getUser(player).getDisplayName(),
                                    warning.getWarnedBy(), warning.getReason(), tmp.size(), getMaxWarnings());
        SinkUser user = SinkLibrary.getInstance().getUser(player);
        String perm;
        if (warning.isAutoWarning()) {
            perm = "sinkantispam.autowarnmessage";
            BukkitUtil.broadcast(message, perm, false);
        } else {
            perm = "sinkantispam.warnmessage";
            BukkitUtil.broadcast(message, perm, true);
        }
        if (!user.hasPermission(perm)) {
            player.sendMessage(message);
        }

        if (tmp.size() >= getMaxWarnings()) {
            UUID uuid = player.getUniqueId();
            player.kickPlayer(m("SinkAntiSpam.TooManyWarnings"));
            tempBanPlayer(player.getUniqueId(),
                          System.currentTimeMillis() + SinkLibrary.getInstance().getSettings().getWarnAutoBanTime() * 60 * 1000);
            warnings.put(uuid, new ArrayList<Warning>()); // Reset warnings
        }
    }

    private static void tempBanPlayer(UUID uniqueId, long unbanTimestamp) {
        bannedPlayers.put(uniqueId, unbanTimestamp);
    }

    public static boolean isBanned(UUID uniqueId) {
        Long unbantimeStamp = bannedPlayers.get(uniqueId);
        return unbantimeStamp != null && unbantimeStamp < System.currentTimeMillis();
    }

    public static int getMaxWarnings() {
        return 5;
    }
}
