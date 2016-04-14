package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final int TAB_INDEX_MESSAGE = 0;
    private static final int TAB_INDEX_ORDER = 1;
    private static final int TAB_INDEX_MARKETING = 2;
    private static final int TAB_INDEX_PERSONAL = 3;

    private List<BaseFragment> mFragmentList = new LinkedList<BaseFragment>();
    private List<View> mBottomBarButtonList = new LinkedList<View>();

    private int mCurrentTabIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBottomBarButtonList.add(findViewById(R.id.main_button_message));
        mBottomBarButtonList.add(findViewById(R.id.main_button_order));
        mBottomBarButtonList.add(findViewById(R.id.main_button_marketing));
        mBottomBarButtonList.add(findViewById(R.id.main_button_personal));

        mFragmentList.add(new MessageFragment());
        mFragmentList.add(new OrderFragment());
        mFragmentList.add(new MessageFragment());
        mFragmentList.add(new PersonalFragment());

        switchFragment(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        Map<String, String> params = new HashMap<>();
        /*params.put(RequestConstant.KEY_PAGE_NUMBER,"1");
        params.put(RequestConstant.KEY_PAGE_SIZE,"10");
        params.put(RequestConstant.KEY_SORT_TYPE,"createdAt");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMENT_LIST);*/

        /*params.put(RequestConstant.KEY_MOBILE,"13265684479");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE,params);*/

        /*params.put(RequestConstant.KEY_INVITE_CODE,"787714");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SUBMIT_INVITE_CODE,params);*/

        //不正常
        /*params.put(RequestConstant.KEY_MOBILE,"13265684478");
        params.put(RequestConstant.KEY_PASSWORD,"123456");
        params.put(RequestConstant.KEY_ICODE,"471857");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REGISTER, params);*/

        /*MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_WORK_TIME);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
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
            if(mCurrentTabIndex == TAB_INDEX_MESSAGE ) {
                BaseFragment fragment = mFragmentList.get(mCurrentTabIndex);
                if(fragment instanceof MessageFragment){
                    ((MessageFragment)fragment).refreshMessage();
                }
            }
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 进入聊天页面
        Intent intent = new Intent(this, ChatActivity.class);
        // it's single chat
        intent.putExtra(ChatConstant.EXTRA_USER_ID, "d7ab1dc3b20882dd88b7cc7f18f894d6");
        startActivity(intent);
    }
}
