package com.xmd.technician.window;


import com.umeng.analytics.MobclickAgent;

/**
 * Created by sdcm on 15-10-26.
 */
public class BaseActivity extends BaseFragmentActivity {

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }
}
