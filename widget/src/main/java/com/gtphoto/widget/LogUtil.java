package com.gtphoto.widget;


import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by kennymac on 16/5/11.
 */
public class LogUtil {
    public static void printException(Throwable e) {
        StringWriter stringWriter = new StringWriter(1024);


        e.printStackTrace(new PrintWriter(stringWriter));
        String toString = stringWriter.toString();
        Log.e("LogUtil", toString);
    }
}
