package com.gtphoto.widget.common.net;

/**
 * Created by kennymac on 16/1/9.
 */
public abstract class ReqCallback<T> {
    protected T rsp;
    public void setRsp(T rsp) {
        this.rsp = rsp;
    }



    abstract public void on();

};
