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


import de.static_interface.sinkcommands.commands.SpectateCommands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class SpectateListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if ( SpectateCommands.specedPlayers.containsValue(event.getPlayer()) )
        {
            Player player = getHashMapKey(SpectateCommands.specedPlayers, event.getPlayer());
            SpectateCommands.specedPlayers.remove(player);
            SpectateCommands.show(player);
            player.sendMessage(SpectateCommands.PREFIX + "Spieler hat das Spiel verlassen, Spectate Modus wurde beendet.");
        }
        if ( SpectateCommands.specedPlayers.containsKey(event.getPlayer()) )
        {
            SpectateCommands.specedPlayers.remove(event.getPlayer());
        }
    }

    private Player getHashMapKey(HashMap<Player, Player> hashmap, Player value)
    {
        for ( Player p : hashmap.keySet() )
        {
            if ( hashmap.get(p).equals(value) )
            {
                return p;
            }
        }
        return null;
    }
}
