package com.gtphoto.mp;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.gtphoto.base.media.MediaLoader;
import com.gtphoto.widget.common.box.Box;
import com.gtphoto.widget.common.bus.UIBus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by kennymac on 16/6/16.
 */
@EViewGroup(R.layout.time_live_view)
public class TimelineView extends FrameLayout {

    @ViewById
    SwipeRefreshLayout swipeLayout;

    @ViewById
    RecyclerView timeLimeList;
    public TimelineView(Context context) {
        super(context);
    }

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimelineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Box.get(UIBus.class).register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Box.get(UIBus.class).unregister(this);
    }

    @AfterViews
    void onAfterView() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                Box.get(MediaLoader.class).loadAsync(getContext(), ((AppCompatActivity) getContext()).getSupportLoaderManager(), new MediaLoader.LoadCallback() {
                    @Override
                    public void onLoad(MediaLoader mediaLoader) {
                        swipeLayout.setRefreshing(false);

                    }
                });
            }
        });
    }



}
