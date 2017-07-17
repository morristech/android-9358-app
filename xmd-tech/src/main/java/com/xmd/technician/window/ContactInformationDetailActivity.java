package com.xmd.technician.window;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crazyman.library.PermissionTool;
import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.XmdChat;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.ContactPermissionInfo;
import com.xmd.permission.PermissionConstants;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ContactHandlerBean;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.bean.DeleteContactResult;
import com.xmd.technician.bean.OrderBean;
import com.xmd.technician.bean.SayHiBaseResult;
import com.xmd.technician.bean.VisitBean;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AddToBlacklistResult;
import com.xmd.technician.http.gson.ClubEmployeeDetailResult;
import com.xmd.technician.http.gson.ContactPermissionResult;
import com.xmd.technician.http.gson.InBlacklistResult;
import com.xmd.technician.http.gson.RemoveFromBlacklistResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.DropDownMenuDialog;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.RoundImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/7/5.
 */
public class ContactInformationDetailActivity extends BaseActivity {
    @BindView(R.id.customer_head)
    RoundImageView mContactHead;
    @BindView(R.id.tv_customer_name)
    TextView mContactName;
    @BindView(R.id.tv_customer_telephone)
    TextView mContactTelephone;
    @BindView(R.id.tv_customer_nick_name)
    TextView mContactNickName;
    @BindView(R.id.tv_customer_remark)
    TextView mContactRemark;
    @BindView(R.id.customer_order)
    TextView mContactOrder;
    @BindView(R.id.customer_reward)
    TextView mContactReward;
    @BindView(R.id.ll_customer_order)
    LinearLayout mContactOrderLayout;
    @BindView(R.id.order_time)
    TextView orderTime;
    @BindView(R.id.order_state)
    TextView orderState;
    @BindView(R.id.order_item_detail)
    TextView orderItem;
    @BindView(R.id.rl_order)
    RelativeLayout rlOrder;
    @BindView(R.id.order2_time)
    TextView order2Time;
    @BindView(R.id.order2_state)
    TextView order2State;
    @BindView(R.id.order2_item_detail)
    TextView order2Item;
    @BindView(R.id.rl_order2)
    RelativeLayout rlOrder2;
    @BindView(R.id.btn_chat)
    ImageButton btnChat;
    @BindView(R.id.btn_call_phone)
    ImageButton btnCallPhone;
    @BindView(R.id.btn_EmChat)
    ImageButton btnEmChat;
    @BindView(R.id.btn_EmHello)
    ImageButton btnEmHello;
    @BindView(R.id.btn_rm_blacklist)
    Button btnRmBlacklist;
    @BindView(R.id.order_empty_alter)
    TextView orderEmpty;
    @BindView(R.id.contact_more)
    LinearLayout contactMore;
    @BindView(R.id.remark_alert)
    TextView textRemarkAlert;
    @BindView(R.id.ll_tech_number)
    LinearLayout llTechNum;
    @BindView(R.id.tech_number)
    TextView techNum;
    @BindView(R.id.view_div)
    View divView;
    @BindView(R.id.register_alert)
    TextView registerAlert;
    @BindView(R.id.ll_marker_message)
    LinearLayout markMessageLayout;
    @BindView(R.id.text_contact_marker)
    TextView contactMark;
    @BindView(R.id.linear_belong_tech)
    LinearLayout linearBelongTech;
    @BindView(R.id.belong_tech_name)
    TextView belongTechName;
    @BindView(R.id.belong_tech_num)
    TextView belongTechNum;
    @BindView(R.id.belong_tech_times)
    TextView belongTechTimes;
    @BindView(R.id.belong_tech_day)
    TextView belongTechDay;
    @BindView(R.id.belong_tech_visit)
    TextView belongTechVisit;
    @BindView(R.id.customer_type)
    ImageView customerType;
    @BindView(R.id.customer_other_type)
    ImageView customerOtherType;
    @BindView(R.id.rl_recently_visit_time)
    RelativeLayout mVisitTime;
    @BindView(R.id.rl_visit_tech_name)
    RelativeLayout mVisitTechName;
    @BindView(R.id.layout_operation_buttons)
    LinearLayout layoutOperationButtons;
    @BindView(R.id.btn_operation)
    ImageButton btnOperation;

