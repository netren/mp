package com.gtphoto.widget.common.bus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.gtphoto.widget.LogUtil;
import com.gtphoto.widget.common.box.Box;

/**
 * Created by kennymac on 16/1/8.
 */

public class UIBus implements Releaseable {
    EventBus eventBus = new EventBus();
    public UIBus() {
        super();
        eventBus = new EventBus(new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {
                LogUtil.printException(exception);
            }
        });


//        pushStack();

    }

//    Stack<List<WeakReference<Object>>> stacks = new Stack<>();



    public static void register(Object o) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.eventBus.register(o);
        }
    }

    public static void unregister(Object o) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            try {
                uiBus.eventBus.unregister(o);
            } catch (Exception e) {
                LogUtil.printException(e);
            }
        }
    }

    public static void post(Object event) {
        UIBus uiBus = Box.get(UIBus.class);
        if (uiBus != null) {
            uiBus.eventBus.post(event);
        }
    }

    //使用这个可以直接push一个stack记录之后的所有注册的uibus堆栈,方便退出界面后一次过清理所有注册,防止ui一些窗口泄漏.
    //可以在fragment的oncreate调用 push 在destory调用pop
//    public void pushStack() {
//        stacks.push(new ArrayList<WeakReference<Object>>());
//    }

//    public void popStack() {
//        List<WeakReference<Object>> pop = stacks.pop();
//        if (pop == null) {
//            return;
//        }
//        for (WeakReference<Object> r : pop) {
//            Object o = r.get();
//            if (o != null) {
//                unregister(o);
//            }
//        }
//        pop.clear();
//    }


    @Override
    public void release() {
//        while (!stacks.empty()) {
//            popStack();
//        }
    }



}
