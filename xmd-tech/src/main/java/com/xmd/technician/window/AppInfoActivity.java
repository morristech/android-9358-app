package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppInfoActivity extends BaseActivity {

    @BindView(R.id.app_version)
    TextView mAppVersion;
    @BindView(R.id.icon)
    ImageView icon;
    private int i = 0;
    private long starTime = 0;
    private long endTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        ButterKnife.bind(this);

        setTitle(R.string.app_info_title);
        setBackVisible(true);
        mAppVersion.setText(getResources().getString(R.string.app_info_name) + AppConfig.getAppVersionNameAndCode());
    }

    @OnClick(R.id.check_update)
    public void checkUpdate() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE);
    }

    @OnClick(R.id.icon)
    public void getAppMessge() {
        if (i == 0) {
            starTime = System.currentTimeMillis();
        }
        endTime = System.currentTimeMillis();
        i++;
        if (i == 5 && endTime - starTime < 2000) {
            i = 0;
            Intent intent = new Intent(AppInfoActivity.this, ConfigurationMonitorActivity.class);
            startActivity(intent);
        }

    }

}
