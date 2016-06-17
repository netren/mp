package com.gtphoto.widget.common.net;

/**
 * Created by kennymac on 16/1/11.
 */
public interface  Reqback<T> {
    void on(T rsp);

}
