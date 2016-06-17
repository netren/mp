package com.gtphoto.widget.common.bus;

/**
 * Created by kennymac on 16/1/8.
 */
public interface IBusable extends Releaseable{
    void postUI(Object object);
}
