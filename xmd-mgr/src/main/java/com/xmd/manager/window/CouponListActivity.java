package com.xmd.manager.window;

import android.os.Bundle;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

/**
 * Created by Administrator on 2016/11/9.
 */
public class CouponListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);
        setRightVisible(false, 0, null);
        setTitle(ResourceUtils.getString(R.string.coupon_tips));
    }
}
