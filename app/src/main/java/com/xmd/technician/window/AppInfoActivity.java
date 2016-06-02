package com.xmd.technician.window;

import android.os.Bundle;
import android.widget.TextView;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppInfoActivity extends BaseActivity {

    @Bind(R.id.app_version)
    TextView mAppVersion;

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
    public void checkUpdate(){
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE);
    }

}
