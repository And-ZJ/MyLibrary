package com.andzj.mylibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andzj.mylibrary.R;

import java.util.List;

/**
 * Created by zj on 2016/10/6.
 */

public class SearchHistoryListAdapter extends ArrayAdapter<String>
{
    private int resourceId;
    public SearchHistoryListAdapter(Context context, int textViewResourceId, List<String> objects)
    {
        super(context,textViewResourceId,objects);
        this.resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String str = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.historyView = (TextView) view.findViewById(R.id.history_view);
            //viewHolder.copyToEditBtn = (ImageButton) view.findViewById(R.id.copy_to_edit_btn);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.historyView.setText(str);
        return view;
    }

    private class ViewHolder
    {
        private TextView historyView;
        //private ImageButton copyToEditBtn;
    }
}
