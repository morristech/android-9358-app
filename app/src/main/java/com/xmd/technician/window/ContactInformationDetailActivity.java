package com.xmd.technician.window;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ContactPermissionInfo;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.DeleteContactResult;
import com.xmd.technician.bean.ManagerDetailResult;
import com.xmd.technician.bean.OrderBean;
import com.xmd.technician.bean.SayHiResult;
import com.xmd.technician.bean.TechDetailResult;
import com.xmd.technician.bean.VisitBean;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ContactPermissionResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.DropDownMenuDialog;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.RoundImageView;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/7/5.
 */
public class ContactInformationDetailActivity extends BaseActivity {
    @Bind(R.id.customer_head)
    RoundImageView mContactHead;
    @Bind(R.id.tv_customer_name)
    TextView mContactName;
    @Bind(R.id.tv_customer_telephone)
    TextView mContactTelephone;
    @Bind(R.id.tv_customer_nick_name)
    TextView mContactNickName;
    @Bind(R.id.tv_customer_remark)
    TextView mContactRemark;
    @Bind(R.id.customer_order)
    TextView mContactOrder;
    @Bind(R.id.customer_reward)
    TextView mContactReward;
    @Bind(R.id.ll_customer_order)
    LinearLayout mContactOrderLayout;
    @Bind(R.id.order_time)
    TextView orderTime;
    @Bind(R.id.order_state)
    TextView orderState;
    @Bind(R.id.order_item_detail)
    TextView orderItem;
    @Bind(R.id.rl_order)
    RelativeLayout rlOrder;
    @Bind(R.id.order2_time)
    TextView order2Time;
    @Bind(R.id.order2_state)
    TextView order2State;
    @Bind(R.id.order2_item_detail)
    TextView order2Item;
    @Bind(R.id.rl_order2)
    RelativeLayout rlOrder2;
    @Bind(R.id.btn_chat)
    Button btnChat;
    @Bind(R.id.btn_call_phone)
    Button btnCallPhone;
    @Bind(R.id.btn_EmChat)
    Button btnEmChat;
    @Bind(R.id.btn_EmHello)
    Button btnEmHello;
    @Bind(R.id.order_empty_alter)
    TextView orderEmpty;
    @Bind(R.id.contact_more)
    LinearLayout contactMore;
    @Bind(R.id.remark_alert)
    TextView textRemarkAlert;
    @Bind(R.id.ll_tech_number)
    LinearLayout llTechNum;
    @Bind(R.id.tech_number)
    TextView techNum;
    @Bind(R.id.view_div)
    View divView;
    @Bind(R.id.register_alert)
    TextView registerAlert;
    @Bind(R.id.ll_marker_message)
    LinearLayout markMessageLayout;
    @Bind(R.id.text_contact_marker)
    TextView contactMark;
    @Bind(R.id.linear_belong_tech)
    LinearLayout linearBelongTech;
    @Bind(R.id.belong_tech_name)
    TextView belongTechName;
    @Bind(R.id.belong_tech_num)
    TextView belongTechNum;
    @Bind(R.id.belong_tech_times)
    TextView belongTechTimes;
    @Bind(R.id.belong_tech_day)
    TextView belongTechDay;
    @Bind(R.id.belong_tech_visit)
    TextView belongTechVisit;
    @Bind(R.id.customer_type)
    ImageView customerType;
    @Bind(R.id.customer_other_type)
    ImageView customerOtherType;
    @Bind(R.id.rl_recently_visit_time)
    RelativeLayout mVisitTime;
    @Bind(R.id.rl_visit_tech_name)
    RelativeLayout mVisitTechName;


