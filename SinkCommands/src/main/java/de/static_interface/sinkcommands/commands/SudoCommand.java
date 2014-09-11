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

import de.static_interface.sinklibrary.FakeCommandSender;
import de.static_interface.sinklibrary.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SudoCommand extends Command
{
    public SudoCommand(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args)
    {
        if(args.length < 1)
        {
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null)
        {
            sender.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Spieler ist nicht online!");
            return true;
        }

        String commandLine = "";
        for(int i = 1; i < args.length; i++)
        {
            if(commandLine.equals(""))
            {
                commandLine = args[i];
                continue;
            }
            commandLine += " " + args[i];
        }

        FakeCommandSender fakeSender = new FakeCommandSender(target, sender);
        Bukkit.dispatchCommand(fakeSender, commandLine);
        return true;
    }
}
