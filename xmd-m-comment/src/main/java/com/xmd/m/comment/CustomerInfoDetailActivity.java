package com.xmd.m.comment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crazyman.library.PermissionTool;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.CustomerHeadDialog;
import com.xmd.app.widget.DropDownMenuDialog;
import com.xmd.app.widget.PromptConfirmDialog;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.bean.AddToBlacklistResult;
import com.xmd.m.comment.bean.ContactPermissionInfo;
import com.xmd.m.comment.bean.ContactPermissionResult;
import com.xmd.m.comment.bean.DeleteCustomerResult;
import com.xmd.m.comment.bean.InBlacklistResult;
import com.xmd.m.comment.bean.RemoveFromBlacklistResult;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.ShowCustomerHeadEvent;
import com.xmd.m.comment.event.UserInfoEvent;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-7-4.
 */

public class CustomerInfoDetailActivity extends BaseActivity {

    @BindView(R2.id.btn_call_phone)
    ImageButton btnCallPhone;
    @BindView(R2.id.btn_EmChat)
    ImageButton btnEmChat;
    @BindView(R2.id.btn_chat)
    ImageButton btnChat;
    @BindView(R2.id.btn_EmHello)
    ImageButton btnEmHello;
    @BindView(R2.id.layout_operation_buttons)
    LinearLayout layoutOperationButtons;
    @BindView(R2.id.ll_operation)
    LinearLayout llOperation;
    @BindView(R2.id.btn_operation)
    ImageButton btnOperation;

    public static final int REQUEST_CODE_CALL_PERMISSION = 0x001;
    public static final String INTENT_FORM_TYPE = "fromType";
    public static final String CURRENT_USER_IS_TECH = "isTech";
    public static final String CURRENT_USER_ID = "userId";

    private String fromType;  //来自管理者，来自技师manager,tech
    private boolean customerIsTech; //技师详情，用户详情
    private String userId; //用户Id;
    private CustomerInfoDetailManagerFragment mInfoManagerFragment; //管理者客户
    private CustomerInfoDetailTechFragment mInfoTechFragment;//技师普通客户
    private UserAddInfoDetailFragment mUserAddInfoDetailFragment;//技师添加客户
    private TechInfoDetailFragment mTechInfoDetailFragment;//会所成员
    private boolean inBlackList;
    private RelativeLayout rlRightMore;
    private boolean showOperationButtons;
    private ContactPermissionInfo permissionInfo;
    private UserInfoBean mBean;
    private String contactPhone;
    private CustomerHeadDialog mCustomerHeadDialog;


