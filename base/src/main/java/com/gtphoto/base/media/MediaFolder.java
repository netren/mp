package com.gtphoto.base.media;

import java.util.List;

/**
 * 文件夹
 * Created by Nereo on 2015/4/7.
 */
public class MediaFolder {
    public String name;
    public String path;
    public MediaItem cover;
    public List<MediaItem> images;

    @Override
    public boolean equals(Object o) {
        try {
            MediaFolder other = (MediaFolder) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
