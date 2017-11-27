package com.xmd.manager.window;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.manager.R;
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

public class TechSalaryDialogActivity extends BaseActivity {
    private static final String BUSINESS_TYPE_SPA = "spa";
    private static final String BUSINESS_TYPE_GOODS = "goods";
    private static final String BUSINESS_TYPE_CARD = "item_card";
    private static final String BUSINESS_TYPE_PACKAGE = "item_package";
    private static final String BUSINESS_TYPE_PAID_ORDER = "paid_order";
    private static final String BUSINESS_TYPE_RECHARGE = "recharge";

    @BindView(R.id.tv_error_desc)
    TextView mErrorDesc;

    @BindView(R.id.sv_data_layout)
    ScrollView mScrollLayout;
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
    @BindView(R.id.tv_origin_amount)
    TextView mOriginAmount;
    @BindView(R.id.tv_commission_amount)
    TextView mCommissionAmount;
    @BindView(R.id.tv_user)
    TextView mUser;


    public static final String EXTRA_TECH_COMMISSION_ID = "commission_id";
    private String mCommissionId;

    private Subscription mGetTechCommissionDetailInfoSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_salary_dialog);
        mCommissionId = getIntent().getStringExtra(EXTRA_TECH_COMMISSION_ID);

        mGetTechCommissionDetailInfoSubscription = RxBus.getInstance().toObservable(TechCommissionDetailResult.class).subscribe(
                techCommissionDetailResult -> handleTechCommissionDetailInfo(techCommissionDetailResult)
        );

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_COMMISSION_DETAIL_INFO, mCommissionId);
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
                        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(TechSalaryDialogActivity.this, detailInfo.userId, ConstantResources.APP_TYPE_MANAGER, false));
            } else {
                mUser.setText("散客");
            }
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
                    // TODO 预约类型 预约定金
                    break;
                case BUSINESS_TYPE_RECHARGE:    //会员充值
                    mNameTitle.setText("活动名称：");
                    mName.setText(detailInfo.businessName);
                    // TODO 活动名称 充值金额
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
