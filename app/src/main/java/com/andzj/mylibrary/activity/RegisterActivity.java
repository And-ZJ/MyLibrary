package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.model.MainTitleLayout;
import com.andzj.mylibrary.net.HttpCallbackListener;
import com.andzj.mylibrary.net.HttpUtil;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.Base64Utils;
import com.andzj.mylibrary.util.MyLog;
import com.andzj.mylibrary.util.RSAUtils;

import java.io.InputStream;
import java.security.PublicKey;

/**
 * Created by zj on 2016/7/21.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener
{


    private String accountStr;
    private String passwordStr;

    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText passwordEdit2;
    private TextView passwordHint;
    private EditText nicknameEdit;
    private EditText bindStudentAccountEdit;
    private CheckBox agreeAgreementCheck;
    private Button agreeAgreementBtn;

    private Button registerBtn;

    private MainTitleLayout titleLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_register);

        accountStr = getIntent().getStringExtra("account");
        passwordStr = getIntent().getStringExtra("password");

        accountEdit = (EditText) findViewById(R.id.register_account_edit);
        passwordEdit = (EditText) findViewById(R.id.register_password_edit);
        passwordEdit2 = (EditText) findViewById(R.id.register_password2_edit);
        passwordHint = (TextView) findViewById(R.id.password_not_match_hint_view);
        nicknameEdit = (EditText) findViewById(R.id.register_nickname_edit);
        bindStudentAccountEdit = (EditText) findViewById(R.id.register_student_account_edit);
        agreeAgreementCheck = (CheckBox) findViewById(R.id.agree_agreement_check);
        agreeAgreementCheck.setOnClickListener(this);
        agreeAgreementCheck.setChecked(true);
        agreeAgreementBtn = (Button) findViewById(R.id.agree_agreement_btn);
        agreeAgreementBtn.setOnClickListener(this);

        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);


        passwordEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //MyLog.d("RegisterActivity","变化前所有字符:"+ s + ";字符起始位置:" + start + ";变化前总字节数:" + count + ";变化后总字节数:" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //MyLog.d("RegisterActivity","变化后所有字符:"+ s + ";字符起始位置:" + start + ";变化前总字节数:" + before + ";变化后总字节数:" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //MyLog.d("RegisterActivity","变化后所有字符:"+ s );
                if (s == null || "".equals(s.toString()))
                {
                    passwordHint.setVisibility(View.GONE);
                }
                else if (s.toString().equals(passwordEdit.getText().toString()))
                {
                    passwordHint.setVisibility(View.GONE);
                }
                else
                {
                    passwordHint.setVisibility(View.VISIBLE);
                }
            }
        });

        titleLayout = (MainTitleLayout) findViewById(R.id.register_title);

        titleLayout.setMoreBtnOnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RegisterActivity.this.finish();
            }
        });

        if (accountStr != null && !"".equals(accountStr))
        {
            accountEdit.setText(accountStr);
            passwordEdit.setText(passwordStr);
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_REGISTER:
                    JSONObject jsonObject = JSON.parseObject((String)message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_REGISTER) + info,false);
                    //UserAccount userAccount = jsonObject.getObject("data",UserAccount.class);
                    //Toast.makeText(RegisterActivity.this,info,Toast.LENGTH_SHORT).show();
                    if ("AddSuccess".equals(info))
                    {
                        try
                        {
                            SharedPreferences.Editor evidenceEditor = getSharedPreferences("evidence",MODE_PRIVATE).edit();
                            String account = accountEdit.getText().toString();
                            String password = passwordEdit.getText().toString();

                            InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
                            PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);

                            byte[] accountEncryptByte = RSAUtils.encryptData(account.getBytes(),publicKey);
                            String accountAfterEncrypt = Base64Utils.encode(accountEncryptByte);

                            byte[] passwordEncryptByte = RSAUtils.encryptData(password.getBytes(),publicKey);
                            String passwordAfterEncrypt = Base64Utils.encode(passwordEncryptByte);

                            evidenceEditor.putString("savedAccount",accountAfterEncrypt);
                            evidenceEditor.putString("savedPassword",passwordAfterEncrypt);
                            evidenceEditor.putBoolean("rememberPassword",true);
                            evidenceEditor.apply();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Toast.makeText(RegisterActivity.this,"注册成功,感谢您对本软件的信赖,请登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("account",accountEdit.getText().toString());
                        intent.putExtra("password",passwordEdit.getText().toString());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    else if ("AccountExisted".equals(info))
                    {
                        Toast.makeText(RegisterActivity.this,"此帐号已被注册,请检查书写是否正确,或直接去登陆,或点击忘记密码尝试找回",Toast.LENGTH_SHORT).show();
                    }
                    else if ("StudentFormatError".equals(info))
                    {
                        Toast.makeText(RegisterActivity.this,"学号格式错误,请输入纯数字",Toast.LENGTH_SHORT).show();
                    }
                    else if ("AccountFormatError".equals(info))
                    {
                        Toast.makeText(RegisterActivity.this,"帐号格式错误,请输入手机号进行注册",Toast.LENGTH_SHORT).show();
                    }
                    else if ("AddError".equals(info))
                    {
                        Toast.makeText(RegisterActivity.this,"注册过程中出现异常,请再试一次,若一直出现,请联系管理员",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(RegisterActivity.this,"网络错误:" + e,Toast.LENGTH_SHORT).show();
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
            case R.id.agree_agreement_btn:
                TextViewDialogActivity.actionStart(this,TextViewDialogActivity.Category_user_agreement,null,"已阅读",null);
                break;
            case R.id.agree_agreement_check:
                registerBtn.setEnabled(agreeAgreementCheck.isChecked());
                break;
            case R.id.register_btn:
                if (accountEdit.getText() == null || "".equals(accountEdit.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (passwordEdit.getText() == null || "".equals(passwordEdit.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (passwordEdit2.getText() == null || "".equals(passwordEdit2.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this,"请再次输入密码",Toast.LENGTH_SHORT).show();
                }
                else if (!passwordEdit.getText().toString().equals(passwordEdit2.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this,"两次密码需一致,请重新输入",Toast.LENGTH_SHORT).show();
                }
                else if (nicknameEdit.getText() == null || "".equals(nicknameEdit.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (bindStudentAccountEdit.getText() == null || "".equals(bindStudentAccountEdit.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this,"请绑定您的学号,以便于认证您的身份和管理",Toast.LENGTH_SHORT).show();

                }
                else if (!agreeAgreementCheck.isChecked())
                {
                    Toast.makeText(RegisterActivity.this,"请阅读并同意《用户协议》",Toast.LENGTH_SHORT).show();
                }
                else if (!MyNetwork.checkNetworkAvailable())//检查网络
                {
                    Toast.makeText(RegisterActivity.this,"无可用网络",Toast.LENGTH_SHORT).show();
                }
                else //进行注册
                {
                    String accountName = accountEdit.getText().toString();
                    String password = passwordEdit.getText().toString();
                    String bindStudentAccount = bindStudentAccountEdit.getText().toString();
                    String nickname = nicknameEdit.getText().toString();

                    String s = "account_name=" + accountName + "&password=" + password + "&bind_student_account=" +bindStudentAccount + "&nickname=" + nickname;
                    MyNetwork.createHttpConnect(MyNetwork.Address_Register, s,MyNetwork.NET_REGISTER,handler);
                }
                break;
            default:
                break;
        }
    }

    public static void actionStart(Context context,String account,String password)
    {
        Intent registerIntent = new Intent(context,RegisterActivity.class);
        registerIntent.putExtra("account",account);
        registerIntent.putExtra("password",password);
        context.startActivity(registerIntent);
    }

    public static void actionStart(Context context)
    {
        Intent registerIntent = new Intent(context,RegisterActivity.class);
        context.startActivity(registerIntent);
    }
}
