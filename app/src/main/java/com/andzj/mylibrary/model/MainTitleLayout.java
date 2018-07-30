package com.andzj.mylibrary.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andzj.mylibrary.R;

/**
 * Created by zj on 2016/7/21.
 */
public class MainTitleLayout extends LinearLayout
{
    private ImageButton backBtn;
    private TextView titleText;
    public ImageButton moreBtn;
    public MainTitleLayout(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.lay_main_title,this);
        titleText = (TextView) findViewById(R.id.main_title_text);
        backBtn = (ImageButton) findViewById(R.id.main_title_back_btn);
        moreBtn = (ImageButton) findViewById(R.id.main_title_more_btn);
        init(context,attrs);

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }

    private void init(Context context,AttributeSet attrs)
    {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.main_title_attrs);

        int title_backgroundRes = typedArray.getResourceId(R.styleable.main_title_attrs_title_background_res,0);

        int back_btn_visibility = typedArray.getInt(R.styleable.main_title_attrs_back_btn_visibility,2);
        int back_btn_backgroundRes = typedArray.getInt(R.styleable.main_title_attrs_back_btn_background_res,0);

        String title_text = typedArray.getString(R.styleable.main_title_attrs_title_text);
        int title_text_color = typedArray.getColor(R.styleable.main_title_attrs_title_text_color,Color.parseColor("#FF0000"));
        int title_text_backgroundRes =typedArray.getResourceId(R.styleable.main_title_attrs_title_text_background_res,0);

        int more_btn_visibility = typedArray.getInt(R.styleable.main_title_attrs_more_btn_visibility,2);
        int more_btn_backgroundRes = typedArray.getResourceId(R.styleable.main_title_attrs_more_btn_background_res,0);

        if (title_backgroundRes != 0)
            super.setBackgroundResource(title_backgroundRes);

        backBtn.setVisibility(((back_btn_visibility==0) ? View.GONE : ( (back_btn_visibility==1) ? View.INVISIBLE : View.VISIBLE )));
        if (back_btn_backgroundRes != 0)
            backBtn.setBackgroundResource(back_btn_backgroundRes);

        if (title_text != null && !("".equals(title_text)))
        {
            titleText.setText(title_text);
        }
        titleText.setTextColor(title_text_color);
        if (title_text_backgroundRes !=0 )
            titleText.setBackgroundResource(title_text_backgroundRes);

        moreBtn.setVisibility(((more_btn_visibility==0) ? View.GONE : ( (more_btn_visibility==1) ? View.INVISIBLE : View.VISIBLE )));
        if (more_btn_backgroundRes != 0)
            moreBtn.setBackgroundResource(more_btn_backgroundRes);

        typedArray.recycle();
    }

    public void setTitleText(String text)
    {
        titleText.setText(text);
    }

    public void setTitleTextColor(int color)
    {
        titleText.setTextColor(color);
    }

    public void setMoreBtnOnClickedListener(OnClickListener click)
    {
        moreBtn.setOnClickListener(click);
    }

    public void setMoreBtnVisibility(int v)
    {
        moreBtn.setVisibility(((v==0) ? View.GONE : ( (v==1) ? View.INVISIBLE : View.VISIBLE )));
    }

    public void setBackBtnOnClickedListener(OnClickListener click)
    {
        backBtn.setOnClickListener(click);
    }

    public void setBackBtnVisibility(int v)
    {
        backBtn.setVisibility(((v==0) ? View.GONE : ( (v==1) ? View.INVISIBLE : View.VISIBLE )));
    }




}
