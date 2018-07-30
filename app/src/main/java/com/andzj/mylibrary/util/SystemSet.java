package com.andzj.mylibrary.util;


import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zj on 2016/8/7.
 */
public class SystemSet
{
    private static  Context mContext = MyApplication.getContext();
    public void init()
    {
        SharedPreferences systemSetPref = mContext.getSharedPreferences("system_set",MODE_PRIVATE);
        autoReceivedPictureSwitch = systemSetPref.getBoolean("auto_received_picture",false);
        historyRecordOpen = systemSetPref.getBoolean("history_record",true);
    }


    private static  boolean autoReceivedPictureSwitch = false;

    public static boolean isAutoReceivedPictureSwitch() {
        return autoReceivedPictureSwitch;
    }

    public static void setAutoReceivedPictureSwitch(boolean autoReceivedPictureSwitch) {
        SharedPreferences.Editor systemSetEditor = mContext.getSharedPreferences("system_set",MODE_PRIVATE).edit();
        systemSetEditor.putBoolean("auto_received_picture",autoReceivedPictureSwitch);
        systemSetEditor.apply();
        SystemSet.autoReceivedPictureSwitch = autoReceivedPictureSwitch;
    }

    private static boolean historyRecordOpen = true;
    public static boolean isHistoryRecordOpen() {
        return historyRecordOpen;
    }
    public static void setHistoryRecordOpen(boolean historyRecordOpen) {
        SharedPreferences.Editor systemSetEditor = mContext.getSharedPreferences("system_set",MODE_PRIVATE).edit();
        systemSetEditor.putBoolean("history_record",historyRecordOpen);
        systemSetEditor.apply();
        SystemSet.historyRecordOpen = historyRecordOpen;
    }
}
