package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EasyUtils;
import com.xmd.technician.Adapter.ChatListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.CheckedCoupon;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.DefaultEmojiconDatas;
import com.xmd.technician.chat.Emojicon;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.chat.chatview.EMessageListItemClickListener;
import com.xmd.technician.common.CommonMsgOnClickInterface;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmojiconMenu;
import com.xmd.technician.widget.RewardConfirmDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class ChatActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    protected static final int REQUEST_CODE_LOCAL = 1;

    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.et_sendmessage) EditText mSendMsgEd;
    @Bind(R.id.list_view) RecyclerView mMsgListView;
    @Bind(R.id.btn_face) View mFaceBtn;
    @Bind(R.id.emojicon_menu_container) EmojiconMenu mEmojiconMenuContainer;
    @Bind(R.id.common_msg_layout)LinearLayout mCommonMsgLayout;
    @Bind(R.id.btn_common_msg)View mCommonBtn;
    @Bind(R.id.round_indicator_left) ImageView indicatorLeft;
    @Bind(R.id.round_indicator_right)ImageView indicatorRight;
    ViewPager mCommentMsgView;

    private String mToChatUsername;
    private int mChatType = ChatConstant.CHATTYPE_SINGLE;
    private InputMethodManager mInputManager;
    private ChatListAdapter mChatAdapter;
    private boolean mIsMessageListInited;
    private boolean mIsLoading;
    private boolean mHaveMoreData = true;
    private int mPageSize = 20;
    private EMConversation mConversation;

    private Subscription mManagerOrderSubscription;
    private Subscription mGetRedpacklistSubscription;
    private Subscription mSendMessageSubscription;
    private List<CouponInfo> mCouponList = new ArrayList<>();
    private String mTechCode;
    private ArrayList<Fragment> fragmentsViewPagerList;
    private CommonMsgFragmentOne fragmentOne;
    private CommonMsgFragmentTwo fragmentTwo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_chat_primary_menu);

        ButterKnife.bind(this);

        mToChatUsername = getIntent().getExtras().getString(ChatConstant.EMCHAT_ID);

        UserUtils.setUserNick(mToChatUsername, mAppTitle);

        setBackVisible(true);

        mInputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mRefreshLayout.setColorSchemeResources(R.color.colorMain);
        mRefreshLayout.setOnRefreshListener(this);

        initEmojicon();

        onConversationInit();
        initChatList();

        mGetRedpacklistSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                redpackResult -> getRedpackListResult(redpackResult));

        mManagerOrderSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                result -> managerOrderResult(result));
        mSendMessageSubscription = RxBus.getInstance().toObservable(CheckedCoupon.class).subscribe(
                  checkedCoupon -> handlerCheckedCoupon(checkedCoupon)
        );

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(ChatConstant.EMCHAT_ID);
        if (mToChatUsername.equals(username)) {
            //刷新ui
            if(mIsMessageListInited) {
                mChatAdapter.refreshSelectLast();
            }
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsMessageListInited) {
           mChatAdapter.refreshList();
        }
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
        if(mGetRedpacklistSubscription!=null){
            RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mSendMessageSubscription){
            RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription, mManagerOrderSubscription,mSendMessageSubscription);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        if(!mIsLoading && mHaveMoreData){
            List<EMMessage> messages;
            try {
                if (mChatType == ChatConstant.CHATTYPE_SINGLE) {
                    messages = mConversation.loadMoreMsgFromDB(mChatAdapter.getItem(0).getMsgId(),
                            mPageSize);
                } else {
                    messages = mConversation.loadMoreMsgFromDB(mChatAdapter.getItem(0).getMsgId(),
                            mPageSize);
                }
            }catch (Exception e){
                mRefreshLayout.setRefreshing(false);
                return;
            }

            if(messages.size() > 0){
                mChatAdapter.refreshSeekTo(messages.size() - 1);
                if(messages.size() != mPageSize){
                    mHaveMoreData = false;
                }
            }else {
                mHaveMoreData = false;
            }

            mIsLoading = false;
        }else {
            makeShortToast(getString(R.string.no_more_messages));
        }

        mRefreshLayout.setRefreshing(false);
    }

    private void initEmojicon(){
        mEmojiconMenuContainer.setEmojiconMenuListener(new EmojiconMenu.EmojiconMenuListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                int index = mSendMsgEd.getSelectionStart();
                Editable edit = mSendMsgEd.getEditableText();
                edit.insert(index, SmileUtils.getSmiledText(ChatActivity.this,emojicon.getEmojiText()));
            }
        });
        mEmojiconMenuContainer.init(Arrays.asList(DefaultEmojiconDatas.getData()));
    }

    private void onConversationInit(){
        // 获取当前conversation对象

        mConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, CommonUtils.getConversationType(mChatType), true);
        // 把此会话的未读数置为0
        mConversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }

    }

    private void initChatList(){
        mChatAdapter = new ChatListAdapter(this,mMsgListView,mToChatUsername,mChatType);
        mMsgListView.setHasFixedSize(true);
        mMsgListView.setLayoutManager(new LinearLayoutManager(this));
        mMsgListView.setAdapter(mChatAdapter);
        mMsgListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                hideExtendMenuContainer();
                return false;
            }
        });
        mIsMessageListInited = true;
        mChatAdapter.refreshSelectLast();
        setListItemClickListener();
    }

    private void setListItemClickListener(){
        mChatAdapter.setListItemClickListener(new EMessageListItemClickListener() {
            @Override
            public void onResendClick(EMMessage message) {
                new RewardConfirmDialog(ChatActivity.this, getString(R.string.resend), getString(R.string.confirm_resend)) {
                    @Override
                    public void onConfirmClick() {
                        super.onConfirmClick();
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, (Runnable) () -> resendMessage(message));
                    }
                }.show();
            }
        });
    }

    private void getRedpackListResult(CouponListResult result){
        if(result.statusCode == 200){
            mTechCode = result.respData.techCode;
            if(result.respData.coupons != null){
                mCouponList.clear();
                for(CouponInfo info :result.respData.coupons){
                    mCouponList.add(info);
                }
            }
        }
    }

    private void managerOrderResult(OrderManageResult result){
        String replyMessage = mChatAdapter.getOrderReplyMessage(result.orderId);
        if(!TextUtils.isEmpty(replyMessage)){
            sendOrderMessage(replyMessage, result.orderId);
            mChatAdapter.refreshSelectLast();
        }
    }

    @OnClick(R.id.btn_send)
    public void onSendBtnClicked(){
        String s = mSendMsgEd.getText().toString();
        mSendMsgEd.setText("");
        if(TextUtils.isEmpty(s)){
            makeShortToast(getString(R.string.messages_cannot_be_empty));
        }else{
            sendTextMessage(s);
        }
    }

    @OnClick(R.id.btn_photo)
    public void onToggleExtendClicked(){
        hideKeyboard();
        hideExtendMenuContainer();
        Util.selectPicFromLocal(this, REQUEST_CODE_LOCAL);
    }

    @OnClick(R.id.et_sendmessage)
    public void onEditTextClicked(){
        hideExtendMenuContainer();
    }

    @OnClick(R.id.btn_face)
    public void onToggleEmojiconClicked(View view){
        if(mCommonMsgLayout.getVisibility() ==View.VISIBLE){
            mCommonBtn.setSelected(false);
            mCommonMsgLayout.setVisibility(View.GONE);
        }
        if (mEmojiconMenuContainer.getVisibility() == View.VISIBLE) {
            view.setSelected(false);
            mEmojiconMenuContainer.setVisibility(View.GONE);
        } else {
            hideKeyboard();
            view.setSelected(true);
            mEmojiconMenuContainer.setVisibility(View.VISIBLE);

        }
    }

    @OnClick(R.id.btn_common_msg)
    public void showCommonMessage(View view){
        hideKeyboard();
        if(mCommonMsgLayout.getVisibility()==View.VISIBLE){
            mCommonMsgLayout.setVisibility(View.GONE);
            view.setSelected(false);
        }else{
            initCommentViewPager( view);
            mCommonMsgLayout.setVisibility(View.VISIBLE);
            view.setSelected(true);
            mFaceBtn.setSelected(false);
            mEmojiconMenuContainer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_common_reward)
    public void requestReward(){
        hideKeyboard();
        hideExtendMenuContainer();
        new RewardConfirmDialog(this, getString(R.string.beg_reward), getString(R.string.send_request_user_reward)) {
            @Override
            public void onConfirmClick() {
                sendBegRewardMessage("<i></i>万水千山总是情<br/>打赏两个行不行~");
                super.onConfirmClick();
            }
        }.show();
    }
    @OnClick(R.id.btn_common_coupon)
    public void showCouponInfo(View view) {
        hideKeyboard();
        hideExtendMenuContainer();
        if (mCouponList.isEmpty()) {
            makeShortToast(getString(R.string.no_coupon));
            return;
        }
        view.setSelected(true);
        AvailableCouponListActivity.setData(mCouponList);
        Intent intent = new Intent(ChatActivity.this,AvailableCouponListActivity.class);
        startActivity(intent);
        view.setSelected(false);
    }
    private void   handlerCheckedCoupon(CheckedCoupon result){
            if(!("paid").equals(result.couponType)){
                sendCouponMessage(String.format("<i>%s</i><span>%d</span>元<b>%s</b>", result.useTypeName, result.actValue, result.couponPeriod), result.actId);
            }else{
                sendPaidCouponMessage(String.format("<i>求点钟</i>立减<span>%1$d</span>元<b>%2$s</b>", result.actValue, result.couponPeriod), result.actId);
            }
    }
    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                mInputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏整个扩展按钮栏(包括表情栏)
     */
    public void hideExtendMenuContainer() {
        mFaceBtn.setSelected(false);
        mEmojiconMenuContainer.setVisibility(View.GONE);
        if(mCommonMsgLayout.getVisibility()==View.VISIBLE) {
            mCommonMsgLayout.setVisibility(View.GONE);
            //    viewDiv.setVisibility(View.GONE);
            mCommonBtn.setSelected(false);
        }
    }

    //发送消息方法
    //==========================================================================

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                makeShortToast(getString(R.string.cant_find_pictures));
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                makeShortToast(getString(R.string.cant_find_pictures));
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    protected void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, mToChatUsername);
        sendMessage(message);
    }

    protected void sendBegRewardMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "begReward");
        sendMessage(message);
    }

    private void sendCouponMessage(String content, String actId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "ordinaryCoupon");
        message.setAttribute(ChatConstant.KEY_ACT_ID, actId);
        message.setAttribute(ChatConstant.KEY_TECH_CODE, mTechCode);
        sendMessage(message);
    }

    private void sendOrderMessage(String content, String orderId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "order");
        message.setAttribute(ChatConstant.KEY_ORDER_ID, orderId);
        sendMessage(message);
    }

    private void sendPaidCouponMessage(String content, String actId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "paidCoupon");
        message.setAttribute(ChatConstant.KEY_ACT_ID, actId);
        message.setAttribute(ChatConstant.KEY_TECH_CODE, mTechCode);
        sendMessage(message);
    }

    protected void sendMessage(EMMessage message){
//        if(chatFragmentListener != null){
//            //设置扩展属性
//            chatFragmentListener.onSetMessageAttributes(message);
//        }
        // 如果是群聊，设置chattype,默认是单聊
        if (mChatType == ChatConstant.CHATTYPE_GROUP){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }else if(mChatType == ChatConstant.CHATTYPE_CHATROOM){
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }

        message.setAttribute(ChatConstant.KEY_NAME, SharedPreferenceHelper.getUserName());
        message.setAttribute(ChatConstant.KEY_HEADER, SharedPreferenceHelper.getUserAvatar());
        message.setAttribute(ChatConstant.KEY_TECH_ID, SharedPreferenceHelper.getUserId());
        message.setAttribute(ChatConstant.KEY_SERIAL_NO, SharedPreferenceHelper.getSerialNo());
        message.setAttribute(ChatConstant.KEY_TIME, String.valueOf(System.currentTimeMillis()));
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        //刷新ui
        if(mIsMessageListInited) {
            mChatAdapter.refreshSelectLast();
        }
    }

    public void resendMessage(EMMessage message){
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        mChatAdapter.refreshList();
    }

    EMMessageListener mEMMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }

                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(mToChatUsername)) {
                    mChatAdapter.refreshSelectLast();
                    // 声音和震动提示有新消息
                    //EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    // 如果消息不是和当前聊天ID的消息
                    //EaseUI.getInstance().getNotifier().onNewMsg(message);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            if(mIsMessageListInited) {
                mChatAdapter.refreshList();
            }
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            if(mIsMessageListInited) {
                mChatAdapter.refreshList();
            }
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            if(mIsMessageListInited) {
                mChatAdapter.refreshList();
            }
        }
    };

    private void initCommentViewPager(View view){
        indicatorLeft.setEnabled(true);
        indicatorRight.setEnabled(false);

        fragmentOne = CommonMsgFragmentOne.getInstance(new CommonMsgOnClickInterface() {
            @Override
            public void onMsgClickListener(String s) {
                view.setSelected(false);
                sendTextMessage(s);
                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        if(mCommonMsgLayout.getVisibility() == View.VISIBLE){
                            mCommonMsgLayout.setVisibility(View.GONE);
                        }
                    }
                }, 50);
            }
        });
        fragmentTwo = CommonMsgFragmentTwo.getInstance(new CommonMsgOnClickInterface() {
            @Override
            public void onMsgClickListener(String s) {
                view.setSelected(false);
                sendTextMessage(s);
                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        if(mCommonMsgLayout.getVisibility() == View.VISIBLE){
                            mCommonMsgLayout.setVisibility(View.GONE);
                        }
                    }
                }, 50);
            }
        });
        fragmentsViewPagerList = new ArrayList<>();
        fragmentsViewPagerList.add(fragmentOne);
        fragmentsViewPagerList.add(fragmentTwo);
        mCommentMsgView = (ViewPager) findViewById(R.id.comment_msg_view);
        mCommentMsgView.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return fragmentsViewPagerList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentsViewPagerList.size();
            }
        });
        mCommentMsgView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    indicatorLeft.setEnabled(true);
                    indicatorRight.setEnabled(false);
                }else{
                    indicatorLeft.setEnabled(false);
                    indicatorRight.setEnabled(true);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



}
