package com.gtphoto.widget.common.box;

import com.gtphoto.widget.LogUtil;
import com.gtphoto.widget.common.bus.Releaseable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kennymac on 15/12/29.
 */
public class Box {
    static Map<Class<?>, Object> boxMap = new ConcurrentHashMap<>();

    static Map<Class<?>, Object> getMap() {
        try {
            return boxMap;
        } catch (Exception e) {
            LogUtil.printException(e);
            return new HashMap<>();
        }
    }

    static public <T> T get(Class<T> tClass) {
        return (T)getMap().get(tClass);

    }




    static public <T> void add(T instance) throws Exception {
        add(instance.getClass(), instance);
    }

    static public <I, T> void add(Class<I> interfaceClass, T instance) throws Exception {
        boolean b = getMap().containsKey(interfaceClass);

        if (!interfaceClass.isInstance(instance)) {
            throw new Exception(instance.getClass() + " no a class of " + interfaceClass);
        }
        if (b ) {
            throw new Exception("has create");
        }
        else {
            getMap().put(interfaceClass, instance);
        }
    }

    static public <I, T> void set(Class<I> interfaceClass, T instance) {
        boolean b = getMap().containsKey(interfaceClass);


        if (b) {
            Object o = getMap().get(interfaceClass);
            if (o instanceof Releaseable) {
                ((Releaseable) o).release();
            }
            getMap().remove(interfaceClass);
        }
        else {

        }

        getMap().put(interfaceClass, instance);
    }


    public static void release() {
        for (Map.Entry<Class<?>, Object> entry :getMap().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Releaseable) {
                ((Releaseable)value).release();
            }
        }
        getMap().clear();

    }

}
