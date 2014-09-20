/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc;

import de.static_interface.sinklibrary.event.IrcJoinEvent;
import de.static_interface.sinklibrary.event.IrcKickEvent;
import de.static_interface.sinklibrary.event.IrcNickChangeEvent;
import de.static_interface.sinklibrary.event.IrcPartEvent;
import de.static_interface.sinklibrary.event.IrcPrivateMessageEvent;
import de.static_interface.sinklibrary.event.IrcQuitEvent;
import de.static_interface.sinklibrary.event.IrcReceiveMessageEvent;
import org.bukkit.Bukkit;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class PircBotXLinkListener extends ListenerAdapter<PircBotX> {

    @Override
    public void onMessage(MessageEvent<PircBotX> event) {
        IrcReceiveMessageEvent bukkitEvent = new IrcReceiveMessageEvent(event.getUser(), event.getChannel(), event.getMessage(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }

    @Override
    public void onJoin(JoinEvent<PircBotX> event) {
        IrcJoinEvent bukkitEvent = new IrcJoinEvent(event.getUser(), event.getChannel(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }

    @Override
    public void onPart(PartEvent<PircBotX> event) {
        IrcPartEvent bukkitEvent = new IrcPartEvent(event.getUser(), event.getChannel(), event.getReason(), event.getDaoSnapshot(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }

    @Override
    public void onKick(KickEvent<PircBotX> event) {
        IrcKickEvent bukkitEvent = new IrcKickEvent(event.getUser(), event.getRecipient(), event.getChannel(), event.getReason(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }

    @Override
    public void onQuit(QuitEvent<PircBotX> event) {
        IrcQuitEvent bukkitEvent = new IrcQuitEvent(event.getUser(), event.getReason(), event.getDaoSnapshot(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }

    @Override
    public void onNickChange(NickChangeEvent<PircBotX> event) {
        IrcNickChangeEvent bukkitEvent = new IrcNickChangeEvent(event.getOldNick(), event.getNewNick(), event.getUser(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) {
        IrcPrivateMessageEvent bukkitEvent = new IrcPrivateMessageEvent(event.getUser(), event.getMessage(), event.getBot());
        Bukkit.getPluginManager().callEvent(bukkitEvent);
    }
}
