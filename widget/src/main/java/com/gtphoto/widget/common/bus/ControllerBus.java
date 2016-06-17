package com.gtphoto.widget.common.bus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.gtphoto.widget.LogUtil;

/**
 * Created by kennymac on 16/1/8.
 */
public class ControllerBus extends EventBus{
//    public ControllerBus() {
//        super(new SubscriberExceptionHandler() {
//            @Override
//            public void handleException(Throwable exception, SubscriberExceptionContext context) {
//
//                exception.printStackTrace();
//
//            }
//        });
//    }
//
//    public ControllerBus(String identifier) {
//        super(identifier);
//    }
//
//    public ControllerBus(SubscriberExceptionHandler exceptionHandler) {
//        super(exceptionHandler);
//    }
    public ControllerBus() {
        super(new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {

                LogUtil.printException(exception);

            }
        });
    }

    public ControllerBus(String identifier) {
        super(identifier);
    }

    public ControllerBus(SubscriberExceptionHandler exceptionHandler) {
        super(exceptionHandler);
    }

}
