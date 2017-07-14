package com.xmd.technician.window;

import android.os.Bundle;

import com.xmd.technician.R;

/**
 * Created by Administrator on 2016/10/26.
 */
public class OrderFragmentActivity extends BaseActivity implements BaseFragment.IFragmentCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_fragment);
    }
}