    private Context mContext;
    private static final int REQUEST_CODE_CALL_PERMISSION = 0x1;
    private static final int REQUEST_CODE_SET_REMARK = 0x2;
    private String userId;
    private ContactPermissionInfo permissionInfo;   // 聊天限制
    private String contactId;
    private String contactType;     //联系人类型
    private boolean isMyCustomer;   //是否为我的客户,用来判断是否拥有更多操作的权限
    private String contactPhone;    // 电话号码
    private String emChatId;  // 环信ID
    private boolean remarkIsNotEmpty;   //是否添加了备注
    private String emChatName;    // 用户名称
    private String chatHeadUrl; // 用户头像
    private String chatType; //用户类型
    private String impression;  // 印象
    private boolean inBlacklist = false; //是否在聊天黑名单中
    private boolean showOperationButtons;
    private String[] mContactMoreItems;
    private UserInfoService userService = UserInfoServiceImpl.getInstance();
    private User mUser;

    private Subscription getCustomerInformationSubscription; //客户信息
    private Subscription getClubEmployeeDetailSubscription; //会所人员详情
    private Subscription doDeleteContactSubscription; //删除联系人
    private Subscription doShowVisitViewSubscription; //访客信息
    private Subscription sayHiDetailSubscription;  // 打招呼
    private Subscription contactPermissionDetailSubscription;//用户权限信息
    private Subscription addToBlacklistSubscription; //添加到黑名单
    private Subscription removeFromBlacklistSubscription; //从黑名单移除
    private Subscription inBlacklistSubscription;//是否在黑名单


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_information_deatil);
        ButterKnife.bind(this);
        mContext = this;
        getIntentData();
        initView();

        mContactHead.setOnClickListener((v) -> {
            if (!TextUtils.isEmpty(chatHeadUrl)) {
                ImageView imageView = new ImageView(v.getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                AlertDialog dialog = new AlertDialog
                        .Builder(v.getContext())
                        .setView(imageView)
                        .create();
                dialog.show();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = ScreenUtils.getScreenWidth() * 4 / 5;
                lp.height = ScreenUtils.getScreenWidth() * 4 / 5;
                dialog.getWindow().setAttributes(lp);
                imageView.getLayoutParams().width = lp.width;
                imageView.getLayoutParams().height = lp.height;
                Glide.with(v.getContext()).load(chatHeadUrl).into(imageView);
            }
        });
    }

    public void getIntentData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra(RequestConstant.KEY_USER_ID);
        contactId = intent.getStringExtra(RequestConstant.KEY_CUSTOMER_ID);     //except ChatFragment
        contactType = intent.getStringExtra(RequestConstant.KEY_CONTACT_TYPE);
        permissionInfo = (ContactPermissionInfo) intent.getSerializableExtra(RequestConstant.KEY_CONTACT_PERMISSION_INFO);
        isMyCustomer = intent.getBooleanExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);
    }

    private void initView() {
        setTitle(R.string.customer_information_deatail);
        setBackVisible(true);
        intiViewForType(contactType);
    }

    private void intiViewForType(String contactType) {
        initOperationButtonForType(contactType);
        switch (contactType) {
            case Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER:  //客户
                // 处理客户信息
                getCustomerInformationSubscription = RxBus.getInstance().toObservable(CustomerDetailResult.class).subscribe(customer -> handlerCustomer(customer));
                // 处理客户访问信息
                doShowVisitViewSubscription = RxBus.getInstance().toObservable(VisitBean.class).subscribe(visitBean -> handlerVisitView(visitBean));
                // 处理打招呼结果
                sayHiDetailSubscription = RxBus.getInstance().toObservable(SayHiBaseResult.class).subscribe(result -> {
                    handleSayHiDetailResult(result);
                });
                // 处理关系权限
                contactPermissionDetailSubscription = RxBus.getInstance().toObservable(ContactPermissionResult.class).subscribe(result -> {
                    handleContactPermissionDetail(result);
                });
                //删除联系人
                doDeleteContactSubscription = RxBus.getInstance().toObservable(DeleteContactResult.class).subscribe(result -> {
                    handlerDeleteCustomer(result);
                });
                //添加到黑名单
                addToBlacklistSubscription = RxBus.getInstance().toObservable(AddToBlacklistResult.class).subscribe(result -> handlerAddToBlacklist(result));
                //移除黑名单
                removeFromBlacklistSubscription = RxBus.getInstance().toObservable(RemoveFromBlacklistResult.class).subscribe(result -> handlerRemoveFromBlacklist(result));
                //是否在黑名单
                inBlacklistSubscription = RxBus.getInstance().toObservable(InBlacklistResult.class).subscribe(result -> handlerInBlacklist(result));
                // 获取客户信息
                getCustomerInfo();
                // 获取客户访问情况
                getVisitView();
                //判断用户是否在聊天黑名单中
                booleanInBlacklist();
                //获取用户的联系权限
                getContactPermissionDetail();
                break;
            case Constant.CONTACT_INFO_DETAIL_TYPE_TECH: //技师
            case Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER: //管理员
                getClubEmployeeDetailSubscription = RxBus.getInstance().toObservable(ClubEmployeeDetailResult.class).subscribe(
                        employeeDetail -> handlerEmployeeView(employeeDetail)
                );
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CLUB_EMPLOYEE_DETAIL, Utils.isNotEmpty(contactId) ? contactId : userId);
                break;

            default:
                break;
        }
    }


    @OnClick(R.id.contact_more)
    public void toDoMore() {
        DropDownMenuDialog.getDropDownMenuDialog(ContactInformationDetailActivity.this, mContactMoreItems, (index -> {
            switch (index) {
                case 0:
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
                    if (Utils.isNotEmpty(contactPhone) && Utils.matchPhoneNumFormat(contactPhone)) {
                        intent.putExtra(RequestConstant.KEY_PHONE_NUMBER, contactPhone);
                    }

                    startActivityForResult(intent, REQUEST_CODE_SET_REMARK);
                    break;
                case 1:
                    if (mContactMoreItems[1].equals(ResourceUtils.getString(R.string.delete_contact))) {
                        new RewardConfirmDialog(ContactInformationDetailActivity.this, getString(R.string.alert_delete_contact), getString(R.string.alert_delete_contact_message), "") {
                            @Override
                            public void onConfirmClick() {
                                Map<String, String> param = new HashMap<>();
                                param.put(RequestConstant.KEY_ID, contactId);
                                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_CONTACT, param);
                                super.onConfirmClick();
                            }
                        }.show();
                    } else {

                        new RewardConfirmDialog(ContactInformationDetailActivity.this, getString(R.string.alert_add_to_blacklist), getString(R.string.alert_add_to_blacklist_message), "") {
                            @Override
                            public void onConfirmClick() {
                                if (Utils.isEmpty(userId)) {
                                    ContactInformationDetailActivity.this.makeShortToast(getString(R.string.add_to_blacklist_failed));
                                } else if (!inBlacklist) {
                                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_ADD_TO_BLACKLIST, userId);
                                }
                                super.onConfirmClick();
                            }
                        }.show();
                    }
                    break;

            }
        })).show(contactMore);
    }

    // 打电话
    @OnClick(R.id.btn_call_phone)
    public void callPhoneToCustomer() {
        if (!showOperationButtons) {
            return;
        }
        // 判断客户号码是否可用
        if (TextUtils.isEmpty(contactPhone) || !Utils.matchPhoneNumFormat(contactPhone)) {
            this.makeShortToast("手机号码不存在");
            return;
        }
        PermissionTool.requestPermission(this, new String[]{Manifest.permission.CALL_PHONE}, new String[]{"拨打电话"}, REQUEST_CODE_CALL_PERMISSION);
    }

    // 发短信
    @OnClick(R.id.btn_chat)
    public void sendMessageToCustomer() {
        if (!showOperationButtons) {
            return;
        }
        // 判断客户号码是否可用
        if (TextUtils.isEmpty(contactPhone) || !Utils.matchPhoneNumFormat(contactPhone)) {
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
        if (!showOperationButtons) {
            return;
        }
        // 判断emChatId是否存在
        if (TextUtils.isEmpty(emChatId)) {
            this.makeShortToast("聊天失败，缺少客户信息");
            return;
        }
        if (emChatId.equals(SharedPreferenceHelper.getEmchatId())) {
            this.makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
        } else {
            UINavigation.gotoChatActivity(this, emChatId);
        }

    }

    // 同客户打招呼
    @OnClick(R.id.btn_EmHello)
    public void emHelloToCustomer() {
        if (!showOperationButtons) {
            return;
        }
        //  环信打招呼
        if (TextUtils.isEmpty(emChatId)) {
            this.makeShortToast("打招呼失败，缺少客户信息");
            return;
        }
        sayHello(userId);
    }

    // 移出聊天黑名单
    @OnClick(R.id.btn_rm_blacklist)
    public void removeFromBlacklist() {
        if (inBlacklist && Utils.isNotEmpty(userId)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REMOVE_FROM_BLACKLIST, userId);
        }
    }

    @OnClick(R.id.btn_operation)
    public void onClickBtnOperation() {
        TransitionDrawable drawable = (TransitionDrawable) btnOperation.getDrawable();
        if (!showOperationButtons) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(layoutOperationButtons, "alpha", 1.0f);
            animator.setDuration(500);
            animator.start();
            drawable.startTransition(500);
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(layoutOperationButtons, "alpha", 0.0f);
            animator.setDuration(500);
            animator.start();
            drawable.reverseTransition(500);
        }
        showOperationButtons = !showOperationButtons;
    }

    private void initOperationButtonForType(String contactType) {
        switch (contactType) {
            case Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER:
                layoutOperationButtons.setVisibility(View.GONE);
                btnOperation.setVisibility(View.GONE);
                btnEmHello.setVisibility(View.GONE);
                btnEmChat.setVisibility(View.GONE);
                btnCallPhone.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                break;
            case Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER:
            case Constant.CONTACT_INFO_DETAIL_TYPE_TECH:
                mContactTelephone.setVisibility(View.GONE);
                btnEmHello.setVisibility(View.GONE);
                btnEmChat.setVisibility(View.VISIBLE);
                if (Utils.isNotEmpty(contactPhone) && Utils.matchPhoneNumFormat(contactPhone)) {
                    btnCallPhone.setVisibility(View.VISIBLE);
                } else {
                    btnCallPhone.setVisibility(View.GONE);
                }
                btnChat.setVisibility(View.GONE);
                break;
            default:
                layoutOperationButtons.setVisibility(View.VISIBLE);
                btnOperation.setVisibility(View.VISIBLE);
                btnEmHello.setVisibility(View.GONE);
                btnEmChat.setVisibility(View.GONE);
                btnCallPhone.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                break;
        }
    }

    @CheckBusinessPermission((PermissionConstants.CONTACTS_EMP_PHONE))
    public void showEmployeePhone() {
        if (Utils.isNotEmpty(contactPhone) && Utils.matchPhoneNumFormat(contactPhone)) {
            mContactTelephone.setVisibility(View.VISIBLE);
            mContactTelephone.setText("电话：" + contactPhone);
            btnEmChat.setVisibility(View.VISIBLE);
            btnCallPhone.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
        } else {
            mContactTelephone.setVisibility(View.GONE);
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
            mContactMoreItems = new String[]{ResourceUtils.getString(R.string.add_remark), ResourceUtils.getString(R.string.add_to_blacklist)};
            contactMore.setVisibility(View.VISIBLE);
        }

        XLogger.d("userService", "update by customer detail data");
        if (!TextUtils.isEmpty(mCustomerInfo.id)) {
            mUser = new User(mCustomerInfo.userId);
            mUser.setName(mCustomerInfo.userName);
            mUser.setChatId(mCustomerInfo.emchatId);
            mUser.setAvatar(mCustomerInfo.avatarUrl);
            mUser.setMarkName(mCustomerInfo.userNoteName);
            userService.saveUser(mUser);
        }

        emChatId = mCustomerInfo.emchatId;
        chatHeadUrl = mCustomerInfo.avatarUrl;
        chatType = mCustomerInfo.customerType;
        Glide.with(mContext).load(mCustomerInfo.avatarUrl).error(R.drawable.img_default_square).into(mContactHead);

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

        if (TextUtils.isEmpty(mCustomerInfo.emchatId) || mCustomerInfo.customerType.equals(RequestConstant.TECH_ADD)) {
            mContactMoreItems = new String[]{ResourceUtils.getString(R.string.add_remark), ResourceUtils.getString(R.string.delete_contact)};
        }
        if (TextUtils.isEmpty(mCustomerInfo.emchatId)) {
            registerAlert.setVisibility(View.VISIBLE);
            contactPhone = mCustomerInfo.userLoginName;
            emChatId = "";
            mContactName.setText(mCustomerInfo.userNoteName);
            if (Utils.isNotEmpty(contactPhone) && Utils.matchPhoneNumFormat(contactPhone)) {
                mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + contactPhone);
            }

            mContactNickName.setVisibility(View.GONE);
            if (TextUtils.isEmpty(mCustomerInfo.remark)) {
                mContactRemark.setText(ResourceUtils.getString(R.string.customer_remark_empty));
                textRemarkAlert.setVisibility(View.GONE);
            } else {
                remarkIsNotEmpty = true;
                mContactRemark.setText(mCustomerInfo.remark);
                textRemarkAlert.setVisibility(View.VISIBLE);
            }
            customerType.setVisibility(View.GONE);
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
                customerType.setVisibility(View.GONE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
            } else if (mCustomerInfo.customerType.equals(RequestConstant.TEMP_USER)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.temporary_user));
            } else if (mCustomerInfo.customerType.equals(RequestConstant.FANS_USER)) {
                customerType.setVisibility(View.VISIBLE);
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
            } else if (mCustomerInfo.customerType.equals(RequestConstant.FANS_WX_USER)) {
                customerType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                customerOtherType.setVisibility(View.VISIBLE);
                customerType.setVisibility(View.VISIBLE);
                customerOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_contacts));
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

    // 处理打招呼结果
    private void handleSayHiDetailResult(SayHiBaseResult result) {
        if (result.statusCode == 200) {
            showToast("打招呼成功");
            //发送打招呼信息
            HelloSettingManager.getInstance().sendHelloTemplate(mUser);
            //将用户保存为已打招呼
            saveChatContact(emChatId);
            //刷新最近访客列表
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CLUB_CUSTOMER_RECENT_LIST);
        } else {
            showToast("打招呼失败:" + result.msg);
        }
    }

    private void saveChatContact(String chatId) {
        Map<String, String> saveParams = new HashMap<>();
        saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, chatId);
        saveParams.put(RequestConstant.KEY_CHAT_MSG_ID, "");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
    }

    private void handleContactPermissionDetail(ContactPermissionResult result) {
        if (result != null && result.statusCode == 200) {
            permissionInfo = result.respData;
        }
        // 更新按钮状态
        showButton();
    }

    private void showButton() {
        if (inBlacklist) {
            btnEmHello.setVisibility(View.GONE);
            btnEmChat.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnRmBlacklist.setVisibility(View.VISIBLE);
            btnOperation.setVisibility(View.GONE);
            layoutOperationButtons.setVisibility(View.GONE);
            return;
        }

        if (permissionInfo != null) {
            boolean showOperation = permissionInfo.call || permissionInfo.hello || permissionInfo.sms || permissionInfo.echat;
            if (showOperation && !permissionInfo.sms) {
                showOperationButtonsLessThanThree();
            } else {
                btnOperation.setVisibility(showOperation ? View.VISIBLE : View.GONE);
                layoutOperationButtons.setVisibility(showOperation ? View.VISIBLE : View.GONE);
            }

            // 更新按钮状态
            btnEmHello.setVisibility(permissionInfo.hello ? View.VISIBLE : View.GONE);
            btnEmChat.setVisibility(permissionInfo.echat ? View.VISIBLE : View.GONE);
            btnCallPhone.setVisibility(permissionInfo.call ? View.VISIBLE : View.GONE);
            btnChat.setVisibility(permissionInfo.sms ? View.VISIBLE : View.GONE);
        }
    }

    private void showOperationButtonsLessThanThree() {
        btnOperation.setVisibility(View.GONE);
        showOperationButtons = true;
        layoutOperationButtons.setVisibility(View.VISIBLE);
        layoutOperationButtons.setAlpha(1.0f);
    }

    private void handlerDeleteCustomer(DeleteContactResult result) {
        if (result.resultcode == 200) {
            RxBus.getInstance().post(new ContactHandlerBean());
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, () -> {
                ContactInformationDetailActivity.this.finish();
            }, 1500);
        } else {
            makeShortToast(result.msg.toString());
        }
    }

    private void handlerAddToBlacklist(AddToBlacklistResult result) {
        if (result.statusCode == 200) {
            //更新客户列表
            RxBus.getInstance().post(new ContactHandlerBean());
            makeShortToast(ResourceUtils.getString(R.string.add_to_blacklist_success));
            //清空聊天记录
            EMClient.getInstance().chatManager().deleteConversation(emChatId, true);
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, () -> ContactInformationDetailActivity.this.finish(), 1500);
        } else {
            makeShortToast(result.msg.toString());
        }
    }

    private void handlerRemoveFromBlacklist(RemoveFromBlacklistResult result) {
        if (result.statusCode == 200) {
            //更新客户列表
            RxBus.getInstance().post(new ContactHandlerBean());
            makeShortToast(ResourceUtils.getString(R.string.remove_from_blacklist_success));
            //更新黑名单列表
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, () -> {
                ContactInformationDetailActivity.this.finish();
            }, 1500);
        } else {
            makeShortToast(result.msg.toString());
        }
    }

    private void handlerInBlacklist(InBlacklistResult result) {
        if (result.statusCode == 200) {
            inBlacklist = result.respData;
            if (inBlacklist) {
                contactMore.setVisibility(View.GONE);
            } else {
                contactMore.setVisibility(View.VISIBLE);
            }
        }
        // 更新按钮状态
        showButton();
    }

    private void getCustomerInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ID, contactId);
        params.put(RequestConstant.KEY_USER_ID, userId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL, params);
    }

    private void getVisitView() {
        Map<String, String> paramsView = new HashMap<>();
        paramsView.put(RequestConstant.KEY_USER_ID, Utils.isNotEmpty(userId) ? userId : contactId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_VISIT_VIEW, paramsView);
    }

    private void booleanInBlacklist() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_IN_BLACKLIST, Utils.isNotEmpty(userId) ? userId : contactId);
    }

    private void getContactPermissionDetail() {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_REQUEST_CONTACT_PERMISSION_TAG, Constant.REQUEST_CONTACT_PERMISSION_DETAIL);
        params.put(RequestConstant.KEY_ID, Utils.isNotEmpty(userId) ? userId : contactId);
        params.put(RequestConstant.KEY_CONTACT_ID_TYPE, Constant.REQUEST_CONTACT_ID_TYPE_CUSTOMER);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_PERMISSION, params);

    }


    private void handlerEmployeeView(ClubEmployeeDetailResult employeeDetail) {
        if (employeeDetail.statusCode == 200) {
            if (employeeDetail.respData == null) {
                return;
            }
            emChatName = employeeDetail.respData.name;
            emChatId = employeeDetail.respData.emchatId;
            chatHeadUrl = employeeDetail.respData.avatarUrl;
            contactPhone = employeeDetail.respData.telephone;
            Glide.with(mContext).load(employeeDetail.respData.avatarUrl).error(R.drawable.img_default_square).into(mContactHead);
            if (Utils.isNotEmpty(employeeDetail.respData.id)) {
                mUser = new User(employeeDetail.respData.id);
                mUser.setName(employeeDetail.respData.name);
                mUser.setChatId(employeeDetail.respData.emchatId);
                mUser.setAvatar(employeeDetail.respData.avatarUrl);
                userService.saveUser(mUser);
            }
            if (Utils.isNotEmpty(employeeDetail.respData.techNo)) {
                SharedPreferenceHelper.setTechNoOld(employeeDetail.respData.id, employeeDetail.respData.techNo);
            }
            if (TextUtils.isEmpty(employeeDetail.respData.techNo)) {
                mContactName.setText(Utils.StrSubstring(12, employeeDetail.respData.name, true));
            } else {
                mContactName.setText(Utils.StrSubstring(12, employeeDetail.respData.name, true));
                llTechNum.setVisibility(View.VISIBLE);
                techNum.setText(employeeDetail.respData.techNo);
            }
            if (employeeDetail.respData.roles.equals("tech")) {
                if (TextUtils.isEmpty(employeeDetail.respData.description)) {
                    mContactRemark.setText(ResourceUtils.getString(R.string.contact_description_remark_empty));
                } else {
                    textRemarkAlert.setText(ResourceUtils.getString(R.string.contact_description_remark));
                    textRemarkAlert.setVisibility(View.VISIBLE);
                    mContactRemark.setText(employeeDetail.respData.description);
                }
            } else {
                mContactRemark.setVisibility(View.GONE);
            }
            showEmployeePhone();
            mContactOrderLayout.setVisibility(View.GONE);
            linearBelongTech.setVisibility(View.GONE);
            mContactNickName.setVisibility(View.GONE);
            if (Utils.isEmpty(emChatId)) {
                btnEmChat.setVisibility(View.GONE);
            } else {
                btnEmChat.setVisibility(View.VISIBLE);
            }
            if (Utils.isEmpty(emChatId) && Utils.isEmpty(contactPhone)) {
                btnOperation.setVisibility(View.GONE);
            } else {
                btnOperation.setVisibility(View.VISIBLE);
            }

        } else {
            showToast(employeeDetail.msg);
        }
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


    // 打招呼
    private void sayHello(String customerId) {
        if (!XmdChat.getInstance().isOnline()) {
            showToast("当前已经离线，无法打招呼!");
            return;
        }
        if (mUser == null) {
            showToast("没有用户信息!");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CALL_PERMISSION) {
            if (resultCode == RESULT_OK) {
                toCallPhone();
            } else {
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getCustomerInformationSubscription != null) {
            RxBus.getInstance().unsubscribe(getCustomerInformationSubscription, doDeleteContactSubscription, doShowVisitViewSubscription, sayHiDetailSubscription, contactPermissionDetailSubscription,
                    addToBlacklistSubscription, removeFromBlacklistSubscription, inBlacklistSubscription);
        }
        if (getClubEmployeeDetailSubscription != null) {
            RxBus.getInstance().unsubscribe(getClubEmployeeDetailSubscription);
        }
    }
}
