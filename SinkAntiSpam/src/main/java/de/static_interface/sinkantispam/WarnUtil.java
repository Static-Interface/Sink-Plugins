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

import de.static_interface.sinkantispam.database.row.PredefinedWarning;
import de.static_interface.sinkantispam.database.row.Warning;
import de.static_interface.sinkantispam.database.table.PredefinedWarningsTable;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.Identifiable;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class WarnUtil {
    static String prefix = m("SinkAntiSpam.Prefix") + ' ' + ChatColor.RESET;

    public static void performWarning(Warning warning) {
        IngameUser target = SinkLibrary.getInstance().getIngameUser(UUID.fromString(warning.player));
        List<Warning> tmp = getWarnings(target, false);
        if (tmp == null) {
            tmp = new ArrayList<>();
        }
        tmp.add(warning);

        addWarning(warning);

        String message = prefix + m("SinkAntiSpam.Warn", target.getDisplayName(),
                                    warning.getWarnerDisplayName(), warning.reason, warning.points);
        String perm;
        if (warning.isAutoWarning) {
            perm = "sinkantispam.autowarnmessage";
        } else {
            perm = "sinkantispam.warnmessage";
        }

        BukkitUtil.broadcast(message, perm, true);

        if (target.isOnline() && !target.hasPermission(perm)) {
            target.sendMessage(message);
        }
    }

    public static List<Warning> getWarnings(IngameUser user, boolean includeDeleted) {
        String sql = "SELECT * FROM `{TABLE}` WHERE player = ?";
        if (!includeDeleted) {
            sql += " AND is_deleted = 0";
        }
        sql += ";";
        List<Warning> warnings = Arrays.asList(SinkAntiSpam.getInstance().getWarningsTable().get(sql, user.getUniqueId().toString()));
        Collections.sort(warnings);

        return warnings;
    }

    public static int getNextWarningId(IngameUser user) {
        return getWarnings(user, true).size() + 1;
    }

    public static void addWarning(Warning warning) {
        SinkAntiSpam.getInstance().getWarningsTable().insert(warning);
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
        PredefinedWarning[] res = tbl.get("SELECT * FROM `{TABLE}` WHERE `nameId` = ?", name);
        if (res == null || res.length < 1) {
            return null;
        }
        return res[0];
    }
}
