package com.andzj.mylibrary.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.activity.AddCommentActivity;
import com.andzj.mylibrary.activity.BookDetailActivity;
import com.andzj.mylibrary.activity.CommentActivity;
import com.andzj.mylibrary.bean.BorrowInformation;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.MyDialogUtils;

import java.util.List;

/**
 * Created by zj on 2016/9/28.
 */

public class BorrowMsgListAdapter extends ArrayAdapter<BorrowInformation> implements View.OnClickListener
{
    private int resourceId;
    private Context mContext;
    private int bookDetailMode;
    private List<BorrowInformation> objects;
    private Handler mHandler;

    private ProgressDialog progressDialog;

    public BorrowMsgListAdapter(Context context, int textViewResourceId, List<BorrowInformation> objects, Handler handler,ProgressDialog progressDialog, int bookDetailMode)
    {
        super(context,textViewResourceId,objects);
        this.mContext = context;
        this.resourceId = textViewResourceId;
        this.bookDetailMode = bookDetailMode;
        this.objects = objects;
        this.mHandler = handler;
        this.progressDialog = progressDialog;

    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BorrowInformation borrowInformation = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null)
        {
            view = LayoutInflater.from(mContext).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.bookNameTableRow = (TableRow) view.findViewById(R.id.book_name_table_row);
            viewHolder.bookIsbnTableRow = (TableRow) view.findViewById(R.id.book_isbn_table_row);
            viewHolder.borrowAccountNameTableRow = (TableRow)view.findViewById(R.id.borrow_account_name_table_row);

            viewHolder.bookNameView = (TextView) view.findViewById(R.id.book_name_view);
            viewHolder.bookIsbnView = (TextView) view.findViewById(R.id.book_isbn_view);
            viewHolder.borrowAccountNameView = (TextView) view.findViewById(R.id.borrow_account_name_view);
            viewHolder.borrowTimeView = (TextView) view.findViewById(R.id.borrow_time_view);
            viewHolder.returnTimeView = (TextView) view.findViewById(R.id.return_time_view);
            viewHolder.borrowStateView = (TextView) view.findViewById(R.id.borrow_state_view);

            viewHolder.bookOperateLayout = (LinearLayout) view.findViewById(R.id.book_operate_layout);
            viewHolder.commentBookBtn = (Button) view.findViewById(R.id.comment_book_btn);
            viewHolder.returnBookBtn = (Button) view.findViewById(R.id.return_book_btn);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (bookDetailMode == BookDetailActivity.MODE_AllMsg)
        {
            viewHolder.bookNameTableRow.setVisibility(View.GONE);
            viewHolder.bookIsbnTableRow.setVisibility(View.GONE);
            viewHolder.borrowAccountNameTableRow.setVisibility(View.VISIBLE);
            viewHolder.bookOperateLayout.setVisibility(View.GONE);
        }
        else if(bookDetailMode == BookDetailActivity.MODE_BorrowMsg)
        {
            viewHolder.bookNameTableRow.setVisibility(View.GONE);
            viewHolder.bookIsbnTableRow.setVisibility(View.VISIBLE);
            viewHolder.bookIsbnView.setText(borrowInformation.getBookIsbn());
            //viewHolder.bookIsbnView.setTag(borrowInformation.getBookIsbn());
            viewHolder.bookIsbnView.setOnClickListener(this);
            viewHolder.borrowAccountNameTableRow.setVisibility(View.GONE);
            viewHolder.bookOperateLayout.setVisibility(View.VISIBLE);
            viewHolder.commentBookBtn.setVisibility(View.VISIBLE);
            viewHolder.commentBookBtn.setTag(borrowInformation.getBookIsbn());
            viewHolder.commentBookBtn.setOnClickListener(this);
            viewHolder.returnBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出确认的dialog
                    MyDialogUtils.alertDialogShow(mContext,"警告", "确认归还吗?", "确认归还", "取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //归还
                                    progressDialogShow("正在执行归还操作","请稍候...");
                                    MyNetwork.createHttpConnect(MyNetwork.Address_Return_Book,"borrow_id="+borrowInformation.getBorrowId(),MyNetwork.NET_RETURN_BOOK,mHandler);
                                }
                            },null);
                }
            });
        }
        viewHolder.borrowAccountNameView.setText(borrowInformation.getBorrowAccountName());
        viewHolder.borrowTimeView.setText(borrowInformation.getBorrowTime());
        viewHolder.returnTimeView.setText(borrowInformation.getReturnTime());
        viewHolder.borrowStateView.setText(borrowInformation.getBorrowState());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.book_isbn_view:
                TextView textView = (TextView) v;
                BookDetailActivity.actionStart(mContext,textView.getText().toString());
                break;
            case R.id.comment_book_btn:
                String bookIsbn = (String) v.getTag();
                AddCommentActivity.actionStart(mContext,bookIsbn);
            default:
                break;
        }
    }

    private class ViewHolder
    {
        TableRow bookNameTableRow;
        TableRow bookIsbnTableRow;
        TableRow borrowAccountNameTableRow;

        TextView bookNameView;
        TextView bookIsbnView;
        TextView borrowAccountNameView;
        TextView borrowTimeView;
        TextView returnTimeView;
        TextView borrowStateView;

        LinearLayout bookOperateLayout;
        Button commentBookBtn;
        Button returnBookBtn;
    }

    private void progressDialogShow(String title,String message)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);//借阅/评论等操作不可取消(因为数据库那边可能添加上了)
        progressDialog.show();
    }
}

