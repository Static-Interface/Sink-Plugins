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

package de.static_interface.sinklibrary.api.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SinkTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
                                      String[] args) {
        List<String> result = new ArrayList<>();
        HashMap<UUID, String> tmp = new HashMap<>();

        SinkLibrary.getInstance().getCustomLogger()
                .debug("onTabComplete: sender: " + sender.getName() + ", cmd: " + cmd.getName() + ", label: " + label + ", args: " + StringUtil
                        .formatArrayToString(
                                args, ", "));
        for (Player p : Bukkit.getOnlinePlayers()) {
            IngameUser user = SinkLibrary.getInstance().getUser(p);
            String name = user.getName();
            String displayName = ChatColor.stripColor(user.getDisplayName());
            UUID uuid = user.getUniqueId();

            // only add one entry per user
            if (tmp.get(uuid) != null) {
                SinkLibrary.getInstance().getCustomLogger().debug("Skipping user " + user.getName() + ": user already added: " + tmp.get(uuid));
                continue;
            }

            // check if alias starts with displayname, if not, check if starts with default name
            if (StringUtil.isStringEmptyOrNull(label) || displayName.startsWith(label)
                || user.getName().startsWith(label)) {
                SinkLibrary.getInstance().getCustomLogger().debug("Adding value: " + name);
                tmp.put(uuid, name);
            }
        }

        result.addAll(tmp.values());
        return result;
    }
}
