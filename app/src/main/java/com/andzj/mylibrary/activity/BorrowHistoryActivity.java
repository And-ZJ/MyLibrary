package com.andzj.mylibrary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.adapter.BorrowHistoryMsgListAdapter;
import com.andzj.mylibrary.bean.BorrowHistoryInformation;
import com.andzj.mylibrary.bean.BorrowHistoryResult;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.MyDialogUtils;
import com.andzj.mylibrary.util.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2016/11/23.
 */

public class BorrowHistoryActivity extends BaseActivity {

    private LinearLayout borrowHistoryNumberLayout;
    private TextView borrowHistoryNumber;
    private TextView noBorrowHistoryMsgHintView;
    private ListView borrowHistoryMsgListView;

    private BorrowHistoryMsgListAdapter borrowHistoryMsgListAdapter;
    private List<BorrowHistoryInformation> borrowHistoryInformationList = new ArrayList<>();

    private String accountName;

    private ProgressDialog progressDialog;
    private boolean cancelLoad = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_borrow_history_msg);


        borrowHistoryNumberLayout = (LinearLayout) findViewById(R.id.borrow_history_number_layout);
        borrowHistoryNumber = (TextView) findViewById(R.id.borrow_history_number);
        noBorrowHistoryMsgHintView = (TextView) findViewById(R.id.no_borrow_history_msg_hint_view);
        borrowHistoryMsgListView = (ListView) findViewById(R.id.borrow_history_msg_list_view);
        borrowHistoryMsgListView.setEmptyView(noBorrowHistoryMsgHintView);

        borrowHistoryMsgListAdapter = new BorrowHistoryMsgListAdapter(BorrowHistoryActivity.this,R.layout.lay_borrow_history_msg_item,borrowHistoryInformationList);
        borrowHistoryMsgListView.setAdapter(borrowHistoryMsgListAdapter);

        accountName = getIntent().getStringExtra("account_name");

        MyNetwork.createHttpConnect(MyNetwork.Address_Search_Borrow_History,"mode=account&search_words=" + accountName,MyNetwork.NET_SEARCH_BORROW_HISTORY,handler);
        progressDialog = MyDialogUtils.progressDialogShow(BorrowHistoryActivity.this, "正在加载", "请稍候...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelLoad = true;
            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_SEARCH_BORROW_HISTORY:
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    if (!cancelLoad)
                    {

                        BorrowHistoryResult borrowHistoryResult = JSON.parseObject((String)message.obj,BorrowHistoryResult.class);
                        String info = borrowHistoryResult.getInfo();
                        if ("Match".equals(info))
                        {
                            borrowHistoryNumberLayout.setVisibility(View.VISIBLE);
                            borrowHistoryNumber.setText(String.valueOf(borrowHistoryResult.getData().size()));
                            borrowHistoryInformationList.addAll(borrowHistoryResult.getData());
                            borrowHistoryMsgListAdapter.notifyDataSetChanged();
                        }
                        else if ("NotMatch".equals(info))
                        {
                            borrowHistoryNumberLayout.setVisibility(View.GONE);
                        }
                    }

                    break;
                }
                case MyNetwork.NET_ERROR:
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    String e = (String) message.obj;
                    MyLog.e("NET_Error", e, false);
                    Toast.makeText(BorrowHistoryActivity.this, "网络错误:" + e, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    public static void actionStart(Context context,String accountName)
    {
        Intent intent = new Intent(context,BorrowHistoryActivity.class);
        intent.putExtra("account_name",accountName);
        context.startActivity(intent);
    }
}
