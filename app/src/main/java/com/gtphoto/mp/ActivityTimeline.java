package com.gtphoto.mp;

import com.gtphoto.base.media.MediaLoader;
import com.gtphoto.widget.common.box.Box;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_timline)
public class ActivityTimeline extends BaseActivity {


    @AfterViews
    void onAfterView() {
        Box.get(MediaLoader.class).loadAsync(this, getSupportLoaderManager(), new MediaLoader.LoadCallback() {
            @Override
            public void onLoad(MediaLoader mediaLoader) {
//                EventItemManager.sharedInstance(0).updateEventList();
            }
        });
    }
}
