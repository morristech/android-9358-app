package com.xmd.technician.window;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.Order;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.SpaService;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;


import com.xmd.technician.widget.StepView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

/**
 * Created by sdcm on 16-4-12.
 */
public class OrderDetailActivity extends BaseActivity {

    public static final String KEY_ORDER_ID = "orderId";
    public static final String KEY_ORDER = "order";

    @BindView(R.id.order_steps)
    StepView mOrderSteps;
    @BindView(R.id.avatar)
    CircleAvatarView mAvatar;
    @BindView(R.id.customer_name)
    TextView mCustomerName;
    @BindView(R.id.telephone)
    TextView mTelephone;
    @BindView(R.id.remain_time)
    TextView mRemainTime;
    @BindView(R.id.down_payment)
    TextView mDownPayment;
    @BindView(R.id.order_reward)
    TextView mOrderReward;
    @BindView(R.id.order_comment)
    TextView mOrderComment;
    @BindView(R.id.order_ratings)
    RatingBar mOrderRatings;
    @BindView(R.id.create_time)
    TextView mCreateTime;
    @BindView(R.id.service_price)
    TextView mServicePrice;
    @BindView(R.id.order_service)
    TextView mOrderService;
    @BindView(R.id.order_time)
    TextView mOrderTime;
    @BindView(R.id.action_telephone)
    TextView mActionTelephone;
    @BindView(R.id.action_chat)
    TextView mActionChat;
    @BindView(R.id.operation)
    LinearLayout mOperationLayout;
    @BindView(R.id.comment_section)
    LinearLayout mCommentSection;
    @BindView(R.id.remain_time_section)
    LinearLayout mRemainTimeSection;
    @BindView(R.id.paid_order_amount_container)
    View mPaidAmountContainer;
    @BindView(R.id.paid_amount_line)
    View mPaidAmountLine;

    @BindView(R.id.negative)
    Button mNegative;
    @BindView(R.id.positive)
    Button mPositive;

    private String mOrderId;
    private Order mOrder;
    private User mUser;
    private UserInfoService userService = UserInfoServiceImpl.getInstance();

    private Subscription getOrderRequest;

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
        if (obj != null) {
            mOrder = (Order) obj;
        } else {
            mOrderId = arguments.getString(KEY_ORDER_ID);
        }

        if (mOrder == null && mOrderId == null) {
            XToast.show("请传入Order或者OrderId!");
            finish();
            return;
        }

        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        setTitle(R.string.order_fragment_title);
        setBackVisible(true);

        if (mOrder != null) {
            initView();
        } else {
            loadOrderById();
        }
    }

    private void loadOrderById() {
        Observable<BaseBean<Order>> observable = XmdNetwork.getInstance()
                .getService(SpaService.class)
                .getOrderDetail(mOrderId);
        getOrderRequest = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<Order>>() {
            @Override
            public void onCallbackSuccess(BaseBean<Order> result) {
                mOrder = result.getRespData();
                initView();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show("加载订单失败！");
                finish();
            }
        });
    }

    private void initView() {

        setupStepView();
        setupButtons();
        if (!TextUtils.isEmpty(mOrder.emchatId)) {
            mUser = new User(mOrder.userId);
            mUser.setName(mOrder.userName);
            mUser.setChatId(mOrder.emchatId);
            mUser.setAvatar(mOrder.headImgUrl);
            mUser.setNoteName(Utils.isEmpty(mOrder.customerName) ? mOrder.userName : mOrder.customerName);
            userService.saveUser(mUser);
        }
        mAvatar.setUserInfo(mUser);
        mCustomerName.setText(mOrder.customerName);
        mTelephone.setText(mOrder.phoneNum);
        mRemainTime.setText(mOrder.remainTime.contains("-") ? "0" : mOrder.remainTime);

        mDownPayment.setText(String.format(ResourceUtils.getString(R.string.amount_unit_format), mOrder.downPayment));
        mOrderTime.setText(mOrder.formatAppointTime);

        mOrderService.setText(mOrder.serviceName);
        mServicePrice.setText(TextUtils.isEmpty(mOrder.servicePrice) ? getString(R.string.order_detail_service_price_pending) : mOrder.servicePrice);
        mCreateTime.setText(mOrder.formatCreateTime);

        if (Constant.ORDER_STATUS_COMPLETE.equals(mOrder.status)) {

            mOrderComment.setText(mOrder.comment);
            mOrderRatings.setRating(mOrder.rating);
            if (mOrder.rewardAmount > 0) {
                float reward = mOrder.rewardAmount / 100f;
                mOrderReward.setText(String.format("%1.2f元", reward));
            } else {
                mOrderReward.setText("0.0");
            }

        } else {
            mCommentSection.setVisibility(View.GONE);
        }

        if (!Constant.ORDER_TYPE_PAID.equals(mOrder.orderType)) {
            mPaidAmountContainer.setVisibility(View.GONE);
            mPaidAmountLine.setVisibility(View.GONE);
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
            if (Constant.ORDER_TYPE_PAID.equals(mOrder.orderType)) {
                mNegative.setVisibility(View.GONE);
                mPositive.setVisibility(View.GONE);
            }
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
                    doHideOrder(ResourceUtils.getString(R.string.order_detail_delete_order_confirm), mOrder);
                }
                break;
            case R.id.action_telephone:
                doMakeCall();
                break;
            case R.id.action_chat:
                if (TextUtils.isEmpty(mOrder.emchatId)) {
                    XToast.show("缺少用户聊天信息，无法聊天");
                } else {
                    EventBus.getDefault().post(new EventStartChatActivity(mOrder.emchatId));
                }
                break;
        }
    }

    /**
     * 删除订单，退出此界面
     *
     * @param type
     * @param order
     * @param reason
     */
    private void doNegativeOrder(String description, String type, Order order, String reason) {
        new AlertDialogBuilder(this)
                .setMessage(description)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> doManageOrder(type, order, reason))
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    private void doHideOrder(String description, Order order) {
        new AlertDialogBuilder(this)
                .setMessage(description)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_HIDE_ORDER, order.orderId);
                    finish();
                })
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    /**
     * 操作订单
     *
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Constant.REQUEST_CODE_FOR_ORDER_DETAIL_ACTIVITY);
            return;
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mOrder.phoneNum)));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getOrderRequest);
    }
}
