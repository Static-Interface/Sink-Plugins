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

import static de.static_interface.sinklibrary.database.query.Query.eq;
import static de.static_interface.sinklibrary.database.query.Query.from;
import static de.static_interface.sinklibrary.database.query.Query.gt;

import de.static_interface.sinkantispam.config.SasLanguage;
import de.static_interface.sinkantispam.config.SasSettings;
import de.static_interface.sinkantispam.database.row.PredefinedWarning;
import de.static_interface.sinkantispam.database.row.WarnedPlayer;
import de.static_interface.sinkantispam.database.row.Warning;
import de.static_interface.sinkantispam.database.table.PredefinedWarningsTable;
import de.static_interface.sinkantispam.sanction.WarningSanction;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.Identifiable;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.database.query.Order;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class WarnUtil {

    static String prefix = SasLanguage.SAS_PREFIX.format() + ' ' + ChatColor.RESET;

    public static void performWarning(Warning warning, SinkUser warner) {
        WarnedPlayer wPlayer = getWarnedPlayer(warning.userId);
        IngameUser target = SinkLibrary.getInstance().getIngameUser(UUID.fromString(wPlayer.playerUuid));
        int pointsBefore = getPoints(target);
        addWarning(warning);

        String message = prefix + SasLanguage.SAS_WARN_MESSAGE.format(warner, target, null, warning.reason, warning.points);
        String perm;
        if (warning.isAutoWarning) {
            perm = "sinkantispam.autowarnmessage";
        } else {
            perm = "sinkantispam.warnmessage";
        }

        BukkitUtil.broadcast(message, perm, !warning.isAutoWarning);

        if (target.isOnline() && !target.hasPermission(perm)) {
            target.sendMessage(message);
        }

        int pointsNow = getPoints(target);
        int matched = -1;
        List<String> strings = null;
        for (int i : getConfigSanctions().keySet()) {
            if (i == pointsNow) {
                matched = i;
                break;
            }

            if (pointsBefore < i && pointsNow > i && i > matched) {
                matched = i;
            }
        }

        if (matched != -1) {
            strings = getConfigSanctions().get(matched);
        }

        if (strings == null) {
            return;
        }

        String name = strings.get(0);
        WarningSanction sanction = SinkAntiSpam.getInstance().getWarningSanction(name);
        if (sanction == null) {
            SinkAntiSpam.getInstance().getLogger().warning("Sanction type not found: " + name);
            return;
        }
        String[] args = new String[strings.size() - 1];

        int i = 0;
        boolean skipped = false;
        for (String s : strings) {
            if (!skipped) {
                skipped = true;
                continue;
            }

            args[i] = s;
            i++;
        }

        sanction.execute(target, args);
    }

    private static Map<Integer, List<String>> getConfigSanctions() {
        Map<Integer, List<String>> map = new HashMap<>();
        for (String s : SasSettings.SAS_SANCTIONS.getValue()) {
            String[] splits = s.split(":");
            if (splits.length < 2 || StringUtil.isEmptyOrNull(splits[1])) {
                SinkAntiSpam.getInstance().getLogger().warning("Couldn't parse sanction: " + s + ": Invalid format");
                continue;
            }

            Integer points;
            try {
                points = Integer.valueOf(splits[0]);
            } catch (Exception e) {
                SinkAntiSpam.getInstance().getLogger().warning("Couldn't parse sanction: " + s + ": Invalid points: " + splits[0]);
                continue;
            }

            if (map.containsKey(points)) {
                SinkAntiSpam.getInstance().getLogger().warning("Couldn't parse sanction: " + s + ": Points already added: " + points);
                continue;
            }

            String label = "";
            for (String a : splits) {
                if (label.equalsIgnoreCase("")) {
                    label = a;
                    continue;
                }
                label += ":" + a;

            }

            splits = label.split(" ");
            List<String> args = new ArrayList<>();
            for (int i = 1; i < splits.length; i++) {
                args.add(splits[i]);
            }
            map.put(points, args);
        }

        return map;
    }

    public static List<Warning> getWarnings(IngameUser user, boolean includeDeleted) {
        int userId = getWarnedPlayer(user).id;
        Warning[] warnings;
        if (!includeDeleted) {
            warnings = from(SinkAntiSpam.getInstance().getWarningsTable()).select()
                    .where("user_id", eq("?"))
                    .and("expireTime", gt("?")).openParanthesis()
                    .or("expire_time", eq(null)).closeParanthesis()
                    .orderBy("id", Order.ASC)
                    .getResults(userId, System.currentTimeMillis());
        } else {
            warnings = from(SinkAntiSpam.getInstance().getWarningsTable()).select()
                    .where("user_id", eq("?"))
                    .orderBy("id", Order.ASC)
                    .getResults(userId, System.currentTimeMillis());
        }

        List<Warning> sortedWarnings = Arrays.asList(warnings);
        Collections.sort(sortedWarnings);

        return sortedWarnings;
    }

    public static int getNextWarningId(IngameUser user) {
        return getWarnings(user, true).size() + 1;
    }

    public static void addWarning(Warning warning) {
        WarnedPlayer wPlayer = getWarnedPlayer(warning.userId);
        IngameUser user = SinkLibrary.getInstance().getIngameUser(UUID.fromString(wPlayer.playerUuid));
        if (wPlayer.deleted_points > 0 && getWarnings(user, false).size() == 0) {
            setDeletedPoints(wPlayer, 0);
        }

        SinkAntiSpam.getInstance().getWarningsTable().insert(warning);
    }

    @Nullable
    private static WarnedPlayer getWarnedPlayer(int userId) {
        return from(SinkAntiSpam.getInstance().getWarnedPlayersTable()).select().where("id", eq("?")).get(userId);
    }

    public static void deleteWarning(Warning warning, @Nullable SinkUser deleter) {
        UUID uuid = null;
        if (deleter != null && deleter instanceof Identifiable) {
            uuid = ((Identifiable) deleter).getUniqueId();
        }
        String name = deleter == null ? null : ((deleter instanceof IrcUser) ? deleter.getDisplayName() + " (IRC)" : deleter.getDisplayName());

        SinkAntiSpam.getInstance().getWarningsTable().executeUpdate(
                "UPDATE `{TABLE}` SET `is_deleted` = 1, `deleter` = ?, `deleter_uuid` = ?, `delete_time` = ? "
                + "WHERE `id` = ?",
                name, uuid == null ? null : uuid.toString(), System.currentTimeMillis(), warning.id);
    }

    public static PredefinedWarning getPredefinedWarning(String name) {
        PredefinedWarningsTable tbl = SinkAntiSpam.getInstance().getPredefinedWarningsTable();
        return from(tbl).select().where("nameId", eq("?")).get(name);
    }

    public static WarnedPlayer getWarnedPlayer(IngameUser user) {
        WarnedPlayer
                result =
                from(SinkAntiSpam.getInstance().getWarnedPlayersTable()).select().where("player_uuid", eq("?")).get(user.getUniqueId().toString());
        if (result == null) {
            return insertWarnedUser(user);
        }
        return result;
    }

    public static void setDeletedPoints(WarnedPlayer wPlayer, int points) {
        if (points < 0) {
            return;
        }
        SinkAntiSpam.getInstance().getWarnedPlayersTable()
                .executeUpdate("UPDATE `{TABLE}` SET `deleted_points` = ? WHERE `id` = ?", points, wPlayer.id);
    }

    public static int getPoints(IngameUser target) {
        int points = 0;
        WarnedPlayer player = getWarnedPlayer(target);
        for (Warning warning : getWarnings(target, false)) {
            points += warning.points;
        }

        points -= player.deleted_points;
        return points;
    }

    private static WarnedPlayer insertWarnedUser(IngameUser user) {
        WarnedPlayer wPlayer = new WarnedPlayer();
        wPlayer.playerName = user.getName();
        wPlayer.playerUuid = user.getUniqueId().toString();
        wPlayer.deleted_points = 0;
        return SinkAntiSpam.getInstance().getWarnedPlayersTable().insert(wPlayer);
    }
}
