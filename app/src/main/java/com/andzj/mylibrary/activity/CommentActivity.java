package com.andzj.mylibrary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.CommentInformation;
import com.andzj.mylibrary.bean.CommentResult;
import com.andzj.mylibrary.bean.ScoreInformation;
import com.andzj.mylibrary.bean.ScoreResult;
import com.andzj.mylibrary.model.MainTitleLayout;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.MyDialogUtils;
import com.andzj.mylibrary.util.MyLog;
import com.andzj.mylibrary.view.HeightListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2016/11/24.
 */

public class CommentActivity extends BaseActivity {
    private MainTitleLayout mainTitleLayout;
    private LinearLayout commentNumberLayout;
    private TextView commentNumberView;
    private TextView noCommentMsgHintView;
    private ListView scoreMsgListView;

    private List<ScoreInformation> scoreMsgList = new ArrayList<>();
    private AccountScoreMsgListAdapter accountScoreMsgListAdapter;
    private Map<String,List<CommentInformation>> commentMap = new HashMap<>();
    Map<String, AccountCommentContentMsgListAdapter> adapterMap = new HashMap<>();
    List<CommentInformation> commentList = new ArrayList<>();


    private String accountName;

    private ProgressDialog progressDialog;
    private boolean load = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lay_comment_msg);
        accountName = getIntent().getStringExtra("account_name");
        mainTitleLayout = (MainTitleLayout) findViewById(R.id.title);
        commentNumberLayout = (LinearLayout) findViewById(R.id.comment_number_layout);
        commentNumberView = (TextView) findViewById(R.id.comment_number_view);
        noCommentMsgHintView = (TextView) findViewById(R.id.no_comment_msg_hint_view);
        scoreMsgListView = (ListView) findViewById(R.id.score_msg_list_view);
        scoreMsgListView.setEmptyView(noCommentMsgHintView);
        mainTitleLayout.setVisibility(View.VISIBLE);
        MyNetwork.createHttpConnect(MyNetwork.Address_Search_Comment,"mode=account&search_words="+accountName,MyNetwork.NET_SEARCH_COMMENT,handler);
        progressDialog = MyDialogUtils.progressDialogShow(CommentActivity.this, "正在加载", "请稍后", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                load = false;
            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_SEARCH_COMMENT:
                {
                    if (!load)
                    {
                        break;
                    }
                    CommentResult commentResult = JSON.parseObject((String) message.obj, CommentResult.class);
                    String info2 = commentResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_COMMENT) + info2, false);
                    if ("Match".equals(info2))
                    {
                        commentList.addAll(commentResult.getData());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (CommentInformation commentInformation : commentList)
                                {

                                    if (commentMap.get(commentInformation.getBookIsbn()) == null)
                                    {
                                        List<CommentInformation> newCommentList = new ArrayList<>();
                                        newCommentList.add(commentInformation);
                                        commentMap.put(commentInformation.getBookIsbn(), newCommentList);

                                        AccountCommentContentMsgListAdapter accountCommentContentMsgListAdapter = new AccountCommentContentMsgListAdapter(CommentActivity.this, R.layout.lay_comment_content_msg_item, newCommentList);
                                        adapterMap.put(commentInformation.getBookIsbn(), accountCommentContentMsgListAdapter);
                                    }
                                    else
                                    {
                                        commentMap.get(commentInformation.getBookIsbn()).add(commentInformation);
                                        adapterMap.get(commentInformation.getBookIsbn()).notifyDataSetChanged();
                                    }
                                }
                                Message message1 = new Message();
                                message1.what = 100000;
                                handler.sendMessage(message1);
                            }
                        }).start();
                    }
                    else if ("NotMatch".equals(info2))
                    {
                        if (!load)
                        {
                            break;
                        }
                        commentMap.clear();
                        accountScoreMsgListAdapter = new AccountScoreMsgListAdapter(CommentActivity.this, R.layout.lay_score_msg_item, scoreMsgList,adapterMap);
                        scoreMsgListView.setAdapter(accountScoreMsgListAdapter);
                        MyNetwork.createHttpConnect(MyNetwork.Address_Search_Score, "mode=account&search_words="+accountName, MyNetwork.NET_SEARCH_SCORE, handler);
                        accountScoreMsgListAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(CommentActivity.this, "返回数据出错(请联系管理员解决此问题):" + info2, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_SEARCH_SCORE:
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    if (!load)
                    {
                        break;
                    }
                    ScoreResult scoreResult = JSON.parseObject((String) message.obj, ScoreResult.class);
                    String info3 = scoreResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_SCORE) + info3, false);
                    if ("Match".equals(info3))
                    {
                        commentNumberView.setText(String.valueOf(scoreResult.getData().size()));
                        scoreMsgList.addAll(scoreResult.getData());
                        accountScoreMsgListAdapter.notifyDataSetChanged();
                    }
                    else if ("NotMatch".equals(info3))
                    {
                        scoreMsgList.clear();
                        accountScoreMsgListAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(CommentActivity.this, "返回数据出错(请联系管理员解决此问题):" + info3, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_ERROR:
                {
                    if (!load)
                    {
                        break;
                    }
                    String e = (String) message.obj;
                    MyLog.e("NET_Error", e, false);
                    Toast.makeText(CommentActivity.this, "网络错误:" + e, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 100000:
                {
                    if (!load)
                    {
                        break;
                    }
                    accountScoreMsgListAdapter = new AccountScoreMsgListAdapter(CommentActivity.this, R.layout.lay_score_msg_item, scoreMsgList,adapterMap);
                    scoreMsgListView.setAdapter(accountScoreMsgListAdapter);
                    MyNetwork.createHttpConnect(MyNetwork.Address_Search_Score,"mode=account&search_words="+accountName, MyNetwork.NET_SEARCH_SCORE, handler);
                    accountScoreMsgListAdapter.notifyDataSetChanged();
                    break;
                }
                case MyNetwork.NET_DELETE_COMMENT:
                {
                    JSONObject jsonObject = JSON.parseObject((String)message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_DELETE_COMMENT) + info, false);
                    if ("DeleteSuccess".equals(info))
                    {
                        Toast.makeText(CommentActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                    else if ("DeleteError".equals(info))
                    {
                        Toast.makeText(CommentActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(CommentActivity.this, "返回数据出错(请联系管理员解决此问题):" + info, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private class AccountScoreMsgListAdapter extends ArrayAdapter<ScoreInformation> implements View.OnClickListener
    {
        private int resourceId;
        private Context mContext;
        private List<ScoreInformation> objects;
        private Map<String,AccountCommentContentMsgListAdapter> adapterMap;


        public AccountScoreMsgListAdapter(Context context, int textViewResourceId, List<ScoreInformation> objects, Map<String,AccountCommentContentMsgListAdapter> adapterMap) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
            this.mContext = context;
            this.objects = objects;
            this.adapterMap = adapterMap;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ScoreInformation scoreInformation = getItem(position);
            View view;
            ScoreViewHolder scoreViewHolder;
            if (convertView == null)
            {
                view = LayoutInflater.from(mContext).inflate(resourceId, null);
                scoreViewHolder = new ScoreViewHolder();
                scoreViewHolder.bookIsbnView = (TextView) view.findViewById(R.id.comment_indicator_view);
                scoreViewHolder.bookScoreBar = (RatingBar) view.findViewById(R.id.book_score_bar);
                scoreViewHolder.commentContentListView = (HeightListView) view.findViewById(R.id.comment_content_list_view);
                view.setTag(scoreViewHolder);
            }
            else
            {
                view = convertView;
                scoreViewHolder = (ScoreViewHolder) view.getTag();
            }
            scoreViewHolder.bookIsbnView.setText(scoreInformation.getBookIsbn());
            scoreViewHolder.bookIsbnView.setOnClickListener(this);
            scoreViewHolder.bookScoreBar.setRating(scoreInformation.getCommentScore().floatValue());
            AccountCommentContentMsgListAdapter accountCommentContentMsgListAdapter = adapterMap.get(scoreInformation.getBookIsbn());
            if (accountCommentContentMsgListAdapter == null)
            {
                scoreViewHolder.commentContentListView.setAdapter(null);
            }
            else
            {
                scoreViewHolder.commentContentListView.setAdapter(accountCommentContentMsgListAdapter);
            }
            return view;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.comment_indicator_view:
                    TextView textView = (TextView) v;
                    BookDetailActivity.actionStart(mContext,textView.getText().toString());
                    break;
                default:
                    break;
            }
        }

        private class ScoreViewHolder
        {
            private TextView bookIsbnView;
            private RatingBar bookScoreBar;
            private HeightListView commentContentListView;
        }
    }

    public class AccountCommentContentMsgListAdapter extends ArrayAdapter<CommentInformation> implements View.OnClickListener{
        private int resourceId;
        private Context mContext;
        private List<CommentInformation> objects;

        public AccountCommentContentMsgListAdapter(Context context, int textViewResourceId, List<CommentInformation> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
            this.mContext = context;
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CommentInformation commentInformation = getItem(position);
            View view;
            ContentViewHolder contentViewHolder;
            if (convertView == null)
            {
                view = LayoutInflater.from(mContext).inflate(resourceId, null);
                contentViewHolder = new ContentViewHolder();
                contentViewHolder.commentContentView = (TextView) view.findViewById(R.id.comment_content_view);
                contentViewHolder.commentTimeView = (TextView) view.findViewById(R.id.comment_time_view);
                contentViewHolder.deleteContentView =(TextView) view.findViewById(R.id.delete_content_view);
                contentViewHolder.deleteContentView.setVisibility(View.VISIBLE);
                contentViewHolder.deleteContentView.setOnClickListener(this);
                view.setTag(contentViewHolder);
            }
            else
            {
                view = convertView;
                contentViewHolder = (ContentViewHolder) view.getTag();
            }
            contentViewHolder.commentContentView.setText(commentInformation.getCommentContent());
            contentViewHolder.commentTimeView.setText(commentInformation.getCommentTime());
            contentViewHolder.deleteContentView.setTag(commentInformation.getCommentId());
            return view;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.delete_content_view:
                    final Integer deleteId = (Integer) v.getTag();
                    //System.out.println("delete_click" + (Integer) v.getTag());
                    MyDialogUtils.alertDialogShow(CommentActivity.this, "警告", "确认删除吗?", "确认删除", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyNetwork.createHttpConnect(MyNetwork.Address_Delete_Comment, "comment_id=" + String.valueOf(deleteId), MyNetwork.NET_DELETE_COMMENT, handler);
                        }
                    },null);

                    break;
                default:
                    break;
            }
        }

        public class ContentViewHolder
        {
            private TextView commentContentView;
            private TextView commentTimeView;
            private TextView deleteContentView;
        }
    }

    public static void actionStart(Context context,String accountName)
    {
        Intent intent = new Intent(context,CommentActivity.class);
        intent.putExtra("account_name",accountName);
        context.startActivity(intent);
    }

}
