package com.gtphoto.mp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by kennymac on 15/10/11.
 */
public class MarqueeText extends TextView {
    public MarqueeText(Context context) {
        super(context);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MarqueeText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    boolean forceMarquee = false;

    public void setForceMarquee(boolean forceMarquee) {
        this.forceMarquee = forceMarquee;
    }

//    Handler handler = new Handler();
//    public void delayMarquee(long ms) {
//        handler.postDelayed()
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setForceMarquee(true);
//                setFocusable(true);
//
//            }
//        }, ms);
//    }

    @Override
    public boolean isFocused() {
        if (forceMarquee) {
            return forceMarquee;
        }
        else {
            return super.isFocused();
        }
    }

}
