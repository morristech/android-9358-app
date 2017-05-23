package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.PayTypeChoiceListAdapter;
import com.xmd.cashier.contract.PayTypeChoiceContract;
import com.xmd.cashier.presenter.PayTypeChoicePresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

public class PayTypeChoiceActivity extends BaseActivity implements PayTypeChoiceContract.View {
    private PayTypeChoiceContract.Presenter mPresenter;
    private TextView mNeedPayMoneyView;
    private Button mPayButtonView;
    private PayTypeChoiceListAdapter mPayTypeChoiceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_type_choice);

        mPresenter = new PayTypeChoicePresenter(this, this);
        initView();

        mPresenter.onCreate();
    }

    private void initView() {
        mNeedPayMoneyView = (TextView) findViewById(R.id.tv_need_pay);
        mPayButtonView = (Button) findViewById(R.id.confirm_pay);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mPayTypeChoiceListAdapter = new PayTypeChoiceListAdapter(mPresenter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CustomRecycleViewDecoration(2));
        recyclerView.setAdapter(mPayTypeChoiceListAdapter);

        showToolbar(R.id.toolbar, R.string.pay_type_title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setPresenter(PayTypeChoiceContract.Presenter presenter) {

    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showNeedPayMoney(String money) {
        mNeedPayMoneyView.setText(String.format(getString(R.string.pay_type_amount), money));
    }

    @Override
    public void showPayType(int payType) {
        mPayTypeChoiceListAdapter.setCurrentPayTypeId(payType);
        mPayTypeChoiceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPayButtonEnable(boolean enable) {
        mPayButtonView.setEnabled(enable);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onCancel();
        finish();
        return true;
    }

    public void onClickPay(View view) {
        mPresenter.onClickPay();
    }
}
