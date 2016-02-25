package org.xml.convert;

import android.util.Log;

import org.xml.annotation.XmlElementMap;
import org.xml.core.KXml;
import org.xml.core.Reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class TypeAdapters {

    final static HashMap<Class<?>, TypeAdapter<?>> ADAPTERS_HASH_MAP = new HashMap<>();

    final static NumberAdapter<Integer> IntegerAdapter = new NumberAdapter<Integer>() {
        @Override
        public Integer read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return (value.startsWith("0x")) ?
                    Integer.parseInt(value.substring(2), 16) : Integer.parseInt(value);

        }
    };
    final static NumberAdapter<Long> LongAdapter = new NumberAdapter<Long>() {
        @Override
        public Long read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return (value.startsWith("0x")) ?
                    Long.parseLong(value.substring(2), 16) : Long.parseLong(value);
        }
    };

    final static NumberAdapter<Double> DoubleAdapter = new NumberAdapter<Double>() {
        @Override
        public Double read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return Double.parseDouble(value);
        }
    };
    final static NumberAdapter<Float> FloatAdapter = new NumberAdapter<Float>() {
        @Override
        public Float read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return Float.parseFloat(value);
        }
    };
    final static NumberAdapter<Byte> ByteAdapter = new NumberAdapter<Byte>() {
        @Override
        public Byte read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            if (value == null || value.length() == 0) return null;
            return value.getBytes()[0];
        }
    };
    final static NumberAdapter<Short> ShortAdapter = new NumberAdapter<Short>() {
        @Override
        public Short read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return (value.startsWith("0x")) ?
                    Short.parseShort(value.substring(2), 16) : Short.parseShort(value);
        }
    };
    final static NumberAdapter<BigInteger> BigIntegerAdapter = new NumberAdapter<BigInteger>() {
        @Override
        public BigInteger read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return new BigInteger(value);
        }
    };
    final static NumberAdapter<BigDecimal> BigDecimalAdapter = new NumberAdapter<BigDecimal>() {
        @Override
        public BigDecimal read(TypeToken token, AnnotatedElement element, Object old) {
            if (token == null) return null;
            String value = token.getText();
            return new BigDecimal(value);
        }
    };

    final static TypeAdapter<Character> CharAdapter = new TypeAdapter<Character>() {
        @Override
        public Character read(TypeToken token, AnnotatedElement type, Object old) {
            if (token == null) return null;
            String value = token.getText();
            if (value == null || value.length() == 0)
                return null;
            return value.toCharArray()[0];
        }

        @Override
        public void write(TypeToken parent, AnnotatedElement type, String name, Character character) {
            if (parent == null) return;
            if (name == null || name.length() == 0) {
                name = getTagName(type);
            }
            TypeToken typeToken = new TypeToken(name);
            typeToken.setType(type);
            if (character != null) {
                typeToken.setText(character.toString());
                parent.add(typeToken);
            }
        }
    };
    final static TypeAdapter<String> StringAdapter = new TypeAdapter<String>() {
        @Override
        public String read(TypeToken token, AnnotatedElement type, Object old) {
            if (token == null) return null;
            return token.getText();
        }

        @Override
        public void write(TypeToken parent, AnnotatedElement type, String name, String str) {
            if (parent == null) return;
            if (name == null || name.length() == 0) {
                name = getTagName(type);
            }
            TypeToken typeToken = new TypeToken(name);
            typeToken.setType(type);
            if (str != null) {
                typeToken.setText(str);
                parent.add(typeToken);
            }
        }
    };
    public final static TypeAdapter<Collection> CollectionAdapter = new TypeAdapter<Collection>() {
        @SuppressWarnings("unchecked")
        @Override
        public Collection read(TypeToken token, AnnotatedElement type, Object old) {
            if (token == null) return null;
            List<TypeToken> typeTokens = token.getTypeTokens();
            String name = getTagName(type);
            if (typeTokens == null || name == null) {
                return null;
            }
            Class<?> subClass = KXml.getListClass(type);
            if (KXml.DEBUG)
                Log.v("xml", "list " + subClass.getName());
            Collection list;
            if (old != null) {
                list = (Collection) old;
            } else {
                list = createCollection(subClass);
            }
            for (TypeToken typeToken : typeTokens) {
                if (typeToken == null) continue;
                if (name.equals(typeToken.getName())) {
                    typeToken.setType(subClass);
                    Object sub = value(typeToken, subClass, null);
                    if (sub != null && subClass.isInstance(sub)) {
                        list.add(sub);
                    } else {
                        Log.w("xml", typeToken.getName() + "@" + subClass.getName() + " is null");
                    }
                }
            }
            return list;
        }

        @Override
        public void write(TypeToken parent, AnnotatedElement type, String name, Collection list) {
            if (parent == null || list == null) return;
            if (name == null || name.length() == 0) {
                name = getTagName(type);
            }
            for (Object obj : list) {
                if (obj == null) continue;
                TypeToken sub = token(obj, type, name);
                if (sub != null) {
                    parent.add(sub);
                }
            }
        }
    };

    public final static TypeAdapter<Map> MapAdapter = new TypeAdapter<Map>() {
        @SuppressWarnings("unchecked")
        @Override
        public Map read(TypeToken token, AnnotatedElement type, Object old) {
            if (token == null) return null;
            String name = getTagName(type);
            List<TypeToken> typeTokens = token.getTypeTokens();
            if (typeTokens == null || name == null) {
                return null;
            }

            Class<?>[] subClasses = KXml.getMapClass(type);
            if (KXml.DEBUG)
                Log.v("xml", " put " + subClasses[0] + "," + subClasses[1] + " size=" + typeTokens.size());
            Map map;
            if (old == null) {
                map = createMap(getType(type), subClasses[0], subClasses[1]);
            } else {
                map = (Map) old;
            }
            String keyName;
            String valueName;
            XmlElementMap xmlElementMap = type.getAnnotation(XmlElementMap.class);
            if (xmlElementMap == null) {
                keyName = KXml.MAP_KEY_NAME;
                valueName = KXml.MAP_VALUE_NAME;
            } else {
                keyName = xmlElementMap.keyName();
                valueName = xmlElementMap.valueName();
            }
            for (TypeToken typeToken : typeTokens) {
                if (typeToken == null) continue;
                if (name.equals(typeToken.getName())) {
                    Class<?> kc = subClasses[0];
                    TypeToken tk = typeToken.get(keyName);
                    Object k = value(tk, kc, null);
                    Class<?> vc = subClasses[1];
                    TypeToken tv = typeToken.get(valueName);
                    Object v = value(tv, vc, null);
                    if (KXml.DEBUG) {
                        Log.v("xml", typeToken.getName() + " put " + (tk != null) + "=" + (tv != null));
                        Log.v("xml", typeToken.getName() + " put " + k + "=" + v);
                    }
                    if (k != null)
                        map.put(k, v);
                }
            }
            return map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(TypeToken parent, AnnotatedElement type, String name, Map map) {
            if (parent == null || map == null) {
                return;
            }
            Class<?>[] subClasses = KXml.getMapClass(type);
            String keyName;
            String valueName;
            XmlElementMap xmlElementMap = type.getAnnotation(XmlElementMap.class);
            if (xmlElementMap == null) {
                keyName = KXml.MAP_KEY_NAME;
                valueName = KXml.MAP_VALUE_NAME;
            } else {
                keyName = xmlElementMap.keyName();
                valueName = xmlElementMap.valueName();
            }
            if (name == null || name.length() == 0) {
                name = getTagName(type);
            }
            Set<Map.Entry<?, ?>> set = map.entrySet();
            for (Map.Entry<?, ?> entry : set) {
                if (entry.getKey() == null) continue;
                TypeToken sub = new TypeToken(name);
                sub.setType(type);
                sub.add(token(entry.getKey(), subClasses[0], keyName));
                sub.add(token(entry.getValue(), subClasses[1], valueName));
                parent.add(sub);
            }
        }
    };

    //endregion

    static {
        ADAPTERS_HASH_MAP.put(Character.class, CharAdapter);
        ADAPTERS_HASH_MAP.put(char.class, CharAdapter);
        ADAPTERS_HASH_MAP.put(String.class, StringAdapter);
        ADAPTERS_HASH_MAP.put(Collection.class, CollectionAdapter);
        ADAPTERS_HASH_MAP.put(BigInteger.class, BigIntegerAdapter);
        ADAPTERS_HASH_MAP.put(BigDecimal.class, BigDecimalAdapter);

        ADAPTERS_HASH_MAP.put(byte.class, ByteAdapter);
        ADAPTERS_HASH_MAP.put(int.class, IntegerAdapter);
        ADAPTERS_HASH_MAP.put(long.class, LongAdapter);
        ADAPTERS_HASH_MAP.put(float.class, FloatAdapter);
        ADAPTERS_HASH_MAP.put(double.class, DoubleAdapter);
        ADAPTERS_HASH_MAP.put(short.class, ShortAdapter);

        ADAPTERS_HASH_MAP.put(Byte.class, ByteAdapter);
        ADAPTERS_HASH_MAP.put(Integer.class, IntegerAdapter);
        ADAPTERS_HASH_MAP.put(Long.class, LongAdapter);
        ADAPTERS_HASH_MAP.put(Float.class, FloatAdapter);
        ADAPTERS_HASH_MAP.put(Double.class, DoubleAdapter);
        ADAPTERS_HASH_MAP.put(Short.class, ShortAdapter);
    }


    private static TypeToken token(Object object, AnnotatedElement element, String name) {
//name
        if (name == null || name.length() == 0) {
            name = TypeAdapter.getTagName(element);
        }
        TypeToken typeToken = new TypeToken(name);
        typeToken.setType(element);
        return typeToken;
    }

    @SuppressWarnings("unchecked")
    private static <T> T value(TypeToken parent, AnnotatedElement element, Object object) {
        String name = TypeAdapter.getTagName(element);
        Class<?> pClass = TypeAdapter.getType(element);
        if (pClass.isArray()) {
            return array(parent, pClass.getComponentType(), object, name);
        } else {
            TypeAdapter<?> adapter = ADAPTERS_HASH_MAP.get(pClass);
            if (adapter != null) {
                return (T) adapter.read(parent, element, object);
            }
            return any(parent, pClass, object);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T array(TypeToken parent, Class<?> pClass, Object object, String name) {
        List<TypeToken> typeTokens = parent.getTypeTokens();
        List<Object> list = new ArrayList<>();
        for (TypeToken token : typeTokens) {
            if (token == null) continue;
            if (name.equals(token.getName())) {
                Object o = value(token, pClass, null);
                if (o != null) {
                    list.add(o);
                }
            }
        }
        int count = list.size();
        T t;
        if (object != null) {
            t = (T) object;
        } else {
            t = (T) Array.newInstance(pClass, count);
        }
        for (int i = 0; i < count; i++) {
            Object o = list.get(i);
            Array.set(t, i, o);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    private static <T> T any(TypeToken parent, Class<?> pClass, Object object) {
        //enum
        if (pClass.isEnum()) {
            String value = parent.getText();
            try {
                int intvalue = Integer.parseInt(value);
                Object[] vals = (Object[]) Reflect.call(pClass, null, "values");
                if (vals != null) {
                    if (intvalue >= 0 && intvalue < vals.length) {
                        return (T) vals[intvalue];
                    }
                }
            } catch (Exception e) {
                Object[] vals = (Object[]) Reflect.call(pClass, null, "values");
                if (vals != null) {
                    for (Object o : vals) {
                        if (value.equalsIgnoreCase(String.valueOf(o))) {
                            return (T) o;
                        }
                    }
                }
            }
        } else {
            //attribute
            //sub tag
            //text
            try {
                object = object(parent, pClass, object);
            } catch (Exception e) {

            }
        }
        return (T) object;
    }

    @SuppressWarnings("unchecked")
    private static <T> T object(TypeToken typeToken, Class<?> pClass, Object object) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t = (object == null) ? (T) Reflect.create(pClass) : (T) object;
        //attr
        if (KXml.DEBUG) {
            Log.d("xml", typeToken.getName() + " attr = " + typeToken.getAttributes().size());
        }
        for (Map.Entry<String, String> e : typeToken.getAttributes().entrySet()) {
            setAttribute(t, e.getKey(), e.getValue());
        }
        setText(t, typeToken.getText());
        int count = typeToken.size();
        List<String> oldtags = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TypeToken el = typeToken.get(i);
            String name = el.getName();
            if (oldtags.contains(name))
                continue;
            Field field = Reflect.getTagFiled(pClass, name);
            if (field == null) {
                Log.w("xml", "no find field " + name);
                continue;
            }
            oldtags.add(name);
            Class<?> cls = field.getType();
            Object val = Reflect.get(field, t);
            Object obj = value(typeToken, cls, val);
            if (!Modifier.isFinal(field.getModifiers())) {
                Reflect.set(field, t, obj);
            }
        }
        return t;
    }

    private static void setAttribute(Object object, String tag, String value)
            throws IllegalAccessException {
        if (object == null || tag == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            String name = KXml.getAttributeName(field);
            if (tag.equals(name)) {
                Reflect.set(field, object, Reflect.wrapper(field.getType(), value));
                if (KXml.DEBUG)
                    Log.v("xml", tag + " set " + value);
                break;
            }
        }
    }

    private static void setText(Object object, String value)
            throws IllegalAccessException {
        if (object == null) return;
        Collection<Field> fields = Reflect.getFileds(object.getClass());
        for (Field field : fields) {
            if (KXml.isXmlValue(field)) {
                Reflect.set(field, object, Reflect.wrapper(field.getType(), value));
                break;
            }
        }
    }

    private static <K, V> Map<K, V> createMap(Class<?> rawType, Class<K> key, Class<V> value) {
        if (SortedMap.class.isAssignableFrom(rawType)) {
            return new TreeMap<K, V>();
        } else if (LinkedHashMap.class.isAssignableFrom(rawType)) {
            return new LinkedHashMap<K, V>();
        } else {
            return new HashMap<K, V>();
        }

    }

    private static <T> Collection<T> createCollection(Class<T> rawType) {
        if (SortedSet.class.isAssignableFrom(rawType)) {
            return new TreeSet<T>();
        } else if (EnumSet.class.isAssignableFrom(rawType)) {
            Type type = rawType.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
                if (elementType instanceof Class) {
                    return EnumSet.noneOf((Class) elementType);
                } else {
                    throw new RuntimeException("Invalid EnumSet type: " + type.toString());
                }
            } else {
                throw new RuntimeException("Invalid EnumSet type: " + type.toString());
            }
        } else if (Set.class.isAssignableFrom(rawType)) {
            return new LinkedHashSet<T>();
        } else if (Queue.class.isAssignableFrom(rawType)) {
            return new LinkedList<T>();
        } else {
            return new ArrayList<T>();
        }
    }

}
