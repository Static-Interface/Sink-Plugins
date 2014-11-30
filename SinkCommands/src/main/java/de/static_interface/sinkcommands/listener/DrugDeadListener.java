/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkcommands.listener;

import de.static_interface.sinkcommands.command.DrugCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;

public class DrugDeadListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().equals(DrugCommand.lastPlayer) && event.getEntityType() == EntityType.PLAYER) {
            IngameUser user = SinkLibrary.getInstance().getIngameUser(event.getEntity());
            event.setDeathMessage(ChatColor.RED + user.getDisplayName() + ChatColor.RESET + " nahm zu viele Drogen und ist gestorben.");
            DrugCommand.lastPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
            DrugCommand.lastPlayer.removePotionEffect(PotionEffectType.CONFUSION);
            DrugCommand.lastPlayer = null;
        }
    }
}
