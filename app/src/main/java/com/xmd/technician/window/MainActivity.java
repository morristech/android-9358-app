package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.IsBindResult;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.gson.SystemNoticeResult;
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
    private ContactsFragment mContactsFragment;
    private ShareCouponFragment mMarketingFragment;


    private int mCurrentTabIndex = -1;
    private Subscription mSysNoticeNotifySubscription;
    private Subscription mGetUserIsBindWXSubscription;

    @Bind(R.id.main_unread_message)
    TextView mUnreadMsgLabel;


    private IBusinessPermissionManager permissionManager = BusinessPermissionManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        permissionManager.loadPermissions(new Callback<Void>() {
//            @Override
//            public void onResult(Throwable error, Void result) {
//                if (error == null) {
//                    addFragmentHome();
//                    addFragmentMessage();
//                    addFragmentContacts();
//                    addFragmentMarketing();
//                    if (mFragmentList.size() == 0) {
//                        Toast.makeText(MainActivity.this, "对不起，您没有任何权限，请询问管理员", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    switchFragment(0);
//                    permissionManager.checkAndSyncPermissions();
//                } else {
//                    Toast.makeText(MainActivity.this, "加载权限失败:" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        addFragmentHome();
        addFragmentMessage();
        addFragmentContacts();
        addFragmentMarketing();
        if (mFragmentList.size() == 0) {
            Toast.makeText(MainActivity.this, "对不起，您没有任何权限，请询问管理员", Toast.LENGTH_LONG).show();
            return;
        }
        switchFragment(0);

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        UserProfileProvider.getInstance().initContactList();

        mSysNoticeNotifySubscription = RxBus.getInstance().toObservable(SystemNoticeResult.class).subscribe(
                result -> updateUnreadMsgLabel());
        mGetUserIsBindWXSubscription = RxBus.getInstance().toObservable(IsBindResult.class).subscribe(
                result -> handlerIsBindResult(result)
        );

        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND,
                () -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID));
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND,
                () -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE));

    }

    @CheckBusinessPermission(PermissionConstants.HOME)
    public void addFragmentHome() {
        mHomeFragment = (MainFragment) addFragment(R.id.main_button_home, MainFragment.class);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE)
    public void addFragmentMessage() {
        mChatFragment = (ChatFragment) addFragment(R.id.main_button_message, ChatFragment.class);
    }

    @CheckBusinessPermission(PermissionConstants.CONTACTS)
    public void addFragmentContacts() {
        mContactsFragment = (ContactsFragment) addFragment(R.id.main_button_contacts, ContactsFragment.class);
    }

    @CheckBusinessPermission(PermissionConstants.MARKETING)
    public void addFragmentMarketing() {
        mMarketingFragment = (ShareCouponFragment) addFragment(R.id.main_button_marketing, ShareCouponFragment.class);
    }

    private BaseFragment addFragment(int id, Class clazz) {
        View view = findViewById(id);
        if (view != null) {
            BaseFragment baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag("fragment_" + id);
            if (baseFragment == null) {
                try {
                    baseFragment = (BaseFragment) clazz.newInstance();
                } catch (Exception e) {
                    Logger.e("init fragment failed:" + e.getLocalizedMessage());
                    return null;
                }
            }
            mFragmentList.add(baseFragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, baseFragment, "fragment_" + id);
            ft.hide(baseFragment);
            ft.commit();

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
        updateUnreadMsgLabel();
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mSysNoticeNotifySubscription, mGetUserIsBindWXSubscription);
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

    EMMessageListener mMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            /*// 提示新消息
            for (EMMessage message : messages) {
                DemoHelper.getInstance().getNotifier().onNewMsg(message);
            }*/
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUnreadMsgLabel();
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadMsgLabel() {
        int count = getUnreadMsgCountTotal();
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
