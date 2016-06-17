package com.gtphoto.mp;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.gtphoto.base.media.ImageManager;
import com.gtphoto.widget.LogUtil;
import com.gtphoto.base.media.UFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by kennymac on 16/1/12.
 */
public class L {

    //    public static Context context; //happyActivity
    public static SharedPreferences sharedPreferences;
    public static final String IS_USE_FRONTCAMERA = "isUserFrontCamera";
    public static final String KEYBOARD_HEIGHT = "keyboardHeight";

    static int sAppId = -1;

    static Context sApplicationContext = null;

    static public AssetManager getAssets() {
        return L.getApplicationContext().getAssets();
    }

    static public Resources getResources() {
        return getApplicationContext().getResources();
    }

    //    public static String resHost = "http://image.hc-gray.ttyuyin.com";
    public static String resHost = "http://cdn.52tt.com";
    public static String appHost = "http://app.live.52tt.com";//内嵌页面连接
    public static String PicassoCacheUrl = Environment.getExternalStorageDirectory().toString() + "/picasso";

    public static String fullUrl(String url) {
        return String.format("%s%s", resHost, url);
    }

    public static String replaceHostUrl(String url) {
        return url.replace("$host$", resHost);
    }

    public static String fullAppUrl(String url) {
        return String.format("%s%s", appHost, url);
    }

//    Stack<WeakProxy<Context> activityContext;
//
//    static void pushCurrActivity(Activity currActivity) {
//
//    }


    public static void setApplicationContext(Context applicationContext) {
        try {
            sApplicationContext = applicationContext;

        } catch (Exception e) {
            LogUtil.printException(e);
        }
    }

    public static Context getApplicationContext() {
        return sApplicationContext;

    }

    public static String S(@StringRes int resId) {
        if (getApplicationContext() != null) {
            return getResources().getString(resId);
        }
        return "##";
    }


    public static String S(@StringRes int resId, Object... formatArgs) {
        if (getApplicationContext() != null) {
            return getResources().getString(resId, formatArgs);
        }
        return "##";

    }

    public static Drawable getDrawable(@DrawableRes int id) {
        if (getApplicationContext() != null) {
            return getResources().getDrawable(id);
        }
        return null;
    }

    public static float dimen(@DimenRes int id) {
        if (getApplicationContext() != null) {
            return getResources().getDimension(id);
        }
        return 0;
    }

    public static int color(int id) {
        if (getApplicationContext() != null) {
            return getResources().getColor(id);
        }
        return 0;
    }

    private static long lastClickTime = System.currentTimeMillis();

//    public synchronized static boolean isFastClick() {
//        return isFastClick(DebugConst.checkFastTime);
//    }
//
//    public synchronized static boolean isFastClick(int deltaTime) {
//        long time = System.currentTimeMillis();
//        if (time - lastClickTime < deltaTime) {
//            return true;
//        }
//        lastClickTime = time;
//        return false;
//    }


    static void release() {
        setApplicationContext(null);
    }

    public static UFileNameGenerator fileNameGenerator;
    public static ImageLoaderConfiguration imageLoaderConfiguration;
    public static DisplayImageOptions displayImageOptions;

    private static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    @TargetApi(11)
    private static boolean isLargeHeap(Context context) {
        return (context.getApplicationInfo().flags & 1048576) != 0;
    }

    @TargetApi(11)
    private static int getLargeMemoryClass(ActivityManager am) {
        return am.getLargeMemoryClass();
    }


    public static void initImageLoader() {
        if (ImageLoader.getInstance().isInited()) {
            return;
        }
        L.fileNameGenerator = new UFileNameGenerator();

        File cacheDir = StorageUtils.getCacheDirectory(L.getApplicationContext());

        ActivityManager am = (ActivityManager)L.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        if(hasHoneycomb() && isLargeHeap(L.getApplicationContext())) {
            memoryClass = getLargeMemoryClass(am);
        }

        long memoryCacheSize = 1048576 * memoryClass / 8;

//        long cacheSize = SystemUtil.getAvailMemory(this);
//        cacheSize = cacheSize / 10 * 3;
        //long cacheSize = 50 * 1024 * 1024;
//        if (cacheSize > 50 * 1024 * 1024) {
//            cacheSize =
//        }
//        if (cacheSize < 50 * 1024 * 1024) {
//            cacheSize = 50 * 1024 * 1024;
//        }

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200, true, true, false))
                .build();

        int coreNum = SystemUtil.getNumCores();
        int threadNum = 0;
        if (coreNum > 1) {
            switch (threadNum = (int) ((coreNum / 2))) {
            }
        }
        else {
            threadNum = 1;
        }
        L.displayImageOptions = displayImageOptions;
        CacheFileDownloader cacheFileDownloader = new CacheFileDownloader(L.getApplicationContext());

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(L.getApplicationContext())
                .memoryCacheExtraOptions((int) (ImageManager.MaxPhotoWidth * 1.2), (int) (ImageManager.MaxPhotoWidth * 1.2))
//                .diskCacheExtraOptions((int) (ImageManager.MaxPhotoWidth * 1.2), (int) (ImageManager.MaxPhotoWidth * 1.2), new BitmapProcessor() {
//                    @Override
//                    public Bitmap process(Bitmap bitmap) {
//                        return bitmap;
//                    }
//                })
                .imageDownloader(new CacheFileDownloader(L.getApplicationContext()))
                .memoryCache(new ImageCache((int)memoryCacheSize))
//                .denyCacheImageMultipleSizesInMemory()
                .threadPoolSize(threadNum)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
//                .memoryCacheSize(0)
                .diskCache(new ImageDiskCache(cacheDir, null, L.fileNameGenerator))

//                .diskCacheSize(1000 * 1024 * 1024)
//                .diskCacheFileCount(100000)


                .defaultDisplayImageOptions(displayImageOptions)
                        // 添加你的配置需求
//                .writeDebugLogs()
                .build();


        ImageLoader.getInstance().init(configuration);
        L.imageLoaderConfiguration = configuration;
        //Linker.testCore();

    }
    public static ImageLoader imageLoader() {
        if (!ImageLoader.getInstance().isInited()){
            initImageLoader();
        }
        return ImageLoader.getInstance();
    }

    static DisplayImageOptions localResDisplayOption;
    public static DisplayImageOptions getLocalResDisplayOption() {
        if (localResDisplayOption == null) {
            localResDisplayOption = new DisplayImageOptions.Builder().cloneFrom(L.displayImageOptions).cacheOnDisk(false).build();
        }
        return localResDisplayOption;
    }
}
