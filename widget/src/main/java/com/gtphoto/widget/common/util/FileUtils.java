package com.gtphoto.widget.common.util;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.gtphoto.widget.LogUtil;
import com.gtphoto.widget.common.box.Box;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;

/**
 * Created by kennymac on 15/9/29.
 */
public class FileUtils {

    private static final String TAG = "hc.FileUtils";

    static public boolean isAndroidLocalPath(String filePath) {
        if (filePath.startsWith("assets-library://asset")) {
            return false;
        }
        return true;
//        isMovieContentPath(filePath)

    }

    static public boolean isMovieContentPath(String filePath) {
        return filePath.startsWith("content://media/external/video/media/") == true;
    }

    static public boolean isMp4Path(String filePath) {
        return filePath.endsWith(".mp4");
    }
    static public boolean isExist(String fileName){
        try{
            File f=new File(fileName);
            if(!f.exists()){
                return false;
            }

        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    //判断时候有file:// 没有就加上
    static public String checkFullPath(String fileName) {
        if (fileName.indexOf("file://") == 0 ) {
            return fileName;
        }
        else {
            return "file://" + fileName;
        }
    }

    static public String toLocalPath(String fileName) {

        if (fileName.startsWith("file://")) {

            return URLDecoder.decode(fileName.replace("file://", "")) ;
        }
        return fileName;
    }

    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file){

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.printException(e);
        }
        //return size/1048576;
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    static public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LogUtil.printException(e);
            }
        }
    }
    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size/1024;
//        if(kiloByte < 1) {
//            return size + "Byte(s)";
//        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    static public String getCacheDirectory() {
        File f = Box.get(Application.class).getExternalCacheDir();
        if (f != null
                && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.i(TAG, "cache fileDir = " + f.getAbsolutePath());
            return f.getAbsolutePath();
        } else {
            Log.i(TAG, "no external storage available");
            f = Box.get(Application.class).getCacheDir();
            if (f != null) {
                return f.getAbsolutePath();
            }
        }
        return "";
    }
}
