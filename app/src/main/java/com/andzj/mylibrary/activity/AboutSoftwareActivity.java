package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.util.MyLog;

/**
 * Created by zj on 2016/7/22.
 */
public class AboutSoftwareActivity extends BaseActivity implements View.OnClickListener
{
    private Button softwareIntroduceBtn;
    private Button checkUpdateBtn;
    private Button feedbackBtn;
    private Button contactUsBtn;
    private Button supportUsBtn;
    private Button userAgreementBtn;
    private String mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_about_software);

        softwareIntroduceBtn = (Button)findViewById(R.id.software_introduce);
        softwareIntroduceBtn.setOnClickListener(this);
        checkUpdateBtn = (Button)findViewById(R.id.check_update);
        checkUpdateBtn.setOnClickListener(this);
        feedbackBtn = (Button)findViewById(R.id.feedback);
        feedbackBtn.setOnClickListener(this);
        contactUsBtn = (Button)findViewById(R.id.contact_us);
        contactUsBtn.setOnClickListener(this);
        supportUsBtn = (Button)findViewById(R.id.support_us);
        supportUsBtn.setOnClickListener(this);
        userAgreementBtn = (Button)findViewById(R.id.user_agreement);
        userAgreementBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.software_introduce:
                TextViewDialogActivity.actionStart(AboutSoftwareActivity.this,TextViewDialogActivity.Category_software_introduce, null,"好的",null);
                break;
            case R.id.check_update:
                TextViewDialogActivity.actionStart(AboutSoftwareActivity.this,TextViewDialogActivity.Category_check_update_no, null,"好的",null);
                break;
            case R.id.feedback:
                MyLog.d("AboutSoftwareActivity","点击了 反馈/建议");
                break;
            case R.id.contact_us:
                TextViewDialogActivity.actionStart(AboutSoftwareActivity.this,TextViewDialogActivity.Category_contact_us, null,"好的",null);
                break;
            case R.id.support_us:
                TextViewDialogActivity.actionStart(AboutSoftwareActivity.this,TextViewDialogActivity.Category_support_us, null,null,"返回");
                break;
            case R.id.user_agreement:
                TextViewDialogActivity.actionStart(AboutSoftwareActivity.this,TextViewDialogActivity.Category_user_agreement, null,"已阅读",null);
                break;
            default:
                MyLog.e("AboutSoftwareActivity","Error clicked");
                break;
        }
    }

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,AboutSoftwareActivity.class);
        context.startActivity(intent);
    }

}

