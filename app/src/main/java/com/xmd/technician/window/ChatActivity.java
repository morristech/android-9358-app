package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.EasyUtils;
import com.xmd.technician.Adapter.ChatListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.DefaultEmojiconDatas;
import com.xmd.technician.chat.Emojicon;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ArrayBottomPopupWindow;
import com.xmd.technician.widget.RewardConfirmDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Bind(R.id.emojicon_menu_container) GridView mEmojiconMenuContainer;

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
    private List<CouponInfo> mPaidCouponList = new ArrayList<>();
    private List<CouponInfo> mCouponList = new ArrayList<>();
    private String mTechCode;

    private ArrayBottomPopupWindow mCommonMessageWindow;
    private ArrayBottomPopupWindow mPaidCouponWindow;
    private ArrayBottomPopupWindow mCouponWindow;

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

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);

        EMClient.getInstance().login(SharedPreferenceHelper.getEmchatId(), SharedPreferenceHelper.getEMchatPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(ChatConstant.EMCHAT_ID);
        if (mToChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription, mManagerOrderSubscription);
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
        List<Map<String,Object>> list = new ArrayList<>();
        Emojicon[] emojicons = DefaultEmojiconDatas.getData();
        for(int i = 0;i < emojicons.length;i++){
            Map<String,Object> item = new HashMap<>();
            item.put("icon",emojicons[i].getIcon());
            item.put("item",emojicons[i]);
            list.add(item);
        }
        mEmojiconMenuContainer.setAdapter(new SimpleAdapter(this, list,android.R.layout.activity_list_item,new String[]{"icon"},new int[]{android.R.id.icon}));
        mEmojiconMenuContainer.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> item = (Map<String, Object>) parent.getAdapter().getItem(position);
            int index = mSendMsgEd.getSelectionStart();
            Editable edit = mSendMsgEd.getEditableText();
            edit.insert(index,SmileUtils.getSmiledText(ChatActivity.this,((Emojicon)item.get("item")).getEmojiText()));
        });
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
    }

    private void getRedpackListResult(CouponListResult result){
        if(result.statusCode == 200){
            mTechCode = result.respData.techCode;
            if(result.respData.coupons != null){
                mCouponList.clear();
                mPaidCouponList.clear();
                for(CouponInfo info : result.respData.coupons){
                    if(info.couponType.equals("paid")) {
                        mPaidCouponList.add(info);
                    }else {
                        mCouponList.add(info);
                    }
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
            makeShortToast("");
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
        hideExtendMenuContainer();
        view.setSelected(true);

        if(mCommonMessageWindow == null){
            mCommonMessageWindow = new ArrayBottomPopupWindow(view, null, ResourceUtils.getDimenInt(R.dimen.order_list_item_operation_section_width));
            mCommonMessageWindow.setDataSet(Arrays.asList(getResources().getStringArray(R.array.common_greeting_array)));
            mCommonMessageWindow.setItemClickListener((parent, view1, position, id) -> {
                sendTextMessage((String) parent.getAdapter().getItem(position));
            });
        }

        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                mCommonMessageWindow.showAsAboveLeft();
            }
        }, 50);
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

    @OnClick(R.id.btn_common_clock)
    public void showPaidCouponInfo(View view){
        hideKeyboard();
        hideExtendMenuContainer();
        if(mPaidCouponList.isEmpty()){
            makeShortToast(getString(R.string.no_paid_coupon));
            return;
        }
        view.setSelected(true);
        if(mPaidCouponWindow == null){
            mPaidCouponWindow = new ArrayBottomPopupWindow(view, null, ResourceUtils.getDimenInt(R.dimen.order_list_item_operation_item_width));
            mPaidCouponWindow.setDataSet(mPaidCouponList);
            mPaidCouponWindow.setItemClickListener((parent, view1, position, id) -> {
                CouponInfo info = (CouponInfo) parent.getAdapter().getItem(position);
                sendPaidCouponMessage(String.format("<i>求点钟</i>立减<span>%1$d</span>元<b>%2$s</b>", info.actValue, info.couponPeriod), info.actId);
            });
        }

        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                mPaidCouponWindow.showAsAboveCenter();
            }
        }, 50);
    }

    @OnClick(R.id.btn_common_coupon)
    public void showCouponInfo(View view){
        hideKeyboard();
        hideExtendMenuContainer();
        if(mCouponList.isEmpty()){
            makeShortToast(getString(R.string.no_coupon));
            return;
        }

        view.setSelected(true);
        if(mCouponWindow == null){
            mCouponWindow = new ArrayBottomPopupWindow(view, null, ResourceUtils.getDimenInt(R.dimen.order_list_item_operation_item_width));
            mCouponWindow.setDataSet(mCouponList);
            mCouponWindow.setItemClickListener((parent, view1, position, id) -> {
                CouponInfo info = (CouponInfo) parent.getAdapter().getItem(position);
                sendCouponMessage(String.format("<i>%s</i><span>%d</span>元<b>%s</b>", info.useTypeName, info.actValue, info.couponPeriod), info.actId);
            });
        }

        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                mCouponWindow.showAsAboveCenter();
            }
        }, 50);
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
    }

    private void toggleEmojicon() {
        if (mEmojiconMenuContainer.getVisibility() == View.VISIBLE) {
            mEmojiconMenuContainer.setVisibility(View.GONE);
        } else {
            hideKeyboard();
            mEmojiconMenuContainer.setVisibility(View.VISIBLE);
        }
        /*if (mChatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                public void run() {
                    mChatExtendMenuContainer.setVisibility(View.VISIBLE);
                    //chatExtendMenu.setVisibility(View.GONE);
                    mEmojiconMenuContainer.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            if (mEmojiconMenuContainer.getVisibility() == View.VISIBLE) {
                mChatExtendMenuContainer.setVisibility(View.GONE);
                mEmojiconMenuContainer.setVisibility(View.GONE);
            } else {
                //chatExtendMenu.setVisibility(View.GONE);
                mEmojiconMenuContainer.setVisibility(View.VISIBLE);
            }

        }*/
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
}
