package me.paulbgd.bgdcore.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private static Set<Class<?>> getAllTypes(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();

        do {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interfaze : interfaces) {
                    res.addAll(getAllTypes(interfaze));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass
            clazz = superClass;
        } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

        return new HashSet<>(res);
    }

}
