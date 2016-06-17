package com.gtphoto.widget.common;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kennymac on 16/1/19.
 */

//这个类是调get后不会反会null,会返回一个空应用,直接调用是可以
public class WeakProxy<T extends Object> extends WeakReference<T>{
    Class<?> aClass;
    public WeakProxy() {
        super(null);
    }
    public WeakProxy(T r) {
        super(r);
        aClass = r.getClass();
    }

    public WeakProxy(T r, ReferenceQueue<? super T> q) {
        super(r, q);
        aClass = r.getClass();
    }

    static private Map<String, Object> returnValues = new HashMap<>();

    {
        returnValues.put("byte", 0);
        returnValues.put("short", 0);
        returnValues.put("int", 0);
        returnValues.put("long", 0);
        returnValues.put("char", 0);
        returnValues.put("float", 0);
        returnValues.put("double", 0);
        returnValues.put("boolean", false);
        returnValues.put("void", 0);
    }
    static NullInvocationHandler sNullInvocationHandler = new NullInvocationHandler();
    static class NullInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return returnValues.get(method.getReturnType());
        }
    }

    Proxy proxy = null;

    //判断实际是否为空
    boolean isValid() {
        return super.get() != null;
    }
    @Override
    public T get() {

        T t = super.get();
        if (t != null) {
            return t;
        }
        else {
            if (proxy == null) {

                proxy = (Proxy) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{aClass}, sNullInvocationHandler);
            }
            return (T)proxy;
        }
    }
}
