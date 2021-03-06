package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.inner.widget.FullyGridLayoutManager;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.manager.R;
import com.xmd.manager.adapter.ReportTechDetailAdapter;
import com.xmd.manager.beans.CashierClubDetailInfo;
import com.xmd.manager.beans.CommissionTechDetailInfo;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.TechCommissionDetailResult;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-11-25.
 */

public class ReportDetailDialogActivity extends BaseActivity {
    private static final String BUSINESS_TYPE_SPA = "spa";
    private static final String BUSINESS_TYPE_GOODS = "goods";
    private static final String BUSINESS_TYPE_CARD = "item_card";
    private static final String BUSINESS_TYPE_PACKAGE = "item_package";
    private static final String BUSINESS_TYPE_PAID_ORDER = "paid_order";
    private static final String BUSINESS_TYPE_RECHARGE = "recharge";

    @BindView(R.id.tv_error_desc)
    TextView mErrorDesc;

    @BindView(R.id.sv_data_layout)
    NestedScrollView mScrollLayout;
    @BindView(R.id.tv_time)
    TextView mTime;
    @BindView(R.id.tr_flow)
    TableRow mFlowRow;
    @BindView(R.id.tv_flow)
    TextView mFlowNo;
    @BindView(R.id.tv_trade_no)
    TextView mTradeNo;
    @BindView(R.id.tr_pay_channel)
    TableRow mPayChannelRow;
    @BindView(R.id.tv_pay_channel)
    TextView mPayChannel;
    @BindView(R.id.tr_room)
    TableRow mRoomRow;
    @BindView(R.id.tv_room)
    TextView mRoom;
    @BindView(R.id.tr_identify)
    TableRow mIdentifyRow;
    @BindView(R.id.tv_identify)
    TextView mIdentify;
    @BindView(R.id.tv_name_title)
    TextView mNameTitle;
    @BindView(R.id.tv_name)
    TextView mName;
    @BindView(R.id.tr_count)
    TableRow mCountRow;
    @BindView(R.id.tv_count)
    TextView mCount;
    @BindView(R.id.tv_tech_title)
    TextView mTechTitle;
    @BindView(R.id.tv_tech)
    TextView mTech;
    @BindView(R.id.tr_bell_type)
    TableRow mBellTypeRow;
    @BindView(R.id.tv_bell_type)
    TextView mBellType;
    @BindView(R.id.tr_origin_amount)
    TableRow mOriginAmountRow;
    @BindView(R.id.tv_origin_amount_title)
    TextView mOriginAmountTitle;
    @BindView(R.id.tv_origin_amount)
    TextView mOriginAmount;
    @BindView(R.id.tr_commission_amount)
    TableRow mCommissionRow;
    @BindView(R.id.tv_commission_amount)
    TextView mCommissionAmount;
    @BindView(R.id.tv_user)
    TextView mUser;
    @BindView(R.id.rv_tech_detail_list)
    RecyclerView mTechDetailList;

    public static final String TYPE_DETAIL_SALARY = "type_salary";
    public static final String TYPE_DETAIL_CASHIER = "type_cashier";

