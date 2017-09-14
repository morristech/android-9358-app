package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.VerifyRecordDetailAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.VerifyRecordDetailContract;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.presenter.VerifyRecordDetailPresenter;
import com.xmd.cashier.widget.CircleImageView;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.FullyGridLayoutManager;

/**
 * Created by zr on 17-5-2.
 * 核销记录详情
 */

public class VerifyRecordDetailActivity extends BaseActivity implements VerifyRecordDetailContract.View {
    private VerifyRecordDetailContract.Presenter mPresenter;
    private String mRecordId;

    private CircleImageView mAvatar;
    private TextView mUserName;
    private TextView mUserTelephone;
    private TextView mTypeName;
    private TextView mTime;
    private TextView mOperator;
    private TextView mCode;

    private Button mBtnClient;
    private Button mBtnClub;

    private RecyclerView mRecyclerDetail;
    private VerifyRecordDetailAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_record_detail);
        mPresenter = new VerifyRecordDetailPresenter(this, this);
        mRecordId = getIntent().getStringExtra(AppConstants.EXTRA_RECORD_ID);

        initView();

        mPresenter.getVerifyDetailById(mRecordId);
    }

    private void initView() {
        showToolbar(R.id.toolbar, "核销记录详情");
        mAvatar = (CircleImageView) findViewById(R.id.img_detail_avatar);
        mUserName = (TextView) findViewById(R.id.tv_detail_user_name);
        mUserTelephone = (TextView) findViewById(R.id.tv_detail_telephone);
        mTypeName = (TextView) findViewById(R.id.tv_detail_type_name);
        mTime = (TextView) findViewById(R.id.tv_detail_time);
        mOperator = (TextView) findViewById(R.id.tv_detail_operator);
        mCode = (TextView) findViewById(R.id.tv_detail_verify_code);

        mBtnClient = (Button) findViewById(R.id.btn_print_client);
        mBtnClub = (Button) findViewById(R.id.btn_print_club);

        mRecyclerDetail = (RecyclerView) findViewById(R.id.rv_detail_item);
        mAdapter = new VerifyRecordDetailAdapter(this);
        mRecyclerDetail.setHasFixedSize(true);
        mRecyclerDetail.setLayoutManager(new FullyGridLayoutManager(this, 1));
        mRecyclerDetail.addItemDecoration(new CustomRecycleViewDecoration(16));
        mRecyclerDetail.setAdapter(mAdapter);

        // 打印客户联
        mBtnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.printVerifyRecord(false);
            }
        });

        // 打印商户存根
        mBtnClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.printVerifyRecord(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(VerifyRecordDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void onDetailSuccess(VerifyRecordDetailResult.RespData data) {
        VerifyRecordInfo info = data.record;
        Glide.with(this).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(mAvatar);
        mUserName.setText(TextUtils.isEmpty(info.userName) ? "匿名用户" : info.userName);
        mUserTelephone.setText(TextUtils.isEmpty(info.telephone) ? "-" : info.telephone);
        mTypeName.setText(TextUtils.isEmpty(info.businessTypeName) ? "-" : info.businessTypeName);
        mTime.setText(TextUtils.isEmpty(info.verifyTime) ? "-" : info.verifyTime);
        mOperator.setText(TextUtils.isEmpty(info.operatorName) ? "-" : info.operatorName);
        mCode.setText(TextUtils.isEmpty(info.verifyCode) ? "-" : info.verifyCode);

        mAdapter.setData(data.detail);
        mAdapter.notifyDataSetChanged();
    }
}
