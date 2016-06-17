package com.gtphoto.mp.event;//
//  EventItem.swift
//  ungoo
//
//  Created by 陈曦行 on 15/4/21.
//  Copyright (c) 2015年 kenny. All rights reserved.
//
//import android.nfc.Tag;

import android.util.Log;

import com.gtphoto.mp.ErrorCode;
import com.gtphoto.mp.R;
import com.gtphoto.mp.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class EventItem implements Cloneable {
    public void call(ReqCallback callback, ErrorCode reason) {
        if (callback != null) {
            callback.handle(reason);
        }
    }
    public interface ReqCallback {
        void handle(ErrorCode reason);
    };

    final public static String EventMsgDelKey = "onEventMsgDel";
    final public static String EventMsgAddlKey = "onEventMsgAdd";

    public static String VideoSuffix = "mp4";
    public long id = 0;
    public long clientId = -1;
    public long familyId = 0;
    public String title = "";

    public String getShowTitle() {
        if (!this.hasGetDetail) {
            return L.S(R.string.loading_string);
        }
        else {
            return this.title;
        }
    }
    public Timestamp  timestamp = new Timestamp(0);
    public boolean hasGetDetail = false;

    public Timestamp dateBegin = new Timestamp(new Date().getTime());
    public Timestamp dateEnd = new Timestamp(0);
    public boolean isImport = false;
    public long createUserId = 0;
    ArrayList<EventImageItem> images = new ArrayList<>();
    public ArrayList<Integer> uploadSortIndexs = new ArrayList<>();
    public ArrayList<Integer> takeSortIndexs = new ArrayList<>();
    private Map<Long, Set<Long>> packageIds = new HashMap(); //该事件所有的packageId,后边那个是数量
    public Map<Long, WeakReference<EventImageItem> > id2image = new HashMap<>();

    public ArrayList<Long> msgIds = new ArrayList<>();
    public int msgNum() {
        return msgIds.size();
    }


    public void save() {
//        DBEventItemEx.update(this);
        this.saveAllImage();
    }

    public void saveAllImage() {
//        DBEventImageItemEx.deleteByEventId(this.id);
//
//        if (this.images.size()> 0) {
//            DBEventImageItemEx.update(this.images);
//
//        }
    }

    public Map<Long, Set<Long>> getPackageIds() {
        if (packageIds.isEmpty()) {
            for (EventImageItem imageItem : this.images) {
                if (!packageIds.containsKey(imageItem.packageId)) {
                    packageIds.put(imageItem.packageId, new HashSet<Long>());
                }
                packageIds.get(imageItem.packageId).add(imageItem.id);
            }
        }
        return packageIds;
    }


    public boolean update(EventItem newEventItem) {
        long result = (long) (Math.floor(newEventItem.timestamp.getTime() / 1000) - Math.floor(this.timestamp.getTime() / 1000));

        if (result < 0) {
            return false;
        }
//        let delta = floor(newEventItem.timestamp.timeIntervalSince1970) - floor(self.timestamp.timeIntervalSince1970)
//        if delta > 0 {
//            result = NSComparisonResult.OrderedAscending
//        }
//        else if delta == 0 {
//            result = NSComparisonResult.OrderedSame
//        }
//
//
//        var result = self.timestamp.compare(newEventItem.timestamp)
        if ((result == 0 && this.hasGetDetail == false && newEventItem.hasGetDetail == true) || result > 0 || (this.hasGetDetail == false && newEventItem.hasGetDetail == true)) {
            this.title = newEventItem.title;

            this.timestamp = newEventItem.timestamp;
            this.hasGetDetail = newEventItem.hasGetDetail;
            if (newEventItem.hasGetDetail) {

                this.timestamp = newEventItem.timestamp;

                this.isImport = newEventItem.isImport;
                this.createUserId = newEventItem.createUserId;
                this.images = newEventItem.images;
                this.uploadSortIndexs = newEventItem.uploadSortIndexs;
                this.takeSortIndexs = newEventItem.takeSortIndexs;
                this.msgIds = newEventItem.msgIds;

                this.dateBegin = newEventItem.dateBegin;
                this.dateEnd = newEventItem.dateEnd;
                this.packageIds = newEventItem.packageIds;
                this.id2image = newEventItem.id2image;

            } else {
                this.dateBegin = newEventItem.dateBegin;
                this.dateEnd = newEventItem.dateEnd;

            }
            this.save();
            return true;
        }


        return false;
    }



    void init(JSONObject json, long familyId, boolean initImages) {
        this.familyId = familyId;

        this.id = json.optInt("ID");
        this.dateBegin = new Timestamp((long)json.optDouble("StartedAt") * 1000);
        this.dateEnd = new Timestamp((long)json.optDouble("EndedAt") * 1000);
        this.timestamp = new Timestamp((long)json.optDouble("Timestamp") * 1000);
        this.isImport = json.optBoolean("Imported");
        this.createUserId = json.optLong("CreatedBy");
        this.title = json.optString("Title");

        JSONArray msgList = json.optJSONArray("Messages");
        if (msgList != null) {
            for (int i = 0; i < msgList.length(); ++i) {
                this.msgIds.add(msgList.optLong(i));
            }
        }

        if (initImages) {
            JSONArray imageList = json.optJSONArray("Images");
            if (imageList == null) {
                return;
            }
            this.hasGetDetail = true;
            for (int i = 0; i < imageList.length(); ++i) {
                this.addImageItem(new EventImageItem(imageList.optJSONObject(i), this.id, this.familyId));
            }


            this.refreshSortIndexes();
        }


    }

    EventItem(JSONObject json, long familyId, boolean initImages) {
        init(json, familyId, initImages);
    }

    EventItem(JSONObject json, long familyId) {
        init(json, familyId, true);
    }

    public EventItem(long familyId) {
        this.familyId = familyId;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("Title", this.title);
            json.put("FamilyId", this.familyId);

            JSONArray images = new JSONArray();
            for (EventImageItem image : allImages()) {
                images.put(image.toJSON());
            }
            json.put("Images", images);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public void addImageItem(EventImageItem imageItem) {
        this.addImageItem(imageItem, false);
    }
    public void addImageItem(EventImageItem imageItem, boolean needCompareTakeDate) {
        if (imageItem.takeDate != null) {

            if (needCompareTakeDate) {
                if (this.dateBegin.compareTo(imageItem.takeDate) > 0) {
                    this.dateBegin = imageItem.takeDate;
                }
                if (this.dateEnd.compareTo(imageItem.takeDate) < 0) {
                    this.dateEnd = imageItem.takeDate;
                }
            }
        }

        images.add(imageItem);

    }

    public ArrayList<EventImageItem> allImages() {
        return this.images;
    }


    public boolean removeImage(long id) {

        for (int i = 0; i < images.size(); i++) {
            if (this.images.get(i).id == id) {
                this.images.remove(i);
                return true;
            }
        }
        return false;
    }

    public int imagesCount() {
        return this.images.size();
    }

    public EventImageItem imageById(long id) {
        WeakReference<EventImageItem> eventImageItemWeakReference = id2image.get(id);
        if (eventImageItemWeakReference == null) {
            return null;
        }
        EventImageItem imageItem = eventImageItemWeakReference.get();
        if (imageItem != null) {
            return imageItem;
        }
//        for (EventImageItem image :
//                this.images) {
//            if (image.id == id) {
//                return image;
//            }
//        }

        return null;
    }

    public EventImageItem imageByTake(int index) {
        if (index >= this.imagesCount()) {
            Log.d("EventImageItem", String.format("id%d index:%d count:%d", this.id, index, this.imagesCount()));
        }
        return this.images.get(this.takeSortIndexs.get(index));
    }

    public EventImageItem imageByUpload(int index) {
        return this.images.get(this.uploadSortIndexs.get(index));
    }

//    //only for eventItem
//    public void appendImage(EventImageItem item) {
//        this.images.add(item);
//    }


    public static Comparator<EventImageItem> sortImageFuncByTake = new Comparator<EventImageItem>() {
        @Override
        public int compare(EventImageItem lhs, EventImageItem rhs) {
            if (lhs.takeDate == null || rhs.takeDate == null) {
                return (int)(lhs.id - rhs.id) ;
            }
            int result = lhs.takeDate.compareTo(rhs.takeDate);
            if (result == 0) {
                return (int)(lhs.id - rhs.id);

            } else {
                return result;
            }
        }
    };

    public static Comparator<EventImageItem> sortImageFuncByUpload = new Comparator<EventImageItem>() {
        @Override
        public int compare(EventImageItem lhs, EventImageItem rhs) {
            if (lhs.packageId != rhs.packageId) {
                return (int)(lhs.packageId - rhs.packageId);
            }
            if (lhs.takeDate == null || rhs.takeDate == null) {
                return (int)(lhs.id - rhs.id);
            }

            int result = lhs.uploadDate.compareTo(rhs.uploadDate);
            if (result == 0) {
                return (int)(lhs.id - rhs.id );
            } else {
                return result;
            }
        }
    };


    public void refreshSortIndexes() {
        this.packageIds.clear();
        int index = 0;
        for (EventImageItem image :
                this.images) {
            image.sortIndex = index++;
            if (image.filePath.length() > 0 && image.localURL.length() > 0) {
                EventImageItem.sWeb2localAsset.put(image.filePath, image.localURL);
            }


        }

        this.takeSortIndexs.clear();
        id2image.clear();

        ArrayList<EventImageItem> takes = new ArrayList<>(this.images.size());
        for (EventImageItem image: this.images) {
            takes.add(image);
            id2image.put(image.id, new WeakReference<>(image));
        }

        Collections.sort(takes, sortImageFuncByTake);

        for (EventImageItem img :
                takes) {
            this.takeSortIndexs.add(img.sortIndex);
        }
        this.uploadSortIndexs.clear();
        Collections.sort(takes, sortImageFuncByUpload);

        for (EventImageItem img :
                takes) {
            this.uploadSortIndexs.add(img.sortIndex);
        }



    }

    public float getEventUploadPercent() {
        return 100;
//        let uploadPhotoManager = UploadPhotoManager.sharedInstance
//        var total:CGFloat = 0
//        var uploadNum:CGFloat = 0
//        for (packageId, count) in self.packageIds {
//            let num = uploadPhotoManager.getPackageUploadNum(packageId)
//            if num != nil {
//                total += CGFloat(count)
//                uploadNum += CGFloat(num !)
//            }
//        }
//        if total == 0 {
//            return 100
//        }
//        else{
//            var t = floor((total - uploadNum) / total * 100)
//            if t< 0 {
//                t = 0
//            }
//            if t > 100 {
//                logError("sth wrong with upload data \(t)")
//                t = 99.9
//            }
//            return t
//        }

    }

//        typealias ReqCallback = (reason : Int) -> Void
        public void modifyTitle(final String title , final ReqCallback callBack) {

//            Linker.renameEvent(this.id, title, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callBack, code);
//                        return;
//                    }
//
//                    EventItem.this.title = title;
//                    EventItem.this.save();
//                    EventItemManager.sharedInstance(familyId).onEventItemChange(id);
//                    call(callBack,code);
//
//                }
//            });
        }

//
//
//


        public void appendImageItem(ArrayList<EventImageItem> eventImageItems, final ReqCallback callback) {

            long clientId = 1;


            final ArrayList dstImageItems = new ArrayList<>(eventImageItems.size());

            for (EventImageItem imageItem : eventImageItems) {

                boolean found = false;
                for (EventImageItem orgImageItem : this.allImages()) {

                    if ((Objects.equals(imageItem.localURL, orgImageItem.localURL)) && (imageItem.uploadUserId == orgImageItem.uploadUserId))
                    {
                        found = true;
                        break;
                    }

                }

                if (!found) {
                    imageItem.clientId = dstImageItems.size() + 1;
                    dstImageItems.add(imageItem);
                }
            }
            if (dstImageItems.size() == 0) {
                call(callback, ErrorCode.Success);
                return;
            }
//            Linker.fillEvent(this.id, dstImageItems, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callback, code);
//                        return;
//                    }
//
//                    if (JSONObject.class.isInstance(obj)) {
//                        JSONObject json = (JSONObject) obj;
//                        JSONArray uploadList = json.optJSONArray("UploadList");
//
//
////                        EventItem newEventItem = null;
////                        try {
////                            newEventItem = (EventItem) EventItem.this.clone();
////                        }
////                        catch (CloneNotSupportedException e) {
////                            e.printStackTrace();
////
////                        }
//                        timestamp = new Timestamp((long)(json.optDouble("Timestamp") * 1000));
//
//                        if (uploadList != null) {
//                            for (int i = 0; i < uploadList.length(); i++) {
//                                JSONObject upload = uploadList.optJSONObject(i);
//                                if (upload == null) {
//                                    continue;
//                                }
//                                long id = upload.optLong("ID");
//                                long clientId = upload.optLong("ClientId");
//                                EventImageItem imageItem = EventImageItem.findListByClientId(dstImageItems, clientId);
//                                if (imageItem != null) {
//                                    imageItem.id = id;
//                                    imageItem.eventItemId = EventItem.this.id;
//                                    imageItem.fromUploadList(upload);
//                                    addImageItem(imageItem);
//                                    EventItemManager.sharedInstance(familyId).addToOneImageMap(imageItem);
//
//                                    refreshSortIndexes();
//
//                                    Stat.logEvent("个人添加照片", UserInfoManager.sharedInstance().statAccount());
//                                    Stat.logEvent("家庭添加照片", String.format("familyId:%d", UserInfoManager.sharedInstance().currFamilyId));
//                                }
//
////                                ArrayList<EventItem> simpleList = new ArrayList<>();
////                                simpleList.add(newEventItem);
//                                EventItemManager.sharedInstance(familyId).onEventItemChange(EventItem.this.id);
//                                UploadPhotoManager.sharedInstance().uploadEventItem(dstImageItems);
//
//                                Stat.logEventWithDurationTime("添加照片", isImport ? "补充导入照片" : "补充事件照片", dstImageItems.size() * 1000);
//                                Stat.logEvent("添加照片数量", isImport ? String.format("补充导入照片 数量:%d", dstImageItems.size()) : String.format("补充事件照片 数量:%d", dstImageItems.size()));
//                                call(callback, code);
//                            }
//                        }
//                    }
//                }
//            });
        }

    public void removeMsgId(long msgId) {
        for (int i = 0; i < this.msgIds.size(); i++) {

            if (msgIds.get(i) == msgId) {
                msgIds.remove(i);
                return;
            }
        }
    }

    public void deleteMsg(final long msgId, final ReqCallback callback) {
//            Linker.deleteEventMessage(msgId, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callback, code);
//                        return;
//                    }
//                    removeMsgId(msgId);
//                    UserMessageManager.sharedInstance.delEventMsg(msgId);
//                    Map<String, Object> userInfo = new HashMap<>();
//                    userInfo.put("msgId", msgId);
//                    userInfo.put("eventItemId", id);
//
//                    NotificationCenter.defaultCenter().postNotification(EventMsgDelKey, EventItem.this, userInfo);
//                    save();
//                    call(callback, code);
//                    Stat.logEvent("删除事件留言", "文字");
//                }
//            });

        }
//
//        func addTagReq(tagItem : TagItem, callback : ReqCallback) {
//        Linker.bindTagToEvent(self.id, familyId: self.familyId, tag: tagItem) { (seqId, code, result) -> Void in
//        if code != ErrorCode.Success.rawValue {
//        logError("addTagReq failed code:\(code)")
//        callback(reason: code)
//        return
//        }
//        if let FilledTag = result as? JSON {
//        let tagItem = TagItem(filledjson: FilledTag)
//
//        if let tagEventTagIds = TagItemManager.sharedInstance(tagItem.familyId).getEventTag(tagItem.eventItemId) {
//        for id in tagEventTagIds {
//        if id == tagItem.id {
//        continue
//        }
//        if let findtagItem = TagItemManager.sharedInstance(tagItem.familyId).getTagItem(id) {
//        if tagItem.growRecordId == findtagItem.growRecordId {
//        TagItemManager.sharedInstance(tagItem.familyId).removeOneTagFromEvent(findtagItem)
//        }
//        }
//
//        }
//        }
//
//        TagItemManager.sharedInstance(tagItem.familyId).updateTagItem(tagItem)
//        }
//        callback(reason: code)
//        EventItemManager.sharedInstance(self.familyId).onEventItemChange(self.id)
//        Stat.logEvent("标签修改", eventLabel: "添加")
//        }
//        }
//
//        func clearTag(callback : ReqCallback) {
//        Linker.clearTag(self.id) { (seqId, code, result) -> Void in
//        if code != ErrorCode.Success.rawValue {
//        logError("clearTag failed code:\(code)")
//        callback(reason: code)
//        return
//        }
//        TagItemManager.sharedInstance(self.familyId).removeEventTag(self.id)
//        callback(reason: code)
//        Stat.logEvent("标签删除", eventLabel: "清空")
//        }
//        }
//        }
//
//
//
//        extension EventItem {
//        //返回eventUpload百分值 100为完成

//        }

};
