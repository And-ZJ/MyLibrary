package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.Encrypter;
import com.andzj.mylibrary.util.MyLog;

/**
 * Created by zj on 2016/11/16.
 */

public class OldPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText oldPasswordEdit;
    private Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_old_password);
        oldPasswordEdit = (EditText) findViewById(R.id.old_password_edit);
        nextBtn = (Button) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
    }

    Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_CHECK_PASSWORD:
                    JSONObject jsonObject = JSON.parseObject((String) message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_CHECK_PASSWORD) + info,false);
                    if ("PasswordRight".equals(info))
                    {
                        NewPasswordActivity.actionStart(OldPasswordActivity.this);
                    }
                    else if ("PasswordWrong".equals(info))
                    {
                        Toast.makeText(OldPasswordActivity.this, "密码错误,请重新输入", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(OldPasswordActivity.this, "返回数据出错(请联系管理员解决此问题):" + info, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(OldPasswordActivity.this,"网络错误:" + e,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.next_btn:
                if ("".equals(oldPasswordEdit.getText().toString()))
                {
                    Toast.makeText(OldPasswordActivity.this,"请填入现在的密码",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!MyNetwork.checkNetworkAvailable())
                {
                    Toast.makeText(OldPasswordActivity.this,"无可用网络",Toast.LENGTH_SHORT).show();
                    break;
                }
                UserAccount account = MainActivity.getUserAccount(OldPasswordActivity.this);
                if (account != null)
                {
                    String accountName = account.getAccountName();
                    String password = oldPasswordEdit.getText().toString();
                    String passwordMD5 = Encrypter.GetMD5Code(password);
                    String s = "account_name=" + accountName + "&password_md5=" + passwordMD5;
                    MyNetwork.createHttpConnect(MyNetwork.Address_Check_Password,s,MyNetwork.NET_CHECK_PASSWORD, handler);
                }
                else
                {
                    Toast.makeText(OldPasswordActivity.this,"抱歉,程序出错了",Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }
    }

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,OldPasswordActivity.class);
        context.startActivity(intent);
    }
}
