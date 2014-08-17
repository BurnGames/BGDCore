package me.paulbgd.bgdcore.reflection;

import java.lang.reflect.Field;
import lombok.Data;

@Data
public class ReflectionField {

    private final Object object;
    private final Field field;

    public ReflectionObject getValue() {
        try {
            return new ReflectionObject(field.get(object));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setValue(Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return field.getName();
    }

}
