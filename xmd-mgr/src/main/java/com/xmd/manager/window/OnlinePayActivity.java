package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

/**
 * Created by Lhj on 17-4-26.
 */

public class OnlinePayActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_pay);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.online_pay_activity_title));
        setRightVisible(true, R.drawable.ic_search_normal, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnlinePayActivity.this, OnlinePaySearchActivity.class));
            }
        });
    }


}
