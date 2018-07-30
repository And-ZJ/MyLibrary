package com.andzj.mylibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.util.MyApplication;
import com.andzj.mylibrary.util.SystemSet;

/**
 * Created by zj on 2016/8/18.
 */
public class AccountDatabaseHelper extends SQLiteOpenHelper
{
    private static final int ACCOUNT_DATABASE_VERSION = 1;
    private static final String TABLE_USER_ACCOUNT = "UserAccount";
    private static final String DATABASE_USER_ACCOUNT = "Accounts.db";

    public static final String CREATE_ACCOUNT = "create table UserAccount ("
            + "id integer primary key autoincrement, "

            + "account_name         text, "

            + "register_time        text, "
            + "bind_student_account text, "
            + "nickname             text, "
            + "sex                  text, "
            + "image_str            text, "
            + "describe_words       text, "
            + "update_time          text, "

            + "is_user              integer, "
            + "is_friend            integer )";

    private Context mContext;

    public AccountDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {

    }

    public static void saveToDB(UserAccount account)
    {
        if (account == null)
        {
            return;
        }
        System.out.println(account.toString());
        AccountDatabaseHelper accountDBHelper = new AccountDatabaseHelper(
                MyApplication.getContext(),DATABASE_USER_ACCOUNT,null,ACCOUNT_DATABASE_VERSION);
        SQLiteDatabase db = accountDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("register_time",account.getRegisterTime());
        values.put("bind_student_account",account.getBindStudentAccount());
        values.put("nickname",account.getUserNickname());
        values.put("sex",account.getUserSex());
        values.put("image_str",account.getUserImageStr());
        values.put("describe_words",account.getUserDescribeWords());
        values.put("update_time",account.getUserUpdateTime());

        Cursor cursor = db.query(TABLE_USER_ACCOUNT,null,
                "account_name = ?",new String[] {account.getAccountName()},null,null,null);
        if ( cursor.getCount() == 0) //第一次存储进本地数据库
        {
            values.put("account_name",account.getAccountName());
            db.insert(TABLE_USER_ACCOUNT,null,values);
            values.clear();
            //MyLog.d("LoginAccount","注册完成",false);
        }
        else //发现已有数据,更改存储的信息
        {
            db.update(TABLE_USER_ACCOUNT,values,
                    "account_name = ?",new String[]{account.getAccountName()});
            values.clear();
        }
        cursor.close();
        accountDBHelper.close();
    }

    public static UserAccount getAccount(String accountName)
    {
        if (accountName == null)
        {
            return null;
        }
        AccountDatabaseHelper loadAccountDBHelper = new AccountDatabaseHelper(MyApplication.getContext(),
                DATABASE_USER_ACCOUNT,null,ACCOUNT_DATABASE_VERSION);
        SQLiteDatabase db = loadAccountDBHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER_ACCOUNT,null,
                "account_name = ?",new String[] {accountName},null,null,null);
        if ( cursor.getCount() == 0)
        {
            cursor.close();
            loadAccountDBHelper.close();
            return null;
        }
        else
        {
            if (cursor.moveToFirst())
            {
                int idDB = cursor.getInt(cursor.getColumnIndex("id"));
                String registerTimeDB = cursor.getString(cursor.getColumnIndex("register_time"));
                String bindStudentAccountDB = cursor.getString(cursor.getColumnIndex("bind_student_account"));
                String nickNameDB = cursor.getString(cursor.getColumnIndex("nickname"));
                String sexDB = cursor.getString(cursor.getColumnIndex("sex"));
                String imageStrDB = cursor.getString(cursor.getColumnIndex("image_str"));
                String describeWordsDB = cursor.getString(cursor.getColumnIndex("describe_words"));
                String updateTimeDB = cursor.getString(cursor.getColumnIndex("update_time"));

                cursor.close();
                loadAccountDBHelper.close();
                UserAccount account =  new UserAccount(idDB,accountName,registerTimeDB,bindStudentAccountDB,
                        imageStrDB,nickNameDB,sexDB,describeWordsDB,updateTimeDB);
                System.out.println(account.toString());
                return account;
            }
            cursor.close();
            loadAccountDBHelper.close();
            return null;
        }
    }

//    public static boolean isContain(String accountName)
//    {
//        AccountDatabaseHelper accountDBHelper = new AccountDatabaseHelper(MyApplication.getContext(),"Accounts.db",null,ACCOUNT_DATABASE_VERSION);
//        SQLiteDatabase db = accountDBHelper.getWritableDatabase();
//        Cursor cursor = db.queryBookByIsbn("Account",null,"account = ?",new String[] {accountName},null,null,null);
//
//    }

}
