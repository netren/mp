package com.gtphoto.mp.event;//

//  EventItemManager.swift
//  ungoo i
//
//  Created by 陈曦行 on 15/4/23.
//  Copyright (c) 2015年 kenny. All rights reserved.
//


import android.util.Log;

import com.gtphoto.mp.DebugConst;
import com.gtphoto.mp.ErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventItemManager {
    public static class EventImagePair {
        public EventImagePair(long imageId, long eventId) {
            super();
            this.imageId = imageId;
            this.eventId = eventId;
        }

        long imageId = 0;
        long eventId = 0;
    }


    static String TAG = "EventItemManager";

    public final static String FavorChangeKey = "onFavorChange";


    static public class EventItemChangeInfo {
        EventItemChangeInfo(ChangeType type ) {
            this.type = type;
        }
        public enum ChangeType {
            ReloadAll,Add,Modify,Del;
        }
        public static class UpdateData {
            UpdateData(int index, long eventId) {
                this.index = index;
                this.eventId = eventId;
            }
            public int index;
            public long eventId;
        }

        public ArrayList<UpdateData> list = new ArrayList<>();
        public ChangeType type = ChangeType.ReloadAll;

        public Map<String, Object> toUserInfo() {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put(ChangeInfoKey, this);
            return userInfo;
        }
    }

    public void call(ReqCallback callback, ErrorCode reason) {
        if (callback != null) {
            callback.handle(reason);
        }
    }
    public interface ReqCallback {
        void handle(ErrorCode reason);
    };
    public interface AddNewItemCallback {
        void handle(ErrorCode reason, long itemId);
    }
    public interface ImportItemCallback {
        void handle(ErrorCode reason, ArrayList<Long> itemIds);
    }

    public interface GetEventItemCallback {
        void handle(ErrorCode reason, EventItem eventItem);
    }

    public interface ImagesCallback {
        void handle(ErrorCode reason, ArrayList<EventImageItem> images);
    }

    final public static String EventItemManagerChangeKey = "onEventItemManagerChange";
    final public static String ImageRemoveKey = "onImageRemove";
    final public static String ChangeInfoKey = "changeInfo";

//    static let sharedInstance = EventItemManager()

    //这个接口用来给uploadmanager删除某个imageid的照片,通常出现在上传中没有了那张相片,或者换了机,之前的照片没有上传完
    //所以这个接口比较效率低,其他情况不要用
    public static void removeImageId( long imageId ) {
        for (Map.Entry<Long,EventItemManager> family: familes.entrySet()) {
            for (EventItem eventItem : family.getValue().eventItemList) {
                final EventImageItem image = eventItem.imageById(imageId);
                if (image != null) {
                    ArrayList<Long> imageIds = new ArrayList<>();
                    imageIds.add(imageId);
                    family.getValue().onDeleteEventPhoto(eventItem.id, imageIds);

                    return;
                }
            }
        }

    }

    static Map<Long, EventItemManager> familes = new HashMap<>();

    long familyId = 0;
    public static EventItemManager sharedInstance( long familyId ) {
        EventItemManager ret = familes.get(familyId);
        if (ret == null) {
            ret = new EventItemManager(familyId);
            familes.put(familyId, ret);
            ret.load();
            ret.sortItem();

        }
        return ret;
    }

    public static void clearAll() {
        familes.clear();
    }
    EventItemManager(long familyId) {

        this.familyId = familyId;
        //NotificationCenter.defaultCenter().addObserver(this. "onNoticeItem:", OnNoticeItemKey, object: null)
    }


    private ArrayList<EventItem> eventItemList = new ArrayList<>();


    Map<Long, ArrayList<Long>> reqEventData = new HashMap<>();
    Map<String, Integer> imageMap = new HashMap<>(); //本地相片索引快速查找

    long lastAddIndex = 1;
    long nextGenerateId = 0;
    long nextImageId = 1;


    public void onEventItemChange( long id ) {
        int index = this.itemIdToIndex(id);
        if (index >= 0) {
            EventItemChangeInfo modifyChange = new EventItemChangeInfo(EventItemChangeInfo.ChangeType.Modify);
            modifyChange.list.add(new EventItemChangeInfo.UpdateData(index, id));
//            NotificationCenter.defaultCenter().postNotification(EventItemManagerChangeKey, this, modifyChange.toUserInfo());


        }
    }


    public void updateEventList( ArrayList<EventItem> simpleList , boolean needDelete) {
        updateEventList(simpleList, needDelete, false);
    }
    public void updateEventList( ArrayList<EventItem> simpleList , boolean needDelete, boolean dontSave) {

        if (needDelete) {
            {


                EventItemChangeInfo deleteChange = new EventItemChangeInfo(EventItemChangeInfo.ChangeType.Del);

                int index = this.eventItemList.size() - 1;
                while (index >= 0) {
                    EventItem ei = this.eventItemList.get(index);
                    boolean found = false;
                    for (EventItem simpleEvent : simpleList) {
                        if (simpleEvent.id == ei.id) {
                            found = true;
                            break;
                        }
                    }
                    if (found == false) {
                        this.eventItemList.remove(index);

                        deleteChange.list.add(new EventItemChangeInfo.UpdateData(index, ei.id));
                        --index;
                    } else {
                        --index;

                    }
                }

                if (deleteChange.list.size() > 0) {


//                    NotificationCenter.defaultCenter().postNotification(EventItemManagerChangeKey, this, deleteChange.toUserInfo());
                    for (EventItemChangeInfo.UpdateData change : deleteChange.list) {

//                        DBEventItemEx.delete(change.eventId);
//                        DBEventImageItemEx.deleteByEventId(change.eventId);
                    }

                }
            }
        }
        Collections.sort(simpleList, eventListCompFunc);

        {
            EventItemChangeInfo modifyChange = new EventItemChangeInfo(EventItemChangeInfo.ChangeType.Modify);

            int simpleIndex = 0;
            while (simpleIndex < simpleList.size()) {
                EventItem simple = simpleList.get(simpleIndex);
                for (int eindex = 0; eindex < this.eventItemList.size(); eindex++) {
                    EventItem event = this.eventItemList.get(eindex);
                    if (event.id == simple.id) {
                        if (event.update(simple)) {
                            modifyChange.list.add(new EventItemChangeInfo.UpdateData(eindex, event.id));
                        }
                        simpleList.remove(simpleIndex);
                        --simpleIndex;
                        break;
                    }
                }

                ++simpleIndex;
            }

            if (modifyChange.list.size() > 0) {
//                NotificationCenter.defaultCenter().postNotification(EventItemManagerChangeKey, this, modifyChange.toUserInfo());
            }


        }

        {
            EventItemChangeInfo addChange = new EventItemChangeInfo(EventItemChangeInfo.ChangeType.Add);
            int simpleIndex = 0;
            while (simpleIndex < simpleList.size()) {
                EventItem simpleEvent = simpleList.get(simpleIndex);
                int index = this.getInsertItemIndex(simpleEvent);
                this.eventItemList.add(index, simpleEvent);
                if (!dontSave) {
                    simpleEvent.save();
                }
                addChange.list.add(new EventItemChangeInfo.UpdateData(index, simpleEvent.id));

                ++simpleIndex;
            }

            if (addChange.list.size()> 0) {

//                NotificationCenter.defaultCenter().postNotification(EventItemManagerChangeKey, this, addChange.toUserInfo());

            }

        }
    }



    public void getSimpleList( final ReqCallback callBack ) {
//        Linker.peekEvents(familyId, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    call(callBack, code);
//                }
//                if (JSONArray.class.isInstance(obj)) {
//                    JSONArray eventList = (JSONArray) obj;
//                    ArrayList<EventItem> simpleList = new ArrayList<>();
//
//                    for (int i = 0; i < eventList.length(); i++) {
//                        JSONObject json = eventList.optJSONObject(i);
//                        if (json == null) {
//                            continue;
//                        }
//                        simpleList.add(new EventItem(json, familyId, false));
//                    }
//                    updateEventList(simpleList, true);
//                    call(callBack, code);
//
//                }
//
//
//
//            }
//        });

    }

    //如果imageId != 0则要检查一下是否有这张图,没有也要从新获取
    public void getEventItemReq(final long eventItemId, long imageId, boolean force, final GetEventItemCallback  callBack) {
        //assert false;
        final EventItem eventItem = this.getItemByItemId(eventItemId);

        if ((force == true || eventItem == null || eventItem.
            hasGetDetail == false || (imageId != 0 && eventItem.imageById(imageId) == null))){
            EventItemManager.sharedInstance(familyId).getDetailEvent(eventItemId, 0, 0, new ReqCallback() {
                @Override
                public void handle(ErrorCode reason) {
                    if (!reason.isSucceeded()) {
                        if (callBack != null) {
                            callBack.handle(reason, null);
                        }
                        return;

                    }
                    EventItem eventItem = getItemByItemId(eventItemId);
                    if (eventItem != null && eventItem.hasGetDetail == true) {
                        if (callBack != null)
                            callBack.handle(ErrorCode.Success, eventItem);

                    }
                }
            });
        }
        else {
            if (callBack != null) {
                callBack.handle(ErrorCode.Success, eventItem);
            }

        }


    }

    public boolean isEventIdIsGetting( long eventItemId ) {
        for (Map.Entry<Long, ArrayList<Long>> entry: this.reqEventData.entrySet()) {
            for (long id : entry.getValue()) {
                if (id == eventItemId) {
                    return true;
                }
            }
        }
        return false;
    }

    //upperBound = 2
    //lowerBound = 0
    public void getDetailEvent(final long eventId, final long upperBound , final long lowerBound , final ReqCallback callBack) {

        if (isEventIdIsGetting(eventId)) {
            return;
        }
//
        final ArrayList<Long> reqList = new ArrayList<>();
        int startIndex = this.itemIdToIndex(eventId);
        if (startIndex > 0) {
            for (long index = startIndex - upperBound; index <=startIndex + lowerBound; ++index){
                if (index < 0) {
                    continue;
                }
                if (index >= this.count()) {
                    break;
                }
                reqList.add(this.getItem((int)index).id);
            }
            boolean allFound = true;
            for (long reqId : reqList) {
                if (isEventIdIsGetting(reqId) == false) {
                    allFound = false;
                }
            }
            if (allFound) {
                return;
            }
            reqEventData.put(eventId, reqList);
        }
//        Linker.listEvents(familyId, eventId, upperBound, lowerBound, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                reqEventData.remove(eventId);
//                if (!code.isSucceeded()) {
//                    Log.e(TAG, String.format("listEvents failed error code:%d eventId:%d, upperBound:%d lowerBound:%d", code.rawValue(), eventId, upperBound, lowerBound));
//                    if (callBack != null)
//                        callBack.handle(code);
//                    return;
//                }
//
//
//                if (!JSONArray.class.isInstance(obj)) {
//                    if (callBack != null)
//                        callBack.handle(ErrorCode.Success);
//                    //这里应该删除照片
////                    delItem()
//                    return;
//                }
//
//                JSONArray detailEventList = (JSONArray) obj;
//
//
//                ArrayList<EventItem> simpleList = new ArrayList<>();
//
//                for (int i = 0; i < detailEventList.length(); i++) {
//                    JSONObject json = detailEventList.optJSONObject(i);
//                    if (json != null) {
//                        EventItem eventItem = new EventItem(json, familyId);
//                        simpleList.add(eventItem);
//                    }
//
//                }
//
//                updateEventList(simpleList, false);
//
//                if (callBack != null)
//                    callBack.handle(code);
//
//            }
//        });
    }

    public ArrayList<EventItem> allItems() {
        return eventItemList;
    }

    public int count() {
        return eventItemList.size();

    }

    public EventItem getItem( int index ) {
        if (index > eventItemList.size()) {
            Log.d(TAG, String.format("error at getItem index=%d count=%d", index, eventItemList.size()));
        }
        return eventItemList.get(index);
    }

    public EventItem getItemByItemId( long id ){
        for (EventItem item : eventItemList) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public int itemIdToIndex( long id ) {
        int index = 0;
        for (EventItem item : eventItemList) {
            if (item.id == id) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    public int getInsertItemIndex( EventItem eventItem ) {
        int index = eventItemList.size()- 1;

        while (index >= 0) {
            int cmp = eventItem.dateEnd.compareTo(eventItemList.get(index).dateEnd);
            if (cmp > 0 || (cmp == 0 && eventItem.id > eventItemList.get(index).id)) {
                --index;
            } else {

                return index + 1;
            }


        }

        return index + 1;
    }

//    public void addNewItem( EventItem eventItem , TagItem tagItem , AddNewItemCallback  callback ) {
//        assert false;
//        eventItem.refreshSortIndexes();
////
////
////        for (i in 0 ..<eventItem.imagesCount()){
////            var imageItem = eventItem.imageByUpload(i)
////            imageItem.clientId = i + 1
////
////            if (eventItem.dateBegin.compare(imageItem.takeDate !)==
////            NSComparisonResult.OrderedDescending){
////                eventItem.dateBegin = imageItem.takeDate !
////            }
////
////            if (eventItem.dateEnd.compare(imageItem.takeDate !)==
////            NSComparisonResult.OrderedAscending){
////                eventItem.dateEnd = imageItem.takeDate !
////            }
////
////        }
////
////
////
////        Linker.createEvent(eventItem, tag:tagItem, callback:{
////            (seqId, code, result) -> Void in
////            if (code != ErrorCode.Success.rawValue) {
////
////                logError("connect failed error code:\(code)")
////                callback(reason:code, itemId:0)
////                return
////            }
////            if (let org.json = result as ? JSON){
////                var ID = org.json["ID"] as ?long??0
////                if (ID == 0) {
////                    return
////                }
////                eventItem.id = ID
////                jsonSetValue( & eventItem.timestamp, obj:org.json["Timestamp"])
////
////                eventItem.hasGetDetail = true
////                var UploadList = org.json["UploadList"] as ? ArrayList < JSON >
////                if ((UploadList != null)) {
////                    for (uploadList in UploadList !){
////                        var id = uploadList["ID"] as ?long??0
////                        var clientId = uploadList["ClientId"] as ?long??0
////
////                        for (i in 0 ..<eventItem.imagesCount()){
////                            var imageItem = eventItem.imageByUpload(i)
////                            if (imageItem.clientId == clientId) {
////                                imageItem.id = id
////                                imageItem.localURL = uploadList["LocalPath"] as ? String
////                                imageItem.filePath = uploadList["FilePath"] as ? String
////                                imageItem.thumbnailPath = uploadList["ThumbnailPath"] as ? String
////                                imageItem.packageId = uploadList["PackageId"] as ?long??0
////                                Stat.logEvent("个人添加照片", eventLabel:
////                                UserInfoManager.sharedInstance().statAccount)
////                                Stat.logEvent("家庭添加照片", eventLabel:
////                                "familyId:\(UserInfoManager.sharedInstance().currFamilyId)")
////
////                                break
////                            }
////                        }
////                    }
////                    eventItem.refreshSortIndexes()
////                    var images = ArrayList < EventImageItem > ()
////                    for (i in 0 ..<eventItem.imagesCount()){
////                        images.add(eventItem.imageByUpload(i))
////
////                    }
////                    var elst = ArrayList < EventItem > ()
////                    elst.add(eventItem)
////                    this.updateEventList( & elst, needDelete:false)
////                    UploadPhotoManager.sharedInstance.uploadEventItem(images, callback:{
////                        (reason) -> Void in
////
////                    })
////                    if (let FilledTag = org.json["FilledTag"] as ? JSON){
////                        var tagItem = TagItem(filledjson:FilledTag)
////
////                        TagItemManager.sharedInstance(tagItem.familyId).updateTagItem(tagItem)
////                    }
////
////                    Stat.logEventWithDurationTime("添加照片", eventLabel:"创建新事件", durationTime:
////                    Ulong(images.size()* 1000))
////                    Stat.logEvent("添加照片数量", eventLabel:"创建新事件 数量:\(images.size())")
////                    callback(reason:code, itemId:ID)
////
////                }
////            }
////
////
////            logDebug("seqId:\(seqId) code:\(code) \(result)")
////        })
//
//
//    }

    public void importEvent( final ArrayList<EventImageItem> eventImageItems, final ImportItemCallback  callback ) {
        //assert false;
        long clientId = 1;

        for (EventImageItem image : eventImageItems) {
            image.clientId = clientId++;
        }

//        Linker.importEvent(this.familyId, eventImageItems, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    if (callback != null) callback.handle(code, null);
//                    return;
//                }
//
//                ArrayList<EventImageItem> uploadImages = new ArrayList<>();
//                ArrayList<EventItem> simpleList = new ArrayList<>();
//                ArrayList<Long> ids = new ArrayList<>();
//                if (JSONArray.class.isInstance(obj)) {
//
//
//                    JSONArray eventArray = (JSONArray) obj;
//                    for (int i = 0; i < eventArray.length(); i++) {
//                        JSONObject json = eventArray.optJSONObject(i);
//
//                        EventItem eventItem = new EventItem(json, familyId, false);
//
//
//                        JSONArray UploadList = json.optJSONArray("Images");
//                        if (UploadList != null) {
//                            for (int j = 0; j < UploadList.length(); j++) {
//                                JSONObject upload = UploadList.optJSONObject(j);
//                                if (upload == null) {
//                                    continue;
//                                }
//                                long id = upload.optLong("ID");
//                                long clientId = upload.optLong("ClientId");
//
//
//                                EventImageItem imageItem = EventImageItem.findListByClientId(eventImageItems, clientId);
//                                if (imageItem != null) {
//                                    imageItem.id = id;
//                                    imageItem.eventItemId = eventItem.id;
//                                    imageItem.fromUploadList(upload);
//                                    eventItem.addImageItem(imageItem);
//                                    uploadImages.add(imageItem);
//                                    ids.add(eventItem.id);
//                                    Stat.logEvent("个人添加照片", UserInfoManager.sharedInstance().statAccount());
//                                    Stat.logEvent("家庭添加照片", String.format("familyId:%d", UserInfoManager.sharedInstance().currFamilyId));
//                                }
//
//                            }
//                        }
//                        checkUpdateForOldEventItem(eventItem);
//
//                        eventItem.refreshSortIndexes();
//
//                        simpleList.add(eventItem);
//
//                    }
//                }
//
//                updateEventList(simpleList, false);
//                Collections.sort(uploadImages, new Comparator<EventImageItem>() {
//                    @Override
//                    public int compare(EventImageItem lhs, EventImageItem rhs) {
//                        if (lhs.eventItemId == rhs.eventItemId) {
//                            int compareTo = lhs.takeDate.compareTo(rhs.takeDate);
//                            if (compareTo != 0) {
//                                return compareTo;
//                            }
//                            else {
//                                return lhs.id < rhs.id ? -1 : 1;
//                            }
//                        }
//                        else {
//                            int compareTo = lhs.takeDate.compareTo(rhs.takeDate);
//                            if (compareTo != 0) {
//                                return -compareTo;
//                            }
//                            else {
//                                return lhs.id < rhs.id ? -1 : 1;
//                            }
//                        }
//                    }
//                });
//                UploadPhotoManager.sharedInstance().uploadEventItem(uploadImages);
//                Stat.logEventWithDurationTime("添加照片", "导入照片", (uploadImages.size() * 1000));
//                Stat.logEvent("添加照片数量", String.format("导入照片 数量:%d", uploadImages.size()));
//                if (callback != null)
//                    callback.handle(code, ids);
//
//            }
//        });
    }

    //这里是把新的eventItem加入旧的相片,减少重新访问网络的机会
    public void checkUpdateForOldEventItem(EventItem newEventItem) {
        EventItem oldEventItem = this.getItemByItemId(newEventItem.id);

        if (oldEventItem == null) {
            newEventItem.hasGetDetail = true;
            return;
        }
        if (oldEventItem.hasGetDetail == false) {
            newEventItem.hasGetDetail = false;
            return;
        }


        newEventItem.title = oldEventItem.title;
        newEventItem.msgIds = oldEventItem.msgIds;
        for (int i = 0; i < oldEventItem.imagesCount(); i++) {
            newEventItem.addImageItem(oldEventItem.imageByTake(i));
        }
        newEventItem.hasGetDetail = true;


    }

    public void uploadFullPhoto( EventItem eventItem , long  index , long  tryNum ) {
        assert false;
//        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
//                let imageItem = eventItem.imageByUpload(index)
//
//                let url = NSURL(string:imageItem.localURL !)
//
//        PhotoAssetManager.sharedInstance.loadAssetFromUrl(url !, loadTag:imageItem.id){
//            (asset, tag) -> Void in
//            let image = UIImage(CGImage:asset !.
//            defaultRepresentation().fullScreenImage().takeUnretainedValue())
//
//            let data = ImageManager.sharedInstance.saveImageToData(image, maxWidth:
//            MaxPhotoWidth, minHeight:MinPhotoHeight, quality:1)
//            dispatch_async(dispatch_get_main_queue(), {
//                    Linker.uploadFileData(data, imageId:imageItem.id, callback:{
//                (seqId, code, result) -> Void in
//                if (code != ErrorCode.Success.rawValue) {
//                    logError("uploadThumbnailData error code:\(code)")
//                    _ = tryNum + 1
//                    return
//                }
//                let nextIndex = index
//                if (nextIndex == eventItem.imagesCount()) {
//                    this.uploadFullPhoto(eventItem, index:0, tryNum:0)
//                } else {
//                    this.uploadFullPhoto(eventItem, index:nextIndex, tryNum:0)
//
//                }
//            })
//            })
//
//        }
//
//
//        })
    }


    public void uploadDone( EventItem eventItem ) {
        assert false;
//        dispatch_async(dispatch_get_main_queue(), {
//                logDebug("upload eventItem done : id=\(eventItem.id)")
//        })
    }
    public void uploadThumbnail( EventItem eventItem , long  index , long  tryNum ) {

//        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
//            var imageItem = eventItem.imageByUpload(index)
//
//            var url = NSURL(string:imageItem.localURL!)
//
//            PhotoAssetManager.sharedInstance.loadAssetFromUrl(url!, loadTag: imageItem.id) { (asset, tag) -> Void in
//                var image = UIImage(CGImage: asset!.defaultRepresentation().fullScreenImage().takeUnretainedValue())
//
//                var data = ImageManager.sharedInstance.saveImageToData(image, maxWidth: MaxThumbWidth, minHeight:MinThumbHeight,quality: 1)
//                dispatch_async(dispatch_get_main_queue(), {
//                    Linker.uploadThumbnailData(data, imageId: imageItem.id, callback: { (seqId, code, result) -> Void in
//                        if (code != ErrorCode.Success.rawValue) {
//                            logError("uploadThumbnailData error code:\(code)")
//                            var nextTry = tryNum + 1
//                            return
//                        }
//                        var nextIndex = index + 1
//                        if (nextIndex == eventItem.imagesCount()) {
//                            this.uploadFullPhoto(eventItem, index: 0, tryNum: 0)
//                        }
//                        else {
//                            this.uploadThumbnail(eventItem, index : nextIndex, tryNum: 0 )
//
//                        }
//
//                    })
//                })
//
//
//            }
//
//        })

    }
//
//    public void appendImagesItem( long itemId , ArrayList<EventImageItem>  imageItems ) {
//        ArrayList<EventImageItem> dstImageItems = new ArrayList<EventImageItem>(imageItems.size());
//
//
//        let eventItem = this.getItemByItemId(itemId);
//        if ((eventItem != null)) {
//            int index = 0;
//            for (EventImageItem imageItem : imageItems) {
//                boolean found = false;
//
//                for (EventImageItem orgImageItem : eventItem.allImages()){
//                    if (imageItem.localURL == orgImageItem.localURL && imageItem.uploadUserId == orgImageItem.uploadUserId) {
//
//                        found = true;
//                        break;
//                    }
//
//                }
//
//                if (!found) {
//                    dstImageItems.add(imageItem);
//                }
//
//            }
//        }
//
//
//        for (EventImageItem img : dstImageItems) {
//            img.id = .nextImageId++
//
//
//            eventItem !.addImageItem(img)
//            addToOneImageMap(img)
//        }
////        eventItem!.images.sort(EventItemManager.sortImageFuncByTake)
////        for img in tmps) {
////            eventItem!.images.add(img)
////        }
//
//        eventItem !.refreshSortIndexes()
//    }


    public void delEventItemReq( final long itemId , final ReqCallback  callback ) {
//        Linker.deleteEvent(itemId, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    call(callback, code);
//                    return;
//                }
//                Stat.logEvent("删除事件", "tag");
//
//                delItem(itemId);
//                call(callback, code);
//            }
//        });

    }
    public boolean delItem( long itemId ) {
        int index = 0;
        for (EventItem item : eventItemList) {
            if (item.id == itemId) {
                return delItemFromIndex(index);
            }
            ++index;
        }
        return false;
    }


    private boolean delItemFromIndex( int index ) {
        if (index < eventItemList.size()) {

            ArrayList<Long> ids = new ArrayList<>();
            for (int i = 0; i < eventItemList.get(index).imagesCount(); i++) {
                EventImageItem image = eventItemList.get(index).imageByUpload(i);
                ids.add(image.id);
            }
            EventItem eventItem = eventItemList.get(index);
  //          assert false;
//            if (var tags = TagItemManager.sharedInstance(this.familyId).getEventTag(eventItem.id)){
//                TagItemManager.sharedInstance(this.familyId).removeEventTag(eventItem.id)
//            }


            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("imageIds", ids);
//            NotificationCenter.defaultCenter().postNotification(ImageRemoveKey, this, userInfo);


            eventItemList.remove(index);
            EventItemChangeInfo deleteChange = new EventItemChangeInfo(EventItemChangeInfo.ChangeType.Del);
            deleteChange.list.add(new EventItemChangeInfo.UpdateData(index, eventItem.id));
//            NotificationCenter.defaultCenter().postNotification(EventItemManagerChangeKey, this, deleteChange.toUserInfo());

            return true;
        }
        return false;
    }

    static Comparator<EventItem> eventListCompFunc = new Comparator<EventItem>() {
        @Override
        public int compare(EventItem lhs, EventItem rhs) {
            int ret = lhs.dateEnd.compareTo(rhs.dateEnd);
            if (ret == 0) {
                return (rhs.id - lhs.id) < 0 ? -1 : 1;
            }
            else {
                return -ret;
            }
        }
    };
    private void sortItem() {
        Collections.sort(eventItemList, eventListCompFunc);
    }


    public void refreshImageMap() {
        imageMap.clear();

        for (EventItem eventItem : eventItemList) {
            this.addToImageMap(eventItem);
        }
    }

    public void addToOneImageMap( EventImageItem imageItem ) {

//        long selfUserId = UserInfoManager.sharedInstance().selfUserId();
//
//
//
//        if (imageItem.localURL != null && imageItem.uploadUserId == selfUserId) {
////            String key = String.format("%s-%ld", imageItem.localURL, imageItem.uploadUserId);
//
//            this.addImageMap(imageItem.localURL);
//        }

    }

    public void addToImageMap( EventItem eventItem ) {
        for (EventImageItem imageItem : eventItem.allImages()) {
            addToOneImageMap(imageItem);
        }
    }

    public boolean isImageExist( String key) {

        return imageMap.containsKey(key);
    }

    public void addImageMap( String key ) {
        if (imageMap.containsKey(key)) {
            imageMap.get(key);
        }
        else {
            imageMap.put(key, 1);
        }
    }

    public void delImageMap( String key ) {
        if (imageMap.containsKey(key)) {
            Integer integer = imageMap.get(key);
            --integer;
            if (integer == 0) {
                imageMap.remove(key);
            }
        }
    }

    public void load() {
        if (DebugConst.startLoadEventOnLine) {
            return;
        }
//        ArrayList<EventItem> eventItems = DBEventItemEx.findAll(this.familyId);
//        if (eventItems.size() > 0) {
//            this.updateEventList(eventItems, true, true);
//        }

//        NotificationCenter.defaultCenter().addObserver(NotificationProtocol.OnNoticeItemKey, this, "onNoticeItem");
    }


//    public void onNoticeItem(NoticeItemImpl.NoticeCreateEvent createEvent) {
//        this.getEventItemReq(createEvent.id, 0, true, null);
//
//    }
//
//    public void onNoticeItem(NoticeItemImpl.NoticeAppendPhoto appendPhoto) {
//        this.getEventItemReq(appendPhoto.id, 0, true, null);
//    }
//
//    public void onNoticeItem(NoticeItemImpl.NoticeImportPhoto importPhoto) {
//        Set<Long> eventSet = new HashSet();
//        for (EventItemManager.EventImagePair pair : importPhoto.eventItemImages) {
//            eventSet.add(pair.eventId);
//        }
//
//
//        if (eventSet.size() <= 2) {
//            for (Long eventId : eventSet) {
//                EventItem eventItem = this.getItemByItemId(eventId);
//                this.getEventItemReq(eventId, 0, true, null);
//            }
//        }
//        else {
//            getSimpleList(null);
//        }
//
//
//    }

    public boolean isEventNotGetDetail(long eventItemId) {
        EventItem event = this.getItemByItemId(eventItemId);
        if (event != null && event.hasGetDetail == false) {
            return true;
        }
        return false;
    }
//    public void onNoticeItem(final NoticeItemImpl.NoticeLeaveMessage messgageItem) {
//        if (messgageItem.imageId == 0) {
//            if (isEventNotGetDetail(messgageItem.eventItemId)) {
//                return;
//            }
//            getEventMsg(messgageItem.eventItemId, true, null);
//        }
//        else {
//            if (isEventNotGetDetail(messgageItem.eventItemId)) {
//                return;
//            }
//
//
//            getEventItemReq(messgageItem.eventItemId, messgageItem.imageId, false, new GetEventItemCallback() {
//                @Override
//                public void handle(ErrorCode reason, EventItem eventItem) {
//                    if (!reason.isSucceeded()) {
//                        return;
//                    }
//                    EventImageItem image = eventItem.imageById(messgageItem.imageId);
//                    if (image != null) {
//                        image.getImageMsg(null);
//                    }
//                }
//            });
//
//        }
//    }
//    public void onNoticeItem(final NoticeItemImpl.NoticeFavorPhoto favorPhoto) {
//        if (isEventNotGetDetail(favorPhoto.eventItemId)) {
//            return;
//        }
//
//        getEventItemReq(favorPhoto.eventItemId, favorPhoto.imageId, true, new GetEventItemCallback() {
//            @Override
//            public void handle(ErrorCode reason, EventItem eventItem) {
////                eventItem.imageById(favorPhoto.id)
//            }
//        });
//    }
//
//    public void onNoticeItem(final NoticeItemImpl.NoticeDeleteEvent deleteEvent) {
//        this.delItem(deleteEvent.eventItemId);
//    }
//
//    public void onNoticeItem(final NoticeItemImpl.NoticeDeletePhoto deletePhoto) {
//        if (isEventNotGetDetail(deletePhoto.eventItemId)) {
//            return;
//        }
//        getEventItemReq(deletePhoto.eventItemId, 0, true, null);
//
//    }
//
//    public void onNoticeItem(Notification notification) {
//        NoticeItem noticeItem = (NoticeItem) notification.getObject();
//        if (noticeItem == null || noticeItem.familyId != this.familyId) {
//            return;
//        }
//        try {
//            Method onNoticeItemMethod = this.getClass().getMethod("onNoticeItem", noticeItem.getClass());
//            if (onNoticeItemMethod != null) {
//                onNoticeItemMethod.invoke(this, noticeItem);
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
////
//
//
//
//    }
    public void onDeleteEventPhoto(long eventItemId , ArrayList<Long>  ids ) {

        EventItem item = this.getItemByItemId(eventItemId);
        if (item != null) {
            for (Long id : ids) {
                item.removeImage(id);
            }

        }



        if (item != null) {
            item.refreshSortIndexes();
            item.save();
        }

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("imageIds", ids);


//        NotificationCenter.defaultCenter().postNotification(ImageRemoveKey, null, userInfo);

        EventItemChangeInfo modifyChange = new EventItemChangeInfo(EventItemChangeInfo.ChangeType.Modify);


        modifyChange.list.add(new EventItemChangeInfo.UpdateData(itemIdToIndex(eventItemId), eventItemId));
        if (modifyChange.list.size() > 0) {
//            NotificationCenter.defaultCenter().postNotification(EventItemManagerChangeKey, this, modifyChange.toUserInfo());
        }

    }

//    public void deleteEventPhoto(final long eventItemId , final ArrayList<Long>  ids , final ReqCallback  callback ) {
//        Linker.deleteImage(ids, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    call(callback, code);
//                    return;
//                }
//
//                onDeleteEventPhoto(eventItemId, ids);
//                Stat.logEvent("删除图片", "tag");
//                call(callback, code);
//                onEventItemChange(eventItemId);
//            }
//        });
//
//    }

    public boolean favorImage(long eventItemId , long  imageId , boolean  value ) {

        EventItem item = getItemByItemId(eventItemId);
        if (item != null) {
            EventImageItem image = item.imageById(imageId);
            if (image != null) {
                image.favor = value;

                return true;
            }

        }
        return false;
    }

    ArrayList<EventImageItem> favorImages = new ArrayList<>();

    public void unfavorImages( ArrayList<Long> ids ) {
        for (long id : ids) {
            int index = 0;
            for (EventImageItem image : favorImages) {
                if (image.id == id) {
                    favorImages.remove(index);
                }
                ++index;
            }
        }


//        NotificationCenter.defaultCenter().postNotification(FavorChangeKey, null, null);
    }

    public void favorImages( ArrayList<EventImageItem> images ) {
        for (EventImageItem image : images) {
            boolean has = false;
            for (EventImageItem favor : favorImages) {
                if (favor.id == image.id) {
                    has = true;
                    break;
                }

            }
            if (!has) {
                favorImages.add(image);
            }

        }
//        NotificationCenter.defaultCenter().postNotification(FavorChangeKey, null, null);

    }


    public EventImageItem findImage( long imageId , long  eventId ) {
        if (eventId > 0) {
            EventItem eventItem = this.getItemByItemId(eventId);
            if (eventItem != null) {
                return eventItem.imageById(imageId);
            } else {
                return null;
            }

        }
        else {
            for (EventItem eventItem : this.eventItemList) {
                EventImageItem image = eventItem.imageById(imageId);
                if (image != null) {
                    return image;
                }
            }
        }
        return null;
    }

    
    public void getImageList( final List<EventImagePair> imageIds , final ImagesCallback  callback ) {
        final ArrayList<EventImageItem> images = new ArrayList<>();
        ArrayList<Long> getIds = new ArrayList<>(imageIds.size());

        for (EventImagePair eventImagePair : imageIds) {
            EventImageItem image = findImage(eventImagePair.imageId, eventImagePair.eventId);

            if (image != null) {
                images.add(image);
            } else {
                getIds.add(eventImagePair.imageId);
            }

        }

        if (getIds.size() == 0) {
            if (callback != null) {
                callback.handle(ErrorCode.Success, images);
                return;
            }
        }
//        Linker.listEventImages(getIds, familyId, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    if (callback != null) {
//                        if (images.size() > 0) {
//                            callback.handle(ErrorCode.Success, images);
//                        }
//                        else {
//                            callback.handle(code, null);
//                        }
//                    }
//                    return;
//                }
//                if (JSONArray.class.isInstance(obj)) {
//                    JSONArray jsonList = (JSONArray) obj;
//                    for (int i = 0; i < jsonList.length(); i++) {
//                        images.add(new EventImageItem(jsonList.optJSONObject(i), 0, familyId));
//                    }
//
//                }
//                if (callback != null) {
//                    callback.handle(ErrorCode.Success, images);
//
//                }
//            }
//        });
    }

    public void getFavorImage( boolean forceUpdate , final ImagesCallback  callback ) {

        if (forceUpdate == false && favorImages.size() > 0) {
            for (int i = 0; i < favorImages.size(); i++) {
                EventImageItem image = favorImages.get(i);
                EventImageItem findImage = findImage(image.id, image.eventItemId);
                if (findImage != null) {
                    favorImages.set(i, findImage);
                }
            }

            if (callback != null) {
                callback.handle(ErrorCode.Success, favorImages);
            }


            return;
        }
//        Linker.favourites(familyId, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (ErrorCodeUtil.checkShowError(code)) {
//                    if (callback != null) {
//                        callback.handle(code, null);
//                    }
//                    return;
//                }
//                if (obj instanceof JSONArray) {
//                    JSONArray jsonlist = (JSONArray) obj;
//                    favorImages.clear();
//                    for (int i = 0; i < jsonlist.length(); i++) {
//                        JSONObject json = jsonlist.optJSONObject(i);
//                        favorImages.add(new EventImageItem(json, 0, familyId));
//
//                    }
//                    for (int i = 0; i < favorImages.size(); i++) {
//                        EventImageItem image = favorImages.get(i);
//                        EventImageItem findImage = findImage(image.id, image.eventItemId);
//                        if (findImage != null) {
//                            findImage.favor = true;
//                            favorImages.set(i, findImage);
//                            findImage.save();
//
//                        }
//                    }
//                    if (callback != null) {
//                        callback.handle(ErrorCode.Success, favorImages);
//
//                    }
//                }
//            }
//        });


    }

    public boolean addEventTag( long eventId , long  tagId ) {
        return false;
//        var eventItem = this.getItemByItemId(eventId)
//        if (eventItem != null) {
//            for (fortagId in eventItem!.tagIds) {
//                if (fortagId == tagId) {
//                    logDebug("has tagId: \(tagId)" )
//                    return true
//                }
//            }
//
//            if (TagItemManager.sharedInstance(this.familyId).addEventTag(eventId, tagId: tagId)) {
//            	eventItem!.tagIds.add(tagId)
//            }
//            else {
//                return false
//            }
//
//        	return true
//        }
//        else {
//            logDebug("no eventId: \(eventId)" )
//            return false
//        }
    }

    public boolean removeEventTag( long eventId ) {
        ///has remove
        return false;
    }

    public void getEventMsg( long eventId , boolean force , final ReqCallback  callback ) {


        boolean needGet = force;
        final EventItem event = this.getItemByItemId(eventId);
        if (event == null || event.hasGetDetail == false) {
            call(callback, ErrorCode.Failure);
            return;
        }


//        for (long msgId : event.msgIds) {
//            EventMessageItem msgItem = UserMessageManager.sharedInstance.getEventMsg(msgId);
//            if (msgItem == null) {
//                needGet = true;
//                break;
//            }
//        }
//        if (needGet == false) {
//            call(callback, ErrorCode.Success);
//            return;
//        }
//        else {
//            Linker.listEventMessage(eventId, new HttpAsyncCallback() {
//                @Override
//                public void completed(ErrorCode code, Object obj) {
//                    if (!code.isSucceeded()) {
//                        call(callback, code);
//                        return;
//                    }
//                    if (JSONArray.class.isInstance(obj)) {
//                        JSONArray msgList = (JSONArray) obj;
//                        ArrayList<Long> msgIds = new ArrayList<>();
//                        for (int i = 0; i < msgList.length(); i++) {
//                            JSONObject msgJson = msgList.optJSONObject(i);
//                            EventMessageItem messageItem = new EventMessageItem(msgJson);
//                            UserMessageManager.sharedInstance.addEventMsg(messageItem);
//
//                            msgIds.add(messageItem.id);
//                        }
//
//                        if (event != null) {
//                            event.msgIds = msgIds;
//                        }
//                    }
//                    call(callback, code);
//                }
//            });
//        }
    }

//    public void addEventMessage( String inputText , final long  eventId , final ReqCallback  callback ) {
//        Linker.leaveEventMessage(inputText, eventId, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (!code.isSucceeded()) {
//                    call(callback, code);
//                    return;
//                }
//
//                EventMessageItem messageItem = new EventMessageItem((JSONObject) obj);
//
//                UserMessageManager.sharedInstance.addEventMsg(messageItem);
//
//                EventItem event = getItemByItemId(eventId);
//                if (event != null) {
//                    event.msgIds.add(messageItem.id);
//                }
//
//                call(callback, code);
//                if (event != null) {
//                    Map<String, Object> userInfo = new HashMap<>();
//                    userInfo.put("msgId", messageItem.id);
//                    userInfo.put("eventItemId", eventId);
//                    userInfo.put("selfAdd", true);
//
//                    NotificationCenter.defaultCenter().postNotification(EventItem.EventMsgAddlKey, event, userInfo);
//
//
//                    event.save();
//                }
//                Stat.logEvent("添加事件留言", "文字");
//            }
//        });
//
//    }

//    public void onNoticeItem( NSNotification aNotification ) {
//        assert false;

//        if (let noticeItem = aNotification.userInfo![NoticeItemKey] as? NoticeItem) {
//            if (let createItem = noticeItem as? NoticeCreateEvent) {
//                this.getEventItemReq(createItem.eventItemId, imageId: 0, callBack: { (reason, eventItem) -> Void in
//
//                })
//                return
//            }
//
//            if (let createItem = noticeItem as? NoticeAppendPhoto) {
//                this.getEventItemReq(createItem.eventItemId, imageId: 0, force: true, callBack: { (reason, eventItem) -> Void in
//
//                })
//                return
//            }
//
//            if (let importItem = noticeItem as? NoticeImportPhoto) {
//                this.getSimpleList({ (reason) -> Void in
//
//                })
//                return
//            }
//
//            if (let messgageItem = noticeItem as? NoticeLeaveMessage) {
//                if (messgageItem.imageId == 0) {
//	                this.getEventMsg(messgageItem.eventItemId, force: true, callback: { (reason) -> Void in
//
//                    })
//                }
//                else {
//                    this.getEventItemReq(messgageItem.eventItemId, imageId: messgageItem.imageId, callBack: { (reason, eventItem) -> Void in
//                        if (reason != ErrorCode.Success.rawValue) {
//                        	return
//                        }
//                        if (let image = eventItem!.imageById(messgageItem.imageId)) {
//                            image.getImageMsg({ (reason) -> Void in
//
//                            })
//                        }
//
//                    })
//                }
//                return
//            }
//
//            if (let favorItem = noticeItem as? NoticeFavorPhoto) {
//                this.getEventItemReq(favorItem.eventItemId, imageId: favorItem.imageId, callBack: { (reason, eventItem) -> Void in
//
//                })
//                return
//            }
//
//            if (let deleteEventItem = noticeItem as? NoticeDeleteEvent) {
//                let index = this.itemIdToIndex(deleteEventItem.eventItemId)
//                if (index >= 0) {
//                	this.eventItemList.remove(index)
//                    let deleteChange = EventItemChangeInfo(type : .Del)
//                    deleteChange.list.add(EventItemChangeInfo.UpdateData(index : index, eventId : deleteEventItem.eventItemId))
//                    NSNotificationCenter.defaultCenter().postNotificationName(EventItemManagerChangeKey, object: this. userInfo: deleteChange.toUserInfo())
//                    for (change in deleteChange.list) {
//                        DBEventItem.delete(eventId : change.eventId)
//                        DBEventImageItem.deleteItemByEventId(change.eventId)
//                    }
//                }
//
//
//
//                return
//            }
//            if (let deletePhotoItem = noticeItem as? NoticeDeletePhoto) {
//
//                this.getEventItemReq(deletePhotoItem.eventItemId, imageId: 0, force: true, callBack: { (reason, eventItem) -> Void in
//
//                })
//                return
//
//
//            }
//        }

//    }

    public void setImageFavors( final ArrayList<EventImageItem> images , final ReqCallback  callback ) {
        ArrayList<Long> ids = new ArrayList<>();
        for (EventImageItem image : images) {
            ids.add(image.id);
        }

//        Linker.favorImage(ids, new HttpAsyncCallback() {
//            @Override
//            public void completed(ErrorCode code, Object obj) {
//                if (ErrorCodeUtil.checkShowError(code)) {
//                    return;
//                }
//                for (EventImageItem image : images) {
//                    image.favor = true;
//                }
//                favorImages(images);
//                call(callback, code);
//            }
//        });

    }


};

