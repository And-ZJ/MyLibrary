package com.andzj.mylibrary.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.db.AccountDatabaseHelper;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.MyDialogUtils;
import com.andzj.mylibrary.util.MyLog;

import java.nio.charset.Charset;
import java.util.concurrent.BrokenBarrierException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zj on 2016/11/30.
 */

public class EditAccountActivity extends BaseActivity implements View.OnClickListener{

    private EditText nicknameEdit;
    private RadioButton boyRadioBtn;
    private RadioButton girlRadioBtn;
    private RadioButton secretRadioBtn;
    private EditText bindStudentAccountEdit;
    private EditText describeWordsEdit;
    private TextView describeWordsHint;
    private Button saveBtn;
    private ProgressDialog progressDialog;
    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_edit_account);

        nicknameEdit = (EditText) findViewById(R.id.nickname_edit);
        boyRadioBtn = (RadioButton) findViewById(R.id.boy_radio_btn);
        girlRadioBtn = (RadioButton) findViewById(R.id.girl_radio_btn);
        secretRadioBtn = (RadioButton) findViewById(R.id.secret_radio_btn);
        bindStudentAccountEdit = (EditText) findViewById(R.id.bind_student_account_edit);
        describeWordsEdit = (EditText) findViewById(R.id.describe_words_edit);
        describeWordsEdit.setFilters(new InputFilter[]{new InputFilter() {
            String speChat="[</>]";
            Pattern pattern = Pattern.compile(speChat);
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find()) return "";
                else return null;
            }
        },new InputFilter.LengthFilter(200)});
        describeWordsHint = (TextView) findViewById(R.id.describe_words_hint);
        describeWordsHint.setText("不超过200个字符,不允许输入</>等字符");
        saveBtn = (Button) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        userAccount = getIntent().getParcelableExtra("account");

        nicknameEdit.setText(userAccount.getUserNickname());
        bindStudentAccountEdit.setText(userAccount.getBindStudentAccount());
        describeWordsEdit.setText(userAccount.getUserDescribeWords());
        if ("男".equals(userAccount.getUserSex()))
        {
            boyRadioBtn.setChecked(true);
            girlRadioBtn.setChecked(false);
            secretRadioBtn.setChecked(false);
        }
        else if ("女".equals(userAccount.getUserSex()))
        {
            //boyRadioBtn.setChecked(false);
            girlRadioBtn.setChecked(true);
            //secretRadioBtn.setChecked(false);
        }
        else if ("保密".equals(userAccount.getUserSex()))
        {
            //boyRadioBtn.setChecked(false);
            //girlRadioBtn.setChecked(false);
            secretRadioBtn.setChecked(true);
        }

    }

    public Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what)
            {
                case MyNetwork.NET_UPDATE_ACCOUNT:
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    JSONObject jsonObject = JSON.parseObject((String) message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.Address_Update_Account) + info, false);
                    if ("UpdateSuccess".equals(info))
                    {
                        Toast.makeText(EditAccountActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        UserAccount userAccount = jsonObject.getObject("account",UserAccount.class);
                        if (userAccount != null)
                        {
                            Intent intent = new Intent();
                            intent.putExtra("account",userAccount);
                            MainActivity.setUserAccount(userAccount);
                            setResult(RESULT_OK,intent);
                        }
                        else
                        {
                            MyLog.d("EditAccountActivity","can't get userAccount from update");
                            setResult(RESULT_CANCELED);
                        }
                        finish();
                    }
                    else if ("UpdateError".equals(info))
                    {
                        Toast.makeText(EditAccountActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(EditAccountActivity.this, "返回数据出错(请联系管理员解决此问题):" + info, Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                case MyNetwork.NET_ERROR:
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    String e = (String) message.obj;
                    MyLog.e("NET_Error", e, false);
                    Toast.makeText(EditAccountActivity.this, "网络错误:" + e, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save_btn:
                String nickname = nicknameEdit.getText().toString();
                String sex = (boyRadioBtn.isChecked()) ? "男" : ((girlRadioBtn.isChecked())? "女" : "保密");
                String bindStudentAccountName = bindStudentAccountEdit.getText().toString();
                String describeWords = describeWordsEdit.getText().toString();
                String s = "account_name=" +  userAccount.getAccountName() +
                        "&nickname=" + nickname +
                        "&sex=" + sex +
                        "&bind_student_account=" + bindStudentAccountName +
                        "&describe_words=" + describeWords;
                MyNetwork.createHttpConnect(MyNetwork.Address_Update_Account,s,MyNetwork.NET_UPDATE_ACCOUNT,handler);
                progressDialog = MyDialogUtils.progressDialogShow(EditAccountActivity.this, "正在上传", "请稍候...",false,false,null);
                break;
            default:
                break;
        }
    }


    public static void actionStart(Context context, UserAccount userAccount)
    {
        Intent intent = new Intent(context,EditAccountActivity.class);
        intent.putExtra("account",userAccount);
        ((Activity)context).startActivityForResult(intent,MyNetwork.NET_UPDATE_ACCOUNT);
    }

}
