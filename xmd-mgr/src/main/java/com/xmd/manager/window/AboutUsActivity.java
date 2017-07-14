package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.AppConfig;
import com.xmd.manager.Manager;
import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sdcm on 15-11-20.
 */
public class AboutUsActivity extends BaseActivity {

    private static final int ENTER_COUNT = 4;

    @BindView(R.id.version_desc)
    TextView mVersionDesc;
    @BindView(R.id.manually_check_upgrade)
    Button mBtnManuallyCheckUpgrade;

    private int mClickedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mVersionDesc.setText(ResourceUtils.getString(R.string.app_name) + " v" + AppConfig.getAppVersionNameAndCode());
        mVersionDesc.setClickable(true);
        mBtnManuallyCheckUpgrade.setOnClickListener(v -> Manager.getInstance().checkUpgrade(false));
    }

    @OnClick(R.id.version_desc)
    public void onClick(View v) {
        if (v == mVersionDesc) {
            if (mClickedCount == ENTER_COUNT) {
                mClickedCount = 0;

                startActivity(new Intent(this, ConfigurationMonitorActivity.class));

            } else {
                mClickedCount++;
            }
        }
    }
}
