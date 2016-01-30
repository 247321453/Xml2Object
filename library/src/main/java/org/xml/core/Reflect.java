package org.xml.core;

import android.util.Log;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

class Reflect {

    public static void set(Field field, Object parent, Object value) throws IllegalAccessException {
        if (field != null) {
            wrapper(field, parent, value);
        }
    }

    public static Field[] getFileds(Class<?> cls) {
        return cls.getDeclaredFields();
    }

    public static Field getFiled(Class<?> cls, String name) {
        if (name == null) return null;
        Field[] fields = getFileds(cls);
        for (Field f : fields) {
            if (name.equalsIgnoreCase(f.getName())) {
                return f;
            }
        }
        return null;
    }

    private static Object on(Method method, Object object, Object... args)
            throws RuntimeException {
        try {
            accessible(method);
            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return object;
            } else {
                return method.invoke(object, args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object call(Object object, String name, Object... args) throws RuntimeException {
        if (object == null) return null;
        Class<?> cls = object.getClass();
        Class<?>[] types = types(args);
        args = reObjects(args);
        // 尝试调用方法
        try {
            Method method = exactMethod(cls, name, types);
            return on(method, object, args);
        }

        // 如果没有符合参数的方法，
        // 则匹配一个与方法名最接近的方法。
        catch (NoSuchMethodException e) {
            try {
                Method method = similarMethod(cls, name, types);
                return on(method, object, args);
            } catch (NoSuchMethodException e1) {

                throw new RuntimeException(e1);
            }
        }
    }

    /**
     * 根据方法名和方法参数得到该方法。
     */
    private static Method exactMethod(Class<?> type, String name, Class<?>[] types)
            throws NoSuchMethodException {

        // 先尝试直接调用
        try {
            return type.getMethod(name, types);
        }

        // 也许这是一个私有方法
        catch (NoSuchMethodException e) {
            do {
                try {
                    return type.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException ignore) {
                }

                type = type.getSuperclass();
            } while (type != null);

            throw new NoSuchMethodException();
        }
    }

    /**
     * 给定方法名和参数，匹配一个最接近的方法
     */
    private static Method similarMethod(Class<?> type, String name, Class<?>[] types)
            throws NoSuchMethodException {
        // 对于公有方法:
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }
        // 对于私有方法：
        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }
            type = type.getSuperclass();
        } while (type != null);
        throw new NoSuchMethodException("No similar method " + name
                + " with params " + Arrays.toString(types)
                + " could be found on type " + type + ".");
    }

    private static boolean isSimilarSignature(Method possiblyMatchingMethod,
                                              String desiredMethodName, Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName)
                && match(possiblyMatchingMethod.getParameterTypes(),
                desiredParamTypes);
    }

    private static boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)
                    continue;

                if (wrapper(declaredTypes[i]).isAssignableFrom(
                        wrapper(actualTypes[i])))
                    continue;

                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    private static Class<?> wrapper(Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }
        return type;
    }

    private static Class<?>[] types(Object... values) {
        if (values == null) {
            // 空
            return new Class[0];
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value instanceof NULL) {
                result[i] = ((NULL) value).clsName;
            } else {
                result[i] = value == null ? Object.class : value.getClass();
            }
        }
        return result;
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

    private static Object[] reObjects(Object... args) {
        if (args != null) {
            Object[] news = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof NULL) {
                    news[i] = null;
                } else {
                    news[i] = args[i];
                }
            }
            return news;
        }
        return args;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> tClass)
            throws
            RuntimeException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        if (tClass.isArray()) {
            Log.d("xml",tClass+" is array");
            return (T) Array.newInstance(tClass, 0);
        }
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

    public static class NULL {
        public NULL(Class<?> cls) {
            this.clsName = cls;
        }

        public Class<?> clsName;
    }
}
