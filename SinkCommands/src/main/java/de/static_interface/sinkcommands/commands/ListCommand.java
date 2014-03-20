package de.static_interface.sinkcommands.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.static_interface.sinkafk.AFKCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;

public class ListCommand implements CommandExecutor
{
	public static boolean activateAwayListening = false;
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		String msg = "";
		
		for ( User user : SinkLibrary.getUsers().values() )
		{
			if ( activateAwayListening && AFKCommand.isUserAfk(user) )
			{
				msg = String.format("%s, [AFK] [%s] %s%s", msg, user.getPrimaryGroup(), user.getPrefix(), ChatColor.RESET);
				continue;
			}
			
			msg = String.format("%s, [%s] %s%s", msg, user.getPrimaryGroup(), user.getPrefix(), ChatColor.RESET);
		}
		return true;
	}
}
