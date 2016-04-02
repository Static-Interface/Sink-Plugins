/*
 * Copyright (c) 2013 - 2016 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.api.injection;

import de.static_interface.sinklibrary.util.ReflectionUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.util.HotSwapper;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * To use this, the server needs to be started with these parameters:
 * <p>-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000</p>
 */
public class Injector {

    private static HotSwapper swapper = null;

    public static void loadInjections(Class clazz) throws Exception {
        Validate.notNull(clazz);
        loadInjections(clazz, clazz.getClassLoader());
    }

    public static void loadInjections(Class clazz, ClassLoader cl) throws Exception {
        if (swapper == null) {
            swapper = new HotSwapper(8000);
        }
        Validate.notNull(clazz);
        Validate.notNull(cl);
        for (Method m : clazz.getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                if (a instanceof Inject) {
                    injectMethod(cl, m, (Inject) a);
                }

                if (a instanceof InjectConstructor) {
                    injectConstructor(cl, m, (InjectConstructor) a);
                }

                if (a instanceof InjectAt) {
                    injectAt(cl, m, (InjectAt) a);
                }
            }
        }
    }

    private static CtMethod getMethod(ClassPool cp, CtClass clazz, String method, Class[] args) throws NotFoundException {
        //CtClass[] args = toCtClass(cp, params.methodArgs());
        //CtMethod m = clazz.getDeclaredMethod(method, toCtClass(cp, args));
        CtMethod m = null;

        for (CtMethod p : clazz.getDeclaredMethods()) {
            //Todo: support multiple methods with the same name
            if (p.getName().equals(method)) {
                m = p;
                break;
            }
        }
        if (m == null) {
            throw new IllegalArgumentException("Failed to find method " + method + " in class " + clazz.getName());
        }
        return m;
    }

    private static void injectMethod(ClassLoader cl, Method targetMethod, Inject params)
            throws CannotCompileException, NotFoundException, IOException, NoSuchMethodException, ClassNotFoundException {
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(params.targetClass());
        CtMethod m = getMethod(cp, cc, params.method(), params.methodArgs());
        String line = buildCall(cc, m, targetMethod);
        insert(m, line, params.injectTarget());
        postProcess(targetMethod.getDeclaringClass(), cc);
    }

    private static void injectConstructor(ClassLoader cl, Method targetMethod, InjectConstructor params)
            throws NotFoundException, CannotCompileException,
                   ClassNotFoundException, NoSuchMethodException, IOException {
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(params.targetClass());
        CtClass[] args = toCtClass(cp, params.methodArgs());
        CtConstructor m = cc.getDeclaredConstructor(args);
        String line = buildCall(cc, m, targetMethod);
        insert(m, line, params.injectTarget());
        postProcess(targetMethod.getDeclaringClass(), cc);
    }

    private static void insert(CtBehavior behavior, String line, InjectTarget target) throws CannotCompileException {
        switch (target) {
            case BEFORE_METHOD:
                behavior.insertBefore(line);
                break;
            case AFTER_METHOD:
                behavior.insertAfter(line);
                break;
            case BEFORE_BODY:
                ((CtConstructor) behavior).insertBeforeBody(line);
                break;
        }
    }

    private static void injectAt(ClassLoader cl, Method targetMethod, InjectAt params)
            throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, ClassNotFoundException {
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(params.targetClass());
        CtMethod m = getMethod(cp, cc, params.method(), params.methodArgs());
        String line = buildCall(cc, m, targetMethod);
        m.insertAt(params.line(), params.modify(), line);
        postProcess(targetMethod.getDeclaringClass(), cc);
    }

    private static CtClass[] toCtClass(ClassPool cp, Class[] classes) throws NotFoundException {
        CtClass[] args;
        if (classes.length == 1 && classes[0] == Injector.class) {
            args = null;
        } else {
            args = new CtClass[classes.length];
            int i = 0;
            for (Class c : classes) {
                args[i] = cp.get(c.getName());
                i++;
            }
        }
        return args;
    }

    private static Class[] toClass(CtClass[] classes) throws ClassNotFoundException {
        Class[] args = new Class[classes.length];
        int i = 0;
        for (CtClass c : classes) {
            String name = c.getName();
            if (name.endsWith("[]")) {
                name = "L" + name;
            }

            while (name.endsWith("[]")) {
                name = "[" + StringUtil.replaceLast(name, "[]", "");
            }

            name = name.replace("]", "");

            name = name.replace("/", ".");
            args[i] = Class.forName(name);
            i++;
        }
        return args;
    }

    private static void postProcess(Class origin, CtClass target) throws IOException, CannotCompileException {
        byte[] targetByteCode = target.toBytecode();
        swapper.reload(origin.getName(), targetByteCode);
    }

    private static String buildCall(CtClass clazz, CtBehavior behaviour, Method targetMethod)
            throws NotFoundException, ClassNotFoundException, NoSuchMethodException {
        Validate.notNull(clazz, "CtClass is null");
        Validate.notNull(behaviour, "CtBehaviour is null");
        Class declaringClass = targetMethod.getDeclaringClass();
        String line;
        if (targetMethod.getParameterTypes().length == 0) {
            line = "{ " +
                   declaringClass.getName() + "." + targetMethod.getName() + "();" +
                   " }";
            return line;
        }

        //Todo: support multiple methods with the same name
        Method m = ReflectionUtil.getDeclaredMethod(Class.forName(clazz.getName()), behaviour.getName(), toClass(behaviour.getParameterTypes()));

        String args = "";
        for (Parameter param : m.getParameters()) {
            String s = param.getName();
            if (args.equals("")) {
                args = s;
                continue;
            }
            args += ", " + s;
        }

        line = "{ " +
               declaringClass.getName() + "." + targetMethod.getName() + "(" + args + ");" +
               " }";
        return line;
    }
}
