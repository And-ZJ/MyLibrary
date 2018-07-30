package com.andzj.mylibrary.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2016/7/18.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity)
    {
        if (!activities.contains(activity))
        {
            activities.add(activity);
        }

    }

    public static void removeActivity(Activity activity)
    {
        if (activities.contains(activity))
        {
            activities.remove(activity);
        }
    }

    public static void finishAll()
    {
        for (Activity activity : activities)
        {
            if (!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }
}
