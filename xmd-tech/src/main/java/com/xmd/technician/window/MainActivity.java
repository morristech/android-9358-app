package com.xmd.technician.window;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.XmdChat;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.UserInfoEvent;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.permission.BusinessPermissionManager;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.IBusinessPermissionManager;
import com.xmd.permission.PermissionConstants;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.IsBindResult;
import com.xmd.technician.bean.SayHiResult;
import com.xmd.technician.chat.runtimepermissions.PermissionsManager;
import com.xmd.technician.chat.runtimepermissions.PermissionsResultAction;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class MainActivity extends BaseFragmentActivity implements BaseFragment.IFragmentCallback {

    public static final int REQUEST_CODE_JOIN_CLUB = 1;
    public static final int REQUEST_CODE_EDIT_TECH_INFO = 2;


    private List<Fragment> mFragmentList = new LinkedList<>();
    private List<View> mBottomBarButtonList = new LinkedList<View>();

    private MainFragment mHomeFragment;

    private int mCurrentTabIndex = -1;
    private Subscription mGetUserIsBindWXSubscription;

    private String contactPhone;

    @BindView(R.id.main_unread_message)
    TextView mUnreadMsgLabel;

    private IBusinessPermissionManager permissionManager = BusinessPermissionManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLoginStatus();
        permissionManager.loadPermissions(new Callback<Void>() {
            @Override
            public void onResponse(Void result, Throwable error) {
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
                    UINavigation.gotoLogin(MainActivity.this);
                }
            }
        });

        mGetUserIsBindWXSubscription = RxBus.getInstance().toObservable(IsBindResult.class).subscribe(
                result -> handlerIsBindResult(result)
        );

        requestPermissions();
        //检查更新
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE);

        EventBusSafeRegister.register(this);

        XmdChat.getInstance().loadConversation();
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
        addFragment(R.id.main_button_message, TechChatConversationListFragment.class);
    }

    @CheckBusinessPermission(PermissionConstants.CONTACTS)
    public void addFragmentContacts() {
        addFragment(R.id.main_button_contacts, ContactsSummaryFragment.class);
    }

    @CheckBusinessPermission(PermissionConstants.MARKETING)
    public void addFragmentMarketing() {
        addFragment(R.id.main_button_marketing, ShareCouponFragment.class);
    }

    private Fragment addFragment(int id, Class clazz) {
        View view = findViewById(id);
        if (view != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTagById(id));
            if (fragment == null) {
                try {
                    if (clazz.equals(TechChatConversationListFragment.class)) {
                        fragment = TechChatConversationListFragment.newInstance("消息");
                    } else {
                        fragment = (Fragment) clazz.newInstance();
                    }
                    ft.add(R.id.fragment_container, fragment, getFragmentTagById(id));
                } catch (Exception e) {
                    Logger.e("init fragment failed:" + e.getLocalizedMessage());
                    return null;
                }
            }
            ft.hide(fragment);
            ft.commit();

            mFragmentList.add(fragment);

            view.setVisibility(View.VISIBLE);
            mBottomBarButtonList.add(view);
            final int index = mFragmentList.size() - 1;
            view.setOnClickListener(v -> switchFragment(index));
            return fragment;
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
        RxBus.getInstance().unsubscribe(mGetUserIsBindWXSubscription);
        EventBusSafeRegister.unregister(this);
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
    @Subscribe
    public void updateUnreadMsgLabel(EventTotalUnreadCount event) {
        int count = event.getCount();
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

    @Subscribe
    public void sendMessageOrCall(UserInfoEvent event) {
        UserInfoService userService = UserInfoServiceImpl.getInstance();
        User mUser;
        mUser = new User(event.bean.userId);
        mUser.setName(event.bean.emChatName);
        mUser.setChatId(event.bean.emChatId);
        mUser.setAvatar(event.bean.chatHeadUrl);
        mUser.setNoteName(event.bean.userNoteName);
        userService.saveUser(mUser);
        if (event.appType == 1) {
            switch (event.toDoType) {
                case 1://聊天
                    // 判断emChatId是否存在
                    if (TextUtils.isEmpty(event.bean.emChatId)) {
                        this.makeShortToast("聊天失败，缺少客户信息");
                        return;
                    }
                    if (event.bean.emChatId.equals(SharedPreferenceHelper.getEmchatId())) {
                        this.makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
                        return;
                    } else {
                        UINavigation.gotoChatActivity(this, event.bean.emChatId);
                    }
                    break;
                case 3://打招呼
                    if (TextUtils.isEmpty(event.bean.emChatId)) {
                        this.makeShortToast("打招呼失败，缺少客户信息");
                        return;
                    }
                    sayHello(event.bean);
                    break;
            }
        }

    }

    // 打招呼
    private void sayHello(UserInfoBean bean) {
        if (!EMClient.getInstance().isConnected()) {
            showToast("当前已经离线，请重新登录!");
            return;
        }
        HelloSettingManager.getInstance().sendHelloTemplate(bean.emChatId, new Callback<SayHiResult>() {
            @Override
            public void onResponse(SayHiResult result, Throwable error) {
                if (error != null) {
                    XToast.show("打招呼失败：" + error.getMessage());
                } else {
                    XToast.show("打招呼成功！");
                }
            }
        });
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
