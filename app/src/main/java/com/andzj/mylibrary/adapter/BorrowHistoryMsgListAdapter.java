package com.andzj.mylibrary.adapter;

import android.content.Context;
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
import com.andzj.mylibrary.bean.BorrowHistoryInformation;

import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2016/11/23.
 */

public class BorrowHistoryMsgListAdapter  extends ArrayAdapter<BorrowHistoryInformation> implements View.OnClickListener
{
    private int resourceId;
    private Context mContext;
    private List<BorrowHistoryInformation> objects;


    public BorrowHistoryMsgListAdapter(Context context, int textViewResourceId, List<BorrowHistoryInformation> objects)
    {
        super(context,textViewResourceId,objects);
        this.mContext = context;
        this.resourceId = textViewResourceId;
        this.objects = objects;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BorrowHistoryInformation borrowHistoryInformation = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null)
        {
            view = LayoutInflater.from(mContext).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.bookNameTableRow = (TableRow) view.findViewById(R.id.book_name_table_row);
            viewHolder.bookIsbnTableRow = (TableRow) view.findViewById(R.id.book_isbn_table_row);
            viewHolder.bookNameView = (TextView) view.findViewById(R.id.book_name_view);
            viewHolder.bookIsbnView = (TextView) view.findViewById(R.id.book_isbn_view);
            viewHolder.borrowTimeView = (TextView) view.findViewById(R.id.borrow_time_view);
            viewHolder.returnTimeView = (TextView) view.findViewById(R.id.return_time_view);
            viewHolder.borrowHistoryNotesView = (TextView) view.findViewById(R.id.borrow_history_notes_view);

            viewHolder.bookOperateLayout = (LinearLayout) view.findViewById(R.id.book_operate_layout);
            viewHolder.commentBookBtn = (Button) view.findViewById(R.id.comment_book_btn);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.commentBookBtn.setVisibility(View.VISIBLE);
        viewHolder.commentBookBtn.setOnClickListener(this);
        //List<String> strings = new ArrayList<>();
        //strings.add(borrowHistoryInformation.getBookIsbn());
        //strings.add(borrowHistoryInformation.getAccountName());
        ;
        viewHolder.commentBookBtn.setTag(borrowHistoryInformation.getBookIsbn());
        viewHolder.bookIsbnView.setText(borrowHistoryInformation.getBookIsbn());
        viewHolder.bookIsbnView.setOnClickListener(this);
        viewHolder.borrowTimeView.setText(borrowHistoryInformation.getBorrowTime());
        viewHolder.returnTimeView.setText(borrowHistoryInformation.getReturnTime());
        viewHolder.borrowHistoryNotesView.setText(borrowHistoryInformation.getHistoryNotes());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.comment_book_btn:
                String bookIsbn = (String) v.getTag();
                AddCommentActivity.actionStart(mContext,bookIsbn);
                break;
            case R.id.book_isbn_view:
                TextView textView = (TextView) v;
                BookDetailActivity.actionStart(mContext,textView.getText().toString());
                break;
            default:
                break;
        }
    }

    private class ViewHolder
    {
        TableRow bookNameTableRow;
        TableRow bookIsbnTableRow;

        TextView bookNameView;
        TextView bookIsbnView;
        TextView borrowTimeView;
        TextView returnTimeView;
        TextView borrowHistoryNotesView;

        LinearLayout bookOperateLayout;
        Button commentBookBtn;
    }
}

