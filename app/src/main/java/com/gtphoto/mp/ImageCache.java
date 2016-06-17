package com.gtphoto.mp;

import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kennymac on 15/10/5.
 */

class StrongReference<T> extends WeakReference<T> {
    private final T referent;

    StrongReference(T referent) {
        super(null);
        this.referent = referent;
    }

    @Override
    public T get() {
        return referent;
    }

    // implement other methods
}

public class ImageCache extends FIFOLimitedMemoryCache {
    private static final String TAG = "ImageCache";
    private final long maxAge;
    private final long checkInterval = 10 * 1000;
    private final Map<String, Long> loadingDates = new HashMap();

    public ImageCache(int sizeLimit) {
        super(sizeLimit);
        maxAge = 60 * 1000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkDeleteImage();
            }
        }).start();
    }

    public void checkDeleteImage() {
        while (true) {
            try {
                Thread.sleep(checkInterval);
            }
            catch (InterruptedException e) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            ArrayList<String> removeList = new ArrayList<>(20);
            synchronized (this.loadingDates) {
                for (Map.Entry<String, Long> entry : loadingDates.entrySet()) {
                    if (currentTimeMillis - entry.getValue() > maxAge) {
                        removeList.add(entry.getKey());
                    }
                }
            }

            for (String key : removeList) {
                removeByGenKey(key);
            }
        }
    }
    @Override
    public boolean put(String key, Bitmap value) {

        String genKey = generateKey(key);
        synchronized (this.loadingDates) {
            boolean putSuccessfully = super.put(genKey, value);
            if (putSuccessfully) {
                synchronized (this.loadingDates) {
                    this.loadingDates.put(genKey, Long.valueOf(System.currentTimeMillis()));
                }

            }
            return putSuccessfully;
        }
    }

    Pattern filePatter = Pattern.compile("_\\d+x\\d+");
    String generateKey(String key) {
        if (key.length() != 0)
            return key;
        Matcher m = filePatter.matcher(key);
        if (m.find()) {
            return key.substring(0, key.lastIndexOf("_"));
        } else {
            Log.d(TAG, "generateKey " + key);
            return key;
        }
//        final String checkStr = "([0-9]+)";
//
//        if (key.matches(checkStr)) {
//
//        }
//        return key;
    }

    @Override
    public Bitmap get(String key) {
        String genKey = generateKey(key);
        synchronized (this.loadingDates) {
            Bitmap getBitmp = super.get(genKey);
            if (getBitmp != null) {

                this.loadingDates.put(genKey, Long.valueOf(System.currentTimeMillis()));
            }
            return getBitmp;
        }

    }
    public Bitmap removeByGenKey(String genKey) {
        synchronized (this.loadingDates) {
            Bitmap ret = super.remove(genKey);
            this.loadingDates.remove(genKey);
            return ret;
        }
    }

    @Override
    public Bitmap remove(String key) {
        String genKey = generateKey(key);
        return removeByGenKey(genKey);
    }

    @Override
    protected Reference<Bitmap> createReference(Bitmap value) {
        return new StrongReference<>(value);
    }


}
