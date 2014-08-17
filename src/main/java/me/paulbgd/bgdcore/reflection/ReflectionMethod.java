package me.paulbgd.bgdcore.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Data;

@Data
public class ReflectionMethod {

    private final Object object;
    private final Method method;

    public ReflectionObject invoke(Object... arguments) {
        try {
            Object returned = method.invoke(object, arguments);
            if (returned != null) {
                return new ReflectionObject(returned);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