    private static final int CONTACT_TYPE = 0x001;
    private static final int TECH_TYPE = 0x002;
    private static final int MANAGER_TYPE = 0x003;
    private static final int RESULT_ADD_REMARK = 0x010;
    private int currentContactType;
    private Subscription getCustomerInformationSubscription;
    private Subscription getManagerInformationSubscription;
    private Subscription getTechInformationSubscription;
    private Subscription doDeleteContactSubscription;
    private Subscription doShowVisitViewSubscription;
    private Subscription getContactPermissionSubscription;  // 聊天限制
    private Subscription sayHelloSubscription;  // 打招呼
    private String contactId;
    private String userId;
    private boolean isEmptyCustomer;
    private String contactPhone;
    private String customerChatId;
    private String contactType;
    private Context mContext;
    private boolean remarkIsNotEmpty;
    private String chatEmId;
    private String chatName;
    private String chatHeadUrl;
    private String managerHeadUrl;
    private String impression;
    private String userType;
    private String isTech;
    private String userTechName;
    private String userTechNo;
    private String canSayHello;
    private int chatPosition;
    private Map<String, String> params = new HashMap<>();
    private boolean isMyCustomer;

    private static final int REQUEST_CODE_PHONE = 0x0001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_information_deatil);
        ButterKnife.bind(this);
        mContext = this;
        Intent intent = getIntent();
        userId = intent.getStringExtra(RequestConstant.KEY_USER_ID);
        contactId = intent.getStringExtra(RequestConstant.KEY_CUSTOMER_ID);
        contactType = intent.getStringExtra(RequestConstant.KEY_CONTACT_TYPE);
        isEmptyCustomer = intent.getBooleanExtra(RequestConstant.KEY_IS_EMPTY, false);
        managerHeadUrl = intent.getStringExtra(RequestConstant.KEY_MANAGER_URL);
        userType = intent.getStringExtra(RequestConstant.CONTACT_TYPE);
        isMyCustomer = intent.getBooleanExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);
        canSayHello = intent.getStringExtra(RequestConstant.KEY_CAN_SAY_HELLO);
        chatPosition = intent.getIntExtra(ChatConstant.KEY_SAY_HI_POSITION, -1);
        initView();

    }

    private void initView() {
        setTitle(R.string.customer_information_deatail);
        setBackVisible(true);
        doDeleteContactSubscription = RxBus.getInstance().toObservable(DeleteContactResult.class).subscribe(result -> {
            handlerDeleteCustomer(result);
        });
        if (contactType.equals("manager")) {
            currentContactType = MANAGER_TYPE;
        } else if (contactType.equals("tech")) {
            currentContactType = TECH_TYPE;
        } else {
            currentContactType = CONTACT_TYPE;
        }
        getViewFromType(currentContactType);
    }

    private void getViewFromType(int type) {
        switch (type) {
            case CONTACT_TYPE:
                isTech = "";
                if (isMyCustomer) {
                    contactMore.setVisibility(View.VISIBLE);
                }

                getCustomerInformationSubscription = RxBus.getInstance().toObservable(CustomerDetailResult.class).subscribe(
                        customer -> handlerCustomer(customer)
                );
                doShowVisitViewSubscription = RxBus.getInstance().toObservable(VisitBean.class).subscribe(
                        visitBean -> handlerVisitView(visitBean)
                );
                // 聊天限制
                getContactPermissionSubscription = RxBus.getInstance().toObservable(ContactPermissionResult.class).subscribe(contactPermissionResult -> {
                    handleContactPermissionResult(contactPermissionResult);
                });
                // 打招呼
                sayHelloSubscription = RxBus.getInstance().toObservable(SayHiResult.class).subscribe(result -> {
                    handleSayHelloResult(result);
                });

                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_ID, contactId);
                params.put(RequestConstant.KEY_USER_ID, userId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL, params);

                if (Utils.isNotEmpty(userId) && userId.length() > 5) {
                    Map<String, String> paramsView = new HashMap<>();
                    paramsView.put(RequestConstant.KEY_USER_ID, userId);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_VISIT_VIEW, paramsView);
                }

                getContactPermission(userId);
                break;
            case TECH_TYPE:
                isTech = "tech";
                getTechInformationSubscription = RxBus.getInstance().toObservable(TechDetailResult.class).subscribe(
                        tech -> handlerTech(tech)
                );
                Map<String, String> paramTech = new HashMap<>();
                paramTech.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_INFO_DETAIL, paramTech);
                break;
            case MANAGER_TYPE:
                isTech = "manager";
                getManagerInformationSubscription = RxBus.getInstance().toObservable(ManagerDetailResult.class).subscribe(
                        manager -> handlerManager(manager)
                );
                Map<String, String> paramManager = new HashMap<>();
                paramManager.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_MANAGER_INFO_DETAIL, paramManager);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @PermissionGrant(REQUEST_CODE_PHONE)
    public void requestSdcardSuccess() {
        toCallPhone();
    }

    @PermissionDenied(REQUEST_CODE_PHONE)
    public void requestSdcardFailed() {
        Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    public void toCallPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + contactPhone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    // 打电话
    @OnClick(R.id.btn_call_phone)
    public void callPhoneToCustomer() {
        // 判断客户号码是否可用
        if (TextUtils.isEmpty(contactPhone)) {
            this.makeShortToast("手机号码不存在");
            return;
        }
        MPermissions.requestPermissions(ContactInformationDetailActivity.this, REQUEST_CODE_PHONE, Manifest.permission.CALL_PHONE);
    }

    // 发短信
    @OnClick(R.id.btn_chat)
    public void sendMessageToCustomer() {
        // 判断客户号码是否可用
        if (TextUtils.isEmpty(contactPhone)) {
            this.makeShortToast("手机号码不存在");
            return;
        }
        Uri uri = Uri.parse("smsto:" + contactPhone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }

    // 环信聊天
    @OnClick(R.id.btn_EmChat)
    public void enChatToCustomer() {
        // 判断emChatId是否存在
        if (TextUtils.isEmpty(chatEmId)) {
            this.makeShortToast("聊天失败，缺少客户信息");
            return;
        }
        if (chatEmId.equals(SharedPreferenceHelper.getEmchatId())) {
            this.makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
            return;
        } else {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(chatEmId, chatName, chatHeadUrl, isTech));
            SharedPreferenceHelper.setUserIsTech(chatEmId, isTech);
        }

    }

    // 同客户打招呼
    @OnClick(R.id.btn_EmHello)
    public void emHelloToCustomer() {
        //  环信打招呼
        if (TextUtils.isEmpty(chatEmId)) {
            this.makeShortToast("打招呼失败，缺少客户信息");
            return;
        }
        sayHello(userId);
    }

    @OnClick(R.id.contact_more)
    public void toDoMore() {
        final String[] items = new String[]{ResourceUtils.getString(R.string.delete_contact), ResourceUtils.getString(R.string.add_remark)};
        DropDownMenuDialog.getDropDownMenuDialog(ContactInformationDetailActivity.this, items, (index -> {
            switch (index) {
                case 0:
                    new RewardConfirmDialog(ContactInformationDetailActivity.this, getString(R.string.alert_delete_contact), getString(R.string.alert_delete_contact_message), "") {
                        @Override
                        public void onConfirmClick() {
                            Map<String, String> param = new HashMap<>();
                            param.put(RequestConstant.KEY_ID, contactId);
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_CONTACT, param);
                            super.onConfirmClick();
                        }
                    }.show();
                    break;
                case 1:
                    Intent intent = new Intent(ContactInformationDetailActivity.this, EditContactInformation.class);
                    intent.putExtra(RequestConstant.KEY_ID, contactId);
                    intent.putExtra(RequestConstant.KEY_NOTE_NAME, mContactName.getText().toString());
                    intent.putExtra(RequestConstant.KEY_USERNAME, mContactNickName.getText().toString());
                    intent.putExtra(RequestConstant.KEY_MARK_IMPRESSION, contactMark.getText().toString());
                    if (!remarkIsNotEmpty && mContactRemark.getText().toString().equals(ResourceUtils.getString(R.string.customer_remark_empty))) {
                        textRemarkAlert.setVisibility(View.VISIBLE);
                        intent.putExtra(RequestConstant.KEY_REMARK, "");
                    } else {
                        intent.putExtra(RequestConstant.KEY_REMARK, mContactRemark.getText().toString());
                    }


                    intent.putExtra(RequestConstant.KEY_PHONE_NUMBER, contactPhone);
                    startActivityForResult(intent, RESULT_ADD_REMARK);
                    break;
            }
        })).show(contactMore);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (getCustomerInformationSubscription != null) {
            RxBus.getInstance().unsubscribe(getCustomerInformationSubscription);
        }
        if (getManagerInformationSubscription != null) {
            RxBus.getInstance().unsubscribe(getManagerInformationSubscription);
        }
        if (getTechInformationSubscription != null) {
            RxBus.getInstance().unsubscribe(getTechInformationSubscription);
        }
        if (doDeleteContactSubscription != null) {
            RxBus.getInstance().unsubscribe(doDeleteContactSubscription);
        }
        if (doShowVisitViewSubscription != null) {
            RxBus.getInstance().unsubscribe(doShowVisitViewSubscription);
        }
        if (getContactPermissionSubscription != null) {
            RxBus.getInstance().unsubscribe(getContactPermissionSubscription);
        }
        if (sayHelloSubscription != null) {
            RxBus.getInstance().unsubscribe(sayHelloSubscription);
        }
    }

    private void handlerCustomer(CustomerDetailResult customer) {
        if (customer.respData != null) {
            chatEmId = customer.respData.techCustomer.emchatId;
            chatHeadUrl = customer.respData.techCustomer.avatarUrl;
            impression = customer.respData.techCustomer.impression;
        }
        if (Utils.isNotEmpty(impression)) {
            markMessageLayout.setVisibility(View.VISIBLE);
            contactMark.setText(impression);
        } else {
            markMessageLayout.setVisibility(View.GONE);
        }

        if (Utils.isNotEmpty(customer.respData.techCustomer.userNoteName)) {
            chatName = customer.respData.techCustomer.userNoteName;
        } else {
            chatName = customer.respData.techCustomer.userName;
        }
        if (Utils.isNotEmpty(customer.respData.techCustomer.belongsTechName)) {
            userTechName = customer.respData.techCustomer.belongsTechName;
        }
        if (Utils.isNotEmpty(customer.respData.techCustomer.belongsTechSerialNo)) {
            userTechNo = customer.respData.techCustomer.belongsTechSerialNo;
        }
        if (Utils.isNotEmpty(userTechName) || isMyCustomer) {
            if (isMyCustomer) {
                if (Utils.isNotEmpty(SharedPreferenceHelper.getUserName())) {
                    belongTechName.setText(SharedPreferenceHelper.getUserName());
                }
                if (Utils.isNotEmpty(SharedPreferenceHelper.getSerialNo())) {
                    belongTechNum.setText(String.format("[%s]", SharedPreferenceHelper.getSerialNo()));
                }

            } else if (Utils.isNotEmpty(userTechName)) {
                belongTechName.setText(userTechName);
                if (Utils.isNotEmpty(userTechNo)) {
                    belongTechNum.setText(String.format("[%s]", userTechNo));
                }
            }

        } else {
            belongTechName.setText("-");

        }


        if (customer == null) {
            return;
        }
        isEmptyCustomer = TextUtils.isEmpty(customer.respData.techCustomer.emchatId) ? true : false;
        Glide.with(mContext).load(customer.respData.techCustomer.avatarUrl).error(R.drawable.icon22).into(mContactHead);
        if (isEmptyCustomer) {
//            btnEmChat.setEnabled(false);
            registerAlert.setVisibility(View.VISIBLE);
            contactPhone = customer.respData.techCustomer.userLoginName;
            customerChatId = "";
            mContactName.setText(customer.respData.techCustomer.userNoteName);
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + contactPhone);
            mContactNickName.setVisibility(View.GONE);
            if (TextUtils.isEmpty(customer.respData.techCustomer.remark)) {
                mContactRemark.setText(ResourceUtils.getString(R.string.customer_remark_empty));
                textRemarkAlert.setVisibility(View.GONE);
            } else {
                remarkIsNotEmpty = true;
                mContactRemark.setText(customer.respData.techCustomer.remark);
                textRemarkAlert.setVisibility(View.VISIBLE);
            }
            customerType.setVisibility(View.VISIBLE);
            customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
        } else {
//            btnEmChat.setEnabled(true);
            mContactOrderLayout.setVisibility(View.VISIBLE);
            linearBelongTech.setVisibility(View.VISIBLE);
            contactPhone = customer.respData.techCustomer.userLoginName;
            customerChatId = customer.respData.techCustomer.emchatId;
            if (!TextUtils.isEmpty(customer.respData.techCustomer.userNoteName)) {
                mContactName.setText(customer.respData.techCustomer.userNoteName);
            } else if (!TextUtils.isEmpty(customer.respData.techCustomer.userName)) {
                mContactName.setText(customer.respData.techCustomer.userName);
            } else {
                mContactName.setText(ResourceUtils.getString(R.string.default_user_name));
            }
            if (customer.respData.techCustomer.customerType.equals(RequestConstant.TECH_ADD)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
            } else if (customer.respData.techCustomer.customerType.equals(RequestConstant.TEMP_USER)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.temporary_user));
