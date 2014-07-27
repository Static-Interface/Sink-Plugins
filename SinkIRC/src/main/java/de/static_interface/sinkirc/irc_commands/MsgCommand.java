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
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import de.static_interface.sinklibrary.irc.IrcCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class MsgCommand extends IrcCommand
{
    public MsgCommand(Plugin plugin)
    {
        super(plugin);
        setUsage("Usage: " + IrcUtil.getCommandPrefix() + "privmsg <target> <msg>");
    }

    @Override
    public boolean onExecute(CommandSender cs, String label, String[] args)
    {
        IrcCommandSender sender = (IrcCommandSender) cs;
        if ( args.length < 2 )
        {
            return false;
        }
        SinkUser target;
        target = SinkLibrary.getUser(args[0]);
        if ( !target.isOnline() )
        {
            sender.sendMessage(LanguageConfiguration.m("General.NotOnline").replace("%s", args[0]));
            return true;
        }

        String message = ChatColor.YELLOW + "[IRC] [PRIVMSG] " + ChatColor.DARK_AQUA + sender.getName() + ChatColor.YELLOW + ": " + ChatColor.WHITE;

        for ( int x = 1; x < args.length; x++ )
        {
            message += ' ' + args[x];
        }

        message = message.replace("null", "").trim();

        target.getPlayer().sendMessage(message);
        sender.sendMessage("Message was send to \"" + target.getDisplayName() + ChatColor.RESET + "\"!");
        return true;
    }
}
