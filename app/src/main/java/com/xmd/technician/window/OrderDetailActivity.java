package com.xmd.technician.window;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.beans.Order;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.StepView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by sdcm on 16-4-12.
 */
public class OrderDetailActivity extends BaseActivity {

    public static final String KEY_ORDER = "order";

    @Bind(R.id.order_steps)
    StepView mOrderSteps;
    @Bind(R.id.avatar)
    CircleImageView mAvatar;
    @Bind(R.id.customer_name)
    TextView mCustomerName;
    @Bind(R.id.telephone)
    TextView mTelephone;
    @Bind(R.id.remain_time)
    TextView mRemainTime;
    @Bind(R.id.down_payment)
    TextView mDownPayment;
    @Bind(R.id.order_reward)
    TextView mOrderReward;
    @Bind(R.id.order_comment)
    TextView mOrderComment;
    @Bind(R.id.order_ratings)
    RatingBar mOrderRatings;
    @Bind(R.id.create_time)
    TextView mCreateTime;
    @Bind(R.id.service_price)
    TextView mServicePrice;
    @Bind(R.id.order_service)
    TextView mOrderService;
    @Bind(R.id.order_time)
    TextView mOrderTime;
    @Bind(R.id.action_telephone)
    TextView mActionTelephone;
    @Bind(R.id.action_chat)
    TextView mActionChat;
    @Bind(R.id.operation)
    LinearLayout mOperationLayout;
    @Bind(R.id.comment_section) LinearLayout mCommentSection;
    @Bind(R.id.remain_time_section) LinearLayout mRemainTimeSection;

    @Bind(R.id.negative)     Button mNegative;
    @Bind(R.id.positive) Button mPositive;

    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle arguments = intent.getExtras();
        if (arguments == null) {
            makeShortToast("Order should be sent as parameters to this activity");
            finish();
        }

        Object obj = arguments.get(KEY_ORDER);
        if (!(obj instanceof Order)) {
            makeShortToast("Order should be sent to this activity");
            finish();
        }

