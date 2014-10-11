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
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.user.IrcUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SinkTabCompleter implements TabCompleter {

    private final boolean includeIrcUsers;

    public SinkTabCompleter(boolean includeIrcUsers) {
        this.includeIrcUsers = includeIrcUsers;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
                                      String[] args) {
        List<String> result = new ArrayList<>();
        HashMap<SinkUser, String> tmp = new HashMap<>();
        String s = args[args.length - 1];

        List<SinkUser> users = new ArrayList<>();
        for (IngameUser user : SinkLibrary.getInstance().getOnlineUsers()) {
            users.add(user);
        }

        if (includeIrcUsers) {
            for (IrcUser user : SinkLibrary.getInstance().getOnlineIrcUsers()) {
                users.add(user);
            }
        }

        SinkLibrary.getInstance().getCustomLogger().debug("onTabComplete: sender: " + sender.getName() +
                                                          ", cmd: " + cmd.getName() + ", label: " + label + ", s: " + s + ", args: " + StringUtil
                .formatArrayToString(
                        args, ", "));

        for (SinkUser user : users) {
            String name = user.getName() + user.getProvider().getCommandArgsSuffix();
            String displayName = user.getDisplayName();

            boolean hasDisplayname = false;
            if (!StringUtil.isStringEmptyOrNull(displayName)) {
                hasDisplayname = true;
                displayName = ChatColor.stripColor(displayName) + user.getProvider().getCommandArgsSuffix();
            }

            // only add one entry per user
            if (tmp.keySet().contains(user) || (hasDisplayname && tmp.values().contains(displayName))
                || tmp.values().contains(name)) {
                continue;
            }

            // check if alias starts with displayname, if not, check if starts with default name
            if (!StringUtil.isStringEmptyOrNull(s)) {
                if (hasDisplayname && displayName.startsWith(s)) {
                    tmp.put(user, displayName);
                    continue;
                }

                if (user.getName().startsWith(s)) {
                    tmp.put(user, name);
                }
            } else {
                tmp.put(user, displayName);
            }
        }

        result.addAll(tmp.values());
        Collections.sort(result);
        return result;
    }
}
