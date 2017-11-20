package com.xmd.cashier.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerOrderAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerMethodContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.event.InnerUpdateOrderEvent;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.presenter.InnerMethodPresenter;
import com.xmd.cashier.widget.StepView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by zr on 17-11-2.
 * 支付方式页面
 */

public class InnerMethodActivity extends BaseActivity implements InnerMethodContract.View {
    private InnerMethodContract.Presenter mPresenter;

    private RelativeLayout mVerifySelectLayout;
    private TextView mVerifyDescText;
    private Button mPayBtn;

    private TextView mNeedPayAmount;

    private RecyclerView mOrderList;
    private InnerOrderAdapter mAdapter;

    private String mSource;
    private InnerRecordInfo mRecordInfo;

    private StepView mStepView;

    private RelativeLayout mSelectSetLayout;
    private TextView mSelectStatusText;
    private TextView mSelectCountText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_method);
        initView();
        mPresenter = new InnerMethodPresenter(this, this);
        mPresenter.onCreate();

        processExtraData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        mSource = getIntent().getStringExtra(AppConstants.EXTRA_INNER_METHOD_SOURCE);
        mRecordInfo = (InnerRecordInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_INNER_RECORD_DETAIL);
        mAdapter = new InnerOrderAdapter(this, AppConstants.INNER_METHOD_SOURCE_NORMAL.equals(mSource));
        mAdapter.setCallBack(new InnerOrderAdapter.ItemCallBack() {
            @Override
            public void onItemClick(InnerOrderInfo orderInfo, int position) {
                mPresenter.onOrderClick(orderInfo, position);
            }
        });
        mOrderList.setAdapter(mAdapter);
        mPresenter.processData();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "收银");
        mStepView = (StepView) findViewById(R.id.sv_step_method);

        mSelectSetLayout = (RelativeLayout) findViewById(R.id.layout_select_set);
        mSelectStatusText = (TextView) findViewById(R.id.tv_select_status);
        mSelectCountText = (TextView) findViewById(R.id.tv_select_count);
        mSelectStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSelectChange();
            }
        });

        mVerifySelectLayout = (RelativeLayout) findViewById(R.id.layout_verify_select);
        mVerifyDescText = (TextView) findViewById(R.id.tv_verify_desc);
        mPayBtn = (Button) findViewById(R.id.btn_pay);
        mNeedPayAmount = (TextView) findViewById(R.id.tv_need_amount);

        mOrderList = (RecyclerView) findViewById(R.id.rv_order_list);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));

        mVerifyDescText.setVisibility(View.GONE);
        mVerifySelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerifySelect();
            }
        });

        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPayClick();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.onStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(InnerMethodContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showVerifyDesc(String desc) {
        if (!TextUtils.isEmpty(desc)) {
            mVerifyDescText.setVisibility(View.VISIBLE);
            mVerifyDescText.setText(desc);
        }
    }

    @Override
    public void hideVerifyDesc() {
        mVerifyDescText.setVisibility(View.GONE);
    }

    @Override
    public void showNeedPayAmount(int amount) {
        mNeedPayAmount.setText("￥" + Utils.moneyToStringEx(amount));
    }

    @Override
    public void showOrderList(List<InnerOrderInfo> list) {
        mAdapter.setData(list);
    }

    @Override
    public String returnSource() {
        return mSource;
    }

    @Override
    public InnerRecordInfo returnRecordInfo() {
        return mRecordInfo;
    }

    @Override
    public boolean onKeyEventBack() {
        InnerManager.getInstance().removeUnselectedInfos();
        EventBus.getDefault().post(new InnerUpdateOrderEvent());
        finishSelf();
        showExitAnim();
        return true;
    }

    @Override
    public void showEnterAnim() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    @Override
    public void showExitAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public void showStepView() {
        mStepView.setSteps(AppConstants.INNER_PAY_STEPS);
        mStepView.selectedStep(2);
    }

    @Override
    public void setStatusLayout(boolean show) {
        mSelectSetLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateStatus(boolean selected) {
        Drawable leftDrawable = getResources().getDrawable(selected ? R.drawable.ic_order_select_little : R.drawable.ic_order_unselect_little);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        mSelectStatusText.setCompoundDrawables(leftDrawable, null, null, null);
    }

    @Override
    public void showSelectCount(int count) {
        mSelectCountText.setText(String.valueOf(count));
    }

    @Override
    public void updateItem(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void updateAll() {
        mAdapter.notifyDataSetChanged();
    }
}
