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

package de.static_interface.sinkchat.channel.channels;

import de.static_interface.sinkchat.channel.ChannelHandler;
import de.static_interface.sinkchat.channel.ChannelUtil;
import de.static_interface.sinkchat.channel.IChannel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Vector;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class ShoutChannel implements IChannel, Listener
{

    Vector<Player> exceptedPlayers = new Vector<>();
    String PREFIX = ChatColor.GRAY + "[" + getChannelName() + "] " + ChatColor.RESET;

    private String callByChar;

    public ShoutChannel(String callChar)
    {
        callByChar = callChar;
    }


    @Override
    public void addExceptedPlayer(Player player)
    {
        exceptedPlayers.add(player);
    }

    @Override
    public void removeExceptedPlayer(Player player)
    {
        exceptedPlayers.remove(player);
    }

    @Override
    public boolean contains(Player player)
    {
        return exceptedPlayers.contains(player);
    }

    @Override
    public String getChannelName()
    {
        return ChatColor.GRAY + _("SinkChat.Channels.Shout");
    }

    @Override
    public String getPermission()
    {
        return "sinkchat.channel.shout";
    }

    @Override
    public boolean sendMessage(Player player, String message)
    {
        return ChannelUtil.sendMessage(player, message, this, PREFIX, callByChar);
    }

    @Override
    public void registerChannel()
    {
        ChannelHandler.registerChannel(this, getChannelName(), callByChar);
    }
}
