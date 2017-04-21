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
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ContactPermissionInfo;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.bean.DeleteContactResult;
import com.xmd.technician.bean.ManagerDetailResult;
import com.xmd.technician.bean.OrderBean;
import com.xmd.technician.bean.SayHiBaseResult;
import com.xmd.technician.bean.TechDetailResult;
import com.xmd.technician.bean.VisitBean;
import com.xmd.technician.chat.EmchatManager;
import com.xmd.technician.chat.IEmchat;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ContactPermissionResult;
import com.xmd.technician.http.gson.HelloCheckRecentlyResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
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

    private Context mContext;

    private static final int RESULT_ADD_REMARK = 0x010;
    private static final int REQUEST_CODE_PHONE = 0x0001;

    private Subscription getCustomerInformationSubscription;
    private Subscription getManagerInformationSubscription;
    private Subscription getTechInformationSubscription;
    private Subscription doDeleteContactSubscription;
    private Subscription doShowVisitViewSubscription;
    private Subscription sayHiDetailSubscription;  // 打招呼
    private Subscription getSayHiStatusSubscription;
    private Subscription contactPermissionDetailSubscription;

    //  -----------------customer
    private String userId;
    private ContactPermissionInfo permissionInfo;   // 聊天限制
    //  -----------------manager or tech
    private String managerHeadUrl;
    //  -----------------common
    private String contactId;
    private String contactType;
    private boolean isMyCustomer;

    private String contactPhone;    // 电话号码
    private String emChatId;  // 环信ID
    private boolean remarkIsNotEmpty;   //是否添加了备注
    private String emChatName;    // 用户名称
    private String chatHeadUrl; // 用户头像
    private String chatType; //用户类型
    private String impression;  // 印象
    private String isTech;      // 聊天参数

    private Map<String, String> params = new HashMap<>();

    private IEmchat emchat= EmchatManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_information_deatil);
        ButterKnife.bind(this);
        mContext = this;

        Intent intent = getIntent();
        // customer
        userId = intent.getStringExtra(RequestConstant.KEY_USER_ID);
        permissionInfo = (ContactPermissionInfo) intent.getSerializableExtra(RequestConstant.KEY_CONTACT_PERMISSION_INFO);

        // manager or tech
        managerHeadUrl = intent.getStringExtra(RequestConstant.KEY_MANAGER_URL);

        // common
        contactId = intent.getStringExtra(RequestConstant.KEY_CUSTOMER_ID);     //except ChatFragment
        contactType = intent.getStringExtra(RequestConstant.KEY_CONTACT_TYPE);
        isMyCustomer = intent.getBooleanExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);

        initView();
    }

    private void initView() {
        setTitle(R.string.customer_information_deatail);
        setBackVisible(true);
        doDeleteContactSubscription = RxBus.getInstance().toObservable(DeleteContactResult.class).subscribe(result -> {
            handlerDeleteCustomer(result);
        });

        getViewFromType(contactType);
    }


    private void getViewFromType(String contactType) {
        switch (contactType) {
            case Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER:
                isTech = "";
                // 处理客户信息
                getCustomerInformationSubscription = RxBus.getInstance().toObservable(CustomerDetailResult.class).subscribe(
                        customer -> handlerCustomer(customer)
                );
                // 处理客户访问信息
                doShowVisitViewSubscription = RxBus.getInstance().toObservable(VisitBean.class).subscribe(
                        visitBean -> handlerVisitView(visitBean)
                );
                // 处理打招呼状态
                getSayHiStatusSubscription = RxBus.getInstance().toObservable(HelloCheckRecentlyResult.class).subscribe(result -> {
                    handleSayHiStatus(result);
                });

                // 处理打招呼结果
                sayHiDetailSubscription = RxBus.getInstance().toObservable(SayHiBaseResult.class).subscribe(result -> {
                    handleSayHiDetailResult(result);
                });

                // 处理关系权限
                contactPermissionDetailSubscription = RxBus.getInstance().toObservable(ContactPermissionResult.class).subscribe(result -> {
                    handleContactPermissionDetail(result);
                });

                // 获取客户信息
                getCustomerInfo(userId);
                // 获取客户访问情况
                getVisitView(userId);
                break;
            case Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER:
                isTech = Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER;
                getManagerInformationSubscription = RxBus.getInstance().toObservable(ManagerDetailResult.class).subscribe(
                        manager -> handlerManager(manager)
                );
                // 获取管理者信息
                Map<String, String> paramManager = new HashMap<>();
                paramManager.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_MANAGER_INFO_DETAIL, paramManager);
                break;
            case Constant.CONTACT_INFO_DETAIL_TYPE_TECH:
                isTech = Constant.CONTACT_INFO_DETAIL_TYPE_TECH;
                getTechInformationSubscription = RxBus.getInstance().toObservable(TechDetailResult.class).subscribe(
                        tech -> handlerTech(tech)
                );
                // 获取技师信息
                Map<String, String> paramTech = new HashMap<>();
                paramTech.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_INFO_DETAIL, paramTech);
                break;
            default:
                break;
        }
    }

    private void getCustomerInfo(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ID, contactId);
        params.put(RequestConstant.KEY_USER_ID, userId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL, params);
    }

    private void getVisitView(String userId) {
        if (Utils.isNotEmpty(userId) && userId.length() > 5) {
            Map<String, String> paramsView = new HashMap<>();
            paramsView.put(RequestConstant.KEY_USER_ID, userId);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_VISIT_VIEW, paramsView);
        }
    }

    private void getSayHiStatus(String userId) {
        if (Utils.isNotEmpty(userId)) {
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_NEW_CUSTOMER_ID, userId);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_HELLO_RECENTLY, params);
        }
    }

    private void saveChatContact(String chatId) {
        Map<String, String> saveParams = new HashMap<>();
        saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, chatId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
    }

    private void handleSayHiStatus(HelloCheckRecentlyResult result) {
        if (result != null && result.statusCode == 200 && "Y".equals(result.respData)) {
            btnEmHello.setEnabled(false);
            btnEmHello.setText(R.string.had_say_hi);
        } else {
            btnEmHello.setEnabled(true);
            btnEmHello.setText(R.string.to_say_hi);
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
        if (!emchat.isConnected()) {
            showToast("当前已经离线，请稍后再试!");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_REQUEST_SAY_HI_TYPE, Constant.REQUEST_SAY_HI_TYPE_DETAIL);
        params.put(RequestConstant.KEY_USERNAME, emChatName);
        params.put(RequestConstant.KEY_USER_AVATAR, chatHeadUrl);
        params.put(RequestConstant.KEY_USER_TYPE, chatType);
        params.put(RequestConstant.KEY_GAME_USER_EMCHAT_ID, emChatId);
        params.put(RequestConstant.KEY_NEW_CUSTOMER_ID, customerId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_SAY_HELLO, params);
    }

    // 处理打招呼结果
    private void handleSayHiDetailResult(SayHiBaseResult result) {
        if (result.statusCode == 200) {
            showToast("打招呼成功");
            HelloSettingManager.getInstance().sendHelloTemplate(emChatName, emChatId, result.userAvatar, result.userType);
            saveChatContact(emChatId);
            btnEmHello.setEnabled(false);
            btnEmHello.setText(R.string.had_say_hi);
        } else {
            showToast("打招呼失败:" + result.msg);
        }
    }

    private void getContactPermissionDetail(String userId) {
        if (Utils.isNotEmpty(userId)) {
            Map<String, Object> params = new HashMap<>();
            params.put(RequestConstant.KEY_REQUEST_CONTACT_PERMISSION_TAG, Constant.REQUEST_CONTACT_PERMISSION_DETAIL);
            params.put(RequestConstant.KEY_ID, userId);
            params.put(RequestConstant.KEY_CONTACT_ID_TYPE, Constant.REQUEST_CONTACT_ID_TYPE_CUSTOMER);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_PERMISSION, params);
        }
    }

    private void handleContactPermissionDetail(ContactPermissionResult result) {
        if (result != null && result.statusCode == 200) {
            // 更新按钮状态
            if (result.respData.echat && result.respData.hello) {
                btnEmChat.setVisibility(View.VISIBLE);
                btnEmHello.setVisibility(View.GONE);
            } else {
                btnEmChat.setVisibility(View.GONE);
                btnEmHello.setVisibility(View.VISIBLE);
                getSayHiStatus(userId);
            }
            btnCallPhone.setVisibility(result.respData.call ? View.VISIBLE : View.GONE);
            btnChat.setVisibility(result.respData.sms ? View.VISIBLE : View.GONE);
        } else {
            // 默认按钮状态:只能打招呼
            btnEmHello.setVisibility(View.VISIBLE);
            btnEmChat.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
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
        if (TextUtils.isEmpty(emChatId)) {
            this.makeShortToast("聊天失败，缺少客户信息");
            return;
        }
        if (emChatId.equals(SharedPreferenceHelper.getEmchatId())) {
            this.makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
            return;
        } else {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(emChatId, emChatName, chatHeadUrl, isTech));
            SharedPreferenceHelper.setUserIsTech(emChatId, isTech);
        }

    }

    // 同客户打招呼
    @OnClick(R.id.btn_EmHello)
    public void emHelloToCustomer() {
        //  环信打招呼
        if (TextUtils.isEmpty(emChatId)) {
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
        if (sayHiDetailSubscription != null) {
            RxBus.getInstance().unsubscribe(sayHiDetailSubscription);
        }
        if (getSayHiStatusSubscription != null) {
            RxBus.getInstance().unsubscribe(getSayHiStatusSubscription);
        }
        if (contactPermissionDetailSubscription != null) {
            RxBus.getInstance().unsubscribe(contactPermissionDetailSubscription);
        }
    }

    private void handlerCustomer(CustomerDetailResult customer) {
        CustomerInfo mCustomerInfo;
        if (customer.respData != null && customer.respData.techCustomer != null) {
            mCustomerInfo = customer.respData.techCustomer;
        } else {
            showToast(customer.msg);
            return;
        }
        if (isMyCustomer) {
            contactMore.setVisibility(View.VISIBLE);
        }

        if (permissionInfo != null) {
            // 更新按钮状态
            if (permissionInfo.echat && permissionInfo.hello) {
                btnEmChat.setVisibility(View.VISIBLE);
                btnEmHello.setVisibility(View.GONE);
            } else {
                btnEmChat.setVisibility(View.GONE);
                btnEmHello.setVisibility(View.VISIBLE);
                getSayHiStatus(userId);
            }
            btnCallPhone.setVisibility(permissionInfo.call ? View.VISIBLE : View.GONE);
            btnChat.setVisibility(permissionInfo.sms ? View.VISIBLE : View.GONE);
        } else {
            getContactPermissionDetail(userId);
        }

        emChatId = mCustomerInfo.emchatId;
        chatHeadUrl = mCustomerInfo.avatarUrl;
        chatType = mCustomerInfo.customerType;
        Glide.with(mContext).load(mCustomerInfo.avatarUrl).error(R.drawable.icon22).into(mContactHead);

        impression = mCustomerInfo.impression;
        if (Utils.isNotEmpty(impression)) {
            markMessageLayout.setVisibility(View.VISIBLE);
            contactMark.setText(impression);
        } else {
            markMessageLayout.setVisibility(View.GONE);
        }

        if (Utils.isNotEmpty(customer.respData.techCustomer.userNoteName)) {
            emChatName = customer.respData.techCustomer.userNoteName;
        } else {
            emChatName = customer.respData.techCustomer.userName;
        }

        if (Utils.isNotEmpty(mCustomerInfo.belongsTechName)) {
            if (Utils.isNotEmpty(mCustomerInfo.belongsTechName)) {
                belongTechName.setText(mCustomerInfo.belongsTechName);
                if (Utils.isNotEmpty(mCustomerInfo.belongsTechSerialNo)) {
                    belongTechNum.setText(String.format("[%s]", mCustomerInfo.belongsTechSerialNo));
                }
            }
        } else {
            belongTechName.setText("-");
        }

        if (TextUtils.isEmpty(mCustomerInfo.emchatId)) {
            registerAlert.setVisibility(View.VISIBLE);
            contactPhone = mCustomerInfo.userLoginName;
            emChatId = "";
            mContactName.setText(mCustomerInfo.userNoteName);
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + contactPhone);
            mContactNickName.setVisibility(View.GONE);
            if (TextUtils.isEmpty(mCustomerInfo.remark)) {
                mContactRemark.setText(ResourceUtils.getString(R.string.customer_remark_empty));
                textRemarkAlert.setVisibility(View.GONE);
            } else {
                remarkIsNotEmpty = true;
                mContactRemark.setText(mCustomerInfo.remark);
                textRemarkAlert.setVisibility(View.VISIBLE);
            }
            customerType.setVisibility(View.VISIBLE);
            customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
        } else {
            mContactOrderLayout.setVisibility(View.VISIBLE);
            linearBelongTech.setVisibility(View.VISIBLE);
            contactPhone = mCustomerInfo.userLoginName;
            if (!TextUtils.isEmpty(mCustomerInfo.userNoteName)) {
                mContactName.setText(mCustomerInfo.userNoteName);
            } else if (!TextUtils.isEmpty(mCustomerInfo.userName)) {
                mContactName.setText(mCustomerInfo.userName);
            } else {
                mContactName.setText(ResourceUtils.getString(R.string.default_user_name));
            }
            if (mCustomerInfo.customerType.equals(RequestConstant.TECH_ADD)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
            } else if (mCustomerInfo.customerType.equals(RequestConstant.TEMP_USER)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.temporary_user));
            } else if (mCustomerInfo.customerType.equals(RequestConstant.FANS_USER)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
            } else if (mCustomerInfo.customerType.equals(RequestConstant.FANS_WX_USER)) {
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                customerOtherType.setVisibility(View.VISIBLE);
                customerType.setVisibility(View.VISIBLE);
                customerOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
            } else {
                mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + "未知");
                customerType.setVisibility(View.VISIBLE);
                customerOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
            }
            if (!TextUtils.isEmpty(mCustomerInfo.userName) && Utils.isNotEmpty(mCustomerInfo.userNoteName)
                    && !mCustomerInfo.userName.equals(ResourceUtils.getString(R.string.default_user_name))
                    ) {
                mContactNickName.setText("昵称：" + mCustomerInfo.userName);
            } else {
                mContactNickName.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mCustomerInfo.userLoginName) && !mCustomerInfo.customerType.equals(RequestConstant.WX_USER)) {
                mContactTelephone.setText("电话：" + mCustomerInfo.userLoginName);
            } else {
                mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + "未知");
            }
            if (!TextUtils.isEmpty(mCustomerInfo.remark)) {
                textRemarkAlert.setVisibility(View.VISIBLE);
                mContactRemark.setText(mCustomerInfo.remark);
            } else {
                textRemarkAlert.setVisibility(View.GONE);
                mContactRemark.setText(ResourceUtils.getString(R.string.customer_remark_empty));
            }
            if (!TextUtils.isEmpty(String.valueOf(mCustomerInfo.orderCount))) {
                mContactOrder.setText(String.valueOf(mCustomerInfo.orderCount));
            }
            float reward = mCustomerInfo.rewardAmounts / 100f;
            if (reward > 10000) {
                float payMoney = reward / 10000f;
                mContactReward.setText(String.format("%1.2f", payMoney) + "万");
            } else {
                mContactReward.setText(String.format("%1.2f", reward));
            }

            // 处理订单数据
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
        if (tech != null && tech.statusCode == 200) {
            emChatName = tech.respData.name;
            emChatId = tech.respData.emchatId;
            chatHeadUrl = tech.respData.avatarUrl;
            if (Utils.isNotEmpty(tech.respData.serialNo)) {
                SharedPreferenceHelper.setTechNoOld(tech.respData.id, tech.respData.serialNo);
            }
            btnEmChat.setEnabled(true);
            contactPhone = tech.respData.phoneNum;
            emChatId = tech.respData.emchatId;
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
        } else {
            showToast(tech.msg);
        }
    }

    private void handlerManager(ManagerDetailResult manager) {
        if (manager != null && manager.statusCode == 200) {
            emChatName = manager.respData.name;
            chatHeadUrl = manager.respData.avatarUrl;
            contactPhone = manager.respData.phoneNum;
            emChatId = manager.respData.emchatId;
            if (Utils.isNotEmpty(emChatId)) {
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
        } else {
            showToast(manager.msg);
        }
    }

    private void handlerDeleteCustomer(DeleteContactResult result) {
        if (result.resultcode == 200) {
            params.put(RequestConstant.KEY_CONTACT_TYPE, "");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST, params);
            makeShortToast(ResourceUtils.getString(R.string.delete_contact_success));
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, () -> ContactInformationDetailActivity.this.finish(), 1500);
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
                emChatName = data.getStringExtra(RequestConstant.KEY_NOTE_NAME);
                impression = data.getStringExtra(RequestConstant.KEY_MARK_IMPRESSION);
                if (Utils.isNotEmpty(impression)) {
                    markMessageLayout.setVisibility(View.VISIBLE);
                    contactMark.setText(impression);
                } else {
                    markMessageLayout.setVisibility(View.GONE);
                }
                SharedPreferenceHelper.setUserRemarkName(emChatId, emChatName);
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
