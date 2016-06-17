package com.gtphoto.mp;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by kennymac on 15/9/29.
 */
public class CacheFileDownloader extends BaseImageDownloader {

    public CacheFileDownloader(Context context) {
        super(context);
    }

    public CacheFileDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (Scheme.ofUri(imageUri)) {
            case HTTP:
            case HTTPS:
                if (Map.class.isInstance(extra)) {
                    Map<String, Object> extraMap = (Map<String, Object>)(extra);
                    if (extraMap.containsKey("denyDownload")) {
                        Boolean denyDownload = (Boolean)extraMap.get("denyDownload");
                        if (denyDownload) {
                            return null;
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.getStream(imageUri, extra);
    }



}
