package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.StatisticsDetailContract;
import com.xmd.cashier.dal.bean.OfflineStatisticInfo;
import com.xmd.cashier.dal.bean.OnlineStatisticInfo;
import com.xmd.cashier.presenter.StatisticsDetailPresenter;

/**
 * Created by zr on 17-9-19.
 */

public class StatisticsDetailFragment extends Fragment implements StatisticsDetailContract.View {
    private StatisticsDetailContract.Presenter mPresenter;
    private int mType;

    private boolean isInit = false;
    private boolean isLoad = false;

    private View mView;

    private SwipeRefreshLayout mRefreshLayout;

    // ******************************时间筛选******************************
    private ViewStub mTitleStub;

    private LinearLayout mDayLayout;
    private TextView mDayDate;
    private ImageView mDayPlus;
    private ImageView mDayMinus;

    private LinearLayout mWeekLayout;
    private TextView mWeekDate;
    private ImageView mWeekPlus;
    private ImageView mWeekMinus;

    private LinearLayout mMonthLayout;
    private TextView mMonthDate;
    private ImageView mMonthPlus;
    private ImageView mMonthMinus;

    private LinearLayout mTotalLayout;
    private TextView mTotalDate;

    private LinearLayout mCustomLayout;
    private TextView mCustomStart;
    private TextView mCustomEnd;
    private TextView mCustomConfirm;

    // ******************************数据展示******************************
    private ViewStub mDataStub;
    private Button mPrint;

    private LinearLayout mDataNormalLayout;
    private LinearLayout mDataSettleLayout;
    private TextView mSettleTotal;
    private TextView mSettleWX;
    private TextView mSettleAli;
    private TextView mSettleUnion;
    private TextView mOtherTotal;
    private TextView mOtherWX;
    private TextView mOtherAli;
    private TextView mOtherUnion;
    private TextView mOtherCash;
    private TextView mOtherElse;
    private TextView mSwitchToMoney;

    private LinearLayout mDataMoneyLayout;
    private TextView mTotalAmount;
    private TextView mInternalDiscount;
    private TextView mBonusCommission;
    private TextView mTotalFee;
    private TextView mTotalRefund;
    private TextView mGDSettle;
    private TextView mFastPay;
    private TextView mQRTech;
    private TextView mQRClub;
    private TextView mQRPos;
    private TextView mMemberRecharge;
    private TextView mPaidOrder;
    private TextView mMarketing;
    private TextView mPaidCoupon;
    private TextView mPaidServiceItem;
    private TextView mMall;
    private TextView mItemCard;
    private TextView mItemPackage;
    private TextView mPosSettle;
    private TextView mPosSettleUnion;
    private TextView mOtherAmount;
    private TextView mOtherInternalDiscount;
    private TextView mOtherInternalRefund;
    private TextView mOtherPos;
    private TextView mOtherPosCash;
    private TextView mOtherMemberRecharge;
    private TextView mOtherMemberRechargeWX;
    private TextView mOtherMemberRechargeAli;
    private TextView mOtherMemberRechargeUnion;
    private TextView mOtherMemberRechargeCash;
    private TextView mOtherMemberRechargeElse;
    private TextView mSwitchToSettle;

    // ******************************异常提醒******************************
    private ViewStub mErrorStub;

