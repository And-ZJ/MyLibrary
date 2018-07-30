package com.andzj.mylibrary.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by zj on 2016/11/29.
 */

public class ImageResize {

    public static final String TAG = "ImageResize";

    public ImageResize(){};

    public Bitmap decodeSampledBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);

        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resId,options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd,int reqWidth,int reqHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight)
    {
        if (reqWidth == 0 || reqHeight == 0)
        {
            return 1;
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        MyLog.d(TAG,"origin,w=" + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            final int halfHeight = height/2;
            final int halfWidth = width/2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth/inSampleSize >= reqWidth))
            {
                inSampleSize *=2;
            }
        }

        MyLog.d(TAG,"simpleSize:" + inSampleSize);
        return inSampleSize;
    }



}
