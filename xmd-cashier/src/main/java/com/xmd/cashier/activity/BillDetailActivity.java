package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.BillDetailContract;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.presenter.BillDetailPresenter;
import com.xmd.cashier.widget.ArrayPopupWindow;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zr on 16-11-23.
 * 交易详情
 */

public class BillDetailActivity extends BaseActivity implements BillDetailContract.View {
    private static final String MORE_MENU_PRINT = "重打小票";
    private static final String MORE_MENU_REFUND = "退款";
    private static final List<String> MORE_MENU_ITEM = Arrays.asList(MORE_MENU_PRINT, MORE_MENU_REFUND);
    private BillDetailContract.Presenter mPresenter;

    private TextView mOriginMoneyText;
    private TextView mPayMoneyText;
    private TextView mPosPayTypeText;
    private TextView mPosPayMoneyText;
    private TextView mMemberPayMoneyText;
    private TextView mCutMoneyText;
    private TableRow mUserDiscountRow;
    private TextView mUserDiscountMoneyText;
    private TableRow mCouponDiscountRow;
    private TextView mCouponDiscountMoneyText;
    private TextView mMemberDiscountMoneyText;
    private TextView mTradeNoText;
    private TextView mPayTypeText;
    private TextView mPayTimeText;
    private TextView mPayOperatorText;
    private TableRow mOtherDiscountRow;
    private TextView mOtherDiscountMoneyText;

    private TableRow mRefundMoneyRow;
    private TextView mRefundMoneyText;
    private TableRow mRefundNoRow;
    private TextView mRefundTradeNoText;
    private TableRow mRefundTimeRow;
    private TextView mRefundTime;
    private TableRow mRefundOperatorRow;
    private TextView mRefundOperatorText;

    private TextView mPayStatusText;

    private ImageView mImageMore;

    private ArrayPopupWindow<String> mMorePop;