    private LinearLayout mDataErrorLayout;
    private TextView mDataErrorText;
    private TextView mDataRetryText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getInt(AppConstants.EXTRA_BIZ_TYPE);
        }
        mView = inflater.inflate(R.layout.fragment_statistics_detail, container, false);
        isInit = true;

        initView();

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = new StatisticsDetailPresenter(getActivity(), this);
        mPresenter.onCreate();
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(StatisticsDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {

    }

    @Override
    public void showError(String error) {
        ((StatisticsActivity) getActivity()).showError(error);
    }

    @Override
    public void showToast(String toast) {
        ((StatisticsActivity) getActivity()).showToast(toast);
    }

    @Override
    public void showLoading() {
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mRefreshLayout.setRefreshing(false);
    }

    private void loadData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint() && !isLoad) {
            mPresenter.initDate(mType);
            mPresenter.loadData();
            isLoad = true;
        }
    }

    private void initView() {
        mTitleStub = (ViewStub) mView.findViewById(R.id.stub_detail_title);
        mDataStub = (ViewStub) mView.findViewById(R.id.stub_detail_data);
        mErrorStub = (ViewStub) mView.findViewById(R.id.stub_detail_error);

        mRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.srl_refresh);
        mRefreshLayout.setColorSchemeColors(ResourceUtils.getColor(R.color.colorAccent));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadData();
            }
        });
    }

    @Override
    public void initDayDate() {
        mTitleStub.setVisibility(View.VISIBLE);

        mDayLayout = (LinearLayout) mView.findViewById(R.id.layout_day);
        mDayDate = (TextView) mView.findViewById(R.id.tv_day_date);
        mDayPlus = (ImageView) mView.findViewById(R.id.tv_day_plus);
        mDayMinus = (ImageView) mView.findViewById(R.id.tv_day_minus);

        mDayLayout.setVisibility(View.VISIBLE);
        mDayPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.plusDay();
            }
        });
        mDayMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.minusDay();
            }
        });
    }

    @Override
    public void setDayDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            mDayDate.setText(date);
        }
    }

    @Override
    public void setDayPlusEnable(boolean enable) {
        mDayPlus.setEnabled(enable);
    }

    @Override
    public void setDayMinusEnable(boolean enable) {
        mDayMinus.setEnabled(enable);
    }

    @Override
    public void initWeekDate() {
        mTitleStub.setVisibility(View.VISIBLE);

        mWeekLayout = (LinearLayout) mView.findViewById(R.id.layout_week);
        mWeekDate = (TextView) mView.findViewById(R.id.tv_week_date);
        mWeekPlus = (ImageView) mView.findViewById(R.id.tv_week_plus);
        mWeekMinus = (ImageView) mView.findViewById(R.id.tv_week_minus);

        mWeekLayout.setVisibility(View.VISIBLE);
        mWeekMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.minusWeek();
            }
        });
        mWeekPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.plusWeek();
            }
        });
    }

    @Override
    public void setWeekDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            mWeekDate.setText(date);
        }
    }

    @Override
    public void setWeekPlusEnable(boolean enable) {
        mWeekPlus.setEnabled(enable);
    }

    @Override
    public void setWeekMinusEnable(boolean enable) {
        mWeekMinus.setEnabled(enable);
    }


    @Override
    public void initMonthDate() {
        mTitleStub.setVisibility(View.VISIBLE);

        mMonthLayout = (LinearLayout) mView.findViewById(R.id.layout_month);
        mMonthDate = (TextView) mView.findViewById(R.id.tv_month_date);
        mMonthPlus = (ImageView) mView.findViewById(R.id.tv_month_plus);
        mMonthMinus = (ImageView) mView.findViewById(R.id.tv_month_minus);

        mMonthLayout.setVisibility(View.VISIBLE);
        mMonthPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.plusMonth();
            }
        });
        mMonthMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.minusMonth();
            }
        });
    }

    @Override
    public void setMonthDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            mMonthDate.setText(date);
        }
    }

    @Override
    public void setMonthPlusEnable(boolean enable) {
        mMonthPlus.setEnabled(enable);
    }

    @Override
    public void setMonthMinusEnable(boolean enable) {
        mMonthMinus.setEnabled(enable);
    }

    @Override
    public void initTotalDate() {
        mTitleStub.setVisibility(View.VISIBLE);
        mTotalLayout = (LinearLayout) mView.findViewById(R.id.layout_total);
        mTotalDate = (TextView) mView.findViewById(R.id.tv_total_date);
        mTotalLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTotalDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            mTotalDate.setText(date);
        }
    }

    @Override
    public void initCustomDate() {
        mTitleStub.setVisibility(View.VISIBLE);

        mCustomLayout = (LinearLayout) mView.findViewById(R.id.layout_custom);
        mCustomStart = (TextView) mView.findViewById(R.id.tv_custom_start);
        mCustomEnd = (TextView) mView.findViewById(R.id.tv_custom_end);
        mCustomConfirm = (TextView) mView.findViewById(R.id.tv_custom_confirm);

        mCustomLayout.setVisibility(View.VISIBLE);
        mCustomStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCustomStartPick(v);
            }
        });
        mCustomEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCustomEndPick(v);
            }
        });
        mCustomConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.loadCustomData();
            }
        });
    }

    @Override
    public void setCustomStart(String date) {
        if (!TextUtils.isEmpty(date)) {
            mCustomStart.setText(date);
        }
    }

    @Override
    public void setCustomEnd(String date) {
        if (!TextUtils.isEmpty(date)) {
            mCustomEnd.setText(date);
        }
    }

    @Override
    public void initDataLayout() {
        mDataStub.setVisibility(View.VISIBLE);
        mDataNormalLayout = (LinearLayout) mView.findViewById(R.id.layout_normal);
        mPrint = (Button) mView.findViewById(R.id.btn_print);
        mPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPrint(mType);
            }
        });

        mDataSettleLayout = (LinearLayout) mView.findViewById(R.id.layout_statistic_settle);
        mSettleTotal = (TextView) mView.findViewById(R.id.no_settle_total);
        mSettleWX = (TextView) mView.findViewById(R.id.no_settle_wx);
        mSettleAli = (TextView) mView.findViewById(R.id.no_settle_ali);
        mSettleUnion = (TextView) mView.findViewById(R.id.no_settle_union);
        mOtherTotal = (TextView) mView.findViewById(R.id.no_other_total);
        mOtherWX = (TextView) mView.findViewById(R.id.no_other_wx);
        mOtherAli = (TextView) mView.findViewById(R.id.no_other_ali);
        mOtherUnion = (TextView) mView.findViewById(R.id.no_other_union);
        mOtherCash = (TextView) mView.findViewById(R.id.no_other_cash);
        mOtherElse = (TextView) mView.findViewById(R.id.no_other_else);
        mSwitchToMoney = (TextView) mView.findViewById(R.id.tv_switch_to_money);
        mSwitchToMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.styleMoney();
            }
        });

        mDataMoneyLayout = (LinearLayout) mView.findViewById(R.id.layout_statistic_money);
        mTotalAmount = (TextView) mView.findViewById(R.id.no_total_amount);
        mInternalDiscount = (TextView) mView.findViewById(R.id.no_internal_discount);
        mBonusCommission = (TextView) mView.findViewById(R.id.no_bonus_commission);
        mTotalFee = (TextView) mView.findViewById(R.id.no_total_fee);
        mTotalRefund = (TextView) mView.findViewById(R.id.no_total_refund);
        mGDSettle = (TextView) mView.findViewById(R.id.no_gd_settle);
        mFastPay = (TextView) mView.findViewById(R.id.no_fast_pay);
        mQRTech = (TextView) mView.findViewById(R.id.no_qr_tech);
        mQRClub = (TextView) mView.findViewById(R.id.no_qr_club);
        mQRPos = (TextView) mView.findViewById(R.id.no_qr_pos);
        mMemberRecharge = (TextView) mView.findViewById(R.id.no_member_recharge);
        mPaidOrder = (TextView) mView.findViewById(R.id.no_paid_order);
        mMarketing = (TextView) mView.findViewById(R.id.no_marketing);
        mPaidCoupon = (TextView) mView.findViewById(R.id.no_paid_coupon);
        mPaidServiceItem = (TextView) mView.findViewById(R.id.no_paid_service_item);
        mMall = (TextView) mView.findViewById(R.id.no_mall);
        mItemCard = (TextView) mView.findViewById(R.id.no_item_card);
        mItemPackage = (TextView) mView.findViewById(R.id.no_item_package);
        mPosSettle = (TextView) mView.findViewById(R.id.no_pos_settle);
        mPosSettleUnion = (TextView) mView.findViewById(R.id.no_pos_settle_union);
        mOtherAmount = (TextView) mView.findViewById(R.id.no_other_amount);
        mOtherInternalDiscount = (TextView) mView.findViewById(R.id.no_other_internal_discount);
        mOtherInternalRefund = (TextView) mView.findViewById(R.id.no_other_internal_refund);
        mOtherPos = (TextView) mView.findViewById(R.id.no_other_pos);
        mOtherPosCash = (TextView) mView.findViewById(R.id.no_other_pos_cash);
        mOtherMemberRecharge = (TextView) mView.findViewById(R.id.no_other_member_recharge);
        mOtherMemberRechargeWX = (TextView) mView.findViewById(R.id.no_other_member_wx);
        mOtherMemberRechargeAli = (TextView) mView.findViewById(R.id.no_other_member_ali);
        mOtherMemberRechargeUnion = (TextView) mView.findViewById(R.id.no_other_member_union);
        mOtherMemberRechargeCash = (TextView) mView.findViewById(R.id.no_other_member_cash);
        mOtherMemberRechargeElse = (TextView) mView.findViewById(R.id.no_other_member_else);
        mSwitchToSettle = (TextView) mView.findViewById(R.id.tv_switch_to_settle);
        mSwitchToSettle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.styleSettle();
            }
        });

        mErrorStub.setVisibility(View.VISIBLE);
        mDataErrorLayout = (LinearLayout) mView.findViewById(R.id.layout_error);
        mDataErrorText = (TextView) mView.findViewById(R.id.tv_error);
        mDataRetryText = (TextView) mView.findViewById(R.id.tv_click_error);
        mDataRetryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.loadData();
            }
        });
    }

    @Override
    public void setDataNormal(OnlineStatisticInfo online, OfflineStatisticInfo offline) {
        mDataNormalLayout.setVisibility(View.VISIBLE);
        mDataErrorLayout.setVisibility(View.GONE);
        mPresenter.setStyle();

        mSettleTotal.setText(mPresenter.formatAmount(online.totalSettleAmount));
        mSettleWX.setText(mPresenter.formatAmount(online.totalWx));
        mSettleAli.setText(mPresenter.formatAmount(online.totalAli));
        mSettleUnion.setText(mPresenter.formatAmount(online.totalUnion));

        mOtherTotal.setText(mPresenter.formatAmount(offline.totalRecharge + offline.cashPos));
        mOtherWX.setText(mPresenter.formatAmount(offline.wxMember));
        mOtherAli.setText(mPresenter.formatAmount(offline.aliMember));
        mOtherUnion.setText(mPresenter.formatAmount(offline.unionMember));
        mOtherCash.setText(mPresenter.formatAmount(offline.cashMember + offline.cashPos));
        mOtherElse.setText(mPresenter.formatAmount(offline.otherMember));

        mTotalAmount.setText(mPresenter.formatAmount(online.totalAmount));
        mInternalDiscount.setText("-￥" + Utils.moneyToStringEx(Math.abs(online.totalDiscount)));
        mBonusCommission.setText("-￥" + Utils.moneyToStringEx(Math.abs(online.internalCommission)));
        mTotalFee.setText("-￥" + Utils.moneyToStringEx(Math.abs(online.totalSettleFee)));
        mTotalRefund.setText("-￥" + Utils.moneyToStringEx(Math.abs(online.totalRefund)));
        mGDSettle.setText(mPresenter.formatAmount(online.totalSettleAmount));
        mFastPay.setText(mPresenter.formatAmount(online.fastPay));
        mQRTech.setText(mPresenter.formatAmount(online.qrTech));
        mQRClub.setText(mPresenter.formatAmount(online.qrClub));
        mQRPos.setText(mPresenter.formatAmount(online.qrPos));
        mMemberRecharge.setText(mPresenter.formatAmount(online.recharge));
        mPaidOrder.setText(mPresenter.formatAmount(online.paidOrder));
        mMarketing.setText(mPresenter.formatAmount(online.marketing));
        mPaidCoupon.setText(mPresenter.formatAmount(online.paidCoupon));
        mPaidServiceItem.setText(mPresenter.formatAmount(online.paidServiceItem));
        mMall.setText(mPresenter.formatAmount(online.mall));
        mItemCard.setText(mPresenter.formatAmount(online.itemCard));
        mItemPackage.setText(mPresenter.formatAmount(online.itemPackage));
        mPosSettle.setText(mPresenter.formatAmount(online.totalUnion));
        mPosSettleUnion.setText(mPresenter.formatAmount(online.totalUnion));
        mOtherAmount.setText(mPresenter.formatAmount(offline.cashPos + offline.totalRecharge + offline.totalDiscount + offline.totalRefund));
        mOtherInternalDiscount.setText("-￥" + Utils.moneyToStringEx(Math.abs(offline.totalDiscount)));
        mOtherInternalRefund.setText("-￥" + Utils.moneyToStringEx(Math.abs(offline.totalRefund)));
        mOtherPos.setText(mPresenter.formatAmount(offline.cashPos));
        mOtherPosCash.setText(mPresenter.formatAmount(offline.cashPos));
        mOtherMemberRecharge.setText(mPresenter.formatAmount(offline.totalRecharge));
        mOtherMemberRechargeWX.setText(mPresenter.formatAmount(offline.wxMember));
        mOtherMemberRechargeAli.setText(mPresenter.formatAmount(offline.aliMember));
        mOtherMemberRechargeUnion.setText(mPresenter.formatAmount(offline.unionMember));
        mOtherMemberRechargeCash.setText(mPresenter.formatAmount(offline.cashMember));
        mOtherMemberRechargeElse.setText(mPresenter.formatAmount(offline.otherMember));
    }

    @Override
    public void setDataError(String error) {
        mDataNormalLayout.setVisibility(View.GONE);
        mDataErrorLayout.setVisibility(View.VISIBLE);
        mDataErrorText.setText(error);
    }

    @Override
    public void showSettleStyle() {
        mDataSettleLayout.setVisibility(View.VISIBLE);
        mDataMoneyLayout.setVisibility(View.GONE);
    }

    @Override
    public void showMoneyStyle() {
        mDataSettleLayout.setVisibility(View.GONE);
        mDataMoneyLayout.setVisibility(View.VISIBLE);
    }
}
