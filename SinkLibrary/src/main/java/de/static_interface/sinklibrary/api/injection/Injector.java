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

/**
 * To use this, the server needs to be started with these parameters:
 * <p>-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000</p>
 */
public class Injector {

    private static final int SWAPPER_PORT = 8000;
    private static HotSwapper swapper = null;

    public static void loadInjections(Class clazz) throws Exception {
        Validate.notNull(clazz);
        loadInjections(clazz, clazz.getClassLoader());
    }

    public static void loadInjections(Class clazz, ClassLoader cl) throws Exception {
        if (swapper == null) {
            swapper = new HotSwapper(SWAPPER_PORT);
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

    public static void injectCode(Class clazz, Method method, String code, InjectTarget target) throws Exception {
        injectCode(clazz.getName(), clazz.getClassLoader(), method.getName(), method.getParameterTypes(), code, target);
    }

    public static void injectCode(Class clazz, String method, Class[] methodArgs, String code, InjectTarget target) throws Exception {
        injectCode(clazz.getName(), clazz.getClassLoader(), method, methodArgs, code, target);
    }

    public static void injectCode(String clazz, ClassLoader cl, String method, Class[] methodArgs, String code, InjectTarget target)
            throws Exception {
        if (swapper == null) {
            swapper = new HotSwapper(SWAPPER_PORT);
        }
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(clazz);
        CtMethod m = getMethod(cp, cc, method, methodArgs);
        insert(m, "{ " + code + " }", target);
        postProcess(Class.forName(clazz), cc);
    }

    public static void injectCodeConstructor(Class clazz, Class[] constructorArgs, String code, InjectTarget target) throws Exception {
        injectCodeConstructor(clazz.getName(), clazz.getClassLoader(), constructorArgs, code, target);
    }

    public static void injectCodeConstructor(String clazz, ClassLoader cl, Class[] methodArgs, String code, InjectTarget target)
            throws Exception {
        if (swapper == null) {
            swapper = new HotSwapper(SWAPPER_PORT);
        }
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(clazz);
        CtConstructor constructor = cc.getDeclaredConstructor(toCtClass(cp, methodArgs));
        insert(constructor, "{ " + code + " }", target);
        postProcess(Class.forName(clazz), cc);
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
        validateArgs(targetMethod, params.methodArgNames());
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(params.targetClass());
        CtMethod m = getMethod(cp, cc, params.method(), targetMethod.getParameterTypes());
        String line = buildCall(cc, m, targetMethod, params.methodArgNames());
        insert(m, line, params.injectTarget());
        postProcess(Class.forName(params.targetClass()), cc);
    }

    private static void injectConstructor(ClassLoader cl, Method targetMethod, InjectConstructor params)
            throws NotFoundException, CannotCompileException,
                   ClassNotFoundException, NoSuchMethodException, IOException {
        validateArgs(targetMethod, params.methodArgNames());
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(params.targetClass());
        CtClass[] args = toCtClass(cp, targetMethod.getParameterTypes());
        CtConstructor m = cc.getDeclaredConstructor(args);
        String line = buildCall(cc, m, targetMethod, params.methodArgNames());
        insert(m, line, params.injectTarget());
        postProcess(Class.forName(params.targetClass()), cc);
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
                if (!(behavior instanceof CtConstructor)) {
                    throw new IllegalStateException("Can use InjectTarget.BEFORE_BODY only on constructors!");
                }
                ((CtConstructor) behavior).insertBeforeBody(line);
                break;
        }
    }

    private static void injectAt(ClassLoader cl, Method targetMethod, InjectAt params)
            throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, ClassNotFoundException {
        validateArgs(targetMethod, params.methodArgNames());
        ClassPool cp = ClassPool.getDefault();
        ClassPath classPath = new LoaderClassPath(cl);
        cp.appendClassPath(classPath);
        CtClass cc = cp.get(params.targetClass());
        CtMethod m = getMethod(cp, cc, params.method(), targetMethod.getParameterTypes());
        String line = buildCall(cc, m, targetMethod, params.methodArgNames());
        m.insertAt(params.line(), params.modify(), line);
        postProcess(Class.forName(params.targetClass()), cc);
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

    private static void validateArgs(Method targetMethod, String[] argNames) {
        if (targetMethod.getParameterCount() > 0 && argNames.length == 1 && argNames[0].equals("")) {
            return;
        }

        if (targetMethod.getParameterCount() == 0) {
            return;
        }

        if (targetMethod.getParameterCount() != argNames.length) {
            throw new IllegalArgumentException("argNames doesn't match args count");
        }
    }

    private static Class[] toClass(CtClass[] classes) throws ClassNotFoundException {
        Class[] args = new Class[classes.length];
        int i = 0;
        for (CtClass c : classes) {
            String name = c.getName();

            boolean isArray = false;
            if (name.endsWith("[]")) {
                name = "L" + name;
                isArray = true;
            }

            while (name.endsWith("[]")) {
                name = "[" + StringUtil.replaceLast(name, "[]", "");
            }

            name = name.replace("]", "");

            if (isArray) {
                name += ";";
            }

            args[i] = Class.forName(name);
            i++;
        }
        return args;
    }

    private static void postProcess(Class origin, CtClass target) throws IOException, CannotCompileException {
        byte[] targetByteCode = target.toBytecode();
        swapper.reload(origin.getName(), targetByteCode);
    }

    private static String buildCall(CtClass clazz, CtBehavior behaviour, Method targetMethod, String[] methodArgs)
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

        String args = StringUtil.formatArrayToString(methodArgs, ", ");
        line = "{ " +
               declaringClass.getName() + "." + targetMethod.getName() + "(" + args + ");" +
               " }";
        return line;
    }
}
