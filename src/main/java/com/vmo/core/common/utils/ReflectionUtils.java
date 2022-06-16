package com.vmo.core.common.utils;

import com.vmo.core.common.error.ApiException;
import com.vmo.core.common.error.ErrorCode;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Field getClassField(Class classz, String fieldName) throws NoSuchFieldException {
        try {
            Field field = classz.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            if (classz.getSuperclass() != null) {
                return getClassField(classz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

    public static Object getField(Object instance, String fieldName) {
        try {
            Field field = getClassField(instance.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new ApiException(ErrorCode.UNCATEGORIZED_SERVER_ERROR, "Can not get field " + fieldName + " of " + instance.getClass().getSimpleName());
        }
    }
}
