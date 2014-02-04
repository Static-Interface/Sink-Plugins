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

import java.util.Vector;

public abstract class IPrivateChannel implements IChannel
{

    /**
     * Add player to existing conversation
     *
     * @param invitor Player who invited target
     * @param target  Player who was invited
     */

    public abstract void addPlayer(Player invitor, Player target);

    /**
     * Kicks a player off a conversation
     *
     * @param player Player to kick
     * @param kicker Player who kicked
     * @param reason Reason for kick.
     */


    public abstract void kickPlayer(Player player, Player kicker, String reason);

    /**
     * Registers conversation at PrivateChannelHandler
     */

    public void registerChannel()
    {
        PrivateChannelHandler.registerChannel(this);
    }

    /**
     * Returns wether a conversation contains a player or not
     *
     * @param player Player to check for
     * @return True if player is in that conversation, else false.
     */

    public abstract boolean contains(Player player);

    /**
     * Returns channel identifier
     *
     * @return channel identifier
     */

    public abstract String getChannelIdentifier();

    /**
     * Returns the player who started that conversation.
     *
     * @return Player who started that conversation
     */
    public abstract Player getStarter();

    @Override
    public void addExceptedPlayer(Player player)
    {
        throw new RuntimeException("This method is not supported with this class");
    }

    @Override
    public void removeExceptedPlayer(Player player)
    {
        throw new RuntimeException("This method is not supported with this class");
    }

    public abstract void setChannelName(String channelName);

    public abstract Vector<Player> getPlayers();
}