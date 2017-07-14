package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.m.comment.bean.AllGroupListBean;
import com.xmd.m.comment.bean.UserGroupListBean;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.auth.AuthConstants;
import com.xmd.manager.auth.AuthHelper;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.WidgetUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CustomerResult;
import com.xmd.manager.service.response.UserEditGroupResult;
import com.xmd.manager.service.response.UserGroupSaveResult;
import com.xmd.manager.widget.CircularBeadImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class CustomerActivity extends BaseActivity {

    public static final String ARG_CUSTOMER = "arg_customer";

    private Customer mCustomer;
    private String mCustomerUserId;

    @BindView(R.id.civ_avatar)
    CircularBeadImageView mCivAvatar;
    @BindView(R.id.tv_customer_name)
    TextView mTvCustomerName;
    @BindView(R.id.tv_customer_type)
    TextView mTvCustomerType;
    @BindView(R.id.tv_telephone)
    TextView mTvTelephone;
    @BindView(R.id.tv_technician_name)
    TextView mTvTechnicianName;
    @BindView(R.id.tv_last_visit)
    TextView mTvLastVisit;
    @BindView(R.id.tv_orders_count)
    TextView mTvOrdersCount;
    @BindView(R.id.tv_comments_count)
    TextView mTvCommentsCount;
    @BindView(R.id.tv_coupons_count)
    TextView mTvCouponsCount;
    @BindView(R.id.ll_btn_container)
    LinearLayout mFunBtnContainer;
    @BindView(R.id.btn_chat)
    Button mBtnChat;
    @BindView(R.id.btn_sms)
    Button mBtnSms;
    @BindView(R.id.btn_coupon)
    Button mBtnCoupon;
    @BindView(R.id.layout_group_data)
    LinearLayout layoutGroupData;
    @BindView(R.id.customer_group_data)
    TextView mCustomerGroupData;

    private Subscription mGetCustomerViewSubscription;
    private Subscription mGetCustomerGroupSubscription;
    private Subscription mGroupSaveEditSubscription;
    private List<String> allGroups;
    private List<String> userGroups;
    private List<AllGroupListBean> allGroupList;
    private List<UserGroupListBean> userGroupList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomer = (Customer) getIntent().getSerializableExtra(ARG_CUSTOMER);
        mCustomerUserId = getIntent().getStringExtra(RequestConstant.COMMENT_USER_ID);

        if (Utils.isEmpty(mCustomerUserId)) {
            if (mCustomer == null) {
                finish();
            } else {
                mCustomerUserId = mCustomer.userId;
            }
        }
        setContentView(R.layout.activity_customer);
        allGroups = new ArrayList<>();
        userGroups = new ArrayList<>();
        allGroupList = new ArrayList<>();
        userGroupList = new ArrayList<>();

        mGetCustomerViewSubscription = RxBus.getInstance().toObservable(CustomerResult.class).subscribe(
                customerResult -> initContent(customerResult.respData)
        );
        mGetCustomerGroupSubscription = RxBus.getInstance().toObservable(UserEditGroupResult.class).subscribe(
                userGroup -> handlerUserGroup(userGroup)
        );
        mGroupSaveEditSubscription = RxBus.getInstance().toObservable(UserGroupSaveResult.class).subscribe(
                saveResult -> handlerSaveGroupResult(saveResult)
        );
        getCustomerViewAddGroup();
    }

    private void handlerSaveGroupResult(UserGroupSaveResult saveResult) {
        if (saveResult.statusCode == 200) {
            getCustomerViewAddGroup();
        }
    }

    private void handlerUserGroup(UserEditGroupResult userGroup) {
        if (userGroup.statusCode == 200) {
            userGroups.clear();
            allGroups.clear();
            userGroupList.clear();
            allGroupList.clear();

            if (userGroup.respData.userGroupList != null && userGroup.respData.userGroupList.size() > 0) {
                for (int i = 0; i < userGroup.respData.userGroupList.size(); i++) {
                    userGroups.add(userGroup.respData.userGroupList.get(i).name);
                }
                userGroupList.clear();
                userGroupList.addAll(userGroup.respData.userGroupList);
            }
            if (userGroup.respData.allGroupList != null && userGroup.respData.allGroupList.size() > 0) {
                for (int i = 0; i < userGroup.respData.allGroupList.size(); i++) {
                    allGroups.add(userGroup.respData.allGroupList.get(i).name);
                }
                allGroupList.clear();
                allGroupList.addAll(userGroup.respData.allGroupList);
            }
            if (userGroups.size() > 0) {
                mCustomerGroupData.setText(Utils.listToString(userGroups));
            } else {
                mCustomerGroupData.setText(ResourceUtils.getString(R.string.customer_group_is_null));
            }

        }
    }

    private void getCustomerViewAddGroup() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_ID, mCustomerUserId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_CUSTOMER_DETAIL_VIEW, params);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_USER_EDIT_GROUP, mCustomerUserId);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_VIEW, mCustomerUserId);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCustomerViewSubscription, mGetCustomerGroupSubscription, mGroupSaveEditSubscription);
    }

    private void initContent(Customer customer) {
        if (customer == null) {
            return;
        }
        mCustomer = customer;
        if (TextUtils.isEmpty(customer.userName)) {
            customer.userName = "匿名用户";
        }
        setTitle(Utils.briefString(customer.userName, 8));
        if (Utils.isNotEmpty(customer.userHeadimgurl)) {
            Glide.with(this).load(customer.userHeadimgurl).placeholder(ResourceUtils.getDrawable(R.drawable.icon22)).error(R.drawable.icon22).into(mCivAvatar);
        }

        mTvCustomerName.setText(customer.userName);
        mTvTelephone.setText(customer.phoneNum);

        mTvLastVisit.setText(customer.loginDate);
        if (!"tech".equals(customer.userType)) {
            if (Utils.isNotEmpty(customer.techName) && Utils.isNotEmpty(customer.techSerialNo)) {
                String tech = String.format("%s[%s]", customer.techName, customer.techSerialNo);
                mTvTechnicianName.setText(Utils.changeColor(tech, R.color.customer_comment_reward_text_color, customer.techName.length(), tech.length()));
            } else if (Utils.isNotEmpty(customer.techName)) {
                mTvTechnicianName.setText(customer.techName);
            } else {
                mTvTechnicianName.setText("无");
            }

        } else {
            mTvTechnicianName.setText("无");
        }

        if (Constant.CUSTOMER_TYPE_WEIXIN.equals(customer.userType)) {
            mTvCustomerType.setText("微信");
            mTvCustomerType.setTextColor(ResourceUtils.getColor(R.color.customer_type_label_weixin));
            mTvCustomerType.setBackgroundResource(R.drawable.customer_type_weixin);
        } else if (Constant.CUSTOMER_TYPE_TEMP.equals(customer.userType) || Constant.CUSTOMER_TYPE_TEMP_TECH.equals(customer.userType)) {
            mTvCustomerType.setText("领券");
            mTvCustomerType.setTextColor(ResourceUtils.getColor(R.color.customer_type_label_temp));
            mTvCustomerType.setBackgroundResource(R.drawable.customer_type_temp);
        } else {
            layoutGroupData.setVisibility(View.VISIBLE);
            mTvCustomerType.setText("粉丝");
            mTvCustomerType.setTextColor(ResourceUtils.getColor(R.color.customer_type_label_user));
            mTvCustomerType.setBackgroundResource(R.drawable.customer_type_user);
        }

        mTvOrdersCount.setText(String.format(getString(R.string.value_with_braces), String.valueOf(customer.orderCount)));
        mTvCommentsCount.setText(String.format(getString(R.string.value_with_braces), String.valueOf(customer.commentCount)));
        mTvCouponsCount.setText(String.format(getString(R.string.value_with_braces), String.valueOf(customer.couponCount)));

        WidgetUtils.setViewVisibleOrGone(mBtnChat, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_CHAT) && Utils.isNotEmpty(mCustomer.emchatId));
    }

    @OnClick({R.id.rl_orders, R.id.rl_comments, R.id.rl_coupons, R.id.btn_sms, R.id.btn_chat, R.id.btn_coupon, R.id.layout_group_data})
    public void onClicked(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_coupons:
                intent = new Intent(this, CustomerCouponsActivity.class);
                break;
            case R.id.rl_comments:
                intent = new Intent(this, CustomerCommentsActivity.class);
                break;
            case R.id.rl_orders:
                intent = new Intent(this, CustomerOrdersActivity.class);
                break;
            case R.id.btn_coupon:
                intent = new Intent(this, DeliveryCouponActivity.class);
                intent.putExtra(DeliveryCouponActivity.KEY_FROM, mCustomer);
                break;
            case R.id.btn_chat:
//                EmchatUserHelper.startToChat(mCustomer.emchatId, mCustomer.userName, mCustomer.userHeadimgurl);
                EventBus.getDefault().post(new EventStartChatActivity(mCustomer.emchatId));
                break;
            case R.id.btn_sms:
                Utils.invokeAndroidSms(this, mCustomer.phoneNum, "");
                break;
            case R.id.layout_group_data:
                Intent intentAdd = new Intent(this, AddGroupActivity.class);
                intentAdd.putExtra(Constant.KEY_USER_GROUPS, (Serializable) userGroupList);
                intentAdd.putExtra(Constant.KEY_ALL_GROUPS, (Serializable) allGroupList);
                intentAdd.putExtra(Constant.KEY_USER_ID, mCustomerUserId);
                startActivity(intentAdd);
                break;
        }

        if (intent != null && mCustomer != null) {
            intent.putExtra(Constant.PARAM_CUSTOMER_ID, mCustomer.userId);
            startActivity(intent);
        }
    }

}
