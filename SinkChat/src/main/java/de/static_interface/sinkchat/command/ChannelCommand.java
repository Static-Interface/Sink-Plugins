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
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.static_interface.sinkchat.channel.Channel;
import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.commands.Command;
import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

public class ChannelCommand extends Command
{
	public ChannelCommand(Plugin plugin)
	{
		super(plugin);
	}

	public static final String PREFIX = m("SinkChat.Prefix.Channel") + ' ' + ChatColor.RESET;

	private static final String enabled = "SinkChat.Commands.Channel.Enabled",
								disabled = "SinKChat.Commands.Channel.Disabled";

	@Override
	public boolean onExecute(CommandSender sender, String label, String[] args)
	{
		if ( args.length < 1 )
		{
			sendHelp(label, SinkLibrary.getUser(sender));
			return true;
		}
		else
		{
			SinkUser user = SinkLibrary.getUser(sender);
			switch ( args[0].toLowerCase() )
			{
			case "join":
				if ( args.length < 2 )
				{
			        sender.sendMessage(PREFIX + label+" join <channel>");
					return true;
				}
				if ( !user.hasPermission("sinkchat.channel.use") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				else
				{
					Channel channel = ChannelHandler.getRegisteredChannel(args[1]);
					if ( channel == null ) channel = ChannelHandler.getChannelByName(args[1]);
					if ( channel == null )
					{
						user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.ChannelUnknown"), args[1]));
					}
					if ( !user.hasPermission(channel.getPermission()) )
					{
						user.sendMessage(String.format(m("Permissions.SinkChat.Channel"), channel.getName()));
						return true;
					}
					if ( channel.enabledForPlayer(user.getUniqueId()) )
					{
						user.sendMessage(PREFIX + m("SinkChat.AlreadyEnabled"));
						return true;
					}
					else
					{
						channel.setEnabledForPlayer(user.getUniqueId(), true);
						user.sendMessage(PREFIX + m("SinkChat.EnabledChannel"));
						return true;
					}
				}
			case "part":
				if ( !user.hasPermission("sinkchat.channel.use") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				if ( args.length < 2 )
				{
			        sender.sendMessage(PREFIX + label+" part <channel>");
					return true;
				}
			case "leave":
				if ( !user.hasPermission("sinkchat.channel.use") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				if ( args.length < 2 )
				{
			        sender.sendMessage(PREFIX + label+" leave <channel>");
					return true;
				}

				Channel channel = ChannelHandler.getRegisteredChannel(args[1]);
				if ( channel == null ) channel = ChannelHandler.getChannelByName(args[1]);
				if ( channel == null )
				{
					channel = ChannelHandler.getChannelByName(args[1]);
					user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.ChannelUnknown"), args[1]));
					return true;
				}
				if ( !channel.enabledForPlayer(user.getUniqueId()) )
				{
					user.sendMessage(PREFIX + m("SinkChat.AlreadyDisabled"));
					return true;
				}
				else
				{
					channel.setEnabledForPlayer(user.getUniqueId(), false);
					user.sendMessage(PREFIX + m("SinkChat.DisabledChannel"));
					return true;
				}
			case "list":
				if ( !user.hasPermission("sinkchat.channel.list") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				String channels = "\n";

				for ( Channel c : ChannelHandler.getRegisteredChannels().values() )
				{
					channels = channels + String.format("%s %s: %s – %s%n", PREFIX, c.getName(), c.getCallCode(),  ( c.enabledForPlayer(user.getUniqueId()) ? m(enabled) : m(disabled) ) );
				}

				user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.List"), channels));
				return true;
			case "participating":
				if ( !user.hasPermission("sinkchat.channel.use") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				String enabledChannels = "";

				for ( Channel c : ChannelHandler.getRegisteredChannels().values() )
				{
					if ( c.enabledForPlayer(user.getUniqueId()) ) enabledChannels = enabledChannels + c.getName() + " ";
				}

				user.sendMessage(PREFIX + ( enabledChannels.length() > 0 ? enabledChannels : m("SinkChat.NoChannelJoined") ));
				return true;
			case "add":
				if ( !user.hasPermission("sinkchat.channel.admin") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				String name, callCode, prefix, permission;
				boolean enabled, sendToIRC;
				int range;
				if ( args.length < 8 )
				{
					user.sendMessage(PREFIX + label + " add <channelname> <callcode> <enabled(true / false)> <permission>, <prefix> <sendToIrc(true / false)> <range (or 0 if no range)>");
					return true;
				}

				name = args[1];
				callCode = args[2];
				enabled = ( args[3].equalsIgnoreCase("true") ? true : false );
				permission = args[4];
				prefix = args[5]+" "+ChatColor.RESET;
				sendToIRC = ( args[6].equalsIgnoreCase("true") ? true : false );
				range = ( isValidNumber(args[7]) ? Integer.parseInt(args[7]) : 0 );
				Channel newChannel = new Channel(name, callCode, enabled, permission, prefix, sendToIRC, range);
				if ( ChannelHandler.getRegisteredChannel(callCode) != null )
				{
					user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.ChannelAlreadyExists"), name));
					return true;
				}
				else
				{
					user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.SuccessfullyCreated"), name));
					ChannelHandler.registerChannel(newChannel);
				}
				return true;
			case "remove":
			case "delete":
				if ( !user.hasPermission("sinkchat.channel.admin") && !user.hasPermission("sinkchat.channel.*") )
				{
					user.sendMessage(m("Permissions.General"));
					return true;
				}
				if ( args.length < 2 )
				{
					user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.NoChannelGiven")));
					return true;
				}

				if ( ChannelHandler.getChannelByName(args[1]) == null )
				{
					user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.ChannelUnknown"), args[1]));
					return true;
				}
				else
				{
					ChannelHandler.removeRegisteredChannel(args[1]);
					user.sendMessage(PREFIX + String.format(m("SinkChat.Commands.Channel.SuccessfullyDeleted"), args[1]));
					return true;
				}
			}
		}
    	return false;
	}

	@Override
	public boolean isPlayerOnly()
	{
		return true;
	}

	private static boolean isValidNumber(String string)
	{
		boolean validNumber = true;

		for ( char c : string.toCharArray() )
		{
			validNumber = ( Character.isDigit(c)  && validNumber );
			if ( !validNumber ) return validNumber;
		}
		return validNumber;
	}

    private static void sendHelp(String label, SinkUser user)
    {
    	if ( !user.hasPermission("sinkchat.channel.use") && !user.hasPermission("sinkchat.channel.*") )
		{
			user.sendMessage(m("Permissions.General"));
			return;
		}
        user.sendMessage(PREFIX + m("SinkChat.Commands.Channel.Help"));
        user.sendMessage(PREFIX + label+" join <channel>");
        user.sendMessage(PREFIX + label+" part <channel>");
        user.sendMessage(PREFIX + label+" list");
        user.sendMessage(PREFIX + label+" participating");
        if ( user.hasPermission("sinkchat.channel.admin") || user.getPlayer().isOp() )
        {
        	//Red because this can cause serious problems.
        	user.sendMessage(PREFIX + label + ChatColor.RED + " delete <channel>" + ChatColor.RESET);
        	user.sendMessage(PREFIX + label + ChatColor.RED + " add <channel>" + ChatColor.RESET);
        }
    }
}
