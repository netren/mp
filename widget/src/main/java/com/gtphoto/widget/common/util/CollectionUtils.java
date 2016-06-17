package com.gtphoto.widget.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kennymac on 15/11/13.
 */
public class CollectionUtils {
    static public <T extends Object> List<T> toList(T t) {
        ArrayList<T> ret = new ArrayList<>();
        ret.add(t);
        return ret;
    }


    static public <T> boolean hasObject(List<T> list, T obj) {
        return list.contains(obj);

    }

    static public int[] toArray(List<Integer> list) {
        final int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
    static public <T> T findObject(List<T> list, T obj) {

        for (T t : list) {
            if (t.equals(obj)) {
                return t;
            }
        }
        return null;
    }



}
