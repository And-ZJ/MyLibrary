package com.andzj.mylibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.activity.SearchBookActivity;
import com.andzj.mylibrary.bean.BookInformation;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.ImageLoader;

import java.util.List;

/**
 * Created by zj on 2016/9/26.
 */

public class BookItemListAdapter extends ArrayAdapter<BookInformation>
{
    public static final String Address_Image = MyNetwork.Address_Access_File;
    private int resourceId;
    private Context mContext;
    private ImageLoader mImageLoader;
    Bitmap mDefaultBitmap = null;

    public BookItemListAdapter(Context context , int textViewResourceId, List<BookInformation> objects, ImageLoader imageLoader)
    {
        super(context,textViewResourceId,objects);
        mContext = context;
        resourceId = textViewResourceId;
        mImageLoader = imageLoader;
        mDefaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_book_image);
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookInformation bookInformation = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView  == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.bookImageView = (ImageView) view.findViewById(R.id.book_image_view);
            viewHolder.bookNameView = (TextView) view.findViewById(R.id.book_name_view);
            viewHolder.bookScoreBar = (RatingBar) view.findViewById(R.id.book_score_bar);
            viewHolder.bookHintView = (TextView) view.findViewById(R.id.book_hint_view);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        ImageView imageView = viewHolder.bookImageView;
        String address = bookInformation.getBookImageAddress();
        if (address != null)
        {
            //final String tag = (String) imageView.getTag();
//            final String uri = Address_Image + address + bookInformation.getOperateTime();

//            if(!uri.equals(tag))
//            {
//                imageView.setImageBitmap(mDefaultBitmap);
//            }

            if (!SearchBookActivity.isScroll)
            {
                //imageView.setTag(uri);
                mImageLoader.bindBitmap(ImageLoader.getDownloadUrlWithTime(Address_Image + address,bookInformation.getOperateTime()),imageView,56,56);
            }
        }
        else
        {
            imageView.setImageBitmap(mDefaultBitmap);
        }



        //viewHolder.bookImageView.setImageBitmap(BookInformation.getBookImage(mContext));
        viewHolder.bookNameView.setText(bookInformation.getBookName());
        viewHolder.bookScoreBar.setRating(bookInformation.getBookAverageScore().floatValue());
        String bookHint = bookInformation.getBookAuthor() + " " + bookInformation.getBookPublishCompany() + " " + bookInformation.getBookKeyWords();
        viewHolder.bookHintView.setText(bookHint);
        return view;
    }

    class ViewHolder
    {
        ImageView bookImageView;
        TextView bookNameView;
        RatingBar bookScoreBar;
        TextView bookHintView;
    }
}
