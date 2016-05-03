package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.reactnative.ReactManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements BaseFragment.IFragmentCallback{
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

        mFragmentList.add(new ChatFragment());
        mFragmentList.add(new OrderFragment());
        mFragmentList.add(new CouponFragment());
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
                if(fragment instanceof ChatFragment){
                    ((ChatFragment)fragment).refreshMessage();
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
        //Marketing Fragment
        if (mCurrentTabIndex == 2 && ReactManager.sReactInstanceManager != null) {
            /*
             * 这使得JavaScript代码可以控制当用户按下返回键的时候作何处理（譬如控制导航的切换等等）。
             * 如果JavaScript端不处理相应的事件，你的invokeDefaultOnBackPressed 方法会被调用。默认情况下，这会直接结束你的Activity。
             */
            ReactManager.sReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 改动一下开发者菜单。默认情况下，开发者菜单可以通过摇晃设备来触发，不过这对模拟器不是很有用。所以我们让它在按下Menu键的时候也可以被显示：
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mCurrentTabIndex == 2 && keyCode == KeyEvent.KEYCODE_MENU && ReactManager.sReactInstanceManager != null) {
            ReactManager.sReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void handleBackPressed() {
        super.onBackPressed();
    }
}
