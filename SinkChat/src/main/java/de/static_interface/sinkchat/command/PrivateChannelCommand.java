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

import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinkchat.channel.IPrivateChannel;
import de.static_interface.sinkchat.channel.PrivateChannelHandler;
import de.static_interface.sinkchat.channel.channels.PrivateChannel;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;
import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

@SuppressWarnings("UnusedDeclaration")
public class PrivateChannelCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args)
    {
        if ( sender instanceof ConsoleCommandSender )
        {
            sender.sendMessage(_("General.ConsoleNotAvailable"));
            return true;
        }

        if ( args.length < 2 )
        {
            sendHelp(sender, cmd);
            return true;
        }
        IPrivateChannel privateChannel = PrivateChannelHandler.getChannel(args[1]);
        switch ( args[0].toLowerCase() )
        {
            case "list":
            {
                if ( args[1].equals("all") )
                {
                    String channels = "";
                    for ( IPrivateChannel channel : PrivateChannelHandler.getRegisteredChannels() )
                    {
                        if ( channels.isEmpty() )
                        {
                            channels = channel.getChannelName();
                            continue;
                        }
                        channels = channels + ", " + channel.getChannelName();
                    }
                    sender.sendMessage(String.format(_("SinkChat.Channels.Private.Channels"), ChatColor.WHITE + channels));
                    return true;
                }
                if ( privateChannel == null )
                {
                    sender.sendMessage(String.format(_("SinkChat.Channels.Private.DoesntExists"), args[1]));
                    return true;
                }
                String players = "";
                for ( Player player : privateChannel.getPlayers() )
                {
                    if ( player == null ) continue;
                    String name = SinkLibrary.getUser(player).getDisplayName();
                    if ( players.isEmpty() )
                    {
                        players = name;
                        continue;
                    }
                    players = players + ", " + name;
                }
                sender.sendMessage(String.format(_("SinkChat.Channels.Private.Users"), privateChannel.getChannelName()));
                sender.sendMessage(players);
                return true;
            }
            case "rename":
            {
                if ( args.length < 3 )
                {
                    sendHelp(sender, cmd);
                    return true;
                }
                if ( privateChannel == null )
                {
                    sender.sendMessage(String.format(_("SinkChat.Channels.Private.DoesntExists"), args[1]));
                    return true;
                }
                privateChannel.setChannelName(args[2]);
                sender.sendMessage(String.format(_("SinkChat.Channels.Private.Renamed"), args[2]));
                return true;
            }
            case "create":
            {
                if ( args.length < 3 )
                {
                    sendHelp(sender, cmd);
                    return true;
                }

                if ( privateChannel != null )
                {
                    sender.sendMessage(_("SinkChat.Channels.Private.AlreadyExists"));
                    return true;
                }

                String identifier = args[1];
                String channelName = args[2];

                for ( String ident : ChannelHandler.getRegisteredCallChars().keySet() )
                {
                    if ( ident.equals(identifier) )
                    {
                        sender.sendMessage(_("SinkChat.Channels.Private.IdentifierUsed"));
                        return true;
                    }
                }

                privateChannel = new PrivateChannel(identifier, (Player) (sender), channelName);
                PrivateChannelHandler.registerChannel(privateChannel);
                sender.sendMessage(String.format(_("SinkChat.Channels.Private.Created"), channelName));
                return true;
            }
            case "invite":
            {
                if ( args.length < 3 )
                {
                    sendHelp(sender, cmd);
                    return true;
                }

                if ( privateChannel == null )
                {
                    sender.sendMessage(String.format(_("SinkChat.Channels.Private.DoesntExists"), args[1]));
                    return true;
                }

                String players = "";
                for ( int i = 2; i < args.length; i++ )
                {
                    String s = args[i];
                    if ( s.equals("invite") || s.equals(args[1]) || (privateChannel.contains(Bukkit.getPlayer(s))) )
                        continue;
                    if ( Bukkit.getPlayer(s) == null )
                    {
                        sender.sendMessage(String.format(_("SinkChat.Channels.Private.HasInvitedToChat.ErrorNotOnline"), s));
                    }
                    Player player = Bukkit.getPlayer(s);

                    if ( player == null )
                    {
                        sender.sendMessage(String.format(_("General.NotOnline"), s));
                        return true;
                    }

                    User user = SinkLibrary.getUser(player);
                    if ( privateChannel.contains(user.getPlayer()) )
                    {
                        sender.sendMessage(String.format(_("SinkChat.Channels.Private.HasInvitedToChat.ErrorAlreadyInChat"), user.getDisplayName()));
                        continue;
                    }
                    privateChannel.addPlayer(user.getPlayer(), player);
                    if ( players.isEmpty() )
                    {
                        players = user.getDisplayName();
                        continue;
                    }
                    players += ", " + user.getDisplayName();
                }
                sender.sendMessage(String.format(_("SinkChat.Channels.Private.Invites"), players));
                return true;
            }

            case "leave":
            {
                if ( !(privateChannel.contains((Player) (sender))) || (privateChannel == null) )
                {
                    sender.sendMessage(String.format(_("SinkChat.Commands.Channel.ChannelUnknown"), args[1]));
                    return true;
                }
                privateChannel.kickPlayer((Player) sender, (Player) sender, "");
                return true;
            }

            case "kick":
            {
                if ( args.length < 3 )
                {
                    //Not enough arguments. Sending command help
                    sendHelp(sender, cmd);
                    return true;
                }
                if ( !(privateChannel.contains((Player) sender)) || (privateChannel == null) )
                {
                    //Channel does not exist or command sender does not participate in that channel
                    sender.sendMessage(String.format(_("SinkChat.Commands.Channel.ChannelUnknown"), args[1]));
                    return true;
                }
                if ( !(privateChannel.getStarter().equals(sender)) )
                {
                    //Command sender is not the creator of that channel
                    sender.sendMessage(_("Permissions.General"));
                    return true;
                }
                if ( Bukkit.getPlayer(args[2]) == null )
                {
                    //Target is not online
                    sender.sendMessage(String.format(_("SinkChat.Channels.Private.HasInvitedToChat.ErrorNotOnline"), args[2]));
                }
                if ( !(privateChannel.contains(Bukkit.getPlayer(args[2]))) )
                {
                    //Target does not participate in that channel
                    sender.sendMessage(String.format(_("SinkChat.Channels.Private.PlayerKicked"), args[2]));
                    return true;
                }

                String reason = "Kicked!";

                for ( int x = 4; x < args.length; x++ )
                {
                    if ( reason.equals("Kicked!") )
                    {
                        reason = args[x];
                        continue;
                    }
                    reason = reason + ' ' + args[x];
                }

                Bukkit.getLogger().severe(privateChannel.toString());
                //Target participates in channel, command sender is channel owner & channel participant. 
                privateChannel.kickPlayer(Bukkit.getPlayer(args[2]), (Player) sender, reason);
                return true;
            }

            default:
            {
                sendHelp(sender, cmd);
                return true;
            }
        }
    }

    private void sendHelp(CommandSender sender, Command cmd)
    {
        sender.sendMessage(_("SinkChat.Channels.Private.WrongUsage"));
        sender.sendMessage(COMMAND_PREFIX + cmd.getLabel() + " create <channel identifier> <channel name>");
        sender.sendMessage(COMMAND_PREFIX + cmd.getLabel() + " rename <channel identifier> <new name>");
        sender.sendMessage(COMMAND_PREFIX + cmd.getLabel() + " list <channel identifier|all>");
        sender.sendMessage(COMMAND_PREFIX + cmd.getLabel() + " invite <channel identifier> <channel name> <player> [players...]");
        sender.sendMessage(COMMAND_PREFIX + cmd.getLabel() + " leave <channel identifier>");
        sender.sendMessage(COMMAND_PREFIX + cmd.getLabel() + " kick <channel identifier> <player> [reason]");
    }
}
