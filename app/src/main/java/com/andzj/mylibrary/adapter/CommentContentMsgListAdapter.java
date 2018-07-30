package com.andzj.mylibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.CommentInformation;

import java.util.List;

/**
 * Created by zj on 2016/11/18.
 */

public class CommentContentMsgListAdapter extends ArrayAdapter<CommentInformation> {
    private int resourceId;
    private Context mContext;
    private int bookManageMode;
    private List<CommentInformation> objects;

    public CommentContentMsgListAdapter(Context context, int textViewResourceId, List<CommentInformation> objects, int bookManageMode) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
        this.mContext = context;
        this.bookManageMode = bookManageMode;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommentInformation commentInformation = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.commentContentView = (TextView) view.findViewById(R.id.comment_content_view);
            viewHolder.commentTimeView = (TextView) view.findViewById(R.id.comment_time_view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.commentContentView.setText(commentInformation.getCommentContent());
        viewHolder.commentTimeView.setText(commentInformation.getCommentTime());
        return view;
    }

    public class ViewHolder
    {
        private TextView commentContentView;
        private TextView commentTimeView;
    }
}
