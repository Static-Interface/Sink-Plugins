/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.api.event;

import de.static_interface.sinklibrary.api.stream.MessageStream;
import de.static_interface.sinklibrary.api.user.SinkUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageStreamEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private MessageStream messageStream;
    private SinkUser user;
    private String message;

    public MessageStreamEvent(MessageStream messageStream, SinkUser user, String message) {
        this.messageStream = messageStream;
        this.user = user;
        this.message = message;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public MessageStream getMessageStream() {
        return messageStream;
    }

    public SinkUser getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
