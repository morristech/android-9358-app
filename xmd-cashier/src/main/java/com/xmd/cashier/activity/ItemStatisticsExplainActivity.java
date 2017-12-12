package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xmd.cashier.R;

/**
 * Created by zr on 17-12-11.
 */

public class ItemStatisticsExplainActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_statistics_explain);

        showToolbar(R.id.toolbar, "提示");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
