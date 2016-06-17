package com.gtphoto.widget.common.bus;

import com.gtphoto.widget.common.box.Box;

/**
 * Created by kennymac on 16/1/8.
 */
public class Busable implements IBusable{



    private static IBusable get(Object object) {
        return new Busable(object);
    }

    private Busable(Object object) {
        super();
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.register(object);
        }
    }

    @Override
    public void release() {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.unregister(this);
        }
    }

    @Override
    public void postUI(Object object) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.post(object);
        }
    }
}