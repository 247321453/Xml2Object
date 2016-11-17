package net.kk.xml.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Reflect {
    private Class<?> mClass;
    private static final HashMap<Class<?>, Reflect> sReflectUtils = new HashMap<>();

    private Reflect(Class<?> pClass) {
        this.mClass = pClass;
    }

    public static Reflect get(Class<?> pClass) {
        Reflect reflect = null;
        synchronized (Reflect.class) {
            reflect = sReflectUtils.get(pClass);
            if (reflect == null) {
                reflect = new Reflect(pClass);
                sReflectUtils.put(pClass, reflect);
            }
        }
        return reflect;
    }

    private volatile boolean makeFields = false;
    private volatile boolean findConstructor = false;
    private final HashMap<String, Field> mFields = new HashMap<>();
    private final HashMap<String, Method> mMethods = new HashMap<>();
    private Constructor<?> mConstructor;
    private Constructor<?> mConstructor2;
    private final List<String> mNULLMethods = new ArrayList<>();

    public <T> T create(Class<?>[] args, Object... objs) throws Exception {
        if (args == null) {
            args = new Class[0];
        }
        Constructor<?> constructor = null;
        if (!findConstructor) {
            findConstructor = true;
            try {
                constructor = mClass.getDeclaredConstructor(args);
                mConstructor = constructor;
            } catch (Exception e) {
                int min = Integer.MAX_VALUE;
                for (Constructor<?> con : mClass.getDeclaredConstructors()) {
                    if (args == null || args.length == 0) {
                        //取一个最小参数的构造，
                        if (con.getParameterTypes().length < min) {
                            min = con.getParameterTypes().length;
                            constructor = con;
                        }
                    } else {
                        if (con.getParameterTypes().length == args.length) {
                            constructor = con;
                            break;
                        }
                    }
                }
                if (constructor == null) {
                    //没有默认值的参数
                    throw new RuntimeException("no find default constructor " + mClass);
                }
                mConstructor2 = constructor;
            }
        } else {
            if (mConstructor != null) {
                constructor = mConstructor;
            } else if (mConstructor2 != null) {
                constructor = mConstructor2;
            } else {
                throw new RuntimeException("no find default constructor " + mClass);
            }
        }
        if (args == null || args.length == 0) {
            objs = ReflectUtils.getDefault(constructor.getParameterTypes());
            return (T) constructor.newInstance(objs);
        }
        return (T) constructor.newInstance(objs);
    }

    private String getMethodKey(String name, Class<?>[] types) {
        return name + ":" + Arrays.toString(types);
    }

    private void findAllFields() {
        Field[] fields = mClass.getDeclaredFields();
        if (fields != null) {
            for (Field f : fields) {
                f.setAccessible(true);
                mFields.put(f.getName(), f);
            }
        }
        if (fields != null) {
            fields = mClass.getFields();
            for (Field f : fields) {
                mFields.put(f.getName(), f);
            }
        }
    }

    public Field get(String name) {
        return get(name, false);
    }

    public Collection<Field> getFields(){
        if (!makeFields) {
            findAllFields();
            makeFields = true;
        }
        return mFields.values();
    }
    public Field get(String name, boolean ignonreCase) {
        Collection<Field> fields = getFields();
        if(fields!=null){
            for (Field field: fields){
                if(ignonreCase){
                    if(field.getName().equalsIgnoreCase(name)){
                        return field;
                    }
                }else{
                    if(field.getName().equals(name)){
                        return field;
                    }
                }
            }
        }
        return null;
    }
    public <T> T get(Object obj, String name) throws Exception {
        return get(obj, name, null);
    }
    public <T> T get(Object obj, String name, Object def) throws Exception {
        Field field = get(name);
        if (field == null) {
            return (T)def;
        }
        return (T) field.get(obj);
    }

    public void set(Object obj, String name, Object value) throws Exception {
        Field field = null;
        synchronized (mFields) {
            if (!makeFields) {
                findAllFields();
                makeFields = true;
            }
            field = mFields.get(name);
        }
        if (field != null) {
            field.set(obj, value);
        }
    }

    public <T> T call(Object obj, String name, Object... args) throws Exception {
        Class<?>[] types = ReflectUtils.warpperClass(args);
        String key = getMethodKey(name, types);
        Method method = null;
        synchronized (mMethods) {
            method = mMethods.get(key);
            if (method == null) {
                if (!mNULLMethods.contains(key)) {
                    // 防止重复
                    method = ReflectUtils.findMethod(mClass, name, types);
                    if (method == null) {
                        mNULLMethods.add(key);
                    }
                }
            }
        }
        if (method == null) {
            return null;
        }
        args = ReflectUtils.reObjects(args);
        return (T) method.invoke(obj, args);
    }

    public static Class<?> wrapper(Class<?> type) {
        return ReflectUtils.wrapper(type);
    }

    public static Object wrapperValue(Class<?> type, Object object) throws Exception {
        return ReflectUtils.wrapperValue(type, object);
    }

    public static boolean isNormal(Class<?> type) throws IllegalAccessException {
        if (type == null || type.isEnum()) {
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


    public static class NULL {
        public NULL(Class<?> cls) {
            this.clsName = cls;
        }

        public Class<?> clsName;
    }
}
