package com.gtphoto.widget.common.util;

import java.lang.ref.WeakReference;

/**
 * Created by kennymac on 16/1/9.
 */
public class Ref {
    static public <T> T tryGet(WeakReference<T> weakReference) {
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }
}