    public static final String EXTRA_TYPE_DETAIL = "type_detail";
    public static final String EXTRA_TECH_COMMISSION_ID = "commission_id";
    public static final String EXTRA_CASHIER_DETAIL_INFO = "cashier_info";
    private String mType;
    private CashierClubDetailInfo mCashierDetailInfo;
    private String mCommissionId;
    private Subscription mGetTechCommissionDetailInfoSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail_dialog);
        mType = getIntent().getStringExtra(EXTRA_TYPE_DETAIL);
        switch (mType) {
            case TYPE_DETAIL_CASHIER:
                mCashierDetailInfo = (CashierClubDetailInfo) getIntent().getSerializableExtra(EXTRA_CASHIER_DETAIL_INFO);
                handleCashierDetailInfo(mCashierDetailInfo);
                break;
            case TYPE_DETAIL_SALARY:
                mCommissionId = getIntent().getStringExtra(EXTRA_TECH_COMMISSION_ID);
                mGetTechCommissionDetailInfoSubscription = RxBus.getInstance().toObservable(TechCommissionDetailResult.class).subscribe(
                        techCommissionDetailResult -> handleTechCommissionDetailInfo(techCommissionDetailResult)
                );
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_COMMISSION_DETAIL_INFO, mCommissionId);
                break;
            default:
                break;
        }
    }

    private void handleCashierDetailInfo(CashierClubDetailInfo info) {
        if (info != null) {
            mErrorDesc.setVisibility(View.GONE);
            mScrollLayout.setVisibility(View.VISIBLE);
            mTime.setText(info.orderTime);      //时间
            if (!TextUtils.isEmpty(info.payNo)) {   //流水号
                mFlowRow.setVisibility(View.VISIBLE);
                mFlowNo.setText(info.payNo);
            } else {
                mFlowRow.setVisibility(View.GONE);
            }
            mTradeNo.setText(info.batchNo);     //交易号
            if (!TextUtils.isEmpty(info.payChannel)) {    //支付方式
                mPayChannelRow.setVisibility(View.VISIBLE);
                mPayChannel.setText(info.payChannel);
            } else {
                mPayChannelRow.setVisibility(View.GONE);
            }
            mRoomRow.setVisibility(View.VISIBLE);
            mRoom.setText(info.roomName);
            mIdentifyRow.setVisibility(View.VISIBLE);
            mIdentify.setText(info.userIdentify);
            if (!TextUtils.isEmpty(info.userName)) {
                mUser.setText(info.userName);
                mUser.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
                mUser.setOnClickListener(v ->
                        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(ReportDetailDialogActivity.this, info.userId, ConstantResources.APP_TYPE_MANAGER, false));
            } else {
                mUser.setText("散客");
            }
            switch (info.scope) {
                case BUSINESS_TYPE_GOODS:
                    mNameTitle.setText("实物商品：");
                    mName.setText(info.itemName);
                    mCountRow.setVisibility(View.VISIBLE);
                    mCount.setText(String.valueOf(info.count));
                    mOriginAmountRow.setVisibility(View.VISIBLE);
                    mOriginAmount.setText(Utils.moneyToStringEx(info.totalAmount) + "元");
                    mOriginAmountTitle.setText("订单金额：");
                    break;
                case BUSINESS_TYPE_SPA:
                    mNameTitle.setText("所选项目：");
                    mName.setText(info.itemName);
                    mOriginAmountRow.setVisibility(View.VISIBLE);
                    mOriginAmount.setText(Utils.moneyToStringEx(info.totalAmount) + "元");
                    mOriginAmountTitle.setText("订单金额：");
                    break;
                default:
                    break;
            }
            mCommissionRow.setVisibility(View.GONE);
            if (info.techList != null && !info.techList.isEmpty()) {
                mTechDetailList.setVisibility(View.VISIBLE);
                ReportTechDetailAdapter detailAdapter = new ReportTechDetailAdapter(ReportDetailDialogActivity.this, info.scope);
                detailAdapter.setCallBack(techInfo -> {
                    Intent intent = new Intent(ReportDetailDialogActivity.this, TechSalaryDetailActivity.class);
                    intent.putExtra(TechSalaryDetailActivity.EXTRA_CURRENT_DATE, mCashierDetailInfo.orderTime.substring(0, 10));
                    intent.putExtra(TechSalaryDetailActivity.EXTRA_TECH_ID, techInfo.techId);
                    intent.putExtra(TechSalaryDetailActivity.EXTRA_TECH_NAME, techInfo.techName);
                    intent.putExtra(TechSalaryDetailActivity.EXTRA_TECH_NO, techInfo.techNo);
                    startActivity(intent);
                    finish();
                });

                mTechDetailList.setHasFixedSize(true);
                mTechDetailList.setNestedScrollingEnabled(false);
                mTechDetailList.setLayoutManager(new FullyGridLayoutManager(ReportDetailDialogActivity.this, 1));
                mTechDetailList.setItemAnimator(new DefaultItemAnimator());
                mTechDetailList.setAdapter(detailAdapter);
                detailAdapter.setData(info.techList);
            } else {
                mTechDetailList.setVisibility(View.GONE);
            }
        } else {
            mScrollLayout.setVisibility(View.GONE);
            mErrorDesc.setVisibility(View.VISIBLE);
            mErrorDesc.setText("获取详情失败");
        }
    }

    private void handleTechCommissionDetailInfo(TechCommissionDetailResult result) {
        if (result.statusCode == 200) {
            mErrorDesc.setVisibility(View.GONE);
            mScrollLayout.setVisibility(View.VISIBLE);

            CommissionTechDetailInfo detailInfo = result.respData;
            mTime.setText(DateUtil.long2Date(detailInfo.createTime, "yyyy-MM-dd HH:mm:ss"));    //时间
            if (!TextUtils.isEmpty(detailInfo.tradeNo)) {   //交易流水
                mFlowRow.setVisibility(View.VISIBLE);
                mFlowNo.setText(detailInfo.tradeNo);
            } else {
                mFlowRow.setVisibility(View.GONE);
            }
            mTradeNo.setText(detailInfo.dealNo);    //交易号
            if (!TextUtils.isEmpty(detailInfo.payChannelName)) {    //支付方式
                mPayChannelRow.setVisibility(View.VISIBLE);
                mPayChannel.setText(detailInfo.payChannelName);
            } else {
                mPayChannelRow.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(detailInfo.userName)) {
                mUser.setText(detailInfo.userName);
                mUser.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
                mUser.setOnClickListener(v ->
                        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(ReportDetailDialogActivity.this, detailInfo.userId, ConstantResources.APP_TYPE_MANAGER, false));
            } else {
                mUser.setText("散客");
            }
            mCommissionRow.setVisibility(View.VISIBLE);
            mCommissionAmount.setText(Utils.moneyToStringEx(detailInfo.totalCommission) + "元");
            switch (detailInfo.businessType) {
                case BUSINESS_TYPE_GOODS:   //实物商品
                    mRoomRow.setVisibility(View.VISIBLE);
                    mRoom.setText(detailInfo.roomName);
                    mIdentifyRow.setVisibility(View.VISIBLE);
                    mIdentify.setText(detailInfo.userIdentify);
                    mNameTitle.setText("实物商品：");
                    mName.setText(detailInfo.businessName);
                    mCountRow.setVisibility(View.VISIBLE);
                    mCount.setText(String.valueOf(detailInfo.count));
                    break;
                case BUSINESS_TYPE_SPA:     //足浴按摩
                    mRoomRow.setVisibility(View.VISIBLE);
                    mRoom.setText(detailInfo.roomName);
                    mIdentifyRow.setVisibility(View.VISIBLE);
                    mIdentify.setText(detailInfo.userIdentify);
                    mNameTitle.setText("所选项目：");
                    mName.setText(detailInfo.businessName);
                    mBellTypeRow.setVisibility(View.VISIBLE);
                    mBellType.setText(detailInfo.bellName);
                    break;
                case BUSINESS_TYPE_CARD:    //项目次卡
                case BUSINESS_TYPE_PACKAGE: //项目套餐
                    mNameTitle.setText("活动名称：");
                    mName.setText(detailInfo.businessName);
                    mCountRow.setVisibility(View.VISIBLE);
                    mCount.setText(String.valueOf(detailInfo.count));
                    break;
                case BUSINESS_TYPE_PAID_ORDER:  //付费预约
                    mNameTitle.setText("预约类型：");
                    mName.setText(detailInfo.businessName);
                    mOriginAmountRow.setVisibility(View.VISIBLE);
                    mOriginAmountTitle.setText("预约定金：");
                    mOriginAmount.setText(Utils.moneyToStringEx(detailInfo.price) + "元");
                    break;
                case BUSINESS_TYPE_RECHARGE:    //会员充值
                    mNameTitle.setText("活动名称：");
                    mName.setText(detailInfo.businessName);
                    mOriginAmountRow.setVisibility(View.VISIBLE);
                    mOriginAmountTitle.setText("充值金额：");
                    mOriginAmount.setText(Utils.moneyToStringEx(detailInfo.price) + "元");
                    break;
                default:
                    break;
            }
        } else {
            mScrollLayout.setVisibility(View.GONE);
            mErrorDesc.setVisibility(View.VISIBLE);
            mErrorDesc.setText(result.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechCommissionDetailInfoSubscription);
    }

    @OnClick({R.id.img_dialog_close, R.id.btn_dialog_confirm})
    public void onDialogClose() {
        finish();
    }
}
