package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.ImageLoader;

/**
 * Created by zj on 2016/7/25.
 */
public class AccountMsgActivity extends BaseActivity implements View.OnClickListener
{
    private ImageView headPortraitView;
    private TextView accountNameView;
    private TextView nicknameView;
    private TextView sexView;
    private TextView describeWordsView;
    private TextView bindStudentAccountView;
    private TextView registerTimeView;

    private Button editInfoBtn;
    private Button alterPasswordBtn;
    //private Button addBuddyBtn;
    private Button exitLoginBtn;
    private UserAccount userAccount;

    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_account_msg);

        headPortraitView = (ImageView) findViewById(R.id.head_portrait_view);
        headPortraitView.setOnClickListener(this);
        accountNameView = (TextView) findViewById(R.id.account_name_view);
        nicknameView = (TextView) findViewById(R.id.nickname_view);
        sexView = (TextView) findViewById(R.id.sex_view);
        describeWordsView = (TextView) findViewById(R.id.describe_words_view);
        bindStudentAccountView = (TextView) findViewById(R.id.bind_student_account_view);
        registerTimeView = (TextView) findViewById(R.id.register_time_view);
        editInfoBtn = (Button) findViewById(R.id.edit_info_btn);
        editInfoBtn.setOnClickListener(this);
        alterPasswordBtn = (Button) findViewById(R.id.alter_password_btn);
        alterPasswordBtn.setOnClickListener(this);
        exitLoginBtn = (Button) findViewById(R.id.exit_login_btn);
        exitLoginBtn.setOnClickListener(this);

        imageLoader = MainActivity.getImageLoader(AccountMsgActivity.this);
        userAccount = getIntent().getParcelableExtra("account");
        if (userAccount != null)
        {
            showAccountMsg(userAccount);
        }
        else
        {
            finish();
        }

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.head_portrait_view:
                ChoosePhotoActivity.actionStart(AccountMsgActivity.this,userAccount.getAccountName());
                break;
            case R.id.edit_info_btn:
                EditAccountActivity.actionStart(AccountMsgActivity.this,userAccount);
                break;
            case R.id.alter_password_btn:
                OldPasswordActivity.actionStart(AccountMsgActivity.this);
                break;
            case R.id.exit_login_btn:
                SharedPreferences.Editor evidenceEditor = getSharedPreferences("evidence",MODE_PRIVATE).edit();
                evidenceEditor.putBoolean("login_state",false);
                evidenceEditor.putBoolean("auto_login",false);
                evidenceEditor.apply();
                MainActivity.setLoginState(false);
                MainActivity.exitLogin();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case MyNetwork.NET_UPDATE_ACCOUNT:
                if (resultCode == RESULT_OK)
                {
                    userAccount = data.getParcelableExtra("account");
                    if (userAccount != null)
                    {
                        showAccountMsg(userAccount);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserAccount userAccount = MainActivity.getUserAccount(AccountMsgActivity.this);
        if (userAccount != null)
        {
            showAccountMsg(userAccount);
        }
    }

    private void showAccountMsg(UserAccount userAccount)
    {
        //头像
        String imageStr = userAccount.getUserImageStr();
        if (imageStr != null)
        {
            imageLoader.bindBitmap(ImageLoader.getDownloadUrlWithTime(MyNetwork.Address_Access_File + imageStr,userAccount.getUserUpdateTime()),headPortraitView);

        }
        accountNameView.setText(userAccount.getAccountName());
        nicknameView.setText(userAccount.getUserNickname());
        sexView.setText(userAccount.getUserSex());
        describeWordsView.setText(userAccount.getUserDescribeWords());
        bindStudentAccountView.setText(userAccount.getBindStudentAccount());
        registerTimeView.setText(userAccount.getRegisterTime());
    }

    @Deprecated
    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,AccountMsgActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(Context context,UserAccount userAccount)
    {
        Intent intent = new Intent(context,AccountMsgActivity.class);
        intent.putExtra("account",userAccount);
        context.startActivity(intent);
    }

}
