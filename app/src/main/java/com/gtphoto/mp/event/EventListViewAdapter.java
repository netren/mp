package com.gtphoto.mp.event;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gtphoto.mp.DebugConst;
import com.gtphoto.mp.R;
import com.gtphoto.widget.common.URange;
import com.gtphoto.widget.common.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by kennymac on 15/9/24.
 */

public class EventListViewAdapter extends RecyclerView.Adapter<TimelineViewHolder>  {


    static int rangeInterval(URange<Integer> range) {
        return range.getUpper() - range.getLower();
    }
    HashSet<Long> eventsExpandSet = new HashSet<>();
    LayoutGenerate layoutGenerate = new LayoutGenerate();
    TimelineViewHolder.ViewHolderListener viewHolderListener;



    public void setViewHolderListener(TimelineViewHolder.ViewHolderListener viewHolderListener) {
        this.viewHolderListener = viewHolderListener;
    }

    private static final String TAG = "LogTag";


    public boolean isExpandMsg(long eventItemId) {
        return eventsExpandSet.contains(eventItemId);
    }

    private EventItemManager eventItemManager;



    public EventListViewAdapter(EventItemManager manager) {

        this.eventItemManager = manager;


    }

    @Override
    public void onViewRecycled(TimelineViewHolder holder) {
        holder.onRecycle();
        //super.onViewRecycled(holder);
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(type, viewGroup, false);

        return TimelineViewHolder.createViewHolder(v, type);
//        return v;
//        if (type == 0) {
//
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_view_title, viewGroup, false);
//            return new EventListViewHolder.EventTitleBar(v);
//        } else if (type == 1) {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_image_group_1, viewGroup, false);
//            return new EventListViewHolder.EventImageGroup(v, 1);
//        } else if (type == 2) {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_image_group_2, viewGroup, false);
//            return new EventListViewHolder.EventImageGroup(v, 2);
//        } else if (type == 3) {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_image_group_3_1, viewGroup, false);
//            return new EventListViewHolder.EventImageGroup(v, 3);
//        } else {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_view_bottom, viewGroup, false);
//            return new EventListViewHolder.EventTitleBar(v);
//        }

    }

    @Override
    public int getItemViewType(int position) {
        return this.timelineItems.get(position).layoutId();

    }

    @Override
    public long getItemId(int position) {
        Timeline.ForEventItem timelineEventItem = (Timeline.ForEventItem) this.timelineItems.get(position);
        if (timelineEventItem != null) {
            return timelineEventItem.eventItem.id;
        }
        return 0;

    }

    @Override
    public void onViewAttachedToWindow(TimelineViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }



    @Override
    public void onBindViewHolder(TimelineViewHolder viewHolder, int i) {


        viewHolder.showCell(timelineItems.get(i), viewHolderListener);
    }



    @Override
    public int getItemCount() {
        return this.timelineItems.size();
    }

    static class Timeline {
        static public abstract class Item {
            abstract int layoutId();


            //int index = 0;//数组索引

        }
        static public class ForHeader extends Item {
            @Override
            int layoutId() {
                return R.layout.event_view_top;
            }
        }
        //EventItemTitle
        static public abstract class ForEventItem extends Item {
            EventItem eventItem; //事件id
            ForEventItem(EventItem eventItem) {
                this.eventItem = eventItem;
            }
        }


        static public class ForEventTitle extends ForEventItem {
            ForEventTitle(EventItem eventItem) {
                super(eventItem);
            }

            boolean isModifyTitle = false;


            @Override
            int layoutId() {
                return R.layout.event_view_title;
            }

            String getTitle() {
                return eventItem.getShowTitle();
            }
            String getDate() {
                return DateUtil.getShowDateString(eventItem.dateEnd);
            }

        }

        static public class ForEventBottom extends ForEventItem {
            ForEventBottom(EventItem eventItem) {
                super(eventItem);
            }

            boolean isExpand = false;

            @Override
            int layoutId() {
                return R.layout.event_view_bottom;
            }
        }
        static public class ForEventTail extends ForEventItem {
            ForEventTail(EventItem eventItem) {
                super(eventItem);
            }

            @Override
            int layoutId() {
                return R.layout.event_view_tail;
            }
        }

        static public class ForEventImage extends ForEventItem {
            ForEventImage(EventItem eventItem) {
                super(eventItem);
            }

            int imageStartIndex;
            int imageCount;
            int layoutId;
            @Override
            int layoutId() {
                return layoutId;
            }
        }

        static public class ForMessage extends ForEventItem {
            ForMessage(EventItem eventItem, long msgId) {
                super(eventItem);
                this.msgId = msgId;

            }

            long msgId;

            @Override
            int layoutId() {
                return R.layout.event_view_message;
            }
        }

        static public class ForMessageInput extends ForEventItem {
            ForMessageInput(EventItem eventItem) {
                super(eventItem);
            }


            @Override
            int layoutId() {
                return R.layout.event_view_message_input;
            }
        }


    }




    ArrayList<Timeline.Item> timelineItems = new ArrayList<>();
    //HashMap<Long, Integer> eventId2Index = new HashMap<Long, Integer>();


    public void reloadData() {
        int index = 0;


        timelineItems.ensureCapacity(eventItemManager.count() * 4);

        timelineItems.clear();
        Timeline.Item item = new Timeline.ForHeader();

        addTimelineItem(0 , item);
        for (EventItem ei : eventItemManager.allItems()) {
            insertTimeEventItem(timelineItems.size(), ei, false, false);
        }
        refreshAllTimelineItem(0);
        this.notifyDataSetChanged();
    }

    //retrun numOfLineInsert
    public URange<Integer> insertTimeEventItem(int insertIndex, EventItem ei, boolean isNotifyChange, boolean isRefreshAll) {
        int numOfInsert = 0;
        {
            int imageCount = ei.imagesCount();
            if (ei.hasGetDetail && ei.imagesCount() == 0) {
                return null;
            }
            Timeline.ForEventItem titleItem = new Timeline.ForEventTitle(ei);

            addTimelineItem(insertIndex + numOfInsert++, titleItem);


            List<LayoutGenerate.ImageLayoutType> layoutTypes = this.layoutGenerate.generateImageLayout(ei);
            int curImageIndex = 0;
            for (LayoutGenerate.ImageLayoutType layoutType : layoutTypes) {
                Timeline.ForEventImage timelineImageItem = new Timeline.ForEventImage(ei);
                timelineImageItem.imageStartIndex = curImageIndex;
                timelineImageItem.imageCount = layoutType.num;
                timelineImageItem.layoutId = layoutType.resId;

                curImageIndex += layoutType.num;
                addTimelineItem(insertIndex + numOfInsert++, timelineImageItem);

            }
            boolean isExpandMsg = this.eventsExpandSet.contains(ei.id);

            Timeline.ForEventBottom bottomItem = new Timeline.ForEventBottom(ei);
            bottomItem.isExpand = isExpandMsg;
            addTimelineItem(insertIndex + numOfInsert++, bottomItem);

            if (isExpandMsg) {
                URange<Integer> msgRange = insertTimeEventMsg(insertIndex + numOfInsert, ei, ei.msgIds, false, false);
                numOfInsert += msgRange.getUpper() - msgRange.getLower();

                Timeline.ForMessageInput msgItemInput = new Timeline.ForMessageInput(ei);
                addTimelineItem(insertIndex + numOfInsert++, msgItemInput);
            }

            Timeline.ForEventItem tailItem = new Timeline.ForEventTail(ei);
            addTimelineItem(insertIndex + numOfInsert++, tailItem);
        }
        URange<Integer> range = new URange<>(insertIndex, insertIndex + numOfInsert);
        if (isNotifyChange) {

            this.notifyItemRangeInserted(range.getLower(), range.getUpper() - range.getLower());
        }
        if (isRefreshAll) {
            refreshAllTimelineItem(range.getLower());
        }
        return range;

    }

    int insertInputMsg(int insertIndex, EventItem eventItem, boolean isNotifyChange, boolean isRefreshAll) {
        Timeline.ForMessageInput msgItem = new Timeline.ForMessageInput(eventItem);
        addTimelineItem(insertIndex++ , msgItem);

        if (isNotifyChange) {
            this.notifyItemInserted(insertIndex);
        }
        if (isRefreshAll) {
            refreshAllTimelineItem(insertIndex);
        }
        return insertIndex;
    }

    //msgIds 可以填一个,方便更新增加一条的情况
    public URange<Integer> insertTimeEventMsg(int insertIndex, EventItem eventItem, List<Long> msgIds, boolean isNotifyChange, boolean isRefreshAll) {
        int numOfInsert = 0;
        if (msgIds.size() == 0) {
            return new URange<>(insertIndex, insertIndex);
        }
        for (int i = 0; i < msgIds.size(); ++i) {
            Timeline.ForMessage msgItem = new Timeline.ForMessage(eventItem, msgIds.get(i));
            addTimelineItem(insertIndex + numOfInsert++, msgItem);
        }



        URange<Integer> range = new URange<>(insertIndex, insertIndex + numOfInsert);
        if (msgIds.size() == 0) {
            return range;
        }

        if (isNotifyChange) {
            this.notifyItemRangeInserted(range.getLower(), rangeInterval(range));
        }
        if (isRefreshAll) {
            refreshAllTimelineItem(range.getLower());
        }

        return range;
    }

    public void addMsg(long eventItemId, long msgId, boolean selfAdd) {
        URange<Integer> range = findEventTimelineRange(eventItemId);
        if (range != null) {
            int msgItemIndex = 0;
            boolean hasExpand = isExpandMsg(eventItemId);

            for (int i = range.getLower(); i < range.getUpper(); i++) {
                Timeline.Item timeline = this.timelineItems.get(i);
                if (Timeline.ForEventBottom.class.isInstance(timeline)) {
                    notifyItemChanged(i);
                    continue;
                }

                if (hasExpand) {
                    if (Timeline.ForMessageInput.class.isInstance(timeline)) {

                        EventItem eventItem = eventItemManager.getItemByItemId(eventItemId);
                        if (eventItem != null) {
                            ArrayList<Long> msgIds = new ArrayList<>();
                            msgIds.add(msgId);
                            insertTimeEventMsg(i, eventItem, msgIds, true, true);
                            return;
                        }
                    }
                }
//                else if (selfAdd) {
//                    if (Timeline.ForMessageInput.class.isInstance(timeline)) {
//
//                        EventItem eventItem = eventItemManager.getItemByItemId(eventItemId);
//                        if (eventItem != null) {
//                            ArrayList<Long> msgIds = new ArrayList<>();
//                            msgIds.add(msgId);
//                            URange<Integer> insertRange = insertTimeEventMsg(i, eventItem, msgIds, false, false);
//                            insertRange.setUpper(insertInputMsg(insertRange.getUpper(), eventItem, false, false));
//                            notifyItemRangeInserted(insertRange.getLower(), rangeInterval(range));
//                            return;
//                        }
//                    }
//
//                }

            }
        }
    }
    public void deleteMsg(long eventItemId, long msgId) {

        URange<Integer> range = findEventTimelineRange(eventItemId);
        if (range != null) {
            int msgItemIndex = 0;
            for (int i = range.getLower(); i < range.getUpper(); i++) {
                Timeline.Item timeline = this.timelineItems.get(i);
                if (Timeline.ForEventBottom.class.isInstance(timeline)) {
                    notifyItemChanged(i);
                    continue;
                }


                if (Timeline.ForMessage.class.isInstance(timeline)) {

                    Timeline.ForMessage msgItem = (Timeline.ForMessage) timeline;
                    if (msgItem.msgId == msgId) {

                        timelineItems.remove(i);
                        notifyItemRemoved(i);

                        if (msgItemIndex == 0 && msgItem.eventItem.msgIds.size() > 0) { //通知下一行去掉separator
                            notifyItemChanged(i);
                        }
                        --i;
                        continue;
                    }
                    ++msgItemIndex;
                }

                if (Timeline.ForMessageInput.class.isInstance(timeline)) {
                    EventItem eventItem = eventItemManager.getItemByItemId(eventItemId);
                    if (eventItem != null) {
                        if (eventItem.msgIds.size() == 0) {
                            timelineItems.remove(i);
                            notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void expandMsg(boolean value, long eventItemId ) {
        if (value == isExpandMsg(eventItemId))
            return;
        EventItem eventItem = eventItemManager.getItemByItemId(eventItemId);
        if (eventItem == null) {
            return;
        }
        URange<Integer> range = findEventTimelineRange(eventItemId);
        if (range == null) {
            return;
        }
        for (int i = range.getLower(); i < range.getUpper(); i++) {
            Timeline.Item timeline = this.timelineItems.get(i);
            if (Timeline.ForEventBottom.class.isInstance(timeline)) {
                ((Timeline.ForEventBottom) timeline).isExpand = value;
                notifyItemChanged(i);
                continue;
            }

            if (Timeline.ForEventTail.class.isInstance(timeline)) {
                range = insertTimeEventMsg(i, eventItem, eventItem.msgIds, false, false);
                range.setUpper(insertInputMsg(range.getUpper(), eventItem, false, false));
                notifyItemRangeInserted(range.getLower(), rangeInterval(range));

            }

        }



        eventsExpandSet.add(eventItemId);


    }
    //use zero for all refresh
    void refreshAllTimelineItem(int startIndex) {
//        if (startIndex == 0) {
//            eventId2Index.clear();
//        }
//        for (int i = startIndex; i < timelineItems.size(); i++) {
//            Timeline.Item timelineItem = timelineItems.get(i);
//            timelineItem.index = i;
//            if (Timeline.ForEventTitle.class.isInstance(timelineItem)) {
//                eventId2Index.put(((Timeline.ForEventItem) timelineItem).eventItem.id, i);
//            }
//        }
    }


    public URange<Integer> deleteEventItem(long eventItemId, boolean isNotifyChange, boolean isRefreshAll) {
        URange<Integer> range = findEventTimelineRange(eventItemId);
        if (range == null) {
            return null;
        }

        timelineItems.subList(range.getLower(), range.getUpper()).clear();
        if (isNotifyChange) {
            this.notifyItemRangeRemoved(range.getLower(), range.getUpper() - range.getLower());
        }
        if (isRefreshAll) {
//            eventId2Index.remove(eventItemId);
            refreshAllTimelineItem(range.getLower());
        }
        return range;

    }


    public URange<Integer> modifyEventItem(EventItem eventItem, boolean isNotifyChange, boolean isRefreshAll) {
        URange<Integer> range = deleteEventItem(eventItem.id, false, false);
        if (range == null) {
            return null;
        }

        URange<Integer> insertRange = insertTimeEventItem(range.getLower(), eventItem, false, false);
        if (insertRange == null) {
            return null;
        }

        if (isNotifyChange) {
            if (range.getUpper().equals(insertRange.getUpper())) {

                this.notifyItemRangeChanged(insertRange.getLower(), insertRange.getUpper() - insertRange.getLower());
            }
            if (range.getUpper() > insertRange.getUpper()) {
                this.notifyItemRangeChanged(insertRange.getLower(), insertRange.getUpper() - insertRange.getLower());
                this.notifyItemRangeRemoved(insertRange.getUpper(), range.getUpper() - insertRange.getUpper());
            } else if (range.getUpper() < insertRange.getUpper()) {
                this.notifyItemRangeChanged(range.getLower(), range.getUpper() - range.getLower());
                this.notifyItemRangeInserted(range.getUpper(), insertRange.getUpper() - range.getUpper());


            }
        }

        if (isRefreshAll) {
            refreshAllTimelineItem(range.getLower());
        }
        return new URange<>(insertRange.getLower(), Math.max(insertRange.getUpper(), range.getUpper()));
    }


    public void insertEventItemBefore(EventItem eventItem, long afterEventId) {
        URange<Integer> range = this.findEventTimelineRange(afterEventId);
        insertTimeEventItem(range.getLower(), eventItem, true, true);
    }

    //这个是eventItem在eventItemManager的Index,



    public int getInsertEventItemIndex(int eventIndex) {
        int currEventIndex = 0;
        for (int i = 0; i < timelineItems.size(); i++) {
            Timeline.Item timelineItem = timelineItems.get(i);
            if (Timeline.ForEventTitle.class.isInstance(timelineItem)) {

                if (currEventIndex == eventIndex) {
                    return i;
                } else {
                    currEventIndex++;
                }
            }
        }

        return timelineItems.size();


    }
//    public void insertEventItemAfter(EventItem eventItem, long beforeEventId) {
//        URange<Integer> range = this.findEventTimelineRange(beforeEventId);
//        insertTimeEventItem(range.getUpper(), eventItem, true, true);
//    }

    public Integer findEventTimeClassPos(long eventItemId, Class clazz) {
        URange<Integer> range = findEventTimelineRange(eventItemId);
        if (range != null) {
            for (int i = range.getLower(); i < range.getUpper(); i++) {
                if (clazz.isInstance(timelineItems.get(i))) {
                    return i;
                }
            }
        }
        return null;
    }

    public Integer findEventTail(long eventItemId) {
        return findEventTimeClassPos(eventItemId, Timeline.ForEventTail.class);
    }
    public Integer findEventTimeMessageInputPos(long eventItemId) {
        return findEventTimeClassPos(eventItemId, Timeline.ForMessageInput.class);
    }
    public Integer findEventTimeEventBottomPos(long eventItemId) {
        return findEventTimeClassPos(eventItemId, Timeline.ForEventBottom.class);
    }

    public URange<Integer> findEventTimelineRange(long eventItemId) {

//        if (!eventId2Index.containsKey(eventItemId)) {
//            return null;
//        }
//        int index = eventId2Index.get(eventItemId);
        for (int j = 0 ; j < timelineItems.size(); ++j) {
            if (Timeline.ForEventTitle.class.isInstance(timelineItems.get(j))) {
                Timeline.ForEventTitle eventTitle = (Timeline.ForEventTitle) timelineItems.get(j);
                if (eventTitle.eventItem.id == eventItemId) {
                    int index = j;
                    for (int i = index + 1; i < timelineItems.size(); i++) {
                        Timeline.Item timelineItem = timelineItems.get(i);
                        if (Timeline.ForEventTail.class.isInstance(timelineItem)) {
                            return new URange<>(index, i + 1);

                        }

                    }
                }
            }
        }
        return null;
    }



    public int modifyTitle(long eventItemId, boolean value, EventListView eventListView) {
        URange<Integer> range = findEventTimelineRange(eventItemId);
        if (range != null) {
            Integer index = range.getLower();
            Timeline.Item item = this.timelineItems.get(index);
            if (Timeline.ForEventTitle.class.isInstance(item)) {
                Timeline.ForEventTitle forEventTitle = (Timeline.ForEventTitle) item;
                forEventTitle.isModifyTitle = value;
                if (value) {
                    this.notifyItemChanged(index);
                }
                else {
                    RecyclerView.ViewHolder viewHolder = eventListView.findViewHolderForAdapterPosition(index);
                    if (TimelineViewHolder.ForEventTitle.class.isInstance(viewHolder)) {
                        TimelineViewHolder.ForEventTitle forEventTitleView = (TimelineViewHolder.ForEventTitle) viewHolder;
                        forEventTitleView.hideModify();

                        this.notifyItemChanged(index);

                    }

                }

                return index;
            }

        }
        return -1;
    }



    void addTimelineItem(int insertIndex, Timeline.Item timelineItem) {
//        timelineItem.index = insertIndex;
        this.timelineItems.add(insertIndex, timelineItem);

    }

//    <T extends Timeline.ForEventItem> T createTineLineEventItem(EventItem eventItem, Class<T> c)  {
//        try {
//            T ret = c.newInstance();
//            ret.eventItem = eventItem;
//            //ret.createTineLineEventItem(eventItem);
//            return ret;
//
//        }
//        catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//        catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        finally {
//        }
//        return null;
//        //
//
//    }

}

class LayoutGenerate {

    private static final String TAG = "LayoutGenerate";
    static int layoutSeed = new Random().nextInt(10000000);
    Random random = new Random();
    class ImageLayoutType {
        int resId;
        int num;
        ImageLayoutType(int resId, int num) {
            this.resId = resId;
            this.num = num;
        }

    }
    public static HashMap<Integer, ArrayList<ImageLayoutType>> layoutTypes = new HashMap<>();

    LayoutGenerate() {
        if (layoutTypes.size() > 0) {
            return;
        }
        {
            ArrayList<ImageLayoutType> tmp = new ArrayList<>();
            tmp.add(new ImageLayoutType(R.layout.event_image_group_1, 0));
            layoutTypes.put(0, tmp);
        }
        {
            ArrayList<ImageLayoutType> tmp = new ArrayList<>();
            tmp.add(new ImageLayoutType(R.layout.event_image_group_1, 1));
            layoutTypes.put(1, tmp);
        }
        {
            ArrayList<ImageLayoutType> tmp = new ArrayList<>();
            tmp.add(new ImageLayoutType(R.layout.event_image_group_2, 2));
            layoutTypes.put(2, tmp);

        }

        {
            ArrayList<ImageLayoutType> tmp = new ArrayList<>();
            tmp.add(new ImageLayoutType(R.layout.event_image_group_3_0, 3));
            tmp.add(new ImageLayoutType(R.layout.event_image_group_3_1, 3));
            tmp.add(new ImageLayoutType(R.layout.event_image_group_3_2, 3));
            layoutTypes.put(3, tmp);
        }

    }
    List<ImageLayoutType> generateImageLayout(EventItem eventItem) {
        ArrayList<ImageLayoutType> ret = new ArrayList<>();
        if (eventItem.imagesCount() == 0) {
            ret.add(layoutTypes.get(0).get(0));
            return ret;
        }

        if (eventItem.imagesCount() <= 2) {
            ret.add(layoutTypes.get(eventItem.imagesCount()).get(0));
            return ret;
        }

        if (eventItem.imagesCount() == 3) {
            ArrayList<ImageLayoutType> layouts = layoutTypes.get(eventItem.imagesCount());

            ret.add(layouts.get(random.nextInt(2)+ 1));
            return ret;
        }
        if (eventItem.imagesCount() == 4) {
            ret.add(layoutTypes.get(2).get(0));
            ret.add(layoutTypes.get(2).get(0));
            return ret;
        }


        Stack<Integer> result = new Stack<>();
        if (DebugConst.useSameEventLayout) {
            random.setSeed(eventItem.id);
        }
        else {
            random.setSeed(eventItem.id * layoutSeed);
        }
        if (trackGenerateImage(result, eventItem.imagesCount(), 0)) {

            return generateNextImageType(result);
        }
        else {
            Log.e(TAG, "generateImageLayout sth wrong with find generImage");
            return null;
        }
    }


    boolean trackGenerateImage(Stack<Integer> result, int remainNum, int lastChoiceNum) {
        if (remainNum == 0) {
            return true;
        }

        if (remainNum == 1) {
            return false;
        }
        if (remainNum <= 3) {

            if (lastChoiceNum == 2 && lastChoiceNum == remainNum) { //保证上次不同
                return false;
            }
            else {
                result.push(remainNum);
                return true;
            }

        }
        else {

            int selectList[];

            //获取下一个选择顺序
            {
                if (lastChoiceNum == 2) {
                    int tmp[] = {3};
                    selectList = tmp;

                } else {
                    float r = random.nextFloat();
                    if (r < 0.25) {
                        int tmp[] = {2, 3};
                        selectList = tmp;
                    } else {
                        int tmp[] = {3, 2};
                        selectList = tmp;
                    }
                }
            }
            for (int i = 0; i < selectList.length; ++i) {
                int selectNum = selectList[i];
                result.push(selectNum);
                if (trackGenerateImage(result, remainNum - selectNum, selectNum)) {
                    return true;
                }
                result.pop();
            }
        }


        return false;
    }


    List<ImageLayoutType> generateNextImageType(List<Integer> numQueues) {
        List<ImageLayoutType> result = new ArrayList<>();
        int lastChoice3Index = -1;
        for (Integer num :
                numQueues) {
            if (num == 2) {
                lastChoice3Index = -1;
                result.add(layoutTypes.get(num).get(0));
            } else if (num == 3) {
                int size = layoutTypes.get(num).size();
                int selectIndex = random.nextInt(size - 1);
                if (lastChoice3Index == -1 || selectIndex < lastChoice3Index) {
                    lastChoice3Index = selectIndex;
                    result.add(layoutTypes.get(num).get(selectIndex));
                } else {
                    lastChoice3Index = selectIndex + 1;
                    result.add(layoutTypes.get(num).get(selectIndex + 1));
                }

            }
        }
        return result;
    }


}