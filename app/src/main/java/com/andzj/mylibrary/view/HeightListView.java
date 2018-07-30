package com.andzj.mylibrary.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by zj on 2016/11/18.
 */

public class HeightListView extends ListView {

    public HeightListView(Context context) {
        super(context);
    }

    public HeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public HeightListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

