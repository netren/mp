package com.gtphoto.mp;

import android.content.Context;
import android.util.Log;

import com.gtphoto.base.media.MediaLoader;
import com.gtphoto.widget.LogUtil;
import com.gtphoto.widget.common.box.Box;


/**
 * Created by kennymac on 16/1/8.
 */
//专门管理单件的全局类
public class BoxManager {

    private static final String TAG = "BoxManager";
    static int count = 0;
    static boolean inited = false;


    public static void increaseRef(Context context) {
        Log.d(TAG, "increaseRef: count:" + count);
        L.setApplicationContext(context.getApplicationContext());

        if (inited == false) {
            init(context);

        }

//        if (count == 0) {
//            init(context);
//        }

        count++;
    }

    public static void decreaseRef() {
        count--;
        Log.d(TAG, "decreaseRef: count:" + count);

        if (count == 0) {
//            release();
            //现在不会逻辑是不会release的
        }
    }

    static void init(Context context) {


        try {
            Box.add(new MediaLoader());

        } catch (Exception e) {
            LogUtil.printException(e);
        }
        inited = true;
    }

    static void release() {
        inited = false;
        Box.release();
    }

}
