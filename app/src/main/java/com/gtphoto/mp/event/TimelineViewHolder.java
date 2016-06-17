package com.gtphoto.mp.event;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gtphoto.mp.DebugConst;
import com.gtphoto.mp.R;
import com.gtphoto.mp.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;

/**
 * Created by kennymac on 15/9/28.
 */


public class TimelineViewHolder extends RecyclerView.ViewHolder {

    public interface ViewHolderListener {

    }

    public TimelineViewHolder(View itemView) {
        super(itemView);
    }

    public void showCell(EventListViewAdapter.Timeline.Item item, ViewHolderListener listener) {}
    public void onRecycle() {}
    static public TimelineViewHolder createViewHolder(View view, int layoutId) {
        switch (layoutId) {
            case R.layout.event_view_title:
                return new ForEventTitle(view);
            case R.layout.event_view_bottom:
                return new ForEventBottom(view);
            case R.layout.event_image_group_1:
                return new EventImageGroup(view, 1);
            case R.layout.event_image_group_2:
                return new EventImageGroup(view, 2);
            case R.layout.event_image_group_3_0:
            case R.layout.event_image_group_3_1:
            case R.layout.event_image_group_3_2:
                return new EventImageGroup(view, 3);
            case R.layout.event_view_message:
                return new ForEventMessage(view);
            case R.layout.event_view_message_input:
                return new ForEventMessageInput(view);
            case R.layout.event_view_tail:
            case R.layout.event_view_top:
                return new TimelineViewHolder(view);

            default:
                return null;
        }
    }

    public static class ForEventHolder extends TimelineViewHolder{
        public EventItem eventItem;

        public ForEventHolder(View itemView) {
            super(itemView);
        }

        public void showCell(EventListViewAdapter.Timeline.Item item, ViewHolderListener listener) {
            eventItem = ((EventListViewAdapter.Timeline.ForEventItem) item).eventItem;
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            this.eventItem = null;
        }
    }

    public interface ForEventTitleListener extends ViewHolderListener {
        public abstract void onModifyTitleLostFocus(ForEventTitle forEventTitle);
        public abstract void onInputTitleDone(ForEventTitle forEventTitle, String text);
        public abstract void onMoreBtn(ForEventTitle holder);
    }

    public static class ForEventTitle extends ForEventHolder implements View.OnClickListener {

        // each data item is just a string in this case

        ForEventTitleListener listener = null;

        EventListViewAdapter.Timeline.ForEventTitle forEventTitleItem;
        TextView mEventTitle;
        boolean settingForUpload = false;
        TextView mEventDate;
        ImageButton moreBtn;
        EditText mTitleInput;

        private String TitleOperationKey = "onTitleOperation";

        public ForEventTitle(View v) {
            super(v);


            mEventTitle = (TextView) v.findViewById(R.id.event_title);
            mEventDate= (TextView) v.findViewById(R.id.event_date);
            mTitleInput = (EditText)v.findViewById(R.id.event_title_input);

//            moreBtn = (ImageButton) this.itemView.findViewById(R.id.more_btn);
//            moreBtn.setOnClickListener(this);



        }

        public EditText getInput() {
            return mTitleInput;
        }
        public void refreshTitle() {

            if (eventItem.hasGetDetail == false) {
                EventItemManager.sharedInstance(eventItem.familyId).getDetailEvent(eventItem.id, 3, 0, null);
            }


            itemView.findViewById(R.id.title_layout).setVisibility(!forEventTitleItem.isModifyTitle ? View.VISIBLE : View.GONE);
            itemView.findViewById(R.id.input_layout).setVisibility(forEventTitleItem.isModifyTitle ? View.VISIBLE : View.GONE);
            mTitleInput.setVisibility(forEventTitleItem.isModifyTitle ? View.VISIBLE : View.GONE);
            settingForUpload = false;
            if (!forEventTitleItem.isModifyTitle) {
                mEventDate.setVisibility(forEventTitleItem.eventItem.hasGetDetail ? View.VISIBLE : View.GONE);

                if (DebugConst.showEventTitleId) {
                    mEventTitle.setText(String.format("%s id:%d", forEventTitleItem.getTitle(), eventItem.id));
                }
                else {
                    mEventTitle.setText(forEventTitleItem.getTitle());
                }

                mEventDate.setText(forEventTitleItem.getDate());
            } else {
                mTitleInput.setText(forEventTitleItem.eventItem.title);

                mTitleInput.setFocusableInTouchMode(true);

                prepareForModifyTitle();
                mTitleInput.requestFocus();
            }
        }

