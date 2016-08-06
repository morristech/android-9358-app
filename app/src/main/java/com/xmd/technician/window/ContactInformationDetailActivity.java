package com.xmd.technician.window;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.DeleteContactResult;
import com.xmd.technician.bean.ManagerDetailResult;
import com.xmd.technician.bean.OrderBean;
import com.xmd.technician.bean.TechDetailResult;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.DropDownMenuDialog;
import com.xmd.technician.widget.RewardConfirmDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ContactInformationDetailActivity extends BaseActivity {
    @Bind(R.id.customer_head)
    CircleImageView mContactHead;
    @Bind(R.id.tv_customer_name)
    TextView mContactName;
    @Bind(R.id.tv_customer_telephoe)
    TextView mContactTelephone;
    @Bind(R.id.tv_customer_nick_name)
    TextView mContactNickName;
    @Bind(R.id.tv_customer_remark)
    TextView mContactRemark;
    @Bind(R.id.customer_order)
    TextView mContactOrder;
    @Bind(R.id.customer_reward)
    TextView mContactReward;
    @Bind(R.id.btn_chat)
    Button btnChat;
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
    @Bind(R.id.btn_call_phone)
    Button btnCallPhone;
    @Bind(R.id.btn_EmChat)
    Button btnEmChat;
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


    private static final int CONTACT_TYPE = 0x001;
    private static final int TECH_TYPE = 0x002;
    private static final int MANAGER_TYPE = 0x003;
    private static final int RESULT_ADD_REMARK = 0x010;
    private int currenContactType;

    private Subscription getCustomerInformationSubscription;
    private Subscription getManagerInformationSubscription;
    private Subscription getTechInformationSubscription;
    private Subscription doDeleteContactSubscription;
    private String contactId;
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

    private String userType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_information_deatil);
        ButterKnife.bind(this);
        mContext = this;
        Intent intent = getIntent();
        contactId = intent.getStringExtra(RequestConstant.KEY_CUSTOMER_ID);
        contactType = intent.getStringExtra(RequestConstant.KEY_CONTACT_TYPE);
        isEmptyCustomer = intent.getBooleanExtra(RequestConstant.KEY_IS_EMPTY, false);
        managerHeadUrl = intent.getStringExtra(RequestConstant.KEY_MANAGER_URL);
        initView();
    }

    private void initView() {
        setTitle(R.string.customer_information_deatail);
        setBackVisible(true);
        doDeleteContactSubscription = RxBus.getInstance().toObservable(DeleteContactResult.class).subscribe(result ->{
            handlerDeleteCustomer(result);
        });
        if (contactType.equals("manager")) {
            currenContactType = MANAGER_TYPE;
        } else if (contactType.equals("tech")) {
            currenContactType = TECH_TYPE;
        } else {
            currenContactType = CONTACT_TYPE;
        }
        getViewFromType(currenContactType);
    }

    private void getViewFromType(int type) {
        switch (type) {
            case CONTACT_TYPE:
                contactMore.setVisibility(View.VISIBLE);
                getCustomerInformationSubscription = RxBus.getInstance().toObservable(CustomerDetailResult.class).subscribe(
                        customer -> handlerCustomer(customer)
                );
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL, params);
                break;
            case TECH_TYPE:
                getTechInformationSubscription = RxBus.getInstance().toObservable(TechDetailResult.class).subscribe(
                        tech -> handlerTech(tech)
                );
                Map<String, String> paramTech = new HashMap<>();
                paramTech.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_INFO_DETAIL, paramTech);
                break;
            case MANAGER_TYPE:
                getManagerInformationSubscription = RxBus.getInstance().toObservable(ManagerDetailResult.class).subscribe(
                        manager -> handlerManager(manager)
                );
                Map<String, String> paramManager = new HashMap<>();
                paramManager.put(RequestConstant.KEY_ID, contactId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_MANAGER_INFO_DETAIL, paramManager);
                break;
        }
    }


    @OnClick(R.id.btn_call_phone)
    public void callPhone() {
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactPhone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(dialIntent);

    }

    @OnClick(R.id.btn_chat)
    public void sendMessageToCustomer() {
        Uri uri = Uri.parse("smsto:" + contactPhone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }

    @OnClick(R.id.btn_EmChat)
    public void chatToCustomer() {
        Log.i("TAG","chatName>>"+chatName);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(chatEmId, chatName, chatHeadUrl));
    }

    @OnClick(R.id.contact_more)
    public void toDoMore() {
        final String [] items = new String[]{ResourceUtils.getString(R.string.delete_contact),ResourceUtils.getString(R.string.add_remark)};
        DropDownMenuDialog.getDropDownMenuDialog(ContactInformationDetailActivity.this,items,(index -> {
            switch (index){
                case 0:
                    new RewardConfirmDialog(ContactInformationDetailActivity.this,getString(R.string.alert_delete_contact), getString(R.string.alert_delete_contact_message)){
                        @Override
                        public void onConfirmClick() {
                            Map<String, String> param = new HashMap<>();
                            param.put(RequestConstant.KEY_ID, contactId);
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_CONTACT,param);
                            super.onConfirmClick();
                        }
                    }.show();
                    break;
                case 1:
                    Intent intent = new Intent(ContactInformationDetailActivity.this,EditContactInformation.class);
                    intent.putExtra(RequestConstant.KEY_ID,contactId);
                    intent.putExtra(RequestConstant.KEY_NOTE_NAME,mContactName.getText().toString());
                    intent.putExtra(RequestConstant.KEY_USERNAME,mContactNickName.getText().toString());
                    if(!remarkIsNotEmpty&&mContactRemark.getText().toString().equals(ResourceUtils.getString(R.string.customer_remark_empty))){
                        textRemarkAlert.setVisibility(View.VISIBLE);
                        intent.putExtra(RequestConstant.KEY_REMARK,mContactRemark.getText().toString());
                    }else{
                        intent.putExtra(RequestConstant.KEY_REMARK,"");
                    }

                    intent.putExtra(RequestConstant.KEY_PHONE_NUMBER,contactPhone);
                    startActivityForResult(intent,RESULT_ADD_REMARK);
                    break;
            }
        })).show(contactMore);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(getCustomerInformationSubscription!=null){
            RxBus.getInstance().unsubscribe(getCustomerInformationSubscription);
        }
        if(getManagerInformationSubscription!=null){
            RxBus.getInstance().unsubscribe(getManagerInformationSubscription);
        }
        if(getTechInformationSubscription!=null){
            RxBus.getInstance().unsubscribe(getTechInformationSubscription);
        }
        if(doDeleteContactSubscription!=null){
            RxBus.getInstance().unsubscribe(doDeleteContactSubscription);
        }

    }

    private void handlerCustomer(CustomerDetailResult customer) {
        chatEmId = customer.respData.techCustomer.emchatId;
        chatHeadUrl = customer.respData.techCustomer.avatarUrl;
        if(Utils.isNotEmpty(customer.respData.techCustomer.userNoteName)){
            chatName = customer.respData.techCustomer.userNoteName;
        }else {
            chatName = customer.respData.techCustomer.userName;
        }

        if(customer==null){
          return;
        }
        isEmptyCustomer = TextUtils.isEmpty(customer.respData.techCustomer.emchatId) ? true : false;
        Glide.with(mContext).load(customer.respData.techCustomer.avatarUrl).into(mContactHead);
        if (isEmptyCustomer) {
            btnEmChat.setEnabled(false);
            contactPhone = customer.respData.techCustomer.userLoginName;
            customerChatId = "";
            mContactName.setText(customer.respData.techCustomer.userNoteName);
            mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone)+contactPhone);
            mContactNickName.setVisibility(View.GONE);
            if(TextUtils.isEmpty(customer.respData.techCustomer.remark)){
                mContactRemark.setText(ResourceUtils.getString(R.string.customer_remark_empty));
                textRemarkAlert.setVisibility(View.GONE);

            }else{
                remarkIsNotEmpty = true;
                mContactRemark.setText(customer.respData.techCustomer.remark);
                textRemarkAlert.setVisibility(View.VISIBLE);
            }
        } else {
            btnEmChat.setEnabled(true);
            mContactOrderLayout.setVisibility(View.VISIBLE);
            contactPhone = customer.respData.techCustomer.userLoginName;
            customerChatId = customer.respData.techCustomer.emchatId;
            if (!TextUtils.isEmpty(customer.respData.techCustomer.userNoteName)) {
                mContactName.setText(customer.respData.techCustomer.userNoteName);
            } else if(!TextUtils.isEmpty(customer.respData.techCustomer.userName)){
                mContactName.setText(customer.respData.techCustomer.userName);
            }else {
                mContactName.setText(ResourceUtils.getString(R.string.default_user_name));
            }
            if (!TextUtils.isEmpty(customer.respData.techCustomer.userName)&&Utils.isNotEmpty(customer.respData.techCustomer.userNoteName)
                    &&!customer.respData.techCustomer.userName.equals(ResourceUtils.getString(R.string.default_user_name))
                    ) {
                mContactNickName.setText("昵称："+ customer.respData.techCustomer.userName);
            }else{
                mContactNickName.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(customer.respData.techCustomer.userLoginName)) {
                mContactTelephone.setText("电话："+customer.respData.techCustomer.userLoginName);
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
            if (!TextUtils.isEmpty(String.valueOf(customer.respData.techCustomer.rewardAmount))) {
                mContactReward.setText(String.valueOf(customer.respData.techCustomer.rewardAmount));
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
        chatName = tech.respData.name;
        chatEmId = tech.respData.emchatId;
        chatHeadUrl = tech.respData.avatarUrl;

        btnEmChat.setEnabled(true);
        contactPhone = tech.respData.phoneNum;
        customerChatId = tech.respData.emchatId;
        Glide.with(mContext).load(tech.respData.avatarUrl).into(mContactHead);
        if (TextUtils.isEmpty(tech.respData.serialNo)) {
            mContactName.setText(tech.respData.name);
        } else {
            mContactName.setText(tech.respData.name);
            llTechNum.setVisibility(View.VISIBLE);
            techNum.setText(tech.respData.serialNo);

        }
        mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + tech.respData.phoneNum);
        if(TextUtils.isEmpty(tech.respData.description)){
            mContactRemark.setText(ResourceUtils.getString(R.string.contact_description_remark_empty));
        }else{
            textRemarkAlert.setText(ResourceUtils.getString(R.string.contact_description_remark));
            textRemarkAlert.setVisibility(View.VISIBLE);
            mContactRemark.setText(tech.respData.description);
        }

        mContactOrderLayout.setVisibility(View.GONE);
        mContactNickName.setVisibility(View.GONE);
    }

    private void handlerManager(ManagerDetailResult manager) {
        chatName = "店长";
        chatHeadUrl = manager.respData.avatarUrl;
        chatEmId = manager.respData.emchatId;
        contactPhone = manager.respData.phoneNum;
        customerChatId = manager.respData.emchatId;
        if(Utils.isNotEmpty(customerChatId)){
            btnEmChat.setEnabled(true);
        }
        if(!TextUtils.isEmpty(managerHeadUrl)){
            Glide.with(mContext).load(managerHeadUrl).into(mContactHead);
        }
        mContactName.setText(ResourceUtils.getString(R.string.contact_manager));
        mContactTelephone.setText(ResourceUtils.getString(R.string.contact_telephone) + manager.respData.phoneNum);
        mContactOrderLayout.setVisibility(View.GONE);
        mContactNickName.setVisibility(View.GONE);
        mContactRemark.setVisibility(View.GONE);
    }
    private void handlerDeleteCustomer(DeleteContactResult result){
        if(result.resultcode==200){
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST);
            makeShortToast(ResourceUtils.getString(R.string.delete_contact_success));
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    ContactInformationDetailActivity.this.finish();
                }
            }, 1500);
        }else{
            makeShortToast(result.msg.toString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(Utils.isNotEmpty(data.getStringExtra(RequestConstant.KEY_NOTE_NAME))){
                mContactName.setText(data.getStringExtra(RequestConstant.KEY_NOTE_NAME));
            }
           if(Utils.isNotEmpty(data.getStringExtra(RequestConstant.KEY_REMARK))){
                mContactRemark.setText(data.getStringExtra(RequestConstant.KEY_REMARK));
           }

        }
    }
}
