package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.xmd.technician.R;

/**
 * Created by Administrator on 2016/8/10.
 */
public class CreditRuleExplainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_rule);
        initView();
    }
    private void initView(){
        setBackVisible(true);
        setTitle("积分规则");
    }
}
