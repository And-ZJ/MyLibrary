package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.util.MyLog;
import com.andzj.mylibrary.util.SystemSet;

/**
 * Created by zj on 2016/7/22.
 */
public class SystemSetActivity extends BaseActivity implements View.OnClickListener
{
    private Switch autoReceiveImageSetSwitch;
    private Switch historyRecordSetSwitch;

    private Button helpBtn;
    private Button aboutBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_system_set);

        autoReceiveImageSetSwitch = (Switch)findViewById(R.id.auto_receive_image_set_switch);
        autoReceiveImageSetSwitch.setOnClickListener(this);
        autoReceiveImageSetSwitch.setChecked(SystemSet.isAutoReceivedPictureSwitch());
        historyRecordSetSwitch = (Switch) findViewById(R.id.history_record_set_switch);
        historyRecordSetSwitch.setOnClickListener(this);
        historyRecordSetSwitch.setChecked(SystemSet.isHistoryRecordOpen());

        helpBtn = (Button)findViewById(R.id.help_btn);
        helpBtn.setOnClickListener(this);
        aboutBtn = (Button)findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.auto_receive_image_set_switch:
                SystemSet.setAutoReceivedPictureSwitch(autoReceiveImageSetSwitch.isChecked());
                break;
            case R.id.history_record_set_switch:
                SystemSet.setHistoryRecordOpen(historyRecordSetSwitch.isChecked());
                break;
            case R.id.help_btn:
                MyLog.d("SystemSetActivity","点击了 帮助");
                Toast.makeText(SystemSetActivity.this,"请联系管理员以获得帮助,如有不便请见谅",Toast.LENGTH_SHORT).show();

                break;
            case R.id.about_btn:
                MyLog.d("SystemSetActivity","点击了 关于");
                AboutSoftwareActivity.actionStart(SystemSetActivity.this);
                break;
            default:
                MyLog.e("SystemSetActivity","Error clicked");
                break;
        }
    }


    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context,SystemSetActivity.class);
        context.startActivity(intent);
    }
}
