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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.event.MessageStreamEvent;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.util.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MessageStream<T extends SinkUser> {

    private String name;

    public MessageStream(@Nonnull String name) {
        Validate.notNull(name);
        this.name = name;
    }

    public MessageStream() {
        this.name = null;
    }

    public final boolean sendMessage(String message) {
        return sendMessage(null, message);
    }

    public final boolean sendMessage(@Nullable T user, String message, Object... args) {
        Debug.logMethodCall(user != null ? user.getName() : null, message);
        if (getName() == null) {
            throw new IllegalStateException("Name was not set!");
        }

        if (SinkLibrary.getInstance().getMessageStream(getName()) == null) {
            throw new IllegalStateException("MessageStream is not registered");
        }

        String formatMessage = formatMessage(user, message);

        boolean result = onSendMessage(user, formatMessage, args);
        if (result) {
            MessageStreamEvent event = new MessageStreamEvent(this, user, message);
            Bukkit.getPluginManager().callEvent(event);
        }

        return result;
    }

    public String formatMessage(@Nullable T user, String message) {
        return message;
    }

    protected abstract boolean onSendMessage(@Nullable T user, String message, Object... args);

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }
}
