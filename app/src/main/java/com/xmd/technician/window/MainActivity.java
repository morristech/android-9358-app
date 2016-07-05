package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
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

public class MainActivity extends BaseFragmentActivity implements BaseFragment.IFragmentCallback{
    private static final int TAB_INDEX_MESSAGE = 0;
    private static final int TAB_INDEX_ORDER = 1;
    private static final int TAB_INDEX_MARKETING = 2;
    private static final int TAB_INDEX_PERSONAL = 3;

    private List<BaseFragment> mFragmentList = new LinkedList<BaseFragment>();
    private List<View> mBottomBarButtonList = new LinkedList<View>();

    private int mCurrentTabIndex = 1;

    private Subscription mSysNoticeNotifySubscription;

    @Bind(R.id.main_unread_message) TextView mUnreadMsgLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBottomBarButtonList.add(findViewById(R.id.main_button_message));
        mBottomBarButtonList.add(findViewById(R.id.main_button_order));
        mBottomBarButtonList.add(findViewById(R.id.main_button_marketing));
        mBottomBarButtonList.add(findViewById(R.id.main_button_personal));

        mFragmentList.add(new ChatFragment());
        mFragmentList.add(new OrderFragment());
        mFragmentList.add(new CouponFragment());
        mFragmentList.add(new PersonalFragment());

        mSysNoticeNotifySubscription = RxBus.getInstance().toObservable(SystemNoticeResult.class).subscribe(
                result -> updateUnreadMsgLabel());
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
        RxBus.getInstance().unsubscribe(mSysNoticeNotifySubscription);
    }

    @OnClick(R.id.main_button_message)
    public void gotoMessageFragment(){
        switchFragment(TAB_INDEX_MESSAGE);
    }

    @OnClick(R.id.main_button_order)
    public void gotoOrderFragment(){
        switchFragment(TAB_INDEX_ORDER);
    }

    @OnClick(R.id.main_button_marketing)
    public void gotoMarketingFragment(){
        switchFragment(TAB_INDEX_MARKETING);
    }

    @OnClick(R.id.main_button_personal)
    public void gotoPersonFragment(){
        switchFragment(TAB_INDEX_PERSONAL);
    }

    private void switchFragment(int index){
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
        for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
            if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal-chatroomUnreadMsgCount;
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadMsgLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            mUnreadMsgLabel.setText(String.valueOf(count));
            mUnreadMsgLabel.setVisibility(View.VISIBLE);
        } else {
            mUnreadMsgLabel.setVisibility(View.INVISIBLE);
        }
    }
}
