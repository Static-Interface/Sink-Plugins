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

public class IRCPingEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final String sourceNick;
    private final String sourceLogin;
    private final String sourceHostname;
    private final String target;
    private final String pingValue;

    private boolean cancelled = false;

    public IRCPingEvent(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue)
    {
        this.sourceNick = sourceNick;
        this.sourceLogin = sourceLogin;
        this.sourceHostname = sourceHostname;
        this.target = target;
        this.pingValue = pingValue;
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

    public String getSourceNick()
    {
        return sourceNick;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSourceLogin()
    {
        return sourceLogin;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSourceHostname()
    {
        return sourceHostname;
    }

    public String getTarget()
    {
        return target;
    }

    public String getPingValue()
    {
        return pingValue;
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
