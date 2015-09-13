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

package de.static_interface.sinkantispam.sanction.impl;

import de.static_interface.sinkantispam.sanction.WarningSanction;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;

public class KickWarningSanction implements WarningSanction {

    @Override
    public String getId() {
        return "KICK";
    }

    @Override
    public void execute(IngameUser user, String[] args) {
        if (!user.isOnline()) {
            return;
        }

        String reason = StringUtil.formatArrayToString(args, " ").trim();
        if (StringUtil.isEmptyOrNull(reason)) {
            reason = ChatColor.DARK_RED + "Too many warnings";
        }
        user.getPlayer().kickPlayer(reason);
    }
}
