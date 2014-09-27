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

package de.static_interface.sinkirc.irc_command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends IrcCommand {

    public ListCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        if (BukkitUtil.getOnlinePlayers().size() == 0) {
            sender.sendMessage("There are currently no online players");
            return true;
        }

        List<String> out = new ArrayList<>();
        String onlineMessage = "Online Players (" + BukkitUtil.getOnlinePlayers().size() + '/' + Bukkit.getMaxPlayers() + "): ";

        boolean firstPlayer = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            SinkUser user = SinkLibrary.getInstance().getUser(player);
            if (firstPlayer) {
                onlineMessage += user.getDisplayName();
                firstPlayer = false;
            } else {
                if (onlineMessage.length() > 0) {
                    onlineMessage += ChatColor.RESET + ", ";
                }

                onlineMessage += user.getDisplayName();
            }

            // standard length: 512 (inclusive headers)
            if (onlineMessage.length() > 400) {
                out.add(onlineMessage);
                onlineMessage = "";
            }
        }

        if (onlineMessage.length() > 0) {
            out.add(onlineMessage);
        }

        sender.sendMessage(out.toArray(new String[out.size()]));

        return true;
    }
}
