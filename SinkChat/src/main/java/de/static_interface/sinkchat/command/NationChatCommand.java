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

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import de.static_interface.sinkchat.TownyBridge;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class NationChatCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if ( !(sender instanceof Player) )
        {
            sender.sendMessage(_("General.ConsoleNotAvailable"));
            return true;
        }

        Player player = (Player) sender;
        Resident resident = TownyBridge.getResident(player.getName());

        if ( !resident.hasTown() )
        {
            player.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Du bist in keiner Town!");
            return true;
        }

        if ( !resident.hasNation() )
        {
            player.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Du bzw. deine Town ist in keiner Nation!");
            return true;
        }

        if ( args.length < 1 )
        {
            player.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Du musst eine Nachricht eingeben!");
            return true;
        }

        Nation nation;
        try
        {
            nation = resident.getTown().getNation();
        }
        catch ( NotRegisteredException ignored ) //Shouldn't happen...
        {
            player.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Du bist in keiner Town / Nation!");
            return true;
        }

        String msg = "";
        for ( String arg : args )
        {
            msg += arg + ' ';
        }

        msg = msg.trim();

        String prefixName = TownyFormatter.getFormattedResidentName(resident);

        String formattedMessage = ChatColor.GRAY + "[" + ChatColor.GOLD + nation.getName() + ChatColor.GRAY + "] " + prefixName + ChatColor.GRAY + ": " + ChatColor.WHITE + msg;

        for ( Resident nationResident : nation.getResidents() )
        {
            if ( nationResident.isNPC() ) continue;

            Player onlineResident = Bukkit.getPlayerExact(nationResident.getName());
            if ( onlineResident == null ) continue;

            onlineResident.sendMessage(formattedMessage);
        }

        SinkLibrary.getCustomLogger().log(Level.INFO, formattedMessage);

        return true;
    }
}
