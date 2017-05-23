package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.SettleSummaryAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleDetailContract;
import com.xmd.cashier.dal.bean.SettleSummaryInfo;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.presenter.SettleDetailPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.FullyGridLayoutManager;

import java.util.List;

/**
 * Created by zr on 17-4-7.
 * 结算记录详情
 */

public class SettleDetailActivity extends BaseActivity implements SettleDetailContract.View {
    private SettleDetailContract.Presenter mPresenter;

    private ScrollView svDetailData;

    private TextView tvOriginTotal;

    private TextView tvCutTotal;
    private TextView tvCutCoupon;
    private TextView tvCutUser;
    private TextView tvCutMember;

    private TextView tvRefundTotal;
    private TextView tvRefundBank;
    private TextView tvRefundCash;
    private TextView tvRefundWechat;
    private TextView tvRefundMember;

    private TextView tvPayTotal;
    private TextView tvPayBank;
    private TextView tvPayCash;
    private TextView tvPayWechat;
    private TextView tvPayMember;

    private TextView tvCountOrder;
    private TextView tvCountRefund;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvCashierName;

    private TextView tvSettleName;
    private TextView tvSettleTime;

    private RecyclerView rvSubSummary;

    private Button mPrintBtn;

    private SettleSummaryAdapter mAdapter;

    private String mRecordId;

    private SettleSummaryResult.RespData mRespData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_detail);
        mPresenter = new SettleDetailPresenter(this, this);
        mRecordId = getIntent().getStringExtra(AppConstants.EXTRA_RECORD_ID);
        initView();

        mPresenter.getSummaryById(mRecordId);
    }

    private void initView() {
        showToolbar(R.id.toolbar, "结算");
        svDetailData = (ScrollView) findViewById(R.id.sv_detail_data);

        tvOriginTotal = (TextView) findViewById(R.id.settle_origin_total);

        tvCutTotal = (TextView) findViewById(R.id.settle_cut_total);
        tvCutCoupon = (TextView) findViewById(R.id.settle_cut_coupon_discount);
        tvCutUser = (TextView) findViewById(R.id.settle_cut_user_discount);
        tvCutMember = (TextView) findViewById(R.id.settle_cut_member_discount);

        tvRefundTotal = (TextView) findViewById(R.id.settle_refund_total);
        tvRefundBank = (TextView) findViewById(R.id.settle_refund_bank_card);
        tvRefundCash = (TextView) findViewById(R.id.settle_refund_cash);
        tvRefundWechat = (TextView) findViewById(R.id.settle_refund_weixin);
        tvRefundMember = (TextView) findViewById(R.id.settle_refund_member);

        tvPayTotal = (TextView) findViewById(R.id.settle_pay_total);
        tvPayBank = (TextView) findViewById(R.id.settle_pay_bank_card);
        tvPayCash = (TextView) findViewById(R.id.settle_pay_cash);
        tvPayWechat = (TextView) findViewById(R.id.settle_pay_weixin);
        tvPayMember = (TextView) findViewById(R.id.settle_pay_member);

        tvCountOrder = (TextView) findViewById(R.id.settle_total_count);
        tvCountRefund = (TextView) findViewById(R.id.settle_refund_count);
        tvStartTime = (TextView) findViewById(R.id.settle_start_time);
        tvEndTime = (TextView) findViewById(R.id.settle_end_time);
        tvCashierName = (TextView) findViewById(R.id.settle_cashier_people);

        tvSettleName = (TextView) findViewById(R.id.settle_js_people);
        tvSettleName.setVisibility(View.VISIBLE);
        tvSettleTime = (TextView) findViewById(R.id.settle_js_time);
        tvSettleTime.setVisibility(View.VISIBLE);

        rvSubSummary = (RecyclerView) findViewById(R.id.rv_sub_summary_list);

        mPrintBtn = (Button) findViewById(R.id.btn_settle_print_again);

        mAdapter = new SettleSummaryAdapter(this);
        rvSubSummary.setHasFixedSize(true);
        rvSubSummary.setNestedScrollingEnabled(false);
        rvSubSummary.setLayoutManager(new FullyGridLayoutManager(this, 1));
        rvSubSummary.addItemDecoration(new CustomRecycleViewDecoration(2));
        rvSubSummary.setAdapter(mAdapter);

        mPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPrint(mRespData);
            }
        });
    }

    private void initData(SettleSummaryInfo info, List<SettleSummaryInfo> list) {
        if (list != null && list.size() > 0) {
            tvCashierName.setVisibility(View.GONE);
            mAdapter.setData(list);
            mAdapter.notifyDataSetChanged();
        } else {
            tvCashierName.setVisibility(View.VISIBLE);
            tvCashierName.setText(String.format(getResources().getString(R.string.settle_other_cashier_people), info.cashierName));
        }
        tvOriginTotal.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.orderTotalMoney)));

        tvCutTotal.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.deductTotalMoney)));
        tvCutCoupon.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.preferentialDeduct)));
        tvCutUser.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.manualDeduct)));
        tvCutMember.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.discountDeduct)));

        tvRefundTotal.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.refundTotalMoney)));
        tvRefundBank.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.bankCardRefund)));
        tvRefundCash.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.moneyRefund)));
        tvRefundWechat.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.wechatRefund)));
        tvRefundMember.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayRefund)));

        tvPayTotal.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.incomeTotalMoney)));
        tvPayBank.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.bankCardIncome)));
        tvPayCash.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.moneyIncome)));
        tvPayWechat.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.wechatIncome)));
        tvPayMember.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayIncome)));

        tvCountOrder.setText(String.format(getResources().getString(R.string.settle_other_total_count), String.valueOf(info.orderCount)));
        tvCountRefund.setText(String.format(getResources().getString(R.string.settle_other_refund_count), String.valueOf(info.refundCount)));
        tvStartTime.setText(String.format(getResources().getString(R.string.settle_other_start_time), info.startTime));
        tvEndTime.setText(String.format(getResources().getString(R.string.settle_other_end_time), info.endTime));

        tvSettleName.setText(String.format(getResources().getString(R.string.settle_other_js_people), info.operatorName));
        tvSettleTime.setText(String.format(getResources().getString(R.string.settle_other_js_time), info.createTime));
    }

    @Override
    public void setPresenter(SettleDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void onDetailSuccess(SettleSummaryResult.RespData respData) {
        mRespData = respData;
        hideLoading();
        svDetailData.setVisibility(View.VISIBLE);
        mPrintBtn.setEnabled(true);
        initData(respData.obj, respData.recordDetailList);
    }

    @Override
    public void onDetailFailed() {
        hideLoading();
        svDetailData.setVisibility(View.GONE);
        mPrintBtn.setEnabled(false);
    }
}
