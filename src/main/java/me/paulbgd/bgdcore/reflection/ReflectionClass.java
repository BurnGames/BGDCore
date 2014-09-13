package me.paulbgd.bgdcore.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Data;

@Data
public class ReflectionClass {

    private final Class<?> clazz;


    public ReflectionObject newInstance(Object... arguments) {
        try {
            if (arguments.length == 0) {
                return new ReflectionObject(clazz.newInstance());
            }
            Constructor<?> constructor = clazz.getDeclaredConstructor(Reflection.objectsToClassArray(arguments));
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return new ReflectionObject(constructor.newInstance(arguments));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReflectionField getStaticField(String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return new ReflectionField(null, field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReflectionMethod getStaticMethod(String name, Object... arguments) {
        try {
            Method method = clazz.getDeclaredMethod(name, Reflection.objectsToClassArray(arguments));
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return new ReflectionMethod(null, method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReflectionMethod getStaticMethod(String name, Class[] arguments) {
        try {
            Method method = clazz.getDeclaredMethod(name, arguments);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return new ReflectionMethod(null, method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReflectionMethod getMethod(Object instance, String name, Object... arguments) {
        try {
            Method method = clazz.getDeclaredMethod(name, Reflection.objectsToClassArray(arguments));
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return new ReflectionMethod(instance, method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReflectionMethod getMethod(Object instance, String name, Class[] arguments) {
        try {
            Method method = clazz.getDeclaredMethod(name, arguments);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return new ReflectionMethod(instance, method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


}
