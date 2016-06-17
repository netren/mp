package com.gtphoto.widget.common.util;

import android.net.Uri;

/**
 * Created by kennymac on 15/11/10.
 */
public class StringUtils {
    public static String ext(String filename) {
        int i = filename.lastIndexOf(".");
        if (i >= 0) {
            return filename.substring(i + 1);
        }
        return "";
    }

    static public String urlEncode(String s) {
        return Uri.encode(s);
    }


    static public String concat(String... strings) {

        int total = 0;
        for (String string : strings) {
            total += string.length();
        }
        final StringBuilder stringBuilder = new StringBuilder(total);
        for (String string : strings) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
