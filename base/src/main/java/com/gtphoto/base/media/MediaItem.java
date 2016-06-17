package com.gtphoto.base.media;

import com.gtphoto.widget.LogUtil;

/**
 * Created by kennymac on 16/6/16.
 */
public class MediaItem {
    public String path;
    public String name;
    public long time;
    public long id;
    public boolean isMovie = false;
    public long duration = 0;
    private boolean selected = false;

    public MediaItem(String path, String name, long time, long id, boolean isMovie, long duration) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.id = id;
        this.isMovie = isMovie;
        this.duration = duration;
    }


    @Override
    public boolean equals(Object o) {
        try {
            MediaItem other = (MediaItem) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e) {
            LogUtil.printException(e);
        }
        return super.equals(o);
    }
}
