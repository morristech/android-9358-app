package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;

/**
 * Created by zr on 17-4-27.
 * 跳转到结算,流水,核销记录
 */

public class RecordNavigationActivity extends BaseActivity {
    private RelativeLayout lyBillNavigation;
    private RelativeLayout lySettleNavigation;
    private RelativeLayout lyVerifyNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_navigation);

        showToolbar(R.id.toolbar, "对账");

        lyBillNavigation = (RelativeLayout) findViewById(R.id.layout_navigation_bill);
        lySettleNavigation = (RelativeLayout) findViewById(R.id.layout_navigation_settle);
        lyVerifyNavigation = (RelativeLayout) findViewById(R.id.layout_navigation_verify);

        lyBillNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiNavigation.gotoBillRecordActivity(RecordNavigationActivity.this);
            }
        });

        lySettleNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiNavigation.gotoSettleCurrentActivity(RecordNavigationActivity.this);
            }
        });

        lyVerifyNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiNavigation.gotoVerifyRecordActivity(RecordNavigationActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}