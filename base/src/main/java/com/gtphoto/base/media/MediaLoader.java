package com.gtphoto.base.media;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kennymac on 16/6/16.
 */
public class MediaLoader  {

    public interface LoadCallback {
        void onLoad(MediaLoader mediaLoader);
    }

    LoadCallback loadImageCallback;
    public void loadAsync(Context context, LoaderManager loaderManager, LoadCallback loadCallback) {
        this.loadImageCallback = loadCallback;
        this.context = context;
        this.loaderManager = loaderManager;
        loaderManager.initLoader(LOADER_IMAGE, null, mLoaderCallback);
    }

    Context context;

    LoaderManager loaderManager;

    List<MediaItem> mediaItems;

    ArrayList<MediaFolder> mediaFolders;

    public ArrayList<MediaFolder> getMediaFolders() {
        return mediaFolders;
    }

    public List<MediaItem> getMediaItems() {
        return mediaItems;
    }

    private Map<String, MediaFolder> mResultFolder = new HashMap<>();

    private static final int LOADER_IMAGE = 0;
    private static final int LOADER_VIDEO = 1;

    public MediaLoader() {
        this.context = null;
        this.loaderManager = null;
    }

    boolean hasFolderGened = false;

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        List<MediaItem> images = new ArrayList<>();

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE
        };

        private final String[] VIDEO_PROJECTION = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            if(id == LOADER_IMAGE) {
                doneCount = 0;
                CursorLoader cursorLoader = new CursorLoader(context,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }
            else if (id == LOADER_VIDEO){
                CursorLoader cursorLoader = new CursorLoader(context,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                        null, null, VIDEO_PROJECTION[2] + " DESC");
                return cursorLoader;
            }



            return null;
        }

        static final String MicroMsgDir = "MicroMsg";
        static final String WeiXinDir = "WeiXin";
        static final String WeiXinVideoDir = "video";

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (loader.getId() == LOADER_IMAGE) {
                if (data != null) {
//                    List<Image> mediaItems = new ArrayList<>();
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        do {

                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));

                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                            long id = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                            long size = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                            onCursor(path, name, dateTime, id, size, false, 0);

                        } while (data.moveToNext());
                        onDone(false);

                    } else {
                        onDone(false);

                    }
                } else {
                    onDone(false);
                }
            }
            else if (loader.getId() == LOADER_VIDEO) {
                if (data != null) {
//                    List<Image> mediaItems = new ArrayList<>();
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        do {

                            String path = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));

                            String name = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
                            long dateTime = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                            long id = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
                            long size = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
                            long duration = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                            onCursor(path, name, dateTime, id, size, true, duration / 1000);
                            //Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(cr, id, MediaStore.Video.Thumbnails.MICRO_KIND, null);


                        } while (data.moveToNext());

                        onDone(true);


                    } else {
                        onDone(true);
                    }
                } else {
                    onDone(true);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        int doneCount = 0;
        void onDone(boolean isMoive) {
            ++doneCount;
            if (doneCount == 1) {
               loaderManager.restartLoader(LOADER_VIDEO, null, mLoaderCallback);
            }
            if (doneCount == 2) {

                // 设定默认选择
//                if (resultList != null && resultList.size() > 0) {
//                    mImageAdapter.setDefaultSelected(resultList);
//                }

                mediaFolders = new ArrayList(mResultFolder.values());
//                    for (Map.Entry<String, Folder> f : mResultFolder.entrySet()) {
//                        folders.add(f.getValue());
//                    }

                Collections.sort(mediaFolders, new Comparator<MediaFolder>() {
                    @Override
                    public int compare(MediaFolder lhs, MediaFolder rhs) {
                        if (lhs.name.equals("Camera"))
                            return -1;
                        if (rhs.name.equals("Camera"))
                            return 1;

                        return lhs.name.compareTo(rhs.name);
                    }
                });
                if (loadImageCallback != null) {
                    loadImageCallback.onLoad(MediaLoader.this);
                }

                hasFolderGened = true;
            }
        }
        void onCursor(String path, String name, long dateTime, long id, long size, boolean isMovie, long duration) {
            if (size < 51200) { //图片太小的过滤掉
//                            int a = 0;
                return;
            }
            //过滤 微信 缓存 文件夹
            if (path.lastIndexOf(MicroMsgDir) >= 0) {
                if (path.lastIndexOf(WeiXinDir) == -1 && path.lastIndexOf(WeiXinVideoDir) == -1) {
                    int lastPos = path.lastIndexOf("/");
                    if (lastPos >= 0) {
                        int findStart = lastPos - MicroMsgDir.length();
                        if (path.indexOf(MicroMsgDir, findStart) != findStart) {
                            int a = 0;
                            return;
                        }
                    }
                }
            }
            MediaItem image = new MediaItem(path, name, dateTime, id, isMovie, duration);
            images.add(image);
            if (!hasFolderGened) {
                // 获取文件夹名称
                File imageFile = new File(path);
                File folderFile = imageFile.getParentFile();
                MediaFolder folder = new MediaFolder();
                folder.name = folderFile.getName();
                folder.path = folderFile.getAbsolutePath();
                folder.cover = image;
                String lowerCase = folder.path.toLowerCase();
                if (!mResultFolder.containsKey(lowerCase)) {
                    List<MediaItem> imageList = new ArrayList<>();
                    imageList.add(image);
                    folder.images = imageList;
                    mResultFolder.put(lowerCase, folder);
                } else {
                    // 更新
                    MediaFolder f = mResultFolder.get(lowerCase);
//                                get(mResultFolder.indexOf(folder));
                    f.images.add(image);
                }
            }

        }
    };
}
