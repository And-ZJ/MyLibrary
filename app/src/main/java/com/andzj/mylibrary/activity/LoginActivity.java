package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.db.AccountDatabaseHelper;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.Base64Utils;
import com.andzj.mylibrary.util.Encrypter;
import com.andzj.mylibrary.util.MyLog;
import com.andzj.mylibrary.util.RSAUtils;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by zj on 2016/7/20.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener
{


    private EditText accountEdit;
    private EditText passwordEdit;
    private CheckBox rememberPasswordCheck;
    private TextView rememberPasswordView;
    private CheckBox autoLoginCheck;
    private TextView autoLoginView;
    private Button forgotPasswordBtn;
    private Button loginBtn;
    private Button toRegisterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_login);

        accountEdit = (EditText) findViewById(R.id.login_account_edit);
        passwordEdit = (EditText) findViewById(R.id.login_password_edit);
        rememberPasswordCheck = (CheckBox) findViewById(R.id.remember_password_check);
        rememberPasswordCheck.setOnClickListener(this);
        rememberPasswordCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoLoginCheck.setEnabled(isChecked);
            }
        });
        rememberPasswordView = (TextView) findViewById(R.id.remember_password_view);
        rememberPasswordView.setOnClickListener(this);
        autoLoginCheck = (CheckBox)findViewById(R.id.auto_login_check);
        autoLoginView = (TextView) findViewById(R.id.auto_login_view);
        autoLoginView.setOnClickListener(this);
        forgotPasswordBtn = (Button) findViewById(R.id.forgot_password_btn);
        forgotPasswordBtn.setOnClickListener(this);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        toRegisterBtn = (Button) findViewById(R.id.to_register_btn);
        toRegisterBtn.setOnClickListener(this);

        SharedPreferences evidencePref = getSharedPreferences("evidence",MODE_PRIVATE);

        String savedAccount = evidencePref.getString("saved_account","");
        Boolean rememberPassword = evidencePref.getBoolean("remember_password",false);

        if (!"".equals(savedAccount))
        {
            //解密
            try
            {
                InputStream inPrivate = getResources().getAssets().open("pkcs8_rsa_private_key.pem");
                PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
                byte[] accountDecryptByte = RSAUtils.decryptData(Base64Utils.decode(savedAccount), privateKey);
                if (accountDecryptByte != null && accountDecryptByte.length > 0)
                {
                    accountEdit.setText(new String(accountDecryptByte));
                }
                if (rememberPassword)
                {
                    String savedPassword = evidencePref.getString("saved_password", "");
                    if (!"".equals(savedPassword))
                    {
                        rememberPasswordCheck.setChecked(true);
                        byte[] passwordDecryptByte = RSAUtils.decryptData(Base64Utils.decode(savedPassword), privateKey);
                        if (passwordDecryptByte != null && passwordDecryptByte.length > 0)
                        {
                            passwordEdit.setText(new String(passwordDecryptByte));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    String accountStr = data.getStringExtra("account");
                    String passwordStr = data.getStringExtra("password");
                    rememberPasswordCheck.setChecked(true);
                    if (accountStr != null && !"".equals(accountStr))
                    {
                        accountEdit.setText(accountStr);
                        passwordEdit.setText(passwordStr);
                    }
                }
                break;
            default:break;
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_LOGIN:
                    JSONObject jsonObject = JSON.parseObject((String)message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_LOGIN) + info,false);
                    if ("LoginSuccess".equals(info))
                    {
                        try
                        {
                            InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
                            PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);

                            byte[] accountEncryptByte = RSAUtils.encryptData(accountEdit.getText().toString().getBytes(), publicKey);
                            String accountAfterEncrypt = Base64Utils.encode(accountEncryptByte);
                            byte[] passwordEncryptByte = RSAUtils.encryptData(passwordEdit.getText().toString().getBytes(), publicKey);
                            String passwordAfterEncrypt = Base64Utils.encode(passwordEncryptByte);

                            SharedPreferences.Editor evidenceEditor = getSharedPreferences("evidence", MODE_PRIVATE).edit();
                            evidenceEditor.clear();
                            evidenceEditor.apply();
                            evidenceEditor.putString("saved_account",accountAfterEncrypt);
                            evidenceEditor.putBoolean("remember_password",rememberPasswordCheck.isChecked());
                            if (rememberPasswordCheck.isChecked())
                            {
                                evidenceEditor.putString("saved_password",passwordAfterEncrypt);
                                //evidenceEditor.putBoolean("login_state",true);
                            }
                            evidenceEditor.putBoolean("auto_login",autoLoginCheck.isChecked());
                            evidenceEditor.apply();

                            UserAccount userAccount = jsonObject.getObject("data",UserAccount.class);
                            MainActivity.setUserAccount(userAccount);
                            MainActivity.setLoginState(true);
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        finish();
                    }
                    else if ("PasswordWrong".equals(info))
                    {
                        Toast.makeText(LoginActivity.this, "密码错误,忘记密码了?", Toast.LENGTH_SHORT).show();
                    }
                    else if ("AccountNotExisted".equals(info))
                    {
                        Toast.makeText(LoginActivity.this, "没有此帐号,需要注册?", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(LoginActivity.this,"网络错误:" + e,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.remember_password_check:
                if (rememberPasswordCheck.isChecked())
                {
                    autoLoginCheck.setChecked(false);
                    autoLoginCheck.setEnabled(true);
                }
                else
                {
                    autoLoginCheck.setEnabled(false);
                    autoLoginCheck.setChecked(false);
                }
                break;
            case R.id.remember_password_view:
                rememberPasswordCheck.setChecked(!rememberPasswordCheck.isChecked());
                if (rememberPasswordCheck.isChecked())
                {
                    autoLoginCheck.setChecked(false);
                    autoLoginCheck.setEnabled(true);
                }
                else
                {
                    autoLoginCheck.setEnabled(false);
                    autoLoginCheck.setChecked(false);
                }
                break;
            case R.id.auto_login_view:
                autoLoginCheck.setChecked(rememberPasswordCheck.isChecked() && autoLoginCheck.isEnabled() && !autoLoginCheck.isChecked());
                break;
            case R.id.forgot_password_btn:
                Toast.makeText(LoginActivity.this,"请联系管理员以获得帮助,如有不便请见谅",Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_btn:
                if (accountEdit.getText() == null || "".equals(accountEdit.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"帐号不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (passwordEdit.getText() == null || "".equals(passwordEdit.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (!MyNetwork.checkNetworkAvailable())//检查网络
                {
                    Toast.makeText(LoginActivity.this,"无可用网络",Toast.LENGTH_SHORT).show();
                }
                else //登录
                {
                    String accountName = accountEdit.getText().toString();
                    String password = passwordEdit.getText().toString();
                    String passwordMD5 = Encrypter.GetMD5Code(password);

                    String s = "account_name=" + accountName + "&password_md5=" + passwordMD5;
                    MyNetwork.createHttpConnect(MyNetwork.Address_Login,s,MyNetwork.NET_LOGIN,handler);
//                    HttpUtil.sendHttpRequest(MyNetwork.Address_Server + MyNetwork.Address_Login, s, new HttpCallbackListener() {
//                        @Override
//                        public void onFinish(String response) {
//                            Message message = new Message();
//                            message.what = MyNetwork.NET_LOGIN;
//                            message.obj = response;
//                            handler.sendMessage(message);
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Message message = new Message();
//                            message.what = MyNetwork.NET_ERROR;
//                            message.obj = e.getMessage();
//                            handler.sendMessage(message);
//                        }
//                    });

                }
                break;
            case R.id.to_register_btn:
                //RegisterActivity.actionStart(LoginActivity.this,accountEdit.getText().toString(),passwordEdit.getText().toString());
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                intent.putExtra("account",accountEdit.getText().toString());
                intent.putExtra("password",passwordEdit.getText().toString());
                startActivityForResult(intent,1);
                break;

        }
    }

//    public static LoginAccount getUserAccount()
//    {
//        if (userAccount != null)
//        {
//            return userAccount;
//        }
//        else
//        {
//            Toast.makeText(MyApplication.getContext(),"程序出错 1",Toast.LENGTH_SHORT).show();
//            return new LoginAccount("","");
//        }
//    }


    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

//
//    public static void actionStart(Context context,String account,String password)
//    {
//        Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra("account",account);
//        intent.putExtra("password",password);
//        context.startActivity(intent);
//    }
}
