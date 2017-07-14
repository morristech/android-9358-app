package com.example.xmd_m_comment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.xmd_m_comment.bean.AddToBlacklistResult;
import com.example.xmd_m_comment.bean.ContactPermissionInfo;
import com.example.xmd_m_comment.bean.ContactPermissionResult;
import com.example.xmd_m_comment.bean.DeleteCustomerResult;
import com.example.xmd_m_comment.bean.InBlacklistResult;
import com.example.xmd_m_comment.bean.RemoveFromBlacklistResult;
import com.example.xmd_m_comment.bean.UserInfoBean;
import com.example.xmd_m_comment.event.UserInfoEvent;
import com.example.xmd_m_comment.httprequest.ConstantResources;
import com.example.xmd_m_comment.httprequest.DataManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.BaseActivity;
import com.xmd.app.widget.DropDownMenuDialog;
import com.xmd.app.widget.PromptConfirmDialog;
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

    public static final String INTENT_FORM_TYPE = "fromType";
    public static final String CURRENT_USER_IS_TECH = "isTech";
    public static final String CURRENT_USER_ID = "userId";

    private String fromType;  //来自管理者，来自技师manager,tech
    private boolean customerIsTech; //技师详情，用户详情
    private String userId; //用户Id;
    private CustomerInfoDetailManagerFragment mInfoManagerFragment;
    private CustomerInfoDetailTechFragment mInfoTechFragment;
    private UserAddInfoDetailFragment mUserAddInfoDetailFragment;
    private TechInfoDetailFragment mTechInfoDetailFragment;
    private boolean inBlackList;
    private RelativeLayout rlRightMore;
    private boolean showOperationButtons;
    private ContactPermissionInfo permissionInfo;
    private UserInfoBean mBean;


    public static void StartCustomerInfoDetailActivity(Activity activity, String userId, String fromType, boolean customerIsTech) {
        Intent intent = new Intent(activity, CustomerInfoDetailActivity.class);
        intent.putExtra(CURRENT_USER_ID, userId);
        intent.putExtra(INTENT_FORM_TYPE, fromType);
        intent.putExtra(CURRENT_USER_IS_TECH, customerIsTech);
        activity.startActivity(intent);
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
        if (fromType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            llOperation.setVisibility(View.GONE);
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
            EditCustomerInformationActivity.startEditCustomerInformationActivity(this, mBean.id, ConstantResources.INTENT_TYPE_MANAGER, mBean.emChatName, mBean.userNoteName, mBean.contactPhone, mBean.remarkMessage, mBean.remarkImpression);
        } else {
            EditCustomerInformationActivity.startEditCustomerInformationActivity(this, mBean.id, ConstantResources.INTENT_TYPE_TECH, mBean.emChatName, mBean.userNoteName, mBean.contactPhone, mBean.remarkMessage, mBean.remarkImpression);
        }


    }

    @Subscribe
    public void userInfo(UserInfoBean bean) {
        mBean = bean;
    }

    private void loadBlackInfo() {
        DataManager.getInstance().loadBooleanInBlackList(userId, new NetworkSubscriber<InBlacklistResult>() {
            @Override
            public void onCallbackSuccess(InBlacklistResult result) {
                XLogger.i(">>>", "是否在黑名单中加载成功");
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
                XLogger.i(">>>", "权限加载成功");
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
        if (inBlackList) {
            btnEmHello.setVisibility(View.GONE);
            btnEmChat.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
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

    @OnClick(R2.id.btn_call_phone)
    public void onBtnCallPhoneClicked() {
        XLogger.i(">>>", "打电话");
        if (null != mBean) {
            EventBus.getDefault().post(new UserInfoEvent(0, mBean));
        }

    }

    @OnClick(R2.id.btn_EmChat)
    public void onBtnEmChatClicked() {
        XLogger.i(">>>", "发消息");
        if (null != mBean) {
            EventBus.getDefault().post(new UserInfoEvent(1, mBean));
        }
    }

    @OnClick(R2.id.btn_chat)
    public void onBtnChatClicked() {
        XLogger.i(">>>", "发短信");
        if (null != mBean) {
            EventBus.getDefault().post(new UserInfoEvent(2, mBean));
        }
    }

    @OnClick(R2.id.btn_EmHello)
    public void onBtnEmHelloClicked() {
        XLogger.i(">>>", "打招呼");
        if (null != mBean) {
            EventBus.getDefault().post(new UserInfoEvent(3, mBean));
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
