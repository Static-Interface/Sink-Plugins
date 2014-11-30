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

package de.static_interface.sinkcommands;

import de.static_interface.sinkcommands.command.LagCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;

public class LagTimer implements Runnable {

    private static boolean send = false;
    String PREFIX = LagCommand.PREFIX;

    @Override
    public void run() {
        double tps = SinkLibrary.getInstance().getSinkTimer().getAverageTPS();
        if (tps <= 17 && !send) {
            BukkitUtil.broadcastMessage(PREFIX + ChatColor.RED + "Der Server laggt gerade!");
            send = true;
        } else if (tps <= 18.5 && !send) {
            BukkitUtil.broadcastMessage(PREFIX + ChatColor.YELLOW + "Der Server könnte gerade etwas laggen!");
            send = true;
        } else {
            send = false;
        }
    }
}
