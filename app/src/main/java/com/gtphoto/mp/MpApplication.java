package com.gtphoto.mp;

import android.app.Application;

import com.gtphoto.widget.common.box.Box;

/**
 * Created by kennymac on 16/6/16.
 */
public class MpApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        L.setApplicationContext(this);
        BoxManager.init(this);
        try {
            Box.add(Application.class, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        BoxManager.release();

        super.onTerminate();
        L.setApplicationContext(null);
    }
}
