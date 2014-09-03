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

package de.static_interface.sinkcommands.commands;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.irc.IrcCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.User;

import java.util.*;

public class ListCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        com.earth2me.essentials.Essentials essentials =
                (com.earth2me.essentials.Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        List<String> out = new ArrayList<>();
        HashMap<String, List<SinkUser>> groupUsers = new HashMap<>();
        Collection<SinkUser> onlineUsers = SinkLibrary.getOnlineUsers();
        for ( SinkUser user :  onlineUsers)
        {
            String userGroup;
            if(SinkLibrary.isPermissionsAvailable())
                userGroup = user.getPrimaryGroup();
            else
                userGroup = user.getPlayer().isOp() ? "OP" : "Default";
            List<SinkUser> users = groupUsers.get(userGroup);
            if ( users == null )
            {
                users = new ArrayList<>();
            }

            Collections.sort(users);
            groupUsers.put(userGroup, users);
        }

        SortedSet<String> sortedGroups = new TreeSet<>(groupUsers.keySet());
        int i = 0;
        for ( String group : sortedGroups )
        {
            List<SinkUser> users = groupUsers.get(group);
            String tmp = "";
            for ( SinkUser user : users )
            {
                String prefix = "";
                if(essentials != null)
                {
                    com.earth2me.essentials.User essUser = essentials.getUser(user.getPlayer());

                    if(!sender.isOp() || sender instanceof IrcCommandSender)
                    {
                        continue;
                    }

                    if(essUser.isAfk())
                    {
                        prefix += ChatColor.GRAY + "[Abwesend]" + ChatColor.RESET;
                    }

                    if(essUser.isHidden())
                    {
                        prefix += ChatColor.GRAY + "[Versteckt]" + ChatColor.RESET;
                        i++;
                    }
                }

                if ( tmp.equals("") )
                {
                    tmp = prefix + user.getDisplayName();
                }
                else
                {
                    tmp += ChatColor.GRAY + ", " + prefix + user.getDisplayName();
                }
            }

            out.add(ChatColor.GOLD + group + ChatColor.RESET + ": " + tmp);
        }

        if(SinkLibrary.isIrcAvailable())
        {
            out.add(ChatColor.GOLD + "Online IRC Benutzer: ");
            String ircUsers = "";
            for ( User user : de.static_interface.sinkirc.SinkIRC.getMainChannel().getUsers() )
            {
                String formattedNick = user.getNick();
                if ( formattedNick.equals(de.static_interface.sinkirc.SinkIRC.getIrcBot().getNick()) )
                {
                    continue;
                }

                formattedNick = de.static_interface.sinkirc.IrcUtil.getFormattedName(user);

                if ( ircUsers.isEmpty() )
                {
                    ircUsers = formattedNick;
                }
                else
                {
                    ircUsers = ircUsers + ", " + formattedNick;
                }
            }
            if (de.static_interface.sinkirc.SinkIRC.getMainChannel().getUsers().size() <= 1 )
            {
                ircUsers = "Zur Zeit sind keine Benutzer im IRC.";
            }
            out.add(ircUsers);
        }

        String onlineUsersCount = ChatColor.RED.toString() + (onlineUsers.size()-i)
                                    + ChatColor.GOLD + (i > 0 ? "/" +i : "");
        out.add(0, ChatColor.GOLD + "Es sind " +  onlineUsersCount + " von maximal "
                + ChatColor.RED + Bukkit.getMaxPlayers() + ChatColor.GOLD + " online.");

        String[] msgs = out.toArray(new String[out.size()]);
        sender.sendMessage(msgs);
        return true;
    }
}
