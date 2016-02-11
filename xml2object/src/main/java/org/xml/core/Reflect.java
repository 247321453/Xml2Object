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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Reflect {

    public static void set(Field field, Object parent, Object value) throws IllegalAccessException {
        if (field != null) {
            accessible(field);
            try {
                value = wrapper(field.getType(), value);
            } catch (Throwable e) {

            }
            Log.i("xml" , field.getName() + "=" + value);
            field.set(parent, value);
        }
    }

    public static Object get(Field field, Object parent) throws IllegalAccessException {
        if (field != null) {
            accessible(field);
            return field.get(parent);
        }
        return null;
    }

    public static Collection<Field> getFileds(Class<?> type) {
        Map<String, Field> result = new LinkedHashMap<>();
        do {
            for (Field field : type.getDeclaredFields()) {
                String name = field.getName();
                if (!result.containsKey(name))
                    result.put(name, field);
            }
            type = type.getSuperclass();
        } while (type != null);
        return result.values();
    }

    public static Field getTagFiled(Class<?> type, String name) {
        // 尝试作为公有字段处理
        do {
            Field[] fields = type.getDeclaredFields();
            for (Field f : fields) {
                String xmltag = IXml.getTagName(f);
                if (name.equals(xmltag)) {
                    return f;
                }
            }
            type = type.getSuperclass();
        } while (type != null);
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

    public static Object call(Class<?> cls, Object object, String name, Object... args) throws RuntimeException {
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
        if (type == null||type.isEnum()) {
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

    public static Object wrapper(Class<?> type, Object object) throws IllegalAccessException {
        String value = object == null ? "" : String.valueOf(object);
        value = value.replace("\t", "").replace("\r", "").replace("\n", "");
        if (type == null) {
            return object;
        }
        if (boolean.class == type || Boolean.class == type) {
            return Boolean.parseBoolean(value);
        } else if (int.class == type || Integer.class == type) {
            return (value.startsWith("0x")) ?
                    Integer.parseInt(value.substring(2), 16) : Integer.parseInt(value);
        } else if (long.class == type || Long.class == type) {
            return (value.startsWith("0x")) ?
                    Long.parseLong(value.substring(2), 16) : Long.parseLong(value);
        } else if (short.class == type || Short.class == type) {
            return (value.startsWith("0x")) ?
                    Short.parseShort(value.substring(2), 16) : Short.parseShort(value);
        } else if (byte.class == type || Byte.class == type) {
            return value.getBytes()[0];
        } else if (double.class == type || Double.class == type) {
            return Double.parseDouble(value);
        } else if (float.class == type || Float.class == type) {
            return Float.parseFloat(value);
        } else if (char.class == type || Character.class == type) {
            return value.toCharArray()[0];
        } else if (String.class == type) {
            return object == null ? "" : String.valueOf(object);
        } else if (type.isEnum()) {
            Object[] vals = (Object[]) Reflect.call(type, null, "values");
            for (Object o : vals) {
                if (value.equalsIgnoreCase(String.valueOf(o))) {
                    return o;
                }
            }
        }
        return object;
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
    public static <T> T create(Class<T> tClass, Class<?>... args)
            throws
            RuntimeException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        if (tClass.isArray()) {
            return (T) Array.newInstance(tClass.getComponentType(), 0);
        }
        if (tClass.isInterface() || Modifier.isAbstract(tClass.getModifiers())) {
            if (Collection.class.isAssignableFrom(tClass)) {
                if (args.length < 1) {
                    throw new RuntimeException("create(Class<T>, Class<E>)");
                }
                return (T) createList(tClass);
            }
            if (Map.class.isAssignableFrom(tClass)) {
                if (args.length < 2) {
                    throw new RuntimeException("create(Class<T>, Class<K> Class<V>)");
                }
                return (T) createMap(args[0], args[1]);
            }
        }
        Constructor<T> constructor = null;
        try {
            constructor = tClass.getDeclaredConstructor(args);
        }
        // 这种情况下，构造器往往是私有的，多用于工厂方法，刻意的隐藏了构造器。
        catch (NoSuchMethodException e) {
            // private阻止不了反射的脚步:)
            for (Constructor<?> con : tClass.getDeclaredConstructors()) {
                if (con.getParameterTypes().length == args.length) {
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


    public static <T> List<T> createList(Class<T> type) {
        return new ArrayList<T>();
    }

    public static <K, V> Map<K, V> createMap(Class<K> key, Class<V> value) {
        return new HashMap<K, V>();
    }

    public static class NULL {
        public NULL(Class<?> cls) {
            this.clsName = cls;
        }

        public Class<?> clsName;
    }

    //
//    public static Class<?> getMethodClass(Class<?> cls, String metod, Class<?>... args) {
//        Method method = null;
//        try {
//            method = cls.getMethod(metod, args);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        return method == null ? Object.class : method.getReturnType();
//    }

    private static final String TYPE_NAME_PREFIX = "class ";

    private static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        className = className.trim();
        return className;
    }

    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }
//
//    public static Object newInstance(Type type)
//            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        Class<?> clazz = getClass(type);
//        if (clazz == null) {
//            return null;
//        }
//        return clazz.newInstance();
//    }
//
//    public static Type[] getParameterizedTypes(Object object) {
//        Type superclassType = object.getClass().getGenericSuperclass();
//        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
//            return null;
//        }
//        return ((ParameterizedType) superclassType).getActualTypeArguments();
//    }
}

//class Test {
//    public static void main(String[] args) {
//        PeopleType type = PeopleType.Woman;
//        Object[] vals = (Object[]) Reflect.call(type.getClass(), null, "values");
//        for (Object o : vals) {
//            if (PeopleType.Woman.toString().equals(String.valueOf(o))) {
//                System.out.print("" + o);
//            }
//        }
//    }
//}
