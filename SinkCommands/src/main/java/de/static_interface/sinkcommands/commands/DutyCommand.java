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
import java.sql.Time;
import java.util.Date;

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
	
	public enum dutySumType
	{
		LAST_DUTY_ONLY,
		TODAY,
		YESTERDAY,
		LAST_WEEK,
		LAST_MONTH,
		LAST_X_DAYS
	}
	
	public static void startDuty(User user)
	{
		endDuty(user);
		
		DatabaseHandler.updateUser(user, true);
	}
	
	public static Time endDuty(User user)
	{
		DatabaseHandler.updateUser(user, false);
		
		return getDutySumTime(user, dutySumType.LAST_DUTY_ONLY);
	}
	
	public static Time getDutySumTime(User user, dutySumType sumType)
	{
		return new Time(0);
	}
	
	public static Time getLastDutyTime(User user)
	{
		return getDutySumTime(user, dutySumType.LAST_DUTY_ONLY);
	}
}

class DatabaseHandler
{
	private static final String TABLE_DUTY ="duties";
	private static SQLiteDatabase database = new SQLiteDatabase(SinkLibrary.getCustomDataFolder().getAbsolutePath()+File.separator+"duties.db");
	
	private static void initDatabase()
	{
		if ( !database.containsTable(TABLE_DUTY) )
		{
			database.createTable(TABLE_DUTY, 
									"ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
										+ "UUID text, "
										+ "TIMESTAMP_START timestamp DEFAULT CURRENT_TIMESTAMP, "
										+ "TIMESTAMP_END TIMESTAMP");
		}
	}
	
	public static void updateUser(User user, boolean start)
	{
		initDatabase();
		
		if (start)
		{
			database.insertIntoDatabase(TABLE_DUTY, "UUID", user.getUniqueId().toString());
		}
		else
		{
			database.updateRaw("update " 
									+ TABLE_DUTY 
								+ " set "
									+ "TIMESTAMP_END = NOW(), "
									+ "TIMESTAMP_COMPLETE = SEC_TO_TIME(TIMESTAMP_END - TIMESTAMP_START) "
								+ "where "
									+ "UUID='" + user.getUniqueId().toString() + "'"
									+ " and "
									+ "TIMESTAMP_COMPLETE is NULL;");
		}
	}
}