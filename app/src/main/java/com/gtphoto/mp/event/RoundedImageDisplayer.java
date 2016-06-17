package com.gtphoto.mp.event;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by kennymac on 15/9/24.
 */

//class UImageAware extends ImageViewAware {
//
//}

public class RoundedImageDisplayer implements BitmapDisplayer {

    protected final int cornerRadius;
    int marginX;
    int marginY;

    public RoundedImageDisplayer(int cornerRadiusPixels) {
        this.cornerRadius = cornerRadiusPixels;
        this.marginX = 0;
        this.marginY = 0;
    }


    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        float viewRatio = (float)imageAware.getWidth() / (float)imageAware.getHeight();
        marginX = 0;
        marginX = 0;

        if (ratio > viewRatio) {
            float actWidth = (float) bitmap.getHeight() * viewRatio;
            marginX = Math.round(bitmap.getWidth() - actWidth) / 2;
        }
        else if (ratio < viewRatio) {
            float actHeight = (float) bitmap.getWidth() / viewRatio;
            marginY = Math.round(bitmap.getHeight() - actHeight) / 2;
        }

        imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius, marginX, marginY));
    }

    public static class RoundedDrawable extends Drawable {

        protected final float cornerRadius;
        protected final int marginX;
        protected final int marginY;

        protected final RectF mRect = new RectF(),
                mBitmapRect;
        protected final BitmapShader bitmapShader;
        protected final Paint paint;

        public RoundedDrawable(Bitmap bitmap, int cornerRadius, int marginX, int marginY) {
            this.cornerRadius = cornerRadius;
            this.marginX = marginX;
            this.marginY = marginY;

            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapRect = new RectF (marginX, marginY, bitmap.getWidth() - marginX, bitmap.getHeight() - marginY);

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRect.set(0, 0, bounds.width() - 0, bounds.height() );

            // Resize the original bitmap to fit the new bound
            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
            bitmapShader.setLocalMatrix(shaderMatrix);

        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }
    }
}
