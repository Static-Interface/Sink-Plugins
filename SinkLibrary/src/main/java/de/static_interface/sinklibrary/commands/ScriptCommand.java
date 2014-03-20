/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.commands;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ScriptCommand implements CommandExecutor
{
    static ArrayList<String> enabledUsers = new ArrayList<>();

    public static boolean isEnabled(User user)
    {
        return enabledUsers.contains(user.getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        User user = SinkLibrary.getUser(sender);
        if ( isEnabled(user) )
        {
            disable(user);
            user.sendMessage(ChatColor.DARK_RED + "Disabled Interactive Groovy Console");
            return true;
        }
        enable(user);
        user.sendMessage(ChatColor.DARK_GREEN + "Enabled Interactive Groovy Console");
        return true;
    }

    public static void enable(User user)
    {
        enabledUsers.add(user.getName());
    }

    public static void disable(User user)
    {
        enabledUsers.remove(user.getName());
    }
}
