/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.api.user;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public abstract class SinkUserProvider<K, E extends SinkUser<K>> {
    public Map<K, E> instances;

    public SinkUserProvider() {
        instances = new ConcurrentHashMap<>();
    }

    public Class<E> getImplementationClass() {
        return (Class<E>) getParamizedClass(0);
    }

    public Class<K> getBaseClass() {
        return (Class<K>) getParamizedClass(1);
    }

    private Class<?> getParamizedClass(int index) {
        Object superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            return (Class<?>) superclass;
        }
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = genericSuperclass.getActualTypeArguments()[index];
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        throw new IllegalStateException("Unknown type: " + type.getTypeName());
    }

    public E getUserInstance(K base) {
        if (instances.get(base) == null) {
            loadUser(base);
        }

        return instances.get(base);
    }

    @Nullable
    public abstract E getUserInstance(String name);

    public Collection<E> getUserInstances() {
        return instances.values();
    }

    /**
     * Load an user
     * @param base Base of User
     * @return True if successfully created a new instance, false if already loaded
     */
    public boolean loadUser(K base) {
        if (instances.get(base) != null) {
            return false;
        }
        instances.put(base, newInstance(base));
        return true;
    }

    public E newInstance(K base) {
        try {
            Constructor<E> ctor = getImplementationClass().getConstructor(getBaseClass(), SinkUserProvider.class);
            return ctor.newInstance(base, this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unloadUser(E user) {
        for (K base : instances.keySet()) {
            E value = instances.get(base);

            if (value == user) {
                if (user.getConfiguration() != null) {
                    user.getConfiguration().save();
                }
                instances.remove(base);
                return true;
            }
        }
        return false;
    }

    public boolean unloadUser(K base) {
        for (K k : instances.keySet()) {
            if (k == base) {
                instances.remove(base);
                return true;
            }
        }

        return false;
    }

    public abstract String getTabCompleterSuffix();
}
