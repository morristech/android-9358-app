package com.xmd.technician.window;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.redpoint.RedPointService;
import com.xmd.m.notify.redpoint.RedPointServiceImpl;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.IsBindResult;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.chat.runtimepermissions.PermissionsManager;
import com.xmd.technician.chat.runtimepermissions.PermissionsResultAction;
import com.xmd.technician.common.Callback;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.permission.BusinessPermissionManager;
import com.xmd.technician.permission.CheckBusinessPermission;
import com.xmd.technician.permission.IBusinessPermissionManager;
import com.xmd.technician.permission.PermissionConstants;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

public class MainActivity extends BaseFragmentActivity implements BaseFragment.IFragmentCallback {

    public static final int REQUEST_CODE_JOIN_CLUB = 1;
    public static final int REQUEST_CODE_EDIT_TECH_INFO = 2;

    private List<BaseFragment> mFragmentList = new LinkedList<BaseFragment>();
    private List<View> mBottomBarButtonList = new LinkedList<View>();

    private MainFragment mHomeFragment;
    private ChatFragment mChatFragment;
    private ContactsSummaryFragment mContactsSummaryFragment;
    private ShareCouponFragment mMarketingFragment;


    private int mCurrentTabIndex = -1;
    private Subscription mSysNoticeNotifySubscription;
    private Subscription mGetUserIsBindWXSubscription;
    //环信
    private Subscription mUnreadEmchatCountSubscription;
    private ChatHelper mChatHelper;

    @Bind(R.id.main_unread_message)
    TextView mUnreadMsgLabel;

    private RedPointService redPointService = RedPointServiceImpl.getInstance();


    private IBusinessPermissionManager permissionManager = BusinessPermissionManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLoginStatus();
        mChatHelper = ChatHelper.getInstance();
        permissionManager.loadPermissions(new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                if (error == null) {
                    addFragmentHome();
                    addFragmentMessage();
                    addFragmentContacts();
                    addFragmentMarketing();
                    if (mFragmentList.size() == 0) {
                        Toast.makeText(MainActivity.this, "对不起，您没有任何权限，请询问管理员", Toast.LENGTH_LONG).show();
                        return;
                    }
                    switchFragment(0);
                    processXmdDisplay(getIntent());
                    permissionManager.checkAndSyncPermissions();
                } else {
                    Toast.makeText(MainActivity.this, "加载权限失败:" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

//        mSysNoticeNotifySubscription = RxBus.getInstance().toObservable(SystemNoticeResult.class).subscribe(
//                result -> updateUnreadMsgLabel(mChatHelper.getUnreadMessageCount()));

        mGetUserIsBindWXSubscription = RxBus.getInstance().toObservable(IsBindResult.class).subscribe(
                result -> handlerIsBindResult(result)
        );

        requestPermissions();
        //检查更新
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE);

        redPointService.bind(Constant.RED_POINT_CHAT_ALL_UNREAD, mUnreadMsgLabel, RedPointService.SHOW_TYPE_DIGITAL);
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(String permission) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkLoginStatus();

        if (processXmdDisplay(intent)) {
            return;
        }

        setIntent(intent);
        int index = getIntent().getIntExtra(Constant.EXTRA_FRAGMENT_SWITCH, -1);
        if (index >= 0) {
            switchFragment(index);
        }
    }

    private boolean processXmdDisplay(Intent intent) {
        XmdDisplay display = (XmdDisplay) intent.getSerializableExtra(UINavigation.EXTRA_XMD_DISPLAY);
        if (display != null) {
            UINavigation.processXmdDisplay(this, display);
            return true;
        }
        return false;
    }

    private void checkLoginStatus() {
        if (!LoginTechnician.getInstance().isLogin()) {
            UINavigation.gotoLogin(this);
            finish();
        }
    }

    private String getFragmentTagById(int id) {
        return "fragment_" + id;
    }

    @CheckBusinessPermission(PermissionConstants.HOME)
    public void addFragmentHome() {
        mHomeFragment = (MainFragment) addFragment(R.id.main_button_home, MainFragment.class);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE)
    public void addFragmentMessage() {
        mChatFragment = (ChatFragment) addFragment(R.id.main_button_message, ChatFragment.class);
//        updateUnreadMsgLabel(mChatHelper.getUnreadMessageCount());
//        mUnreadEmchatCountSubscription = RxBus.getInstance().toObservable(EventUnreadMessageCount.class).subscribe(
//                unreadMessageCount -> {
//                    updateUnreadMsgLabel(unreadMessageCount.getUnread());
//                }
//        );
    }

    @CheckBusinessPermission(PermissionConstants.CONTACTS)
    public void addFragmentContacts() {
        mContactsSummaryFragment = (ContactsSummaryFragment) addFragment(R.id.main_button_contacts, ContactsSummaryFragment.class);

    }

    @CheckBusinessPermission(PermissionConstants.MARKETING)
    public void addFragmentMarketing() {
        mMarketingFragment = (ShareCouponFragment) addFragment(R.id.main_button_marketing, ShareCouponFragment.class);
    }

    private BaseFragment addFragment(int id, Class clazz) {
        View view = findViewById(id);
        if (view != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            BaseFragment baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTagById(id));
            if (baseFragment == null) {
                try {
                    baseFragment = (BaseFragment) clazz.newInstance();
                    ft.add(R.id.fragment_container, baseFragment, getFragmentTagById(id));
                } catch (Exception e) {
                    Logger.e("init fragment failed:" + e.getLocalizedMessage());
                    return null;
                }
            }
            ft.hide(baseFragment);
            ft.commit();

            mFragmentList.add(baseFragment);

            view.setVisibility(View.VISIBLE);
            mBottomBarButtonList.add(view);
            final int index = mFragmentList.size() - 1;
            view.setOnClickListener(v -> switchFragment(index));
            return baseFragment;
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(
                mSysNoticeNotifySubscription,
                mGetUserIsBindWXSubscription,
                mUnreadEmchatCountSubscription);
        redPointService.unBind(Constant.RED_POINT_CHAT_ALL_UNREAD, mUnreadMsgLabel);
    }

    public void switchFragment(int index) {
        if (mCurrentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            if (mCurrentTabIndex >= 0) {
                trx.hide(mFragmentList.get(mCurrentTabIndex));
            }
            trx.show(mFragmentList.get(index)).commit();
        }
        if (mCurrentTabIndex >= 0) {
            mBottomBarButtonList.get(mCurrentTabIndex).setSelected(false);
        }
        // 把当前tab设为选中状态
        mBottomBarButtonList.get(index).setSelected(true);
        mCurrentTabIndex = index;
    }


    /**
     * 刷新未读消息数
     */
    public void updateUnreadMsgLabel(int count) {
        if (count > 0) {
            if (count > 99) {
                mUnreadMsgLabel.setText("99+");
            } else {
                mUnreadMsgLabel.setText(String.valueOf(count));
            }
            mUnreadMsgLabel.setVisibility(View.VISIBLE);
        } else {
            mUnreadMsgLabel.setVisibility(View.INVISIBLE);
        }
    }

    public void handlerIsBindResult(IsBindResult result) {
        if ("Y".equals(result.respData)) {
            SharedPreferenceHelper.setBindSuccess(true);
        } else {
            SharedPreferenceHelper.setBindSuccess(false);
        }

    }

    public int getFragmentSize() {
        return mFragmentList.size();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EDIT_TECH_INFO) {
            if (resultCode == Activity.RESULT_OK) {
                //更新技师信息，需要刷新界面
                if (mHomeFragment != null) {
                    mHomeFragment.doUpdateTechInfoSuccess();
                }
            }
        }
    }
}
