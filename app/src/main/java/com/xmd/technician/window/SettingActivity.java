package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.app_version) TextView mAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_setting);
        setBackVisible(true);

        mAppVersion.setText("v"+AppConfig.getAppVersionCode());
    }

    @OnClick(R.id.settings_activity_about_us)
    public void gotoAboutUs(){
        startActivity(new Intent(this, AppInfoActivity.class));
    }

    @OnClick(R.id.settings_activity_suggest)
    public void gotoFeedbackActivity(){
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    @OnClick(R.id.settings_activity_modify_pw)
    public void gotoModifyPassword(){
        startActivity(new Intent(this, ModifyPasswordActivity.class));
    }

    @OnClick(R.id.settings_activity_logout)
    public void logout(){
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT);
    }
}