//                btnEmChat.setEnabled(false);
            } else if (customer.respData.techCustomer.customerType.equals(RequestConstant.FANS_USER)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
            } else if (customer.respData.techCustomer.customerType.equals(RequestConstant.FANS_WX_USER)) {
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                customerOtherType.setVisibility(View.VISIBLE);
                customerType.setVisibility(View.VISIBLE);
                customerOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
            } else {
                mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + "未知");
//                btnCallPhone.setEnabled(false);
//                btnChat.setEnabled(false);
                customerType.setVisibility(View.VISIBLE);
                customerOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
            }
            if (!TextUtils.isEmpty(customer.respData.techCustomer.userName) && Utils.isNotEmpty(customer.respData.techCustomer.userNoteName)
                    && !customer.respData.techCustomer.userName.equals(ResourceUtils.getString(R.string.default_user_name))
                    ) {
                mContactNickName.setText("昵称：" + customer.respData.techCustomer.userName);
            } else {
                mContactNickName.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(customer.respData.techCustomer.userLoginName) && !customer.respData.techCustomer.customerType.equals(RequestConstant.WX_USER)) {
                mContactTelephone.setText("电话：" + customer.respData.techCustomer.userLoginName);
            } else {
                mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + "未知");
            }
            if (!TextUtils.isEmpty(customer.respData.techCustomer.remark)) {
                textRemarkAlert.setVisibility(View.VISIBLE);
                mContactRemark.setText(customer.respData.techCustomer.remark);
            } else {
                textRemarkAlert.setVisibility(View.GONE);
                mContactRemark.setText(ResourceUtils.getString(R.string.customer_remark_empty));
            }
            if (!TextUtils.isEmpty(String.valueOf(customer.respData.techCustomer.orderCount))) {
                mContactOrder.setText(String.valueOf(customer.respData.techCustomer.orderCount));
            }
            float reward = customer.respData.techCustomer.rewardAmounts / 100f;
            if (reward > 10000) {
                float payMoney = reward / 10000f;
                mContactReward.setText(String.format("%1.2f", payMoney) + "万");
            } else {
                mContactReward.setText(String.format("%1.2f", reward));
            }
            if (customer.respData.orders == null) {
                rlOrder.setVisibility(View.GONE);
                rlOrder2.setVisibility(View.GONE);
                orderEmpty.setVisibility(View.VISIBLE);
                return;
            }
            if (customer.respData.orders.size() == 0) {
                rlOrder.setVisibility(View.GONE);
                rlOrder2.setVisibility(View.GONE);
                orderEmpty.setVisibility(View.VISIBLE);
            }
            if (customer.respData.orders.size() == 1) {
                rlOrder2.setVisibility(View.GONE);
                initOrder1(customer);
            }
            if (customer.respData.orders.size() == 2) {
                divView.setVisibility(View.VISIBLE);
                initOrder1(customer);
                initOrder2(customer);
            }
        }
        if (userType.equals(RequestConstant.WX_USER)) {
            mContactTelephone.setVisibility(View.GONE);
//            callUnusable();
        }
    }

    private void handlerVisitView(VisitBean bean) {
        if (bean.statusCode == 200) {
            if (Utils.isNotEmpty(String.valueOf(bean.count)) && bean.count > 0) {
                belongTechDay.setText(RelativeDateFormatUtil.format(bean.recent_date));
                belongTechVisit.setText(String.format("共访问我%s次，平均%s访问一次", bean.count + "", bean.frequency));
            } else {
                belongTechDay.setText("-");
                belongTechVisit.setText(String.format("共访问我%s次，平均%s访问一次", "-", "-"));
            }
        }
    }

    // 打招呼
    private void sayHello(String customerId) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_ID, customerId);
        params.put(ChatConstant.KEY_SAY_HI_POSITION, String.valueOf(chatPosition));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_SAY_HI, params);
    }

    // 处理打招呼结果
    private void handleSayHelloResult(SayHiResult result) {
        if (result.statusCode == 200) {
            showToast("打招呼成功");
        } else {
            showToast("打招呼失败:" + result.msg);
        }
    }

    // 获取聊天限制
    private void getContactPermission(String customerId) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_REQUEST_TAG, true);
        params.put(RequestConstant.KEY_NEARBY_CUSTOMER_ID, customerId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_PERMISSION, params);
    }

    // 处理获取的聊天限制关系
    private void handleContactPermissionResult(ContactPermissionResult result) {
        if (result != null && result.statusCode == 200) {
            ContactPermissionInfo info = result.respData;
            // 更新按钮状态
            if (info.echat && info.hello) {
                btnEmChat.setVisibility(View.VISIBLE);
                btnEmHello.setVisibility(View.GONE);
            } else {
                btnEmChat.setVisibility(View.GONE);
                btnEmHello.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(canSayHello) && canSayHello.equals("0")) {
                    btnEmHello.setEnabled(false);
                    btnEmHello.setText("已招呼");
                } else {
                    btnEmHello.setEnabled(true);
                    btnEmHello.setText("打招呼");
                }
            }
            btnCallPhone.setVisibility(info.call ? View.VISIBLE : View.GONE);
            btnChat.setVisibility(info.sms ? View.VISIBLE : View.GONE);
        } else {
            // 默认按钮状态:只能打招呼
            btnEmHello.setVisibility(View.VISIBLE);
            btnEmChat.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
        }
    }

    private void initOrder1(CustomerDetailResult customer) {
        OrderBean order1 = customer.respData.orders.get(0);
        orderTime.setText(order1.appointTime);
        orderState.setText(order1.statusName);
        if (!TextUtils.isEmpty(order1.serviceItemName)) {
            orderItem.setText(order1.serviceItemName);
        } else {
            orderItem.setText(ResourceUtils.getString(R.string.customer_order_item));
        }
    }

    private void initOrder2(CustomerDetailResult customer) {
        OrderBean order2 = customer.respData.orders.get(1);
        order2Time.setText(order2.appointTime);
        order2State.setText(order2.statusName);
        if (!TextUtils.isEmpty(order2.serviceItemName)) {
            order2Item.setText(order2.serviceItemName);
        } else {
            order2Item.setText(ResourceUtils.getString(R.string.customer_order_item));
        }
    }

    private void handlerTech(TechDetailResult tech) {
        chatName = tech.respData.name;
        chatEmId = tech.respData.emchatId;
        chatHeadUrl = tech.respData.avatarUrl;
        if (Utils.isNotEmpty(tech.respData.serialNo)) {
            SharedPreferenceHelper.setTechNoOld(tech.respData.id, tech.respData.serialNo);
        }
        btnEmChat.setEnabled(true);
        contactPhone = tech.respData.phoneNum;
        customerChatId = tech.respData.emchatId;
        Glide.with(mContext).load(tech.respData.avatarUrl).error(R.drawable.icon22).into(mContactHead);
        if (TextUtils.isEmpty(tech.respData.serialNo)) {
            mContactName.setText(Utils.StrSubstring(12, tech.respData.name, true));
        } else {
            mContactName.setText(Utils.StrSubstring(12, tech.respData.name, true));
            llTechNum.setVisibility(View.VISIBLE);
            techNum.setText(tech.respData.serialNo);

        }
        if (Utils.isNotEmpty(tech.respData.phoneNum)) {
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + tech.respData.phoneNum);
        } else {
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + "未知");
            callUnusable();
        }
        if (TextUtils.isEmpty(tech.respData.description)) {
            mContactRemark.setText(ResourceUtils.getString(R.string.contact_description_remark_empty));
        } else {
            textRemarkAlert.setText(ResourceUtils.getString(R.string.contact_description_remark));
            textRemarkAlert.setVisibility(View.VISIBLE);
            mContactRemark.setText(tech.respData.description);
        }

        mContactOrderLayout.setVisibility(View.GONE);
        linearBelongTech.setVisibility(View.GONE);
        mContactNickName.setVisibility(View.GONE);
    }

    private void handlerManager(ManagerDetailResult manager) {

        chatName = manager.respData.name;
        chatHeadUrl = manager.respData.avatarUrl;
        chatEmId = manager.respData.emchatId;
        contactPhone = manager.respData.phoneNum;
        customerChatId = manager.respData.emchatId;
        if (Utils.isNotEmpty(customerChatId)) {
            btnEmChat.setEnabled(true);
        }
        if (!TextUtils.isEmpty(managerHeadUrl)) {
            Glide.with(mContext).load(managerHeadUrl).error(R.drawable.icon22).into(mContactHead);
        }
        mContactName.setText(manager.respData.name);
        if (Utils.isNotEmpty(manager.respData.phoneNum)) {
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + manager.respData.phoneNum);
        } else {
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + "未知");
            callUnusable();
        }

        mContactOrderLayout.setVisibility(View.GONE);
        linearBelongTech.setVisibility(View.GONE);
        mContactNickName.setVisibility(View.GONE);
        mContactRemark.setVisibility(View.GONE);
    }

    private void handlerDeleteCustomer(DeleteContactResult result) {
        if (result.resultcode == 200) {
            params.put(RequestConstant.KEY_CONTACT_TYPE, "");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST, params);
            makeShortToast(ResourceUtils.getString(R.string.delete_contact_success));
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    ContactInformationDetailActivity.this.finish();
                }
            }, 1500);
        } else {
            makeShortToast(result.msg.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (Utils.isNotEmpty(data.getStringExtra(RequestConstant.KEY_NOTE_NAME))) {
                mContactName.setText(Utils.StrSubstring(12, data.getStringExtra(RequestConstant.KEY_NOTE_NAME), true));
                chatName = data.getStringExtra(RequestConstant.KEY_NOTE_NAME);
                impression = data.getStringExtra(RequestConstant.KEY_MARK_IMPRESSION);
                if (Utils.isNotEmpty(impression)) {
                    markMessageLayout.setVisibility(View.VISIBLE);
                    contactMark.setText(impression);
                } else {
                    markMessageLayout.setVisibility(View.GONE);
                }
                SharedPreferenceHelper.setUserRemarkName(chatEmId, chatName);
            }
            if (Utils.isNotEmpty(data.getStringExtra(RequestConstant.KEY_REMARK))) {
                mContactRemark.setText(data.getStringExtra(RequestConstant.KEY_REMARK));
            }

        }
    }

    private void callUnusable() {
        btnCallPhone.setEnabled(false);
        btnCallPhone.setClickable(false);
        btnCallPhone.setTextColor(ResourceUtils.getColor(R.color.colorBtnChat));
        btnChat.setEnabled(false);
        btnChat.setClickable(false);
        btnChat.setTextColor(ResourceUtils.getColor(R.color.colorBtnChat));
    }
}