    private BillInfo mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        mPresenter = new BillDetailPresenter(this, this);
        initView();
        mInfo = getIntent().getParcelableExtra(AppConstants.EXTRA_BILL_INFO);
        initInfo(mInfo);
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, R.string.bill_detail_title);
        mOriginMoneyText = (TextView) findViewById(R.id.tv_origin_money);
        mPayMoneyText = (TextView) findViewById(R.id.tv_pay_money);
        mPosPayMoneyText = (TextView) findViewById(R.id.tv_pos_pay_money);
        mMemberPayMoneyText = (TextView) findViewById(R.id.tv_member_pay_money);
        mPosPayTypeText = (TextView) findViewById(R.id.tv_pos_pay_type);
        mCutMoneyText = (TextView) findViewById(R.id.tv_cut_money);
        mUserDiscountRow = (TableRow) findViewById(R.id.tr_user_discount_row);
        mUserDiscountMoneyText = (TextView) findViewById(R.id.tv_user_discount_money);
        mCouponDiscountRow = (TableRow) findViewById(R.id.tr_coupon_discount_row);
        mCouponDiscountMoneyText = (TextView) findViewById(R.id.tv_coupon_discount_money);
        mMemberDiscountMoneyText = (TextView) findViewById(R.id.tv_member_discount_money);
        mOtherDiscountRow = (TableRow) findViewById(R.id.tr_other_discount_row);
        mOtherDiscountMoneyText = (TextView) findViewById(R.id.tv_other_discount_money);

        mTradeNoText = (TextView) findViewById(R.id.tv_trade_no);
        mPayTypeText = (TextView) findViewById(R.id.tv_pay_type);
        mPayTimeText = (TextView) findViewById(R.id.tv_pay_time);
        mPayOperatorText = (TextView) findViewById(R.id.tv_pay_operator);

        mRefundMoneyRow = (TableRow) findViewById(R.id.row_refund_money);
        mRefundMoneyText = (TextView) findViewById(R.id.tv_refund_money);
        mRefundNoRow = (TableRow) findViewById(R.id.row_refund_no);
        mRefundTradeNoText = (TextView) findViewById(R.id.tv_refund_no);
        mRefundTimeRow = (TableRow) findViewById(R.id.row_refund_time);
        mRefundTime = (TextView) findViewById(R.id.tv_refund_time);
        mRefundOperatorRow = (TableRow) findViewById(R.id.row_refund_operator);
        mRefundOperatorText = (TextView) findViewById(R.id.tv_refund_operator);

        mPayStatusText = (TextView) findViewById(R.id.tv_bill_status);

        mImageMore = (ImageView) findViewById(R.id.iv_more);

        mMorePop = new ArrayPopupWindow<>(this,
                mImageMore,
                null,
                getWindowManager().getDefaultDisplay().getWidth() / 3,
                R.style.anim_top_to_bottom_style,
                getResources().getDrawable(R.drawable.bg_popup_window), 0);
        mMorePop.setDataSet(MORE_MENU_ITEM);
        mMorePop.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (MORE_MENU_ITEM.get(position)) {
                    case MORE_MENU_PRINT:
                        mPresenter.print(mInfo);
                        break;
                    case MORE_MENU_REFUND:
                        mPresenter.refund(mInfo);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initInfo(BillInfo info) {
        if (info != null) {
            mOriginMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.originMoney)));
            // Pos支付+会员支付
            mPayMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayMoney + info.posPayMoney)));
            mPosPayMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.posPayMoney)));
            mMemberPayMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayMoney)));
            mPosPayTypeText.setText(Utils.getPayTypeString(info.posPayType));
            // 减免金额
            mCutMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayDiscountMoney + info.userDiscountMoney + info.couponDiscountMoney)));
            switch (info.discountType) {
                case Trade.DISCOUNT_TYPE_COUPON:
                    mCouponDiscountRow.setVisibility(View.VISIBLE);
                    mCouponDiscountMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.couponDiscountMoney)));
                    break;
                case Trade.DISCOUNT_TYPE_USER:
                    mUserDiscountRow.setVisibility(View.VISIBLE);
                    mUserDiscountMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.userDiscountMoney)));
                    break;
                case Trade.DISCOUNT_TYPE_NONE:
                default:
                    mOtherDiscountRow.setVisibility(View.VISIBLE);
                    mOtherDiscountMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.userDiscountMoney + info.couponDiscountMoney)));
                    break;
            }
            mMemberDiscountMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayDiscountMoney)));

            mTradeNoText.setText(info.tradeNo);
            mPayTypeText.setText(Utils.getPayTypeString(info.posPayType));
            mPayTimeText.setText(DateUtils.doLong2String(Long.parseLong(info.payDate)));
            mPayOperatorText.setText(TextUtils.isEmpty(info.payOperator) ? "匿名" : info.payOperator);

            mPayStatusText.setText(Utils.getPayStatusString(info.status));
            if (info.status == AppConstants.PAY_STATUS_REFUND) {
                mRefundMoneyRow.setVisibility(View.VISIBLE);
                mRefundNoRow.setVisibility(View.VISIBLE);
                mRefundTimeRow.setVisibility(View.VISIBLE);
                mRefundOperatorRow.setVisibility(View.VISIBLE);
                mRefundMoneyText.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(info.refundMoney)));
                mRefundTradeNoText.setText(info.refundNo);
                mRefundTime.setText(DateUtils.doLong2String(Long.parseLong(info.refundDate)));
                mRefundOperatorText.setText(TextUtils.isEmpty(info.refundOperator) ? "匿名" : info.refundOperator);
            }
        }
    }

    public void onClickMore(View view) {
        mPresenter.onClickMore();
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
    public void setPresenter(BillDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showMorePop() {
        mMorePop.showAsDropDown();
    }

    @Override
    public void showToast(String msg) {
        super.showToast(msg);
    }
}