        void shutForModifyTitle() {
            mTitleInput.setOnFocusChangeListener(null);
            mTitleInput.setOnKeyListener(null);

        }

        void prepareForModifyTitle() {
//            NotificationCenter.defaultCenter().addObserver(TitleOperationKey, this, "onTitleOperation");
            mTitleInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    final boolean isFocus = hasFocus;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) (mTitleInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                            if (isFocus) {
                                imm.showSoftInput(mTitleInput, InputMethodManager.SHOW_IMPLICIT);
                                //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            } else {
//                                if (imm.isActive()) {
//                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
////                                    imm.hideSoftInputFromWindow(mTitleInput.getWindowToken(), 0);
//                                    if (ForEventTitle.this.listener != null) {
//                                        ForEventTitle.this.listener.onModifyTitleLostFocus(ForEventTitle.this);
//
//                                    }
//                                }

//                                        ForEventTitle.this.listener.onModifyTitleLostFocus(ForEventTitle.this);
//                                    }
//                                if (imm.isActive()) {
//                                    //mTitleInput.setCursorVisible(false);
//                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                                    if (ForEventTitle.this.listener != null) {
//                                        ForEventTitle.this.listener.onModifyTitleLostFocus(ForEventTitle.this);
//                                    }
//                                }
                            }
                        }
                    }, 100);

                }
            });

            mTitleInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {

//                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                        if (imm.isActive()) {
//
//                            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//
//                        }
                        if (TextView.class.isInstance(v)) {
                            TextView textView = (TextView) v;
                            String text = textView.getText().toString();
                            if (text != null && listener != null) {
                                listener.onInputTitleDone(ForEventTitle.this, text);
                            }

                        }


                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
        }

        void hideModify() {
            InputMethodManager imm = (InputMethodManager) mTitleInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm.isActive()) {

                imm.hideSoftInputFromWindow(mTitleInput.getWindowToken(), 0);

            }
