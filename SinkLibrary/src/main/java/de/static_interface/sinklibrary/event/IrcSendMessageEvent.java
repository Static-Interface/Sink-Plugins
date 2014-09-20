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

package de.static_interface.sinklibrary.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when sending IRC Messages
 */
public class IrcSendMessageEvent extends Event implements Cancellable
{
    private String message;
    private String target;
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    /**
     * Don't fire this event by yourself, use {@link de.static_interface.sinklibrary.SinkLibrary#sendIrcMessage(String)} instead!
     * @param message Message to send
     */
    public IrcSendMessageEvent(String message, String target)
    {
        this.message = message;
        this.target = target;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }

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
