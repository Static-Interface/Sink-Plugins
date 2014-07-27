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

package de.static_interface.sinkirc.irc_commands;

import de.static_interface.sinkirc.IrcUtil;
import de.static_interface.sinklibrary.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class KickCommand extends IrcCommand
{
    public KickCommand(Plugin plugin)
    {
        super(plugin);
        setUsage("Usage: " + IrcUtil.getCommandPrefix() + "kick <player> <reason>");
    }

    @Override
    public boolean isIrcOpOnly()
    {
        return true;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args)
    {
        String targetPlayerName;
        try
        {
            targetPlayerName = args[0];
        }
        catch ( Exception ignored )
        {
            return false;
        }
        final Player targetPlayer = BukkitUtil.getPlayer(targetPlayerName);
        if ( targetPlayer == null )
        {
            sender.sendMessage("Player \"" + targetPlayerName + "\" is not online!");
            return true;
        }

        String formattedReason;
        if ( args.length > 1 )
        {
            String reason = label.replace(targetPlayerName, "");
            reason = reason.replace("kick ", "").trim();
            formattedReason = " (Reason: " + reason + ')';
        }
        else
        {
            formattedReason = ".";
        }
        formattedReason = ChatColor.translateAlternateColorCodes('&', formattedReason);
        final String finalReason = "kicked by " + sender + " from IRC" + formattedReason;
        Bukkit.getScheduler().runTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                targetPlayer.kickPlayer("You've been " + finalReason);
            }
        });
        BukkitUtil.broadcastMessage(ChatColor.RED + targetPlayer.getDisplayName() + " has been " + finalReason, false);
        return true;
    }
}
