package org.xml.core;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class Reflect {

    public static void set(Field field, Object parent, Object value) throws IllegalAccessException {
        if (field != null) {
            wrapper(field, parent, value);
        }
    }

    public static Field[] getFileds(Class<?> cls) {
        return cls.getDeclaredFields();
    }

    public static Field getFiled(Class<?> cls, String name) throws NoSuchFieldException {
        return cls.getDeclaredField(name);
    }

    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;

            if (Modifier.isPublic(member.getModifiers())
                    && Modifier.isPublic(member.getDeclaringClass()
                    .getModifiers())) {

                return accessible;
            }
        }

        // 默认为false,即反射时检查访问权限，
        // 设为true时不检查访问权限,可以访问private字段和方法
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }

        return accessible;
    }

    public static boolean isNormal(Class<?> type) throws IllegalAccessException {
        if (type == null) {
            return true;
        }
        if (boolean.class == type || Boolean.class == type
                || int.class == type || Integer.class == type
                || long.class == type || Long.class == type
                || short.class == type || Short.class == type
                || byte.class == type || Byte.class == type
                || double.class == type || Double.class == type
                || float.class == type || Float.class == type
                || char.class == type || Character.class == type
                || String.class == type) {
            return true;
        }
        return false;
    }

    private static void wrapper(Field field, Object parent, Object object) throws IllegalAccessException {
        accessible(field);
        String value = object == null ? "" : String.valueOf(object);
        if (parent == null || field == null) {
            return;
        }
        Class<?> type = field.getType();
        if (boolean.class == type || Boolean.class == type) {
            field.set(parent, Boolean.parseBoolean(value));
        } else if (int.class == type || Integer.class == type) {
            field.set(parent, (value.startsWith("0x")) ?
                    Integer.parseInt(value.substring(2), 16) : Integer.parseInt(value));
        } else if (long.class == type || Long.class == type) {
            field.set(parent, (value.startsWith("0x")) ?
                    Long.parseLong(value.substring(2), 16) : Long.parseLong(value));
        } else if (short.class == type || Short.class == type) {
            field.set(parent, (value.startsWith("0x")) ?
                    Short.parseShort(value.substring(2), 16) : Short.parseShort(value));
        } else if (byte.class == type || Byte.class == type) {
            field.set(parent, value.getBytes()[0]);
        } else if (double.class == type || Double.class == type) {
            field.set(parent, Double.parseDouble(value));
        } else if (float.class == type || Float.class == type) {
            field.set(parent, Float.parseFloat(value));
        } else if (char.class == type || Character.class == type) {
            field.set(parent, value.toCharArray()[0]);
        }
        field.set(parent, object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> tClass)
            throws
            RuntimeException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        Constructor<T> constructor = null;
        try {
            constructor = tClass.getDeclaredConstructor();
        }
        // 这种情况下，构造器往往是私有的，多用于工厂方法，刻意的隐藏了构造器。
        catch (NoSuchMethodException e) {
            // private阻止不了反射的脚步:)
            for (Constructor<?> con : tClass.getDeclaredConstructors()) {
                if (con.getParameterTypes().length == 0) {
                    constructor = (Constructor<T>) con;
                    break;
                }
            }
        }

        if (constructor != null) {
            accessible(constructor);
            return constructor.newInstance();
        }
        return null;
    }
}
