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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
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
                    injectMethod(m, (Inject) a);
                }

                if (a instanceof InjectConstructor) {
                    injectConstructor(m, (InjectConstructor) a);
                }

                if (a instanceof InjectAt) {
                    injectAt(m, (InjectAt) a);
                }
            }
        }
    }

    private static void injectMethod(Method targetMethod, Inject params)
            throws CannotCompileException, NotFoundException, IOException, NoSuchMethodException, ClassNotFoundException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(params.targetClass());
        CtClass[] args = toCtClass(cp, params.methodArgs());
        CtMethod m = cc.getDeclaredMethod(params.method(), args);
        String line = buildCall(cc, m, targetMethod);
        insert(m, line, params.injectTarget());
        postProcess(targetMethod.getDeclaringClass(), cc);
    }

    private static void injectConstructor(Method targetMethod, InjectConstructor params) throws NotFoundException, CannotCompileException,
                                                                                                IOException, NoSuchMethodException,
                                                                                                ClassNotFoundException {
        ClassPool cp = ClassPool.getDefault();
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

    private static void injectAt(Method targetMethod, InjectAt params)
            throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, ClassNotFoundException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(params.targetClass());
        CtClass[] args = toCtClass(cp, params.methodArgs());
        CtMethod m = cc.getDeclaredMethod(params.method(), args);
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
            args[i] = Class.forName(c.getName());
            i++;
        }
        return args;
    }

    private static void postProcess(Class origin, CtClass target) throws IOException, CannotCompileException {
        byte[] targetByteCode = target.toBytecode();
        swapper.reload(origin.getName(), targetByteCode);
    }

    private static String buildCall(CtClass clazz, CtBehavior method, Method targetMethod)
            throws NotFoundException, ClassNotFoundException, NoSuchMethodException {
        Class declaringClass = targetMethod.getDeclaringClass();
        String line;
        if (targetMethod.getParameterTypes().length == 0) {
            line = "{ " +
                   declaringClass.getName() + "." + targetMethod.getName() + "();" +
                   " }";
            return line;
        }

        Method m = Class.forName(clazz.getName()).getMethod(method.getName(), toClass(method.getParameterTypes()));

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
