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

package de.static_interface.sinklibrary.api.stream;

import de.static_interface.sinklibrary.api.event.MessageStreamEvent;
import de.static_interface.sinklibrary.api.user.SinkUser;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MessageStream<T extends SinkUser> {

    private final String name;

    public MessageStream(@Nonnull String name) {
        Validate.notNull(name);
        this.name = name;
    }

    public final void sendMessage(String message) {
        sendMessage(null, message);
    }

    public final void sendMessage(@Nullable T sender, String message) {
        if (onSendMessage(sender, message)) {
            MessageStreamEvent event = new MessageStreamEvent(this);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public String formatMessage(T sender, String message) {
        return message;
    }

    protected abstract boolean onSendMessage(@Nullable T sender, String message);


    public final String getName() {
        return name;
    }
}
