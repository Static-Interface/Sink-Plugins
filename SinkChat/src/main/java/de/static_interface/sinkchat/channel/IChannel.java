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

package de.static_interface.sinkchat.channel;

import org.bukkit.entity.Player;

public interface IChannel
{
    /**
     * Joining a Channel will remove you from the list<br>
     * of excepted players.
     */

    /**
     * @param player Given player will be added to the excepted players list.
     *               Will add the player to the excepted players list   <br>
     *               so he will be excepted from the messages sent.     <br>
     */
    public void addExceptedPlayer(Player player);

    /**
     * @param player Player to remove from the excepted players list.
     *               Removes player from the excepted players list.
     */
    public void removeExceptedPlayer(Player player);

    /**
     * Send message to all players in channel
     *
     * @param player  Sender
     * @param message Message
     * @return True if message was send successfully
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean sendMessage(Player player, String message);


    /**
     * Register Channel
     */
    public void registerChannel();

    /**
     * @param player Player to check for.
     * @return True if player was added to exceptedPlayers, else false.
     */
    public boolean contains(Player player);

    public String getChannelName();

    /**
     * @return Permission required to join channel
     */
    public String getPermission();
}
