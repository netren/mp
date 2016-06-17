package com.gtphoto.base.media;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

/**
 * Created by kennymac on 15/9/29.
 */
public class UFileNameGenerator implements FileNameGenerator {



    @Override
    public String generate(String Uri) {
        if (!Uri.startsWith("http://")) {
            return Uri;
        }
        String folder = "";
        if (Uri.startsWith(ImageManager.photoHost())) {
            folder = "photos/";
        }
        else if (Uri.startsWith(ImageManager.movieHost())) {
            folder = "movie/";
        }
        else if(Uri.indexOf("/c2d/") != -1) {
            folder = "c2d/";
        }
        else {
            folder = "images/";
        }

        int last = Uri.lastIndexOf("/");
        if (last > 0) {
            String fileName = Uri.substring(last + 1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(folder);
            String ret = "";
            for (int i = 0; i < Math.min(4, fileName.length()); i++) {
                if (fileName.charAt(i) != '/' ) {

                    stringBuilder.append(fileName.charAt(i));
                    stringBuilder.append('/');
                }
            }
            stringBuilder.append(fileName);
            return stringBuilder.toString();
        }
        return "";
    }

//    public String getLocalFullFile(String Uri) {
//        return L.imageLoader().getDiskCache().getDirectory() + "/" + generate(Uri);
//    }
}
