package com.gtphoto.base.media;

import android.graphics.Bitmap;

import com.gtphoto.widget.common.USize;

//import android.support.annotation.Size;
//import android.util.SizeF;


/**
 * Created by kennymac on 15/9/29.
 */
public class ImageManager {
    public static ImageManager shareInstanced = new ImageManager();
    public static float MaxPhotoWidth = 1280;
    public static float MinPhotoHeight = 960;
    public static float MaxMovieThumbWidth = 720;
    public static float MinMovieThumbHeight = 480;
    public static float MaxThumbWidth = 160;
    public static float MinThumbHeight = 120;
    public static float PhotoQuatily = 0.7f;
    public static float MovieThumbQuatily = 0.2f;
    public static float ThumbQuatily = 0.5f;

    public static String VideoSuffix = ".mp4";
    public static String thumbPhotoHost() { return "http://event-thumbnail-images.oss-cn-shenzhen.aliyuncs.com/";}
    public static String photoHost() { return "http://event-images.oss-cn-shenzhen.aliyuncs.com/";}
    public static String movieHost(){ return "http://event-movies.oss-cn-shenzhen.aliyuncs.com/";}


    public Bitmap convertImage(Bitmap bitmap, float maxWidth, float minHeight, float quatily) {
        @SuppressWarnings("RedundantCast") USize imageSize = new USize(bitmap.getWidth(), bitmap.getHeight());
        USize destSize = getImageSize(imageSize, maxWidth, minHeight);

        if (destSize.equals(imageSize)) {
            return bitmap;
        } else {
            //noinspection RedundantCast,RedundantCast
            return Bitmap.createScaledBitmap(bitmap, destSize.getWidth(), destSize.getHeight(), true);
        }
    }

    USize getImageSize(USize orgImageSize , float maxWidth , float minHeight ){
        float dstMaxWidth = maxWidth;
//        var data : NSData!
        if (orgImageSize.getWidth() > orgImageSize.getHeight()) {
            if (orgImageSize.getWidth() > dstMaxWidth) {
                float photoAspect = orgImageSize.getWidth() / orgImageSize.getHeight();
                USize destSize;
                if (photoAspect > 16.0 / 9.0 + 0.1) { //很长的相片
                    float ratio = minHeight / orgImageSize.getHeight();
                    //noinspection RedundantCast
                    destSize = new USize((int)(minHeight / ratio), (int)minHeight);
                }
                else {
                    float ratio = dstMaxWidth / orgImageSize.getWidth();


                    destSize = new USize((int)dstMaxWidth, (int)((float)Math.floor(orgImageSize.getHeight() * ratio)));
                }
                return destSize;
            }
            else {
                return orgImageSize;
            }
        }
        else {
            if (orgImageSize.getHeight() > dstMaxWidth) {

                float photoAspect = orgImageSize.getHeight() / orgImageSize.getWidth();
                USize destSize;
                if (photoAspect > 16.0 / 9.0 + 0.1) { //很长的相片
                    float ratio = minHeight / orgImageSize.getWidth();
                    destSize = new USize((int)minHeight, (int)(minHeight * ratio));
                }
                else {
                    float ratio = dstMaxWidth / orgImageSize.getHeight();

                    destSize = new USize((int)(Math.floor(orgImageSize.getWidth() * ratio)), (int)dstMaxWidth);

                }
                return destSize;
            }
            else {
                //直接save
                return orgImageSize;
            }
        }

    }

};
