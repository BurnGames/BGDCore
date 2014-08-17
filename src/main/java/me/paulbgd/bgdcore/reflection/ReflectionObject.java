package me.paulbgd.bgdcore.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ReflectionObject {

    private final Object object;

    public ReflectionMethod getMethodByClasses(String name, Class... argumentTypes) {
        try {
            Method method = object.getClass().getDeclaredMethod(name, argumentTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return new ReflectionMethod(this.object, method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReflectionMethod getMethod(String name, Object... arguments) {
        return getMethodByClasses(name, Reflection.objectsToClassArray(arguments));
    }

    public ReflectionField getField(String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return new ReflectionField(this.object, field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ReflectionClass> getSubClasses() {
        return getSubClassesOfType(Object.class);
    }

    public List<ReflectionClass> getSubClassesOfType(Class<?> type) {
        List<ReflectionClass> classes = new ArrayList<>();
        for(Class<?> clazz : object.getClass().getDeclaredClasses()) {
            if(type.isAssignableFrom(clazz)) {
                classes.add(new ReflectionClass(clazz));
            }
        }
        return classes;
    }

}