//            if (TextView.class.isInstance(v)) {
//                TextView textView = (TextView) v;
//                String text = textView.getText().toString();
//                if (text != null && listener != null) {
//                    listener.onModifyTitleLostFocus(this);
//                }
//
//            }
        }
        @Override
        public void showCell(EventListViewAdapter.Timeline.Item item, final ViewHolderListener listener) {
            super.showCell(item, listener);
            this.listener = (ForEventTitleListener) listener;
            forEventTitleItem = (EventListViewAdapter.Timeline.ForEventTitle) item;
            refreshTitle();



        }

        @Override
        public void onRecycle() {
            shutForModifyTitle();
//            if (mTitleInput.getVisibility() == View.VISIBLE && mTitleInput.isFocused()) {
//                InputMethodManager imm = (InputMethodManager) (mTitleInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
//
//                if (imm.isActive()) {
//                    imm.hideSoftInputFromWindow(mTitleInput.getWindowToken(), 0);
//                }
//            }
            forEventTitleItem.isModifyTitle = false;
            super.onRecycle();
            this.listener = null;
        }

        @Override
        public void onClick(View v) {
            if (v.equals(moreBtn)) {
                if (listener != null) {
                    listener.onMoreBtn(this);
                }
            }
        }
    }

    public interface ForEventBottomListenter extends ViewHolderListener {


        public abstract void onAppendPhotoBtn(ForEventBottom holder);
        public abstract void onMsgBtn(ForEventBottom holder);
    }
    public static class ForEventBottom extends ForEventHolder implements View.OnClickListener {


        ImageButton appendPhotoBtn;
        ImageButton msgBtn;
        ForEventBottomListenter listenter;


        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void showCell(EventListViewAdapter.Timeline.Item item, ViewHolderListener listener) {
            super.showCell(item, listener);
            this.listenter = (ForEventBottomListenter) listener;

            if (this.eventItem.msgIds.size() > 0) {
                if (this.eventItem.msgIds.size() <= 9) {

                    int id = 0;
                    try {
                        Field field = R.mipmap.class.getField(String.format("leavemsg_get_btn_%d_normal", this.eventItem.msgIds.size()));
                        id = Integer.parseInt(field.get(null).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (id > 0) {
                        msgBtn.setImageDrawable(L.getDrawable(id));
                    }
                }
                else {
//                    msgBtn.setImageDrawable(L.getDrawable(R.mipmap.leavemsg_get_btn_9plus_normal));
                }


            }
            else {
//                msgBtn.setImageDrawable(L.getDrawable(R.mipmap.leavemsg_none_btn_normal));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                if (((EventListViewAdapter.Timeline.ForEventBottom) item).isExpand) {
                    msgBtn.setAlpha(0.3f);
                } else {
                    msgBtn.setAlpha(1.0f);
                }
            }
        }

        public ForEventBottom(View itemView) {
            super(itemView);


//            appendPhotoBtn = (ImageButton) this.itemView.findViewById(R.id.append_photo_btn);
//            appendPhotoBtn.setOnClickListener(this);
//
//            msgBtn = (ImageButton) this.itemView.findViewById(R.id.msg_btn);
//
//
//            msgBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenter == null) {
                return;
            }

            if(appendPhotoBtn.equals(v)) {
                listenter.onAppendPhotoBtn(this);
            }
            else if(msgBtn.equals(v)) {
                listenter.onMsgBtn(this);
            }
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            this.listenter = null;
        }
    }

    public interface EventImageGroupListener extends ViewHolderListener {
        public abstract void onClickImage(EventImageGroup imageGroup, int index, long imageId);
    }
    public static class EventImageGroup extends ForEventHolder implements View.OnClickListener {
        private int imageNum;

        private EventImageGroupListener listener;

        EventListViewAdapter.Timeline.ForEventImage forEventImage;
        static int ResIds[] = {R.id.imageView0, R.id.imageView1, R.id.imageView2};
        static int MovieDescResIds[] = {R.id.movie_duration_desc0, R.id.movie_duration_desc1, R.id.movie_duration_desc2};

        public EventImageView imageViews[] = new EventImageView[3];
        public View movieDesc[] = new View[3];
        public TextView movieDuration[] = new TextView[3];
        public ImageView uploadingLogo[] = new ImageView[3];
        public ImageView movieLogo[] = new ImageView[3];
        public ImageView uploadingWord[] = new ImageView[3];

        //EventImageItem eventImageItems[] = new EventImageItem[3];


        public EventImageGroup(View itemView, int imageNum) {
            super(itemView);



            this.imageNum = imageNum;

            for (int i = 0; i < imageNum; ++i) {
                EventImageView imageView = (EventImageView) this.itemView.findViewById(ResIds[i]);
                imageViews[i] = imageView;

                movieDesc[i] = this.itemView.findViewById(MovieDescResIds[i]);
                movieDuration[i] = (TextView) movieDesc[i].findViewById(R.id.movie_duration);
//                uploadingLogo[i] = (ImageView) movieDesc[i].findViewById(R.id.uploading_logo);
//                uploadingWord[i] = (ImageView) movieDesc[i].findViewById(R.id.upload_loading_word);
//                movieLogo[i] = (ImageView) movieDesc[i].findViewById(R.id.movie_icon);
                final int index = i;
                //imageView.setTag(index);

                imageView.setOnClickListener(this);


            }

        }

        public boolean isImageLoaded(int index) {
            if (index >= imageViews.length) {
                return false;
            }
            return imageViews[index].isImageLoaded();
        }
        @Override
        public void onClick(View v) {
            if(this.listener == null)
                return;

            for (int i = 0; i < imageNum; ++i) {
                if (imageViews[i].equals(v)) {
                    listener.onClickImage(this, i, (Long)imageViews[i].getTag());
                }
            }
        }

//        void refreshImageDesc(UploadPhotoManager uploadPhotoManager, long imageId, int index) {
//            Float imageUploadPercent = uploadPhotoManager.getImageUploadPercent(imageId);
//            final EventImageItem imageItem = forEventImage.eventItem.imageByTake(index + forEventImage.imageStartIndex);
//
//            if (imageUploadPercent != null && imageUploadPercent < 1.0) {
//                movieDesc[index].setVisibility(View.VISIBLE);
//                movieDesc[index].setBackground(L.getDrawable(R.drawable.movie_duration_uploading_back));
//
//
//                uploadingLogo[index].setVisibility(View.VISIBLE);
//                uploadingWord[index].setVisibility(View.VISIBLE);
//                movieDuration[index].setVisibility(View.GONE);
//                movieLogo[index].setVisibility(View.GONE);
//                boolean paused = uploadPhotoManager.isPaused();
//                uploadingLogo[index].setImageDrawable(!paused ? L.getDrawable(R.mipmap.upload_loading) : L.getDrawable(R.mipmap.upload_pause));
//                uploadingWord[index].setImageDrawable(!paused ? L.getDrawable(R.mipmap.upload_loading_word) : L.getDrawable(R.mipmap.upload_pause_word));
//
//            }
//            else {
//                uploadingLogo[index].setVisibility(View.GONE);
//                uploadingWord[index].setVisibility(View.GONE);
//                movieDesc[index].setVisibility(imageItem.isMovie ? View.VISIBLE : View.GONE);
//                movieDesc[index].setBackground(L.getDrawable(R.drawable.movie_duration_back));
//                if (imageItem.isMovie) {
//
//                    movieDuration[index].setText(String.format("%d:%d", (int)imageItem.movieDuration / 60, (int)imageItem.movieDuration % 60 ));
//                    movieDuration[index].setVisibility(View.VISIBLE);
//                    movieLogo[index].setVisibility(View.VISIBLE);
//                }
//                else {
//                    movieDuration[index].setVisibility(View.GONE);
//                    movieLogo[index].setVisibility(View.GONE);
//                }
//            }
//
//        }


        public void loadImage(final EventImageItem imageItem, final ImageView imageView) {
            if (EventImageView.class.isInstance(imageView)) {
                ((EventImageView)imageView).loadImage(imageItem, null, false, (int)L.dimen(R.dimen.corner_radius));
            }
        }

        public void loadMovie(final EventImageItem imageItem, final ImageView imageView) {
            if (EventImageView.class.isInstance(imageView)) {
                ((EventImageView)imageView).loadImage(imageItem, null, false, (int)L.dimen(R.dimen.corner_radius));
            }
        }
        @Override
        public void showCell(EventListViewAdapter.Timeline.Item item, ViewHolderListener listener) {
            super.showCell(item, listener);
            this.listener = (EventImageGroupListener) listener;
            forEventImage = (EventListViewAdapter.Timeline.ForEventImage) item;
            if (forEventImage.imageCount != imageNum) {
                if (forEventImage.imageCount == 0 && imageNum > 0) {
                    for (int i = 0; i < imageNum; i++) {
                        movieDesc[i].setVisibility(View.GONE);

                    }
                }
                return;
            }

//            UploadPhotoManager uploadPhotoManager = UploadPhotoManager.sharedInstance();
//            for (int i = 0; i < forEventImage.imageCount; ++i) {
//                final EventImageItem imageItem = forEventImage.eventItem.imageByTake(i + forEventImage.imageStartIndex);
//                final ImageView imageView = imageViews[i];
//                //eventImageItems[i] = imageItem;
////                imageView.setTag(imageItem);
//                imageView.setTag(imageItem.id);
//                if (!imageItem.isMovie) {
//                    loadImage(imageItem, imageView);
//                } else {
//                    loadMovie(imageItem, imageView);
//                }
//
//                refreshImageDesc(uploadPhotoManager, imageItem.id, i);
////                movieDesc[i].setVisibility(imageItem.isMovie ? View.VISIBLE : View.GONE);
////                if (imageItem.isMovie) {
////
////                    movieDuration[i].setText(String.format("%d:%d", (int)imageItem.movieDuration / 60, (int)imageItem.movieDuration % 60 ));
////                }
//
//            }

            //空的

        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            this.listener = null;
            this.forEventImage = null;
            for (int i = 0; i < imageNum; ++i) {
                imageViews[i].setTag(null);


                ImageView imageView = imageViews[i];
                if (EventImageView.class.isInstance(imageView)) {
                    ((EventImageView)imageView).cancelLoad();
                }
                imageView.setImageBitmap(null);

            }
        }
    }

    public interface ForEventMessageListener extends ViewHolderListener {
        abstract public void onDeleteMsg(ForEventMessage holder, long msgId);
    }

    public static class ForEventMessage extends ForEventHolder implements View.OnLongClickListener {
        ImageView userImage;
        TextView userName;
        TextView userText;
        ForEventMessageListener listener;
        View seperator;
        long msgId = 0;
        private DisplayImageOptions displayImageOptions;

        public ForEventMessage(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.user_image);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userText = (TextView) itemView.findViewById(R.id.user_text);
            seperator = itemView.findViewById(R.id.message_sperator);
            this.itemView.setLongClickable(true);
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {

                listener.onDeleteMsg(this, msgId);
                return true;
            }
            return true;
        }

