package com.xmd.manager.window;

import android.os.Bundle;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

/**
 * Created by Lhj on 17-4-26.
 */

public class MarketingIncomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_income);
        setTitle(ResourceUtils.getString(R.string.marketing_income_statistics));
    }

}
