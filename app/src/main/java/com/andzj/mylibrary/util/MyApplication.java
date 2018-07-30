package com.andzj.mylibrary.util;

import android.app.Application;
import android.content.Context;


/**
 * Created by zj on 2016/7/17.
 */
public class MyApplication extends Application
{
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context  getContext()
    {
        return context;
    }
}