    public static void StartCustomerInfoDetailActivity(Context context, String userId, String fromType, boolean customerIsTech) {
        Intent intent = new Intent(context, CustomerInfoDetailActivity.class);
        intent.putExtra(CURRENT_USER_ID, userId);
        intent.putExtra(INTENT_FORM_TYPE, fromType);
        intent.putExtra(CURRENT_USER_IS_TECH, customerIsTech);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info_detail);
        ButterKnife.bind(this);
        rlRightMore = (RelativeLayout) findViewById(R.id.rl_toolbar_right);
        getIntentData();
        initView();
        initFragmentView();
    }

    public void getIntentData() {
        Intent intentData = getIntent();
        fromType = intentData.getStringExtra(INTENT_FORM_TYPE);
        customerIsTech = intentData.getBooleanExtra(CURRENT_USER_IS_TECH, false);
        userId = intentData.getStringExtra(CURRENT_USER_ID);
    }

    private void initView() {
        setTitle(R.string.customer_info_detail_activity_title);
        setBackVisible(true);
        if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            showButton();
        } else {
            llOperation.setVisibility(View.VISIBLE);
        }
        if (!customerIsTech && (fromType.equals(ConstantResources.INTENT_TYPE_TECH) || fromType.equals(ConstantResources.CUSTOMER_TYPE_USER_ADD))) {
            setRightVisible(true, R.drawable.contact_icon_more);
            loadBlackInfo();
            loadPermissionInfo();
        } else {
            setRightVisible(false, -1);
        }
    }


    private void initFragmentView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_USER_ID, userId);

        if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            mInfoManagerFragment = new CustomerInfoDetailManagerFragment();
            mInfoManagerFragment.setArguments(bundle);
            ft.replace(R.id.fragment_detail, mInfoManagerFragment);
        } else if (fromType.equals(ConstantResources.INTENT_TYPE_TECH)) {
            if (customerIsTech) {
                mTechInfoDetailFragment = new TechInfoDetailFragment();
                mTechInfoDetailFragment.setArguments(bundle);
                ft.replace(R.id.fragment_detail, mTechInfoDetailFragment);
                showButton();
            } else {
                mInfoTechFragment = new CustomerInfoDetailTechFragment();
                mInfoTechFragment.setArguments(bundle);
                ft.replace(R.id.fragment_detail, mInfoTechFragment);
            }
        } else {
            mUserAddInfoDetailFragment = new UserAddInfoDetailFragment();
            mUserAddInfoDetailFragment.setArguments(bundle);
            ft.replace(R.id.fragment_detail, mUserAddInfoDetailFragment);
        }
        ft.commit();
    }

    @Override
    public void onRightImageClickedListener() {
        super.onRightImageClickedListener();
        final String[] items = new String[2];
        items[0] = "修改备注";
        if (fromType.equals(ConstantResources.CUSTOMER_TYPE_USER_ADD)) {
            items[1] = "删除好友";
        } else {
            if (!inBlackList) {
                items[1] = "加入黑名单";
            } else {
                items[1] = "移出黑名单";
            }
        }

        DropDownMenuDialog dialog = DropDownMenuDialog.getDropDownMenuDialog(this, items, new DropDownMenuDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                switch (index) {
                    case 0:
                        editCustomerRemark();
                        break;
                    case 1:
                        if (items[1].equals("删除好友")) {
                            new PromptConfirmDialog(CustomerInfoDetailActivity.this, "确认删除好友?", "删除好友后您的好友列表不再显示该好友信息.", "", new PromptConfirmDialog.ConfirmClickedListener() {
                                @Override
                                public void onConfirmClick() {
                                    deleteCustomer();
                                }
                            }).show();

                        } else if (items[1].equals("加入黑名单")) {
                            new PromptConfirmDialog(CustomerInfoDetailActivity.this, "加入黑名单", "添加黑名单后,将不再接受对方消息", "", new PromptConfirmDialog.ConfirmClickedListener() {
                                @Override
                                public void onConfirmClick() {
                                    addUserToBlackList();
                                }
                            }).show();
                        } else {
                            removeUserFromBlackList();
                        }
                        break;
                }
            }
        });
        dialog.show(rlRightMore);
    }

    private void editCustomerRemark() {
        if (mBean == null) {
            return;
        }
        if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            EditCustomerInformationActivity.startEditCustomerInformationActivity(this, mBean.userId, mBean.id, ConstantResources.INTENT_TYPE_MANAGER, mBean.emChatName, mBean.userNoteName, mBean.contactPhone, mBean.remarkMessage, mBean.remarkImpression);
        } else {
            EditCustomerInformationActivity.startEditCustomerInformationActivity(this, mBean.userId, mBean.id, ConstantResources.INTENT_TYPE_TECH, mBean.emChatName, mBean.userNoteName, mBean.contactPhone, mBean.remarkMessage, mBean.remarkImpression);
        }
    }

    @Subscribe
    public void userInfo(UserInfoBean bean) {
        mBean = bean;
    }

    @Subscribe
    public void showCustomerHead(ShowCustomerHeadEvent event) {
        mCustomerHeadDialog = new CustomerHeadDialog(this);
        mCustomerHeadDialog.show();
        mCustomerHeadDialog.setImageHead(event.headUrl);
    }

    private void loadBlackInfo() {
        DataManager.getInstance().loadBooleanInBlackList(userId, new NetworkSubscriber<InBlacklistResult>() {
            @Override
            public void onCallbackSuccess(InBlacklistResult result) {
                inBlackList = (boolean) result.getRespData();
            }

            @Override
            public void onCallbackError(Throwable e) {
                Toast.makeText(CustomerInfoDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPermissionInfo() {
        DataManager.getInstance().loadContactPermission(userId, new NetworkSubscriber<ContactPermissionResult>() {
            @Override
            public void onCallbackSuccess(ContactPermissionResult result) {
                permissionInfo = result.getRespData();
                showButton();
            }

            @Override
            public void onCallbackError(Throwable e) {
                Toast.makeText(CustomerInfoDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removeUserFromBlackList() {
        DataManager.getInstance().removeFromBlackList(userId, new NetworkSubscriber<RemoveFromBlacklistResult>() {
            @Override
            public void onCallbackSuccess(RemoveFromBlacklistResult result) {
                inBlackList = false;
            }

            @Override
            public void onCallbackError(Throwable e) {
                Toast.makeText(CustomerInfoDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToBlackList() {
        DataManager.getInstance().addToBlackList(userId, new NetworkSubscriber<AddToBlacklistResult>() {
            @Override
            public void onCallbackSuccess(AddToBlacklistResult result) {
                inBlackList = true;
                Toast.makeText(CustomerInfoDetailActivity.this, "已成功加入黑名单", Toast.LENGTH_SHORT).show();
                CustomerInfoDetailActivity.this.finish();
            }

            @Override
            public void onCallbackError(Throwable e) {
                Toast.makeText(CustomerInfoDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCustomer() {
        DataManager.getInstance().deleteCustomer(userId, new NetworkSubscriber<DeleteCustomerResult>() {
            @Override
            public void onCallbackSuccess(DeleteCustomerResult result) {
                Toast.makeText(CustomerInfoDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCallbackError(Throwable e) {
                Toast.makeText(CustomerInfoDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showButton() {
        if (inBlackList) {//在黑名单中
            btnEmHello.setVisibility(View.GONE);
            btnEmChat.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnOperation.setVisibility(View.GONE);
            layoutOperationButtons.setVisibility(View.GONE);
            return;
        }
        if (customerIsTech) {//本店成员
            btnEmHello.setVisibility(View.GONE);
            btnOperation.setVisibility(View.GONE);
            btnEmChat.setVisibility(View.VISIBLE);
            btnCallPhone.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            layoutOperationButtons.setAlpha(1.0f);
            layoutOperationButtons.setVisibility(View.VISIBLE);
            btnOperation.setVisibility(View.GONE);
            return;
        }
        if (fromType.equals(ConstantResources.CUSTOMER_TYPE_USER_ADD)) { //添加的用户
            btnEmHello.setVisibility(View.GONE);
            btnEmChat.setVisibility(View.GONE);
            btnOperation.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
            layoutOperationButtons.setAlpha(1.0f);
            layoutOperationButtons.setVisibility(View.VISIBLE);
            return;
        }

        if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            btnEmHello.setVisibility(View.GONE);
            btnOperation.setVisibility(View.GONE);
            btnEmChat.setVisibility(View.VISIBLE);
            btnCallPhone.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
            layoutOperationButtons.setAlpha(1.0f);
            layoutOperationButtons.setVisibility(View.GONE);
            btnOperation.setVisibility(View.VISIBLE);
            return;
        }

        if (permissionInfo != null) {
            boolean showOperation = permissionInfo.call || permissionInfo.hello || permissionInfo.sms || permissionInfo.echat;
            if (showOperation && !permissionInfo.sms) {
                showOperationButtonsLessThanThree();
            } else {
                btnOperation.setVisibility(showOperation ? View.VISIBLE : View.GONE);
            }

            // 更新按钮状态
            btnEmHello.setVisibility(permissionInfo.hello ? View.VISIBLE : View.GONE);
            btnEmChat.setVisibility(permissionInfo.echat ? View.VISIBLE : View.GONE);
            btnCallPhone.setVisibility(permissionInfo.call ? View.VISIBLE : View.GONE);
            btnChat.setVisibility(permissionInfo.sms ? View.VISIBLE : View.GONE);
            if (showOperation && permissionInfo.sms) {
                layoutOperationButtons.setVisibility(View.GONE);
            } else {
                layoutOperationButtons.setVisibility(View.VISIBLE);
            }

        }
    }

    private void showOperationButtonsLessThanThree() {
        btnOperation.setVisibility(View.GONE);
        showOperationButtons = true;
        layoutOperationButtons.setVisibility(View.VISIBLE);
        layoutOperationButtons.setAlpha(1.0f);
    }

    @OnClick(R2.id.btn_operation)
    public void onBtnOperationClicked() {
        TransitionDrawable drawable = (TransitionDrawable) btnOperation.getDrawable();
        if (!showOperationButtons) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(layoutOperationButtons, "alpha", 1.0f);
            animator.setDuration(500);
            animator.start();
            drawable.startTransition(500);
            layoutOperationButtons.setVisibility(View.VISIBLE);
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(layoutOperationButtons, "alpha", 0.0f);
            animator.setDuration(500);
            animator.start();
            drawable.reverseTransition(500);
            layoutOperationButtons.setVisibility(View.GONE);
        }
        showOperationButtons = !showOperationButtons;
    }

    @OnClick(R2.id.btn_call_phone)
    public void onBtnCallPhoneClicked() {
        if (null != mBean) {
            contactPhone = mBean.contactPhone;
            PermissionTool.requestPermission(this, new String[]{Manifest.permission.CALL_PHONE}, new String[]{"拨打电话"}, REQUEST_CODE_CALL_PERMISSION);
        }

    }

    @OnClick(R2.id.btn_EmChat)
    public void onBtnEmChatClicked() {
        if (null != mBean) {
            if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
                EventBus.getDefault().post(new UserInfoEvent(0, 1, mBean));
            } else {
                EventBus.getDefault().post(new UserInfoEvent(1, 1, mBean));
            }
        }
    }

    @OnClick(R2.id.btn_EmHello)
    public void onBtnEmHelloClicked() {
        if (null != mBean) {
            if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
                EventBus.getDefault().post(new UserInfoEvent(0, 3, mBean));
            } else {
                EventBus.getDefault().post(new UserInfoEvent(1, 3, mBean));
            }
        }
    }

    @OnClick(R2.id.btn_chat)
    public void onBtnChatClicked() {
        if (null != mBean) {
            if (TextUtils.isEmpty(mBean.contactPhone) || !Utils.matchPhoneNumFormat(mBean.contactPhone)) {
                Toast.makeText(this, "手机号码不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = Uri.parse("smsto:" + mBean.contactPhone);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "");
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CALL_PERMISSION) {
            toCallPhone();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
