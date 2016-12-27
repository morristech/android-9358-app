package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.IsBindResult;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.gson.SystemNoticeResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MainActivity extends BaseFragmentActivity implements BaseFragment.IFragmentCallback {
    private static final int TAB_INDEX_PERSONAL = 0;
    private static final int TAB_INDEX_MESSAGE = 1;
    private static final int TAB_INDEX_CONTACTS = 2;
    private static final int TAB_INDEX_MARKETING = 3;

    public static final int REQUEST_CODE_JOIN_CLUB = 1;
    public static final int REQUEST_CODE_EDIT_TECH_INFO = 2;

    private List<BaseFragment> mFragmentList = new LinkedList<BaseFragment>();
    private List<View> mBottomBarButtonList = new LinkedList<View>();

    private int mCurrentTabIndex = 1;
    private Subscription mSysNoticeNotifySubscription;
    private Subscription mGetUserIsBindWXSubscription;

    @Bind(R.id.main_unread_message)
    TextView mUnreadMsgLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBottomBarButtonList.add(findViewById(R.id.main_button_personal));
        mBottomBarButtonList.add(findViewById(R.id.main_button_message));
        mBottomBarButtonList.add(findViewById(R.id.main_button_contacts));
        mBottomBarButtonList.add(findViewById(R.id.main_button_marketing));

        mFragmentList.add(new MainFragment());
        mFragmentList.add(new ChatFragment());
        mFragmentList.add(new ContactsFragment());
        mFragmentList.add(new CouponFragment());

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        UserProfileProvider.getInstance().initContactList();

        mSysNoticeNotifySubscription = RxBus.getInstance().toObservable(SystemNoticeResult.class).subscribe(
                result -> updateUnreadMsgLabel());
        mGetUserIsBindWXSubscription = RxBus.getInstance().toObservable(IsBindResult.class).subscribe(
                result -> handlerIsBindResult(result)
        );
        switchFragment(0);

        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND,
                () -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID));
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND,
                () -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE));

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

    @OnClick(R.id.main_button_personal)
    public void gotoPersonFragment() {
        switchFragment(TAB_INDEX_PERSONAL);
    }

    @OnClick(R.id.main_button_message)
    public void gotoMessageFragment() {
        switchFragment(TAB_INDEX_MESSAGE);
    }

    @OnClick(R.id.main_button_contacts)
    public void gotoConversionFragment() {
        switchFragment(TAB_INDEX_CONTACTS);
    }

    @OnClick(R.id.main_button_marketing)
    public void gotoMarketingFragment() {
        switchFragment(TAB_INDEX_MARKETING);
    }

    public void switchFragment(int index) {
        if (mCurrentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();

            trx.hide(mFragmentList.get(mCurrentTabIndex));

            if (!mFragmentList.get(index).isAdded()) {
                trx.add(R.id.fragment_container, mFragmentList.get(index));
            }
            trx.show(mFragmentList.get(index)).commit();
        }
        mBottomBarButtonList.get(mCurrentTabIndex).setSelected(false);
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
        if (requestCode == REQUEST_CODE_JOIN_CLUB) {
            if (resultCode == Activity.RESULT_OK) {
                //申请成功，需要刷新技师状态
                if (mFragmentList.size() > TAB_INDEX_PERSONAL && mFragmentList.get(TAB_INDEX_PERSONAL) != null) {
                    MainFragment mainFragment = (MainFragment) mFragmentList.get(TAB_INDEX_PERSONAL);
                    mainFragment.doSendJoinClubRequestSuccess();
                }
            }
        } else if (requestCode == REQUEST_CODE_EDIT_TECH_INFO) {
            if (resultCode == Activity.RESULT_OK) {
                //更新技师信息，需要刷新界面
                if (mFragmentList.size() > TAB_INDEX_PERSONAL && mFragmentList.get(TAB_INDEX_PERSONAL) != null) {
                    MainFragment mainFragment = (MainFragment) mFragmentList.get(TAB_INDEX_PERSONAL);
                    mainFragment.doUpdateTechInfoSuccess();
                }
            }
        }
    }
}
