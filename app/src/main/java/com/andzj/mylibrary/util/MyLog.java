package com.andzj.mylibrary.util;

import android.content.Context;
import android.content.pm.FeatureGroupInfo;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.util.Log;
import android.widget.Toast;

import java.util.StringTokenizer;

/**
 * Created by zj on 2016/7/17.
 */
public class MyLog
{
    public static final Context mContext = MyApplication.getContext();

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    public static final int LEVEL = VERBOSE;

    public static boolean ShowToast = true;

    public static void v(String tag,String msg)
    {
        v(tag,msg,false);
    }

    public static void v(String tag,String msg,boolean useToast)
    {
        if (LEVEL <= VERBOSE)
        {
            Log.v(tag, msg);
        }
        if (ShowToast && useToast)
        {
            Toast.makeText(mContext,"V:" + msg,Toast.LENGTH_SHORT).show();
        }
    }

    public static void d(String tag,String msg)
    {
        d(tag,msg,false);
    }

    public static void d(String tag,String msg,boolean useToast)
    {
        if (LEVEL <= DEBUG)
        {
            Log.d(tag, msg);
        }
        if (ShowToast && useToast)
        {
            Toast.makeText(mContext,"D:" + msg,Toast.LENGTH_SHORT).show();
        }
    }

    public static void i(String tag,String msg)
    {
        d(tag,msg,false);
    }

    public static void i(String tag,String msg,boolean useToast)
    {
        if (LEVEL <= INFO)
        {
            Log.i(tag, msg);
        }
        if (ShowToast && useToast)
        {
            Toast.makeText(mContext,"I:" + msg,Toast.LENGTH_SHORT).show();
        }
    }

    public static void w(String tag,String msg)
    {
        w(tag,msg,false);
    }

    public static void w(String tag,String msg,boolean useToast)
    {
        if (LEVEL <= WARN)
        {
            Log.w(tag, msg);
        }
        if (ShowToast && useToast)
        {
            Toast.makeText(mContext,"W:" + msg,Toast.LENGTH_SHORT).show();
        }

    }

    public static void e(String tag,String msg)
    {
        e(tag,msg,false);
    }

    public static void e(String tag, String msg, boolean useToast)
    {
        if (LEVEL <= ERROR)
        {
            Log.e(tag, msg);
        }
        if (ShowToast && useToast)
        {
            Toast.makeText(mContext,"E:" + msg,Toast.LENGTH_SHORT).show();
        }
    }
}