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

package de.static_interface.sinklibrary.database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class FieldCache {

    private static Map<String, Annotation> cache = new HashMap<>();

    @Nullable
    public static <T extends Annotation> T getAnnotation(Field f, Class<T> annotation) {
        String name = f.getDeclaringClass().getName() + "." + f.getName();
        if (cache.containsKey(name) && cache.get(name) != null && annotation.isAssignableFrom(cache.get(name).getClass())) {
            return (T) cache.get(name);
        }
        cache.put(name, f.getAnnotation(annotation));
        return getAnnotation(f, annotation);
    }
}
