package com.gtphoto.mp.event;


import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.gtphoto.base.media.ImageManager;
import com.gtphoto.mp.ErrorCode;
import com.gtphoto.widget.common.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.provider.MediaStore;
//import java.util.TimeZone;

/**
 * Created by kennymac on 15/9/24.
 */
//
//  EventImageItem.swift
//  ungoo
//
//  Created by 陈曦行 on 15/6/23.
//  Copyright (c) 2015年 kenny. All rights reserved.
//

//import Foundation
//import AssetsLibrary
//typealias ReqCallback = (reason : Int) -> Void

class MyExifInterface extends ExifInterface {
    public MyExifInterface(String filename) throws IOException {
        super(filename);
    }

    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");;

    

    public long getDateTime() {
        String dateTimeString = this.getAttribute(TAG_DATETIME);
        if (dateTimeString == null) return -1;

        ParsePosition pos = new ParsePosition(0);
        try {
            // The exif field is in local time. Parsing it as if it is UTC will yield time
            // since 1/1/1970 local time
            Date datetime = sFormatter.parse(dateTimeString, pos);
            if (datetime == null) return -1;
            long msecs = datetime.getTime();

            String subSecs = getAttribute("SubSecTime");
            if (subSecs != null) {
                try {
                    long sub = Long.valueOf(subSecs);
                    while (sub > 1000) {
                        sub /= 10;
                    }
                    msecs += sub;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return msecs;
        } catch (IllegalArgumentException ex) {
            return -1;
        }
    }
}

public class EventImageItem {

    static public Map<String, String> sWeb2localAsset = new HashMap<>();

    public void call(ReqCallback callback, ErrorCode reason) {
        if (callback != null) {
            callback.handle(reason);
        }
    }
    public interface ReqCallback {
        void handle(ErrorCode reason);
    };

    static String FavorChangeKey = "onFavorChange";

    static String VideoSuffix = "mp4";
    public long id = 0;

    public long clientId = -1;

    public String localURL;

    public String thumbnailPath;
    public String filePath;

    public Timestamp takeDate;
    public Timestamp uploadDate;
    public long uploadUserId = 0;
    public ArrayList<Long> msgIds = new ArrayList<>();
    public String desc = "";
    public boolean favor = false;
    public int sortIndex = -1;

    public String gpsLocation = "";
    public long eventItemId = 0;
    public long packageId = 0;
    public long familyId = 0;
    public int width = 0;
    public int height = 0;
    public boolean isMovie = false;
    public String suffix = "";
    public double movieDuration = 0;

    public boolean isAndroidLocalPath() {
        return FileUtils.isAndroidLocalPath(this.localURL);
    }


    public String videoFullURL() {
        return String.format("%s%s.%s", ImageManager.movieHost(), this.filePath, this.suffix);
//        return "";
    }

    public String imageFullURL() {
        return String.format("%s%s", ImageManager.photoHost(), filePath);
    }

    public String imageLocalURL() {
        if (localURL.startsWith("file://")) {
            return localURL;
        }
        return String.format("file://%s", localURL);
    }
    public String videoLocalURL() {
        return String.format("file://%s.%s", localURL, this.suffix);
    }



    EventImageItem(JSONObject json, long eventId, long familyId) {
        this.id = json.optLong("ID", this.id);
        this.favor = json.optBoolean("Favourite", this.favor);

        this.takeDate = new Timestamp((long)json.optDouble("CreationDate", 0) * 1000);

        this.localURL = json.optString("LocalPath");

        this.filePath = json.optString("FilePath");
        this.thumbnailPath = json.optString("ThumbnailPath");
        this.uploadUserId = json.optLong("UploadedBy", this.uploadUserId);
        this.gpsLocation = json.optString("Location");
        this.packageId = json.optLong("PackageId", this.packageId);
        this.width = json.optInt("Width");
        this.height = json.optInt("Height");
        this.isMovie = json.optBoolean("IsMovie");
        this.suffix = json.optString("Suffix");
        this.movieDuration = json.optDouble("MovieDuration");
        this.desc = json.optString("Description");
        this.eventItemId = eventId;
        this.familyId = familyId;
        this.uploadDate = new Timestamp(new Date().getTime());

        JSONArray msgList = json.optJSONArray("Messages");
        if (msgList != null) {
            for (int i = 0; i < msgList.length(); ++i) {
                this.msgIds.add(msgList.optLong(i));
            }
        }

    }

    static public String getMoviePath(String path) {

        if (!path.startsWith("content://media/external/video/media/")) {
            return path;
        }
        return "";

//        Uri uri = Uri.parse(path);
//        String[] proj = {MediaStore.Video.Media.DATA};
//        Cursor actualimagecursor = Box.get(Application.class).getCurrentActivity().managedQuery(uri, proj, null, null, null);
//        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//        if (actualimagecursor.moveToFirst()) {
//            String img_path = actualimagecursor.getString(actual_image_column_index);
//            File file = new File(img_path);
//            Uri fileUri = Uri.fromFile(file);
//
//            String s = fileUri.toString();
//            return s.replace("file://", "");
//        }
//        return path;
//    }
    }

    static public String getImagePath(String path) {


        if (!path.startsWith("content://media/external/images/media")) {
            return path;
        }

        Uri uri = Uri.parse(path);
        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor actualimagecursor = L.application.getCurrentActivity().managedQuery(uri, proj, null, null, null);
//        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        if (actualimagecursor.moveToFirst()) {
//            String img_path = actualimagecursor.getString(actual_image_column_index);
//            File file = new File(img_path);
//            Uri fileUri = Uri.fromFile(file);
//
//            String s = fileUri.toString();
//            return s.replace("file://", "");
//        }
        return path;

    }
    public EventImageItem(String path, long uploadUserId, Date takeDate, long familyId, boolean isMovie, long movieDuration, long dataId ) {

        if (!isMovie) {
            path = getImagePath(path);
        }
        this.familyId = familyId;

        this.takeDate = new Timestamp(takeDate.getTime());
        try {
            MyExifInterface exifInterface = new MyExifInterface(path);
            long time = exifInterface.getDateTime();

            if (time > 0) {
                //Date takeTime =
                this.takeDate = new Timestamp(time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.uploadDate = new Timestamp(new Date().getTime());
        this.uploadUserId = uploadUserId;
        this.localURL = path;
        this.isMovie = isMovie;
        if (isMovie) {
            this.suffix = path.substring(path.lastIndexOf(".") + 1);
            this.movieDuration = movieDuration;
            if (dataId == 0) {
                this.localURL = path;
            }
            else {
                this.localURL = "content://media/external/video/media/" + dataId;
            }
        }

        int start = path.lastIndexOf(".");
        if (start > 0) {

            String ext = path.substring(start + 1);
            if (ext.indexOf(VideoSuffix) >= 0) {
                this.isMovie = true;
                this.suffix = VideoSuffix;
            }
        }

    }

    public EventImageItem() {

    }

    public void fromUploadList(JSONObject upload) {
        this.localURL = upload.optString("LocalPath");
        this.filePath = upload.optString("FilePath");
        this.thumbnailPath = upload.optString("ThumbnailPath");

        this.packageId = upload.optLong("PackageId");
    }

    static EventImageItem findListByClientId(List<EventImageItem> list, long clientId) {
        for (EventImageItem imageItem : list) {
            if (imageItem.clientId == clientId) {
                return imageItem;
            }

        }
        return null;
    }

//        init(asset : ALAsset, uploadUserId : UserId, uploadDate : NSDate, familyId : Int) {
//
//        self.familyId = familyId
//        type = EventImageType.URLFile
//        let localNSURL = asset.valueForProperty(ALAssetPropertyAssetURL) as! NSURL
//
//        localURL = localNSURL.absoluteString
//        takeDate = asset.valueForProperty(ALAssetPropertyDate) as? NSDate
//
//        self.uploadUserId = uploadUserId
//        self.uploadDate = uploadDate
//
//
//
//        let isMovie = asset.valueForProperty(ALAssetPropertyType) as! String == ALAssetTypeVideo
//
//        self.isMovie = isMovie
//        self.suffix = VideoSuffix
//        if self.isMovie {
//        let videoDuration = asset.valueForProperty(ALAssetPropertyDuration) as! NSNumber
////
//        self.movieDuration = videoDuration.integerValue
//        }
////		var image = asset.defaultRepresentation().fullScreenImage().takeUnretainedValue()
//        self.width = 0 //CGImageGetWidth(image)
//        self.height = 0 //CGImageGetHeight(image)
//
//        }
//
//        init(dictionary dict : NSDictionary) {
//        dict.checkSetValue(&id, key : "id")
//        dict.checkSetValue(&favor, key : "favor")
//        if self.id == 0 {
//        self.id = EventItemManager.sharedInstance(UserInfoManager.sharedInstance().currFamilyId).nextImageId++
//        }
//        else {
//        if self.id >= EventItemManager.sharedInstance(UserInfoManager.sharedInstance().currFamilyId).nextImageId {
//        EventItemManager.sharedInstance(UserInfoManager.sharedInstance().currFamilyId).nextImageId++
//        }
//        }
//        type = EventImageType(rawValue: (dict["type"] as? NSNumber)!.integerValue)
//        if type == EventImageType.LocalFile {
//        localURL = dict["localURL"] as? String
//
//        }
//        else if type == EventImageType.URLFile {
//        localURL = dict["localURL"] as? String
//        let urlString = dict["URL"] as? String
//        if urlString != nil {
//        //URL = NSURL(string: urlString!)
//        }
//        takeDate = dict["takeDate"] as? NSDate
//        uploadDate = dict["uploadDate"] as? NSDate
//        }
//
//        dict.checkSetValue(&uploadUserId, key : "uploadUserId")
//        dict.checkSetValue(&desc, key : "desc")
//        if uploadUserId == 0 {
//        uploadUserId = UserInfoManager.sharedInstance().selfUserId
//        }
//        let mIds = dict.objectForKey("msgIds") as? NSArray
//        if mIds != nil {
//
//        for i in 0 ..< mIds!.count {
//        self.msgIds.append((mIds!.objectAtIndex(i) as! NSNumber).integerValue)
//        }
//        }
//        }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("ClientId", this.clientId);

            if (this.isMovie) {
                json.put("IsMovie", this.isMovie);
                json.put("MovieDuration", this.movieDuration);
            }

            if (this.suffix.length() != 0) {
                json.put("Suffix", this.suffix);
            }


            if (this.takeDate != null) {
                json.put("CreationDate", (double) this.takeDate.getTime() / 1000);
            }

            // location

            if (this.localURL != null) {
                json.put("LocalPath", this.localURL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void save() {

        //
        //DBEventImageItemEx.update(this);
    }

//    public void getImageMsg(final ReqCallback callback) {
//        if (this.msgIds.size() == 0) {
//            call(callback, ErrorCode.Success);
//            return;
//        }
//        boolean needGet = false;
//        for (long msgId : this.msgIds) {
//            EventMessageItem imageMsg = UserMessageManager.sharedInstance.getImageMsg(msgId);
//            if (imageMsg == null) {
//                needGet = true;
//                break;
//            }
//
//        }
//
//
//        if (needGet == false) {
//            call(callback, ErrorCode.Success);
//            return;
//        }
//        else {
//            Linker.listImageMessage(this.id, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callback, code);
//                        return;
//                    }
//                    if (JSONArray.class.isInstance(obj)) {
//                        JSONArray msgList = (JSONArray) (obj);
//                        ArrayList<Long> msgIds = new ArrayList<>();
//                        for (int i = 0; i < msgList.length(); i++) {
//                            JSONObject jsonObject = msgList.optJSONObject(i);
//                            if (jsonObject != null) {
//                                EventMessageItem imageMessageItem = new EventMessageItem(jsonObject);
//                                UserMessageManager.sharedInstance.addImageMsg(imageMessageItem);
//                                msgIds.add(imageMessageItem.id);
//                            }
//                        }
//                        EventImageItem.this.msgIds = msgIds;
//                        save();
//                        call(callback, code);
//
//                    }
//                }
//            });
//        }
//
//
//    }
//
//
//        public void deleteMsg(final long msgId, final ReqCallback callback) {
//            Linker.deleteImageMessage(msgId, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callback, code);
//                        return;
//                    }
//                    UserMessageManager.sharedInstance.delImageMsg(msgId);
//                    msgIds.remove(msgId);
//                    call(callback, code);
//                    Stat.logEvent("删除图片留言", "文字");
//                }
//            });
//        }
//
////
//        public void addImageMessage(String inputText , final ReqCallback callback ) {
//            Linker.leaveImageMessage(inputText, this.id, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callback, code);
//                        return;
//                    }
//                    if (JSONObject.class.isInstance(obj)) {
//                        EventMessageItem messageItem = new EventMessageItem((JSONObject) obj);
//                        UserMessageManager.sharedInstance.addImageMsg(messageItem);
//                        msgIds.add(messageItem.id);
//                        save();
//                        Stat.logEvent("添加图片留言", "文字");
//                    }
//
//                    call(callback, code);
//                }
//            });
//        }
////
//        public void favorImage(Boolean value , final ReqCallback callback) {
//            if (value == false) {
//                Linker.unfavorImage(id, new HttpAsyncCallback() {
//                    @Override
//                    public void completed(ErrorCode code, Object obj) {
//                        if (!code.isSucceeded()) {
//                            call(callback, code);
//                            return;
//                        }
//                        favor = false;
//                        ArrayList<Long> ids = new ArrayList<>();
//                        ids.add(id);
//                        EventItemManager.sharedInstance(familyId).unfavorImages(ids);
//                        save();
//                        call(callback, code);
//                        Stat.logEvent("图片收藏", "取消收藏");
//                    }
//                });
//            }
//            else {
//                final ArrayList<Long> arrayList = new ArrayList<>();
//                arrayList.add(id);
//                Linker.favorImage(arrayList, new HttpAsyncCallback() {
//                    @Override
//                    public void completed(ErrorCode code, Object obj) {
//                        if (!code.isSucceeded()) {
//                            call(callback, code);
//                            return;
//                        }
//                        favor = true;
//                        ArrayList<EventImageItem> eventImageItems = new ArrayList<>();
//                        eventImageItems.add(EventImageItem.this);
//                        EventItemManager.sharedInstance(familyId).favorImages(eventImageItems);
//
//                        save();
//                        call(callback, code);
//                        Stat.logEvent("图片收藏", "添加收藏");
//                    }
//                });
//            }
//        }
//
//    public void addDesc(final String inputText, final ReqCallback callback) {
//        Linker.describeImage(this.id, inputText, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    call(callback, code);
//                    return;
//                }
//                desc = inputText;
//                save();
//                call(callback, code);
//
//                if (inputText.length() == 0) {
//                    Stat.logEvent("图片描述修改", "清空");
//                }
//                else {
//                    Stat.logEvent("图片描述修改", "添加");
//                }
//            }
//        });
//    }
//
//        func addDesc(inputText : String, callback : ) {
//        Linker.describeImage(self.id, desc: inputText) { (seqId, code, result) -> Void in
//        if code != ErrorCode.Success.rawValue {
//        logError("describeImage failed code:\(code)")
//        callback(reason: code)
//        return
//        }
//        else {
//        self.desc = inputText
//        self.save()
//        callback(reason: code)
//        if inputText.isEmpty {
//        Stat.logEvent("图片描述修改", eventLabel: "清空")
//        }
//        else {
//        Stat.logEvent("图片描述修改", eventLabel: "添加")
//        }
//        }
//        }
//
//        }
//
//        }
//

}