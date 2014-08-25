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

package de.static_interface.sinkantispam;

import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

public class WarnCommand extends Command
{
    public static final String PREFIX = ChatColor.RED + "[Warn] " + ChatColor.RESET;

    public WarnCommand(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean isIrcOpOnly()
    {
        return true;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args)
    {
        if ( args.length < 2 )
        {
            sender.sendMessage(PREFIX + m("General.CommandMisused.Arguments.TooFew"));
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: " + getCommandPrefix() + "warn [Player] [Reason]");
            return false;
        }
        Player target = (BukkitUtil.getPlayer(args[0]));
        if ( target == null )
        {
            sender.sendMessage(PREFIX + String.format(m("General.NotOnline"), args[0]));
            return true;
        }
        if ( target.getName().equals(sender.getName()))
        {
            sender.sendMessage(PREFIX + m("SinkAntiSpam.WarnSelf"));
            return true;
        }

        String reason = "";

        for ( int i = 1; i < args.length; i++ )
        {
            if ( reason.isEmpty() )
            {
                reason = args[i];
                continue;
            }
            reason = reason + ' ' + args[i];
        }

        WarnUtil.warnPlayer(target, new Warning(reason, SinkLibrary.getUser(sender).getDisplayName(), true));
        return true;
    }
}
