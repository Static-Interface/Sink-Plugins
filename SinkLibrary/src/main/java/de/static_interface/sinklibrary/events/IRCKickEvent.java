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

package de.static_interface.sinklibrary.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IRCKickEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final String channel;
    private final String kickerNick;
    private final String kickerLogin;
    private final String kickerHostname;
    private final String recipientNick;
    private final String reason;

    private boolean cancelled = false;

    public IRCKickEvent(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)
    {
        this.channel = channel;
        this.kickerNick = kickerNick;
        this.kickerLogin = kickerLogin;
        this.kickerHostname = kickerHostname;
        this.recipientNick = recipientNick;
        this.reason = reason;
    }

    public String getChannel()
    {
        return channel;
    }

    public String getKickerNick()
    {
        return kickerNick;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getKickerLogin()
    {
        return kickerLogin;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getKickerHostname()
    {
        return kickerHostname;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getRecipientNick()
    {
        return recipientNick;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getReason()
    {
        return reason;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean value)
    {
        cancelled = value;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

}
