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

package de.static_interface.sinklibrary.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fieldz = new ArrayList<>();
        fieldz.addAll(Arrays.asList(type.getDeclaredFields()));

        while (type.getSuperclass() != null) {
            type = type.getSuperclass();
            fieldz.addAll(Arrays.asList(type.getDeclaredFields()));
        }

        return fieldz;
    }

    public static <T> T Invoke(Object object, String methodName, Class<T> returnClass, Object... args) {
        try {
            Method method = object.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T InvokeStatic(Class<?> clazz, String methodName, Class<T> returnClass, Object... args) {
        try {
            Method method = clazz.getMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPrimitiveClass(Class clazz) {
        return clazz == byte.class
               || clazz == short.class
               || clazz == int.class
               || clazz == long.class
               || clazz == float.class
               || clazz == double.class
               || clazz == boolean.class
               || clazz == char.class;
    }

    public static boolean isNumber(Class<?> type) {
        return Number.class.isAssignableFrom(type);
    }
}
