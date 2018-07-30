package com.andzj.mylibrary.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.andzj.mylibrary.util.MyLog;

/**
 * Created by zj on 2016/7/17.
 */
public class BaseActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyLog.d("BaseActivity",getClass().getSimpleName() + "  OnCreate.",false);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        MyLog.d("BaseActivity",getClass().getSimpleName() + " OnDestroy.",false);
        ActivityCollector.removeActivity(this);
    }
}
