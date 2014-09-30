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

import com.google.gson.Gson;
import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.UserConfiguration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarnUtil {

    public static String WARNINGS_PATH = "SinkAntiSpam.Warnings";
    static String prefix = m("SinkAntiSpam.Prefix") + ' ' + ChatColor.RESET;

    public static void warnPlayer(Player player, Warning warning) {
        SinkUser user = SinkLibrary.getInstance().getUser(player);
        List<Warning> tmp = getWarnings(SinkLibrary.getInstance().getUser(player));
        if (tmp == null) {
            tmp = new ArrayList<>();
        }
        tmp.add(warning);
        setWarnings(user, tmp);

        String message = prefix + m("SinkAntiSpam.Warn", SinkLibrary.getInstance().getUser(player).getDisplayName(),
                                    warning.getWarnedBy(), warning.getReason(), (tmp.size() % 5) + 1, getMaxWarnings());
        String perm;
        if (warning.isAutoWarning()) {
            perm = "sinkantispam.autowarnmessage";
            BukkitUtil.broadcast(message, perm, false);
        } else {
            perm = "sinkantispam.warnmessage";
            BukkitUtil.broadcast(message, perm, true);
        }

        if (!user.hasPermission(perm)) {
            user.sendMessage(message);
        }

        if (tmp.size() % getMaxWarnings() == 0) {
            player.kickPlayer(m("SinkAntiSpam.TooManyWarnings"));
            long bantime = System.currentTimeMillis() + SinkLibrary.getInstance().getSettings().getWarnAutoBanTime() * 60 * 1000;
            user.ban(m("SinkAntiSpam.AutoBan"), bantime);
        }
    }

    public static void setWarnings(SinkUser user, List<Warning> deserializedWarnings) {
        UserConfiguration config = user.getConfiguration();
        Gson gson = new Gson();
        List<String> serializedWarnings = new ArrayList<>();
        for (Warning warning : deserializedWarnings) {
            serializedWarnings.add(gson.toJson(warning));
        }
        config.set(WARNINGS_PATH, serializedWarnings);
    }

    public static List<Warning> getWarnings(SinkUser user) {
        UserConfiguration config = user.getConfiguration();
        List<Warning> deserializedWarnings = new ArrayList<>();
        List<String> serializedWarnings = config.getYamlConfiguration().getStringList(WARNINGS_PATH);
        if (serializedWarnings == null) {
            setWarnings(user, deserializedWarnings);
            return deserializedWarnings;
        }

        Gson gson = new Gson();
        for (String json : serializedWarnings) {
            deserializedWarnings.add(gson.fromJson(json, Warning.class));
        }
        return deserializedWarnings;
    }

    public static int getWarningId(SinkUser user) {
        return getWarnings(user).size() + 1;
    }

    public static int getMaxWarnings() {
        return SinkLibrary.getInstance().getSettings().getMaxWarnings();
    }
}