        mOrder = (Order) obj;

        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        setTitle(R.string.order_fragment_title);
        setBackVisible(true);
        initView();
    }

    private void initView() {

        setupStepView();
        setupButtons();

        //Avatar
        Glide.with(this).load(mOrder.headImgUrl).into(mAvatar);
        mAvatar.setOnClickListener(v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, mOrder.emchatId));
        mCustomerName.setText(mOrder.customerName);
        mTelephone.setText(mOrder.phoneNum);
        mRemainTime.setText(mOrder.remainTime);

        mDownPayment.setText(String.format(ResourceUtils.getString(R.string.amount_unit_format), mOrder.downPayment));
        mOrderTime.setText(mOrder.formatAppointTime);

        mOrderService.setText(mOrder.serviceName);
        mServicePrice.setText(mOrder.servicePrice);
        mCreateTime.setText(mOrder.formatCreateTime);

        if (Constant.ORDER_STATUS_COMPLETE.equals(mOrder.status)) {
            mOrderComment.setText(mOrder.comment);
            mOrderRatings.setRating(mOrder.rating);
            mOrderReward.setText(String.format(ResourceUtils.getString(R.string.amount_unit_format), mOrder.rewardAmount));
        } else {
            mCommentSection.setVisibility(View.GONE);
        }

    }

    private void setupStepView() {
        String descSubmit = ResourceUtils.getString(R.string.order_status_description_submit);
        String descAccept = ResourceUtils.getString(R.string.order_status_description_accept);
        String descReject = ResourceUtils.getString(R.string.order_status_description_reject);
        String descComplete = ResourceUtils.getString(R.string.order_status_description_complete);
        String descFailure = ResourceUtils.getString(R.string.order_status_description_failure);
        String descOvertime = ResourceUtils.getString(R.string.order_status_description_overtime);

        if (Constant.ORDER_STATUS_SUBMIT.equals(mOrder.status)) {
            mOrderSteps.setup(0, new String[]{descSubmit, descAccept, descComplete});
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(mOrder.status)) {
            mOrderSteps.setup(1, new String[]{descSubmit, descAccept, descComplete});
        } else if (Constant.ORDER_STATUS_OVERTIME.equals(mOrder.status)) {
            mOrderSteps.setup(1, new String[]{descSubmit, descOvertime, descComplete});
        } else if (Constant.ORDER_STATUS_REJECTED.equals(mOrder.status)) {
            mOrderSteps.setup(1, new String[]{descSubmit, descReject, descComplete});
        } else if (Constant.ORDER_STATUS_COMPLETE.equals(mOrder.status)) {
            mOrderSteps.setup(2, new String[]{descSubmit, descAccept, descComplete});
        } else if (Constant.ORDER_STATUS_FAILURE.equals(mOrder.status)) {
            mOrderSteps.setup(2, new String[]{descSubmit, descAccept, descFailure});
        }
    }

    private void setupButtons() {
        if (Constant.ORDER_STATUS_SUBMIT.equals(mOrder.status)) {
            mNegative.setText(ResourceUtils.getString(R.string.order_status_operation_reject));
            mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_accept));
            mRemainTimeSection.setVisibility(View.VISIBLE);
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(mOrder.status)) {
            mNegative.setText(ResourceUtils.getString(R.string.order_status_operation_expire));
            mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_complete));
        } else {
            mNegative.setVisibility(View.GONE);
            mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_delete));
        }
    }

    @OnClick({R.id.negative, R.id.positive, R.id.action_telephone, R.id.action_chat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.negative:
                if (Constant.ORDER_STATUS_SUBMIT.equals(mOrder.status)) {
                    doNegativeOrder(ResourceUtils.getString(R.string.order_detail_reject_order_confirm), Constant.ORDER_STATUS_REJECTED, mOrder, "");
                } else if (Constant.ORDER_STATUS_ACCEPT.equals(mOrder.status)) {
                    doNegativeOrder(ResourceUtils.getString(R.string.order_detail_failure_order_confirm), Constant.ORDER_STATUS_FAILURE, mOrder, "");
                }
                break;
            case R.id.positive:
                if (Constant.ORDER_STATUS_SUBMIT.equals(mOrder.status)) {
                    doManageOrder(Constant.ORDER_STATUS_ACCEPT, mOrder, "");
                } else if (Constant.ORDER_STATUS_ACCEPT.equals(mOrder.status)) {
                    doManageOrder(Constant.ORDER_STATUS_COMPLETE, mOrder, "");
                } else {
                    doNegativeOrder(ResourceUtils.getString(R.string.order_detail_delete_order_confirm), Constant.ORDER_STATUS_DELETE, mOrder, "");
                }
                break;
            case R.id.action_telephone:
                doMakeCall();
                break;
            case R.id.action_chat:
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, mOrder.emchatId);
                break;
        }
    }

    /**
     * 删除订单，退出此界面
     * @param type
     * @param order
     * @param reason
     */
    private void doNegativeOrder(String description, String type, Order order, String reason) {
        new AlertDialogBuilder(this)
                .setMessage(description)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> doManageOrder(type, order,reason))
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    /**
     * 操作订单
     * @param type
     * @param order
     * @param reason
     */
    private void doManageOrder(String type, Order order, String reason) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, order.orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
        finish();
    }

    /**
     * 打电话
     */
    private void doMakeCall() {
        new AlertDialogBuilder(this)
                .setMessage(mOrder.phoneNum)
                .setPositiveButton(ResourceUtils.getString(R.string.make_call), v -> doInternalMakeCall())
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    private void doInternalMakeCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, Constant.REQUEST_CODE_FOR_ORDER_DETAIL_ACTIVITY);
            return;
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mTelephone)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Constant.REQUEST_CODE_FOR_ORDER_DETAIL_ACTIVITY == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doInternalMakeCall();
            } else {
                makeShortToast("请允许本应用调用你的打电话功能");
            }
        }
    }
}