//        void loadComFamilyIdentity(FamilyUserInfo familyUserInfo) {
//            L.imageLoader().displayImage("drawable://" + familyUserInfo.indentify.bigImageName(), userImage, L.getLocalResDisplayOption());
//        }
        @Override
        public void showCell(EventListViewAdapter.Timeline.Item item, ViewHolderListener listener) {
            super.showCell(item, listener);

            EventListViewAdapter.Timeline.ForMessage msgItem = (EventListViewAdapter.Timeline.ForMessage) item;
            this.msgId = msgItem.msgId;
            if (msgId == msgItem.eventItem.msgIds.get(0)) {
                seperator.setVisibility(View.INVISIBLE);
            }
            else {
                seperator.setVisibility(View.VISIBLE);
            }
            this.listener = (ForEventMessageListener)listener;

//            EventMessageItem eventMsg = UserMessageManager.sharedInstance.getEventMsg(msgItem.msgId);
//            if (eventMsg == null) {
//                return;
//
//            }
//            FamilyInfo family = UserInfoManager.currentFamily();
//            if (family != null) {
//                final FamilyUserInfo familyUserInfo = family.findUserById(eventMsg.userId);
//                if (familyUserInfo != null) {
//                    userName.setText(familyUserInfo.showName());
//
//                    String fullPhotoUrl = familyUserInfo.fullPhotoUrl();
//
//                    if (fullPhotoUrl.length() == 0) {
//                        loadComFamilyIdentity(familyUserInfo);
//                    } else {
//
//                        this.displayImageOptions = new DisplayImageOptions.Builder().cloneFrom(L.displayImageOptions)
//                                .showImageOnFail(familyUserInfo.indentify.bigImageName())
//                                .showImageOnLoading(familyUserInfo.indentify.bigImageName())
//                                .displayer(new RoundedImageDisplayer((int) L.dimem(R.dimen.message_user_image_width))).build();
//
//
//                        L.imageLoader().displayImage(fullPhotoUrl, userImage, displayImageOptions);
//                    }
//                }
//            }
//
//
//            if (eventMsg.type == EventMessageItem.EventMessageType.Text) {
//                userText.setText(eventMsg.content);
//            }
//


        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            this.listener = null;
            ImageLoader.getInstance().cancelDisplayTask(userImage);
        }
    }

    public interface ForEventMessageInputListener extends ViewHolderListener {
        abstract public void onLeaveMsg(long eventItemId);
    }

    public static class ForEventMessageInput extends ForEventHolder {
        ImageButton leaveMsgBtn;

        ForEventMessageInputListener listener;
        public ForEventMessageInput(View itemView) {
            super(itemView);
//            leaveMsgBtn = (ImageButton) itemView.findViewById(R.id.leave_msg_btn);
//            leaveMsgBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        listener.onLeaveMsg(eventItem.id);
//                    }
//                }
//            });
        }

        @Override
        public void showCell(EventListViewAdapter.Timeline.Item item, ViewHolderListener listener) {
            super.showCell(item, listener);
            this.listener = (ForEventMessageInputListener) listener;

        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            this.listener = null;
        }
    }


}