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

package de.static_interface.sinkcommands.listener;

import de.static_interface.sinkcommands.commands.DrugCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;

public class DrugDeadListener implements Listener
{

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if ( event.getEntity().equals(DrugCommand.killedByDrugs) && event.getEntityType() == EntityType.PLAYER )
        {
            SinkUser user = SinkLibrary.getUser(event.getEntity());
            event.setDeathMessage(ChatColor.RED + user.getDisplayName() + ChatColor.RESET + " nahm zu viele Drogen und ist gestorben.");
            DrugCommand.killedByDrugs.removePotionEffect(PotionEffectType.BLINDNESS);
            DrugCommand.killedByDrugs.removePotionEffect(PotionEffectType.CONFUSION);
            DrugCommand.killedByDrugs = null;
        }
    }
}
