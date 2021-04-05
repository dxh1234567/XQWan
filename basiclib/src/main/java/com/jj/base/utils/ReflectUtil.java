package com.jj.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectUtil {

    private static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T invoke(String method, Object receiver, Class<T> returnClass,
                               Class[] paramsClass, Object[] params) {
        if (isEmpty(method) || receiver == null) {
            throw new NullPointerException("reflect method or receiver is null");
        }

        if (paramsClass == null && params != null)
            throw new IllegalArgumentException("illegal agument");

        if (paramsClass != null && params != null
                && params.length != paramsClass.length)
            throw new IllegalArgumentException("illegal aguments count");
        try {
            Method methodObj = receiver.getClass().getDeclaredMethod(method, paramsClass);
            methodObj.setAccessible(true);
            return (T) methodObj.invoke(receiver, params);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Object obtainNonStaticFieldValue(Object obj, String fieldName) {
        Object result = null;
        Class<?> cla = obj.getClass();
        try {
            Field f = cla.getDeclaredField(fieldName);
            if (f != null) {
                f.setAccessible(true);
                if (!isStatic(f)) {
                    result = f.get(obj);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Object obtainNonStaticFieldValue(Class<?> cla,Object obj, String fieldName) {
        Object result = null;
        try {
            Field f = cla.getDeclaredField(fieldName);
            if (f != null) {
                f.setAccessible(true);
                if (!isStatic(f)) {
                    result = f.get(obj);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean isStatic(Member member) {
        int mod = member.getModifiers();
        return Modifier.isStatic(mod);
    }
}
