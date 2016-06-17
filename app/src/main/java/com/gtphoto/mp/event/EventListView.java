package com.gtphoto.mp.event;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by kennymac on 15/10/16.
 */
public class EventListView extends RecyclerView {

    public interface SizeChangeListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    SizeChangeListener sizeChangeListener;

    public void setSizeChangeListener(SizeChangeListener sizeChangeListener) {
        this.sizeChangeListener = sizeChangeListener;
    }

    public EventListView(Context context) {
        super(context);
    }

    public EventListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (sizeChangeListener != null) {
            sizeChangeListener.onSizeChanged(w, h, oldw, oldh);
        }
    }
}
