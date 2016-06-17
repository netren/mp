package com.gtphoto.widget.common.bus;

import com.gtphoto.widget.common.box.Box;

/**
 * Created by kennymac on 16/1/8.
 */
public class UIBusable {

    public static void register(Object o) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.register(o);
        }
    }

    public static void unregister(Object o) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.unregister(o);
        }
    }

    public static void post(Object event) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.post(event);
        }
    }

//    public static IBusable get(Object object) {
//        return new UIBusable(object);
//    }
//
//    private UIBusable(Object object) {
//        super();
//        UIBus uiBus = Box.get(UIBus.class);
//        if (uiBus != null) {
//            uiBus.register(object);
//        }
//    }
//
//    @Override
//    public void release() {
//        UIBus uiBus = Box.get(UIBus.class);
//        if (uiBus != null) {
//            uiBus.unregister(this);
//        }
//    }
//
//    @Override
//    public void postUI(Object object) {
//        UIBus uiBus = Box.get(UIBus.class);
//        if (uiBus != null) {
//            uiBus.post(object);
//        }
//    }
}