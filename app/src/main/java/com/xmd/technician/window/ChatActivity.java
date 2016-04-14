package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.Adapter.ChatListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.DefaultEmojiconDatas;
import com.xmd.technician.chat.Emojicon;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Util;
import com.xmd.technician.widget.ConfirmDialog;
import com.xmd.technician.widget.RewardConfirmDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    protected static final int REQUEST_CODE_LOCAL = 1;

    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.et_sendmessage) EditText mSendMsgEd;
    @Bind(R.id.list_view) RecyclerView mMsgListView;
    //@Bind(R.id.extend_menu_container) FrameLayout mChatExtendMenuContainer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_chat_primary_menu);

        ButterKnife.bind(this);

        mToChatUsername = getIntent().getExtras().getString(ChatConstant.EXTRA_USER_ID);

        //setTitle(UserProfileProvider.getInstance().getChatUserInfo(mToChatUsername).getNick());
        setBackVisible(true);

        mInputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mRefreshLayout.setColorSchemeResources(R.color.colorMain);
        mRefreshLayout.setOnRefreshListener(this);

        initEmojicon();

        onConversationInit();
        initChatList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsMessageListInited)
            mChatAdapter.refreshList();
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
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
    public void onToggleEmojiconClicked(){
        toggleEmojicon();
    }

    @OnClick(R.id.btn_common_msg)
    public void showCommonMessage(){
        hideKeyboard();
        hideExtendMenuContainer();
        showPopupWindow(findViewById(R.id.menu_layout));
    }

    @OnClick(R.id.btn_common_reward)
    public void requestReward(){
        hideKeyboard();
        hideExtendMenuContainer();
        new RewardConfirmDialog(this, getString(R.string.beg_reward), getString(R.string.send_request_user_reward)) {
            @Override
            public void onConfirmClick() {
                sendBegRewardMessage(getString(R.string.request_user_reward));
                super.onConfirmClick();
            }
        }.show();
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
        //chatExtendMenu.setVisibility(View.GONE);
        mEmojiconMenuContainer.setVisibility(View.GONE);
        //mChatExtendMenuContainer.setVisibility(View.GONE);
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
        message.setAttribute("msgType", "begReward");
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mToChatUsername);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, mToChatUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, mToChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, mToChatUsername);
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

        message.setAttribute("name", SharedPreferenceHelper.getUserName());
        message.setAttribute("header", SharedPreferenceHelper.getUserAvatar());
        message.setAttribute("time", String.valueOf(new Date()));
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

    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.list_pop_window, null);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.coupon_bg));

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int h = popupWindow.getHeight();
        // 设置好参数之后再show
//        popupWindow.showAsDropDown(view, 100, 100);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,location[0], location[1] - 96);

    }
}
