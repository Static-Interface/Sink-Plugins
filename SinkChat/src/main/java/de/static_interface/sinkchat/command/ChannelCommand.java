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

package de.static_interface.sinkchat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;
import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class ChannelCommand implements CommandExecutor
{
    public static final String PREFIX = _("SinkChat.Prefix.Channel") + ' ' + ChatColor.RESET;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        /*
        User user = SinkLibrary.getUser(sender);
        if ( user.isConsole() )
        {
            sender.sendMessage(_("General.ConsoleNotAvailable"));
            return true;
        }

        Player player = user.getPlayer();

        if ( args.length == 0 )
        {
            sendHelp(player);
            return true;
        }

        String message;

        switch ( args[0] )
        {
            case "join":
                if ( args.length < 2 )
                {
                    player.sendMessage(PREFIX + _("SinkChat.Commands.Channel.NoChannelGiven"));
                    return true;
                }

                try
                {
                    IChannel channel = ChannelHandler.getChannelByName(args[1]);
                    if ( !user.hasPermission(channel.getPermission()) )
                    {
                        player.sendMessage(_("Permissions.General"));
                    }
                    channel.removeExceptedPlayer(player);
                }
                catch ( NullPointerException ignored )
                {
                    message = PREFIX + String.format(_("SinkChat.Commands.Channel.ChannelUnknown"), args[1]);
                    player.sendMessage(message);
                    return true;
                }

                message = PREFIX + String.format(_("SinkChat.Commands.Channel.PlayerJoins"), args[1]);
                ChatColor.translateAlternateColorCodes('&', message);
                player.sendMessage(message);
                return true;

            case "leave":

                if ( args.length < 2 )
                {
                    player.sendMessage(PREFIX + _("SinkChat.Commands.Channel.NoChannelGiven"));
                    return true;
                }

                try
                {
                    IChannel channel = ChannelHandler.getChannelByName(args[1]);
                    if ( !user.hasPermission(channel.getPermission()) )
                    {
                        player.sendMessage(_("Permissions.General"));
                    }
                    channel.addExceptedPlayer(player);
                }
                catch ( NullPointerException ignored )
                {
                    message = PREFIX + String.format(_("SinkChat.Commands.Channel.ChannelUnknown"), args[1]);
                    player.sendMessage(message);
                    return true;
                }

                message = PREFIX + String.format(_("SinkChat.Commands.Channel.PlayerLeaves"), args[1]);
                player.sendMessage(message);


                return true;
            case "list":
            {
                message = PREFIX + String.format(_("SinkChat.Commands.Channel.List"), ChannelHandler.getChannelNames());
                player.sendMessage(message);
                return true;
            }
            case "participating":
                player.sendMessage(PREFIX + _("SinkChat.Commands.Channel.Part"));
                for ( IChannel target : ChannelHandler.getRegisteredChannels() )
                {
                    if ( target.contains(player) )
                    {
                        continue;
                    }

                    player.sendMessage(PREFIX + target.getChannelName());

                }
                return true;

            default:
                sendHelp(player);
                return true;
        }
        */
        return false;
    }

    private static void sendHelp(Player player)
    {
        player.sendMessage(PREFIX + _("SinkChat.Commands.Channel.Help"));
        player.sendMessage(PREFIX + COMMAND_PREFIX + "ch join <channel>");
        player.sendMessage(PREFIX + COMMAND_PREFIX + "ch leave <channel>");
        player.sendMessage(PREFIX + COMMAND_PREFIX + "ch list");
        player.sendMessage(PREFIX + COMMAND_PREFIX + "ch participating");
    }
}
