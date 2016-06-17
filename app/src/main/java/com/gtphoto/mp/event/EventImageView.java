package com.gtphoto.mp.event;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.gtphoto.mp.L;
import com.gtphoto.mp.R;
import com.gtphoto.widget.common.util.FileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;

public class EventImageView extends ImageView {

    boolean imageLoaded = false;
    boolean tryLoadSync = false;

//    public EventImageItem getEventImageItem() {
//        return eventImageItem;
//    }

    int roundRadius = 0;
    DisplayImageOptions displayImageOptions = null;
    public interface OnLoadedListener {
        void onLoaded(EventImageView imageView, Bitmap bitmap);
    }

    public boolean isImageLoaded() {
        return this.imageLoaded;
    }
    EventImageItem eventImageItem;
    OnLoadedListener listener;
    Bitmap showBitmap;

    public Bitmap getShowBitmap() {
        return showBitmap;
    }

    public void setOnLoadedListener(OnLoadedListener listener) {
        this.listener = listener;
    }
    float fixRatio = 1;
    boolean useFixRatio = false;
    int followEdge = 0;

    float heightWidthRatio = 0;

    public void setHeightWidthRatio(float heightWidthRatio) {
        this.heightWidthRatio = heightWidthRatio;
    }

    public float getHeightWidthRatio() {
        return heightWidthRatio;
    }

    public EventImageView(Context context) {
        super(context);
    }

    public EventImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initMyAttr(context, attrs);
    }

    public EventImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initMyAttr(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EventImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initMyAttr(context, attrs);
    }

    public void initMyAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EventImageView, 0, 0);
        try {
            useFixRatio = a.getBoolean(R.styleable.EventImageView_useFitRatio, false);
            fixRatio = a.getFloat(R.styleable.EventImageView_fitRatio, 1);
            followEdge  = a.getInt(R.styleable.EventImageView_followEdge, 0);
        } finally {
            a.recycle();
        }

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (useFixRatio) {
            if (followEdge == 0) {
                int width = this.getMeasuredWidth();
                int height = (int) (width * this.fixRatio);
                setMeasuredDimension(width, height);
            }
            else {
                int height = this.getMeasuredHeight();
                int width = (int) (height * this.fixRatio);
                setMeasuredDimension(width, height);
            }
        }

    }

    public void loadImage(final EventImageItem eventImageItem, OnLoadedListener listener) {
        loadImage(eventImageItem, listener, false, 0);
    }

    public void loadImage(final EventImageItem eventImageItem, OnLoadedListener loadedListener, boolean tryLoadSync, int roundRadius) {
        if (this.roundRadius != roundRadius ) {
            this.displayImageOptions = null;
        }

        this.tryLoadSync = tryLoadSync;
        this.setOnLoadedListener(loadedListener);
        this.roundRadius = roundRadius;
        this.eventImageItem = null;
        if (this.getWidth() > 0 && this.getHeight() > 0) {
            if (tryLoadSync) {
                Bitmap bitmap = trySyncLoadImage(eventImageItem);
            }
            if (this.roundRadius > 0) {
                this.displayImageOptions = new DisplayImageOptions.Builder().cloneFrom(L.displayImageOptions).displayer(new RoundedImageDisplayer(this.roundRadius)).build();
            }
//            if (eventImageItem.uploadUserId == UserInfoManager.sharedInstance().selfUserId() &&
              if (      eventImageItem.isAndroidLocalPath() ) {


                ImageLoader.getInstance().displayImage(eventImageItem.imageLocalURL(), this, this.displayImageOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        loadImageByWeb(eventImageItem);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        imageLoaded = true;
                        showBitmap = bitmap;
                        if (listener != null) {
                            listener.onLoaded(EventImageView.this, bitmap);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            } else {
                loadImageByWeb(eventImageItem);
                //ImageLoader.getInstance().displayImage(eventImageItem.imageFullURL(), this);
            }
        } else {
            this.eventImageItem = eventImageItem;
        }


    }

    public static abstract class OnSizeChangedListener {
        public abstract void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    OnSizeChangedListener onSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.eventImageItem != null && w > 0 & h > 0) {
            loadImage(this.eventImageItem, this.listener, this.tryLoadSync, this.roundRadius);
        }
        if (this.onSizeChangedListener != null) {
            this.onSizeChangedListener.onSizeChanged(w,h,oldw,oldh);
        }
    }

    public Bitmap trySyncLoadImage(EventImageItem eventImageItem) {
        Bitmap bitmap;
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("denyDownload", true);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cloneFrom(L.displayImageOptions).extraForDownloader(extraMap).build();



        ImageViewAware imageViewAware = new ImageViewAware(this);

        ImageSize imageSize = new ImageSize(imageViewAware.getWidth(), imageViewAware.getHeight());
//        if (eventImageItem.uploadUserId == UserInfoManager.sharedInstance().selfUserId() &&
        if (eventImageItem.isAndroidLocalPath() && FileUtils.isExist(eventImageItem.localURL)) {
                bitmap = ImageLoader.getInstance().loadImageSync(eventImageItem.imageLocalURL(), imageSize, options);
            if (bitmap == null) {
                bitmap = ImageLoader.getInstance().loadImageSync(eventImageItem.imageFullURL(), imageSize, options);

            }
            if (bitmap != null) {
                return bitmap;
            }
        } else {
            bitmap = ImageLoader.getInstance().loadImageSync(eventImageItem.imageFullURL(), imageSize, options);
        }

        return bitmap;

    }
    public void loadImage(final EventImageItem eventImageItem) {
        loadImage(eventImageItem, null, false, 0);
    }

    void loadImageByWeb(final EventImageItem eventImageItem) {
        ImageLoader.getInstance().displayImage(eventImageItem.imageFullURL(), this, this.displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                imageLoaded = true;
                showBitmap = bitmap;
                if (listener != null) {
                    listener.onLoaded(EventImageView.this, bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    public void cancelLoad() {
        imageLoaded = false;
        listener = null;
        showBitmap = null;
        this.eventImageItem = null;
        ImageLoader.getInstance().cancelDisplayTask(this);

    }
}

