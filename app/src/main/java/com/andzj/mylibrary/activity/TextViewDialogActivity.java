package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.util.MyLog;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by zj on 2016/8/27.
 */
public class TextViewDialogActivity extends BaseActivity
{
    public static final int Category_NO = 0;
    public static final int Category_software_introduce =1;
    public static final int Category_check_update_ok = 2;
    public static final int Category_check_update_no = 3;
    public static final int Category_contact_us = 5;
    public static final int Category_support_us =6;
    public static final int Category_user_agreement = 7;
    public static final int Category_admin_notice = 8;

    private TextView titleView;
    private TextView textView;
    private Button agreeBtn;
    private Button disagreeBtn;
    private ImageView dialogImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_textview_dialog);
        titleView = (TextView) findViewById(R.id.dialog_title_view);
        dialogImageView = (ImageView) findViewById(R.id.dialog_image_view);
        textView = (TextView) findViewById(R.id.information_view);
        agreeBtn = (Button) findViewById(R.id.agree_btn);
        disagreeBtn = (Button) findViewById(R.id.disagree_btn);

        Intent intent = getIntent();
        if (intent != null)
        {
            String agreeText = intent.getStringExtra("agreeText");
            if (agreeText != null )
            {
                agreeBtn.setText(agreeText);
            }
            String disagreeText = intent.getStringExtra("disagreeText");
            if (disagreeText != null)
            {
                disagreeBtn.setText(disagreeText);
            }
            int category = intent.getIntExtra("dialogCategory",Category_NO);
            String text = intent.getStringExtra("text");
            if (text != null)
            {
                textView.setText(text);
            }
            else
            {
                textView.setText(getTextViewShowStr(category));
            }
            switch (category)
            {
                case Category_NO:
                    textView.setText("出现了问题");
                    registerCloseBtn(disagreeBtn);
                    break;
                case Category_software_introduce:
                    registerCloseBtn(agreeBtn);
                    setTitleViewText(R.string.software_introduce);
                    break;
                case Category_check_update_ok:
                    disagreeBtn.setVisibility(View.VISIBLE);
                    registerCloseBtn(disagreeBtn);
                    setTitleViewText(R.string.check_update);
                    break;
                case Category_check_update_no:
                    registerCloseBtn(agreeBtn);
                    textView.setGravity(Gravity.CENTER);
                    setTitleViewText(R.string.check_update);
                    break;
                case Category_contact_us:
                    registerCloseBtn(agreeBtn);
                    setTitleViewText(R.string.contact_us);
                    break;
                case Category_support_us:
                    dialogImageView.setVisibility(View.VISIBLE);
                    dialogImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.wechat_reward_qr_code_image));
                    registerCloseBtn(agreeBtn);
                    textView.setGravity(Gravity.CENTER);
                    setTitleViewText(R.string.support_us);
                    break;
                case Category_user_agreement:
                    registerCloseBtn(agreeBtn);
                    setTitleViewText(R.string.user_agreement);
                    break;
                case Category_admin_notice:
                    registerCloseBtn(agreeBtn);
                    titleView.setText("管理员须知");
                    break;
                default:
                    textView.setText("出现了问题");
                    registerCloseBtn(disagreeBtn);
                    break;
            }
        }
        else
        {
            textView.setText(getTextViewShowStr(Category_NO));
            registerCloseBtn(disagreeBtn);
        }
    }

    private void registerCloseBtn(Button closeBtn)
    {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setTitleViewText(int stringId)
    {
        titleView.setText(getResources().getString(stringId));
    }

    public String getTextViewShowStr(int category)
    {
        switch (category)
        {
            case Category_NO:
                break;
            case Category_software_introduce:
                return "软件介绍:\n" +
                        "  这是一款基于社交的图书馆App,欢迎您的使用.\n\n" +
                        "更新说明:\n" +
                        "  V1.0beta: \n" +
                        "    暂无说明\n\n";
            case Category_check_update_ok:
                return "发现新版本 V1.1_beta 是否下载?\n";
            case Category_check_update_no:
                return "暂无更新\n";
            case Category_contact_us:
                return "联系方式:\n" +
                        "   微信号:  And-ZJ\n" +
                        "   邮箱:  ZJ.Cosmos@gmail.com\n\n" +
                        "开发人员:\n" +
                        "   张建   叶凌瑶\n\n";
            case Category_support_us:
                return "微信扫一扫,向开发者打赏\n" +
                       "喜欢就鼓励一下,谢谢您的支持\n\n";
            case Category_user_agreement:
                return "本协议为本软件开发者(简称\"开发者\")与本软件使用者(简称\"用户\")所签合约\n\n" +
                        "  1.开发者保留此软件在法律范围内的解释权;\n" +
                        "  2.本软件不会盗用个人信息,只申请必要权限,请用户放心;\n" +
                        "  3.此处省略一万字;\n" +
                        "  4.未能详述之处,还请用户见谅.\n\n";
            case Category_admin_notice:
                return "  申请成为管理员,需要待其它管理员同意后,才能拥有管理员身份,在注册之后,仍可以在账号信息界面进行申请.\n\n" +
                        "  以下为管理员须知:\n" +
                        "  1.管理员不得滥用其管理员权力,不能随意删除读后感,故意编辑错误的图书信息等;\n" +
                        "  2.管理员应积极维护图书信息,保证图书信息的正确性;\n" +
                        "  3.管理员应积极回应用户的消息、反馈、举报等问题;\n" +
                        "  4.对有些帐号的恶意行为,包括但不限于诸如恶意评论、发布不良信息、恶意拖欠等行为,及时采取删除、警告、封号等行动;\n" +
                        "  5.此处省略两万字,未能详述之处,还请管理员用户见谅.\n\n";
            default:
                break;
        }
        return "Error!\n\n";

    }

    public static void actionStart(Context context)
    {
        actionStart(context,0,null,null,null);
    }

    public static void actionStart(Context context ,int dialogCategory)
    {
        actionStart(context,dialogCategory,null,null,null);
    }

    public static void actionStart(Context context, int dialogCategory,String text,String agreeText,String disagreeText)
    {
        Intent intent = new Intent(context,TextViewDialogActivity.class);
        intent.putExtra("dialogCategory",dialogCategory);
        if (text != null)
        {
            intent.putExtra("text",text);
        }
        if (agreeText != null)
        {
            intent.putExtra("agreeText",agreeText);
        }
        if (disagreeText != null)
        {
            intent.putExtra("disagreeText",disagreeText);
        }
        context.startActivity(intent);
    }
}
