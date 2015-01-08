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

package de.static_interface.sinkantispam;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import com.google.gson.Gson;
import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarnUtil {

    public static String WARNINGS_PATH = "Warnings";
    static String prefix = m("SinkAntiSpam.Prefix") + ' ' + ChatColor.RESET;

    public static void warn(IngameUser target, Warning warning) {
        List<Warning> tmp = getWarnings(target);
        if (tmp == null) {
            tmp = new ArrayList<>();
        }
        tmp.add(warning);
        setWarnings(target, tmp);

        for (Warning w : tmp) {
            if (w.isDeleted()) {
                tmp.remove(w);
            }
        }

        int i = tmp.size() % getMaxWarnings() == 0 ? getMaxWarnings() : tmp.size() % getMaxWarnings();
        String message = prefix + m("SinkAntiSpam.Warn", target.getDisplayName(),
                                    warning.getWarnerDisplayName(), warning.getReason(), i, getMaxWarnings());
        String perm;
        if (warning.isAutoWarning()) {
            perm = "sinkantispam.autowarnmessage";
            BukkitUtil.broadcast(message, perm, false);
        } else {
            perm = "sinkantispam.warnmessage";
            BukkitUtil.broadcast(message, perm, true);
        }

        if (!target.hasPermission(perm)) {
            target.sendMessage(message);
        }

        if (i == getMaxWarnings()) {
            target.getPlayer().kickPlayer(m("SinkAntiSpam.TooManyWarnings"));
            long timeout = System.currentTimeMillis() + SinkLibrary.getInstance().getSettings().getWarnAutoBanTime() * 60 * 1000;
            target.ban(m("SinkAntiSpam.AutoBan"), timeout);
        }
    }

    public static void setWarnings(IngameUser user, List<Warning> deserializedWarnings) {
        IngameUserConfiguration config = user.getConfiguration();
        Gson gson = new Gson();
        List<String> serializedWarnings = new ArrayList<>();
        for (Warning warning : deserializedWarnings) {
            serializedWarnings.add(gson.toJson(warning));
        }
        config.set(WARNINGS_PATH, serializedWarnings);
        config.save();
    }

    public static List<Warning> getWarnings(IngameUser user) {
        IngameUserConfiguration config = user.getConfiguration();
        List<Warning> deserializedWarnings = new ArrayList<>();
        List<String> serializedWarnings = config.getYamlConfiguration().getStringList(WARNINGS_PATH);
        if (serializedWarnings == null) {
            setWarnings(user, deserializedWarnings);
        } else {
            Gson gson = new Gson();
            for (String json : serializedWarnings) {
                deserializedWarnings.add(gson.fromJson(json, Warning.class));
            }
        }
        Collections.sort(deserializedWarnings);
        //return fixWarningsIds(deserializedWarnings);
        return deserializedWarnings;
    }

    public static int getWarningId(IngameUser user) {
        return getWarnings(user).size() + 1;
    }

    public static int getMaxWarnings() {
        return SinkLibrary.getInstance().getSettings().getMaxWarnings();
    }

    //public static List<Warning> fixWarningsIds(List<Warning> warnings) {
    //    List<Warning> fixedWarnings = new ArrayList<>();
    //    int id = 1;
    //
    //    for(Warning warning : warnings) {
    //        warning.setId(id);
    //        fixedWarnings.add(warning);
    //        id++;
    //    }
    //
    //    return fixedWarnings;
    //}
}
