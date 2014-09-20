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

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.sender.IrcCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //Get essentials
        com.earth2me.essentials.Essentials essentials =
                (com.earth2me.essentials.Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        List<String> out = new ArrayList<>(); // Output Message
        HashMap<String, List<SinkUser>> groupUsers = new HashMap<>(); // group - list of users in group
        List<Player> onlineUsers = BukkitUtil.getOnlinePlayers();
        out.add("");

        for (Player p : Bukkit.getOnlinePlayers()) {
            SinkUser user = SinkLibrary.getInstance().getUser(p);
            String userGroup;

            if (SinkLibrary.getInstance().isPermissionsAvailable()) {
                userGroup = user.getPrimaryGroup();
            } else {
                userGroup = user.getPlayer().isOp() ? "OP" : "Default";
            }

            List<SinkUser> users = groupUsers.get(userGroup);
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(user);
            Collections.sort(users);
            groupUsers.put(userGroup, users);
        }

        //Sort groups and format to "Group: Player1, Player2, Player n" format
        //todo use essentials permissions instead of isOp
        SortedSet<String> sortedGroups = new TreeSet<>(groupUsers.keySet());
        int vanishUsers = 0;
        for (String group : sortedGroups) {
            List<SinkUser> users = groupUsers.get(group);
            String tmp = "";
            int groupUserCount = 0;
            for (SinkUser user : users) {
                String prefix = "";
                if (essentials != null) {
                    com.earth2me.essentials.User essUser = essentials.getUser(user.getPlayer());

                    boolean hidden = essUser.isHidden();
                    if ((!sender.isOp() || sender instanceof IrcCommandSender) && hidden) {
                        vanishUsers++;
                        continue;
                    }

                    if (essUser.isAfk()) {
                        prefix += ChatColor.GRAY + "[Abwesend]" + ChatColor.RESET;
                    }

                    if (hidden) {
                        prefix += ChatColor.GRAY + "[Versteckt]" + ChatColor.RESET;
                        vanishUsers++;
                    }
                }

                if (tmp.equals("")) {
                    tmp = prefix + user.getDisplayName();
                } else {
                    tmp += ChatColor.GRAY + ", " + prefix + user.getDisplayName();
                }

                groupUserCount++;
            }
            if (groupUserCount > 0) {
                out.add(ChatColor.GOLD + group + ChatColor.RESET + ": " + tmp);
            }
        }

        if (SinkLibrary.getInstance().isIrcAvailable()) {
            HashMap<String, List<org.pircbotx.User>> ircGroupUsers = new HashMap<>(); // group - list of users in group
            Collection<org.pircbotx.User> onlineIrcUsers = de.static_interface.sinkirc.SinkIRC.getMainChannel().getUsers();
            out.add("");
            out.add(ChatColor.GOLD + "Online IRC Benutzer: ");
            if (onlineIrcUsers.size() < 1) {
                out.add("Zur Zeit sind keine Benutzer im IRC.");
            } else {
                for (org.pircbotx.User user : onlineIrcUsers) {
                    String userGroup = de.static_interface.sinkirc.IrcUtil.isOp(user) ? "OP" : "Default";
                    List<org.pircbotx.User> users = ircGroupUsers.get(userGroup);
                    if (users == null) {
                        users = new ArrayList<>();
                    }
                    users.add(user);
                    Collections.sort(users);
                    ircGroupUsers.put(userGroup, users);
                }

                sortedGroups = new TreeSet<>(ircGroupUsers.keySet());
                for (String group : sortedGroups) {
                    List<org.pircbotx.User> users = ircGroupUsers.get(group);
                    String tmp = "";
                    for (org.pircbotx.User user : users) {
                        String formattedNick = user.getNick();
                        if (formattedNick.equals(de.static_interface.sinkirc.SinkIRC.getIrcBot().getNick())) {
                            continue;
                        }

                        formattedNick = de.static_interface.sinkirc.IrcUtil.getFormattedName(user);

                        if (tmp.equals("")) {
                            tmp = formattedNick;
                        } else {
                            tmp += ChatColor.GRAY + ", " + formattedNick;
                        }
                    }
                    out.add(ChatColor.GOLD + group + ChatColor.RESET + ": " + tmp);
                }
            }
        }

        /*

         */

        int onlineUsersCount = onlineUsers.size() - vanishUsers;
        String tmp = onlineUsersCount == 1 ? "ist" : "sind";

        String onlineUsersWithVanishPlayers = ChatColor.RED.toString() + onlineUsersCount
                                              + ChatColor.GOLD + (vanishUsers > 0 && (sender.isOp() && !(sender instanceof IrcCommandSender))
                                                                  ? "/" + vanishUsers : "");

        //Send online players + vanish players count
        out.add(1, ChatColor.GOLD + "Es " + tmp + " " + onlineUsersWithVanishPlayers + " von maximal "
                   + ChatColor.RED + Bukkit.getMaxPlayers() + ChatColor.GOLD + " Spielern online.");
        out.add("");
        String[] msgs = out.toArray(new String[out.size()]);
        sender.sendMessage(msgs);
        return true;
    }
}
