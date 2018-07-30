package com.andzj.mylibrary.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.nfc.cardemulation.OffHostApduService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;

import com.andzj.mylibrary.util.MyApplication;
import com.andzj.mylibrary.util.MyLog;

import java.io.File;
import java.net.URLEncoder;
import java.util.Map;


/**
 * Created by zj on 2016/11/15.
 */

public class MyNetwork {


    public static final int NET_ERROR = 1000;
    public static final int NET_REGISTER = 1001;
    public static final int NET_LOGIN = 1002;
    public static final int NET_AUTO_LOGIN = 1003;
    public static final int NET_CHECK_PASSWORD = 1004;
    public static final int NET_UPDATE_PASSWORD = 1005;
    public static final int NET_SEARCH_BOOK = 1006;
    public static final int NET_SEARCH_BORROW = 1007;
    public static final int NET_SEARCH_COMMENT = 1008;
    public static final int NET_SEARCH_SCORE = 1009;
    public static final int NET_BORROW_BOOK = 1010;
    public static final int NET_BORROW_Again_BOOK = 1011;
    public static final int NET_RETURN_BOOK =1012;
    public static final int NET_COMMENT_BOOK = 1013;
    public static final int NET_SEARCH_BORROW_HISTORY = 1014;
    public static final int NET_DELETE_COMMENT = 1015;
    public static final int NET_UPDATE_ACCOUNT = 1016;
    public static final int NET_UPLOAD_IMAGE = 1017;


    public static final String Address_Server               = "http://192.168.253.1:8080/library/";
    public static final String Address_Register             = Address_Server + "registerUserAccountApp";
    public static final String Address_Login                = Address_Server + "userAccountLoginApp";
    public static final String Address_Auto_Login           = Address_Server + "userAccountAutoLoginApp";
    public static final String Address_Check_Password       = Address_Server + "checkUserPasswordApp";
    public static final String Address_Update_Password      = Address_Server + "updateUserPasswordApp";
    public static final String Address_Search_Book          = Address_Server + "searchBookApp";
    public static final String Address_Search_Borrow        = Address_Server + "searchBorrowApp";
    public static final String Address_Search_Comment       = Address_Server + "searchCommentApp";
    public static final String Address_Search_Score         = Address_Server + "searchScoreApp";
    public static final String Address_Borrow_Book          = Address_Server + "addBorrowApp";
    public static final String Address_Borrow_Again_Book    = Address_Server + "addBorrowAgainApp";
    public static final String Address_Return_Book          = Address_Server + "deleteBorrowApp";
    public static final String Address_Comment_Book         = Address_Server + "addCommentWithScoreApp";
    public static final String Address_Search_Borrow_History= Address_Server + "searchBorrowHistoryApp";
    public static final String Address_Delete_Comment       = Address_Server + "deleteCommentApp";
    public static final String Address_Update_Account       = Address_Server + "updateUserAccountApp";
    public static final String Address_Upload_Image         = Address_Server + "uploadUserImageApp";

    public static final String Address_Share_Book           = Address_Server + "WEB/ShareBook.html";

    public static final String Address_Access_File          = "http://192.168.253.1:8080/library_file/";



    public static void createHttpConnect(final String address,final String uploadStr,final int message_what,final Handler handler)
    {
        String s = UTF8_URL(uploadStr);
        MyLog.d(String.valueOf(message_what),address + "?" + s,false);
        HttpUtil.sendHttpRequest(address, s , new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message message = new Message();
                message.what = message_what;
                message.obj = response;
                handler.sendMessage(message);
            }
            @Override
            public void onError(Exception e) {
                Message message = new Message();
                message.what = MyNetwork.NET_ERROR;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }
        });
    }

    public static void uploadImage(final String address, final Map<String,String> map, File imageFile, final int message_what, final Handler handler)
    {
        HttpUtil.sendHttpImageFile(address, map , imageFile , new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message message = new Message();
                message.what = message_what;
                message.obj = response;
                handler.sendMessage(message);
            }
            @Override
            public void onError(Exception e) {
                Message message = new Message();
                message.what = MyNetwork.NET_ERROR;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }
        });
    }


    public static boolean checkNetworkAvailable()
    {
        Context context = MyApplication.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (NetworkInfo info:networkInfo)
                {
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkMobileNetWork()
    {
        Context context = MyApplication.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            NetworkInfo network = connectivityManager.getActiveNetworkInfo();
            if (network != null && network.getType() == ConnectivityManager.TYPE_MOBILE)
            {
               // MyLog.d("MyNetwork","type mobile");
                return true;
            }
        }
        //MyLog.d("MyNetwork","not type mobile");
        return false;
    }


    private static  String UTF8_URL(String uploadStr)
    {
        if (uploadStr == null || "".equals(uploadStr))
        {
            return null;
        }
        //MyLog.d("URL",uploadStr,false);
        try {
            StringBuffer url = new StringBuffer(uploadStr);
            StringBuffer utf8 = new StringBuffer();
            int position1;
            int position2;
            do
            {
                position1 = url.indexOf("=");
                if (position1 < 0)
                {
                    break;
                }
                utf8.append(url.substring(0, position1)).append("=");
                position2 = url.indexOf("&");
                if (position1 + 1 == position2)
                {
                    utf8.append("&");
                    url.delete(0,position2+1);
                }
                else if (position2 > 0)
                {
                    String s = url.substring(position1+1, position2);
                    utf8.append(URLEncoder.encode(s, "UTF-8")).append("&");
                    url.delete(0,position2+1);
                }
                else if (position1 == url.length()-1)
                {
                    break;
                }
                else
                {
                    String s = url.substring(position1 + 1);
                    utf8.append(URLEncoder.encode(s, "UTF-8"));
                }
            }while (position2 > 0);
            //MyLog.d("UTF",utf8.toString(),false);
            return utf8.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

//    @Deprecated
//    private static  void testUTF8()
//    {
//        String s1 = "account_name=18200197776&password_md5=f1290186a5d0b1ceab27f4e77c0c5d68";
//        String s2 = "book_isbn=12&comment_account_name=18200197776&comment_content=勉强&score=3.0";
//        String s3 = "book_isbn=12&comment_account_name=18200197776&score=3.0&comment_content=勉强";
//        String s4 = "book_isbn=12&comment_account_name=&comment_content=勉强&score=";
//        String s5 = "book_isbn=&comment_account_name=&comment_content=&score=3.0";
//        String s6 = "book_isbn=张建";
//        String s7 = "book_isbn=";
//        String s8 = "book_isbn=张&comment_account_name=建&comment_content=勉强&score=好的";
//
//        UTF8_URL(s1);
//        UTF8_URL(s2);
//        UTF8_URL(s3);
//        UTF8_URL(s4);
//        UTF8_URL(s5);
//        UTF8_URL(s6);
//        UTF8_URL(s7);
//        UTF8_URL(s8);
//    }

}
