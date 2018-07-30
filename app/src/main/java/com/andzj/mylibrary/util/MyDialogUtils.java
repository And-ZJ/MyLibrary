package com.andzj.mylibrary.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by zj on 2016/11/23.
 */

public class MyDialogUtils
{
    public static AlertDialog.Builder alertDialogShow(Context context, String title, String message, String positiveStr, String negativeStr, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(positiveStr,positiveListener);
        alertDialog.setNegativeButton(negativeStr,negativeListener);
        alertDialog.show();
        return alertDialog;
    }

    public static ProgressDialog progressDialogShow(Context context, String title, String message, boolean cancelable, boolean touchOutside, DialogInterface.OnCancelListener cancelListener)
    {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(touchOutside);
        progressDialog.setOnCancelListener(cancelListener);
        progressDialog.show();
        return progressDialog;
    }
}
