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

package de.static_interface.sinklibrary.api.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SinkTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
                                      String[] args) {
        List<String> result = new ArrayList<>();

        SinkCommand command = SinkLibrary.getInstance().getCustomCommand(cmd.getName());

        SinkTabCompleterOptions options;
        if (command != null) {
            options = command.getTabCompleterOptions();
        } else {
            options = new SinkTabCompleterOptions(true, false, false);
        }
        boolean includeIngameUsers = options.includeIngameUsers();
        boolean includeIrcUsers = options.includeIrcUsers();
        boolean includeSuffix = options.includeSuffix();

        if (!includeIngameUsers && !includeIrcUsers) {
            return result;
        }

        List<SinkUser> users = new ArrayList<>();
        HashMap<SinkUser, String> tmp = new HashMap<>();
        String s = args[args.length - 1];

        if (includeIngameUsers) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player && !((Player) sender).canSee(p) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                    continue;
                }
                users.add(SinkLibrary.getInstance().getIngameUser(p));
            }
        }

        if (includeIrcUsers) {
            for (IrcUser user : SinkLibrary.getInstance().getOnlineIrcUsers()) {
                users.add(user);
            }
        }

        Collections.sort(users);
        for (SinkUser user : users) {
            String suffix = user.getProvider().getTabCompleterSuffix();

            String name = user.getName();

            if (includeSuffix) {
                name = name + suffix;
            }

            String displayName = user.getDisplayName() == null ? "" : user.getDisplayName();

            if (includeSuffix) {
                displayName = ChatColor.stripColor(displayName) + suffix;
            } else {
                displayName = ChatColor.stripColor(displayName);
            }

            boolean hasDisplayname = false;

            if (!StringUtil.isEmptyOrNull(displayName.replace(suffix, ""))) {
                hasDisplayname = true;
            }

            // only add one entry per user

            boolean valuesContains = false;
            for (String valuename : tmp.values()) {
                if ((hasDisplayname && valuename.equalsIgnoreCase(displayName)) ||
                    valuename.equalsIgnoreCase(name)) {
                    valuesContains = true;
                    break;
                }
            }
            if (tmp.keySet().contains(user) || (valuesContains)) {
                continue;
            }

            // check if alias starts with displayname, if not, check if starts with default name
            if (!StringUtil.isEmptyOrNull(s)) {
                if (hasDisplayname && displayName.toLowerCase().startsWith(s.toLowerCase())) {
                    tmp.put(user, displayName);
                    continue;
                }

                if (user.getName().toLowerCase().startsWith(s.toLowerCase())) {
                    tmp.put(user, name);
                }
            } else {
                tmp.put(user, displayName);
            }
        }

        result.addAll(tmp.values());
        return result;
    }
}
