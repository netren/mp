package com.gtphoto.mp;

import android.graphics.Bitmap;

import com.gtphoto.base.media.ImageManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kennymac on 15/9/30.
 */
public class ImageDiskCache extends UnlimitedDiscCache {
    public ImageDiskCache(File cacheDir) {
        super(cacheDir);
    }

    public ImageDiskCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }

    public ImageDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
        this.setCompressQuality((int) (ImageManager.PhotoQuatily * 100));
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath());
        tmpFile.getParentFile().mkdirs();
        boolean loaded = false;
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), bufferSize);
            try {
                loaded = IoUtils.copyStream(imageStream, os, listener, bufferSize);
            } finally {
                IoUtils.closeSilently(os);
            }
        }
        catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (loaded && !tmpFile.renameTo(imageFile)) {
                loaded = false;
            }
            if (!loaded) {
                tmpFile.delete();
            }
        }
        return loaded;
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap)  throws IOException  {
        return this.save(imageUri, bitmap, compressFormat, compressQuality);
    }

    public boolean save(String imageUri, Bitmap bitmap, Bitmap.CompressFormat compressFormat, int compressQuality) throws IOException {
        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath());
        tmpFile.getParentFile().mkdirs();
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), bufferSize);
        boolean savedSuccessfully = false;
        try {

            savedSuccessfully = bitmap.compress(compressFormat, compressQuality, os);
        } finally {
            IoUtils.closeSilently(os);
            if (savedSuccessfully && !tmpFile.renameTo(imageFile)) {
                savedSuccessfully = false;
            }
            if (!savedSuccessfully) {
                tmpFile.delete();
            }
        }
        bitmap.recycle();
        return savedSuccessfully;
    }
}
