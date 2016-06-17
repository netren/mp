package com.gtphoto.widget.common.bus;

import com.gtphoto.widget.common.box.Box;

public class ControllerBase implements Releaseable {
    private static final String TAG = "hc.ControllerBase";


    public ControllerBase() {
        super();
        ControllerBus controllerBus = Box.get(ControllerBus.class);
        if (controllerBus != null) {
            controllerBus.register(this);
        }
    }



    @Override
    public void release() {
        ControllerBus controllerBus = Box.get(ControllerBus.class);
        if (controllerBus != null) {
            controllerBus.unregister(this);
        }
    }
//
//    public boolean req(MessageNano reqObj, ReqCallback<?> callback) {
//       // int seq=0;
//         if(reqObj==null) {
//            Log.e(TAG, "erro: MessageNano reqobj==null");
//             return  false;
//        }
//            int seq = Box.get(INetworker.class).req(reqObj, callback);
//      //  }
//        return seq > 0;
//    }
//
//    static Hc.HC_BaseReq baseReq;
//    static {
//        baseReq = new Hc.HC_BaseReq();
//    }
//    protected <M extends MessageNano> M getProtoReq(Class<M> reqClz) {
//        M req = null;
//        try {
//            req = reqClz.newInstance();
//            reqClz.getMethod("clear").invoke(req);
//            ChannelController channelController = Box.get(ChannelController.class);
//            if (channelController != null) {
//                int channel = channelController.getChannelId();
//                baseReq.channelId = channel;
//            }
//
//            reqClz.getField("baseReq").set(req, baseReq);
//        } catch (Exception e) {
//            LogUtil.printException(e);
//        }
//
//        return req;
//
//    }

    protected void postUI(Object object) {
        UIBus.post(object);
    }


}
