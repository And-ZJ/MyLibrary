package com.andzj.mylibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.ScoreInformation;
import com.andzj.mylibrary.view.HeightListView;

import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2016/11/18.
 */

public class ScoreMsgListAdapter extends ArrayAdapter<ScoreInformation> {
    private int resourceId;
    private Context mContext;
    private int bookManageMode;
    private List<ScoreInformation> objects;
    //private Map<String,List<CommentInformation>> commentMap;
    private Map<String,CommentContentMsgListAdapter> adapterMap;


    public ScoreMsgListAdapter(Context context, int textViewResourceId, List<ScoreInformation> objects, int bookManageMode, Map<String,CommentContentMsgListAdapter> adapterMap) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
        this.mContext = context;
        this.bookManageMode = bookManageMode;
        this.objects = objects;
        this.adapterMap = adapterMap;


    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ScoreInformation scoreInformation = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.commentIndicatorView = (TextView) view.findViewById(R.id.comment_indicator_view);
            viewHolder.bookScoreBar = (RatingBar) view.findViewById(R.id.book_score_bar);
            viewHolder.commentContentListView = (HeightListView) view.findViewById(R.id.comment_content_list_view);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.commentIndicatorView.setText(scoreInformation.getScoreAccountName());
        viewHolder.bookScoreBar.setRating(scoreInformation.getCommentScore().floatValue());
        //List<CommentInformation> commentInformationList = commentMap.get(scoreInformation.getScoreAccountName());
        CommentContentMsgListAdapter commentContentMsgListAdapter = adapterMap.get(scoreInformation.getScoreAccountName());
        if (commentContentMsgListAdapter == null)
        {
            //viewHolder.commentContentListView.setVisibility(View.GONE);
            viewHolder.commentContentListView.setAdapter(null);
        }
        else
        {
            viewHolder.commentContentListView.setAdapter(commentContentMsgListAdapter);
            //commentContentMsgListAdapter.notifyDataSetChanged();
        }
//        if (commentInformationList == null)
//        {
//            viewHolder.commentContentListView.setVisibility(View.GONE);
//        }
//        else
//        {
//            AccountCommentContentMsgListAdapter commentContentMsgListAdapter = new AccountCommentContentMsgListAdapter(mContext,R.layout.lay_comment_content_msg_item,commentInformationList,0);
//            viewHolder.commentContentListView.setAdapter(commentContentMsgListAdapter);
//            commentContentMsgListAdapter.notifyDataSetChanged();
//
//        }
        return view;
    }

    public class ViewHolder
    {
        private TextView commentIndicatorView;
        private RatingBar bookScoreBar;
        private HeightListView commentContentListView;
    }
}