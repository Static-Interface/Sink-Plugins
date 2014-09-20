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

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static de.static_interface.sinklibrary.Constants.TICK;

public class DrugCommand implements CommandExecutor
{
    public static final String PREFIX = ChatColor.AQUA + "[Drogen] " + ChatColor.RESET;

    public static Player killedByDrugs;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        SinkUser user = SinkLibrary.getUser(sender);
        if ( user.isConsole() )
        {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler genutzt werden.");
            return true;
        }

        Player player = user.getPlayer();

        if ( !player.hasPotionEffect(PotionEffectType.BLINDNESS) )
        {
            player.sendMessage(PREFIX + ChatColor.BLUE + "Du hast Drogen genommen...");
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 1), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 1), true);
        }
        else
        {
            for ( PotionEffect pe : player.getActivePotionEffects() )
            {
                if ( !pe.getType().equals(PotionEffectType.BLINDNESS) )
                {
                    continue;
                }
                int duration = pe.getDuration();
                int amplifier = pe.getAmplifier();
                if ( amplifier == 1 )
                {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Du hast mehr Drogen genommen...");
                }
                else if ( amplifier == 2 )
                {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Vielleicht solltest du nicht so viele Drogen nehmen.");
                }
                else if ( amplifier == 3 )
                {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Noch mehr Drogen...");
                }
                else if ( amplifier == 4 )
                {
                    player.sendMessage(PREFIX + ChatColor.BLUE + "Wow, du hast so viele Drogen genommen... und lebst immer noch!");
                }
                else if ( amplifier >= 5 )
                {
                    killedByDrugs = player;
                    player.setHealth(0.0);
                    return true;
                }
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.CONFUSION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (duration + (TICK * 30)), amplifier + 1), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) (duration + (TICK * 30)), amplifier + 1), true);
            }
        }
        return true;
    }
}
