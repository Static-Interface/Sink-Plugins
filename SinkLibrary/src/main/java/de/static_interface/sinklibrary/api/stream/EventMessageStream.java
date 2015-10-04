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

import de.static_interface.sinklibrary.api.user.SinkUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EventMessageStream<K extends SinkUser, T extends Event> extends MessageStream<K> implements Listener {

    public EventMessageStream(Plugin plugin) {
        super();
        setName("event_" + getEventClass().getSimpleName().toLowerCase());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Class<T> getEventClass() {
        Object superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            return (Class<T>) superclass;
        }
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = genericSuperclass.getActualTypeArguments()[1];
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        }
        throw new IllegalStateException("Unknown type: " + type.getTypeName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(T event) {
        String message = getMessage(event);
        K u = getUser(event);
        sendMessage(u, message);
    }

    @Nullable
    public abstract K getUser(T event);

    @Nonnull
    public abstract String getMessage(T event);

    @Override
    protected boolean onSendMessage(@Nullable K sender, String message, Object... args) {
        return true;
    }
}
