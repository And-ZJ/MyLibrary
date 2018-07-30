package com.andzj.mylibrary.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zj on 2016/7/19.
 */
public class MyViewPagerAdapter extends PagerAdapter
{
    List<View> viewLists;

    public MyViewPagerAdapter(List<View> lists)
    {
        viewLists = lists;
    }

     @Override
     public int getCount()
     {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0,Object arg1)
    {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView(viewLists.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        container.addView(viewLists.get(position));

        return  viewLists.get(position);
    }
}
