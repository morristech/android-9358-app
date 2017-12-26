package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerPayRecordAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.PayRecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-26.
 */

public class PayRecordActivity extends BaseActivity {
    private RecyclerView mPayRecordList;
    private Button mBtnReturn;

    private List<PayRecordInfo> mData = new ArrayList<>();
    private InnerPayRecordAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        mData = (List<PayRecordInfo>) getIntent().getSerializableExtra(AppConstants.EXTRA_INNER_PAY_RECORD);

        mAdapter = new InnerPayRecordAdapter(this);
        mPayRecordList = (RecyclerView) findViewById(R.id.rv_pay_record_list);
        mBtnReturn = (Button) findViewById(R.id.btn_return);
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPayRecordList.setHasFixedSize(true);
        mPayRecordList.setLayoutManager(new LinearLayoutManager(this));
        mPayRecordList.setAdapter(mAdapter);
        mAdapter.setData(mData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
