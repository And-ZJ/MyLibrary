package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class NewPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText newPasswordEdit1;
    private EditText newPasswordEdit2;
    private TextView passwordHint;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_new_password);
        newPasswordEdit1 = (EditText) findViewById(R.id.new_password_edit_1);
        newPasswordEdit2 = (EditText) findViewById(R.id.new_password_edit_2);
        passwordHint = (TextView) findViewById(R.id.password_not_match_hint_view);

        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);



        newPasswordEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || "".equals(s.toString()))
                {
                    passwordHint.setVisibility(View.GONE);
                }
                else if (s.toString().equals(newPasswordEdit1.getText().toString()))
                {
                    passwordHint.setVisibility(View.GONE);
                }
                else
                {
                    passwordHint.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_UPDATE_PASSWORD:
                    JSONObject jsonObject = JSON.parseObject((String) message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_UPDATE_PASSWORD) + info,false);
                    if ("UpdateSuccess".equals(info))
                    {
                        Toast.makeText(NewPasswordActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                    }
                    else if ("UpdateError".equals(info))
                    {
                        Toast.makeText(NewPasswordActivity.this, "密码错误,请重新输入", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(NewPasswordActivity.this, "返回数据出错(请联系管理员解决此问题):" + info, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(NewPasswordActivity.this,"网络错误:" + e,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.submit_btn:
                if (newPasswordEdit1.getText() == null || "".equals(newPasswordEdit1.getText().toString()))
                {
                    Toast.makeText(NewPasswordActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (newPasswordEdit2.getText() == null || "".equals(newPasswordEdit2.getText().toString()))
                {
                    Toast.makeText(NewPasswordActivity.this,"请再次输入密码",Toast.LENGTH_SHORT).show();
                }
                else if (!newPasswordEdit1.getText().toString().equals(newPasswordEdit2.getText().toString()))
                {
                    Toast.makeText(NewPasswordActivity.this,"两次密码需一致,请重新输入",Toast.LENGTH_SHORT).show();
                }
                else if (!MyNetwork.checkNetworkAvailable())//检查网络
                {
                    Toast.makeText(NewPasswordActivity.this,"无可用网络",Toast.LENGTH_SHORT).show();
                }
                else //进行验证
                {
                    UserAccount account = MainActivity.getUserAccount(NewPasswordActivity.this);
                    if (account != null)
                    {
                        String accountName = account.getAccountName();
                        String password = newPasswordEdit1.getText().toString();
                        String passwordMD5 = Encrypter.GetMD5Code(password);
                        String s = "account_name=" + accountName + "&password_md5=" + passwordMD5;
                        MyNetwork.createHttpConnect(MyNetwork.Address_Update_Password,s,MyNetwork.NET_UPDATE_PASSWORD, handler);
                    }
                    else
                    {
                        Toast.makeText(NewPasswordActivity.this,"抱歉,程序出错了",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,NewPasswordActivity.class);
        context.startActivity(intent);
    }
}
