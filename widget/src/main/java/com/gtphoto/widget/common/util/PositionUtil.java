package com.gtphoto.widget.common.util;

import android.content.Context;


/**
 * Created by kennymac on 15/9/30.
 */
public class PositionUtil {
    static Float scale;
    static Float fontScale;
    public static int dp2px(Context context, float dpValue) {

        if (scale == null) {
            scale = context.getResources().getDisplayMetrics().density;
        }

        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        if (scale == null) {
            scale = context.getResources().getDisplayMetrics().density;
        }

        return (int) (pxValue / scale + 0.5f);
    }


    public static int px2sp(Context context, float pxValue) {
        if (fontScale == null) {
            fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        }

        return (int) (pxValue / fontScale + 0.5f);
    }


    public static int sp2px(Context context, float spValue) {
        if (fontScale == null) {
            fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        }
        return (int) (spValue * fontScale + 0.5f);
    }

    static public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
