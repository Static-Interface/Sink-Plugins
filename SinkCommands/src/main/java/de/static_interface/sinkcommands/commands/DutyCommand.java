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

package de.static_interface.sinkcommands.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.static_interface.shadow.tameru.SQLiteDatabase;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;

public class DutyCommand implements CommandExecutor
{	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments)
	{
		if ( !sender.hasPermission("sinkcommands.duty") )
		{
			sender.sendMessage(LanguageConfiguration._("Permissions.General"));
		}
		
		if ( arguments.length == 0 )
		{
			
		}
		
		
		return true;
	}
}
class DatabaseHandler
{
	private static SQLiteDatabase database = new SQLiteDatabase(SinkLibrary.getCustomDataFolder().getAbsolutePath()+File.separator+"duties.db");
	
	public static void updateUser(User user, boolean start)
	{
		if ( !database.containsTable("duties") )
		{
			database.createTable("duties", "ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UUID text, TIMESTAMP_START timestamp DEFAULT CURRENT_TIMESTAMP, TIMESTAMP_END TIMESTAMP");
		}
		
		
		
	}
}