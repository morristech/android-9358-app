package com.xmd.manager.window;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.adapter.ChatListAdapter;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.CouponSelectResult;
import com.xmd.manager.beans.EmchatMsgResult;
import com.xmd.manager.chat.CommonUtils;
import com.xmd.manager.chat.DefaultEmojiconDatas;
import com.xmd.manager.chat.EmchatConstant;
import com.xmd.manager.chat.EmchatUserHelper;
import com.xmd.manager.chat.Emojicon;
import com.xmd.manager.chat.EmojiconMenu;
import com.xmd.manager.chat.SmileUtils;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.OrderManageResult;
import com.xmd.manager.service.response.UserGetCouponResult;
import com.xmd.manager.widget.ClearableEditText;

import java.io.File;
import java.util.Arrays;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class ChatActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int MSG_COUNT = 20;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.et_sendmessage)
    ClearableEditText mSendMsgEd;
    @BindView(R.id.list_view)
    RecyclerView mMsgListView;
    @BindView(R.id.btn_face)
    View mFaceBtn;
    @BindView(R.id.btn_common_coupon)
    ImageButton commonCoupon;
    @BindView(R.id.emojicon_menu_container)
    EmojiconMenu mEmojiconMenuContainer;

    private String mToChatUsername, userType;
    private int mChatType = EmchatConstant.CHATTYPE_SINGLE;
    private InputMethodManager mInputManager;
    private ChatListAdapter mChatAdapter;
    private boolean mIsLoading;
    private boolean mHaveMoreData = true;
    private EMConversation mConversation;
    private Subscription mManagerOrderSubscription;
    private Subscription mGetSelectedCouponSubscription;
    private Subscription mGetEmchatMsgCountSubscription;
    private Subscription mClubUserGetCouponSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("");
        mToChatUsername = getIntent().getExtras().getString(EmchatConstant.EMCHAT_ID);
        String nickname = getIntent().getExtras().getString(EmchatConstant.EMCHAT_NICKNAME);
        userType = getIntent().getExtras().getString(EmchatConstant.MESSAGE_CHAT_USER_TYPE);
        if (Utils.isNotEmpty(userType) && userType.equals(EmchatConstant.MESSAGE_TECH_TYPE)) {
            commonCoupon.setVisibility(View.GONE);
        } else {
            commonCoupon.setVisibility(View.VISIBLE);
        }
        boolean isNicknameGot = Utils.isNotEmpty(nickname);
        if (isNicknameGot) {
            setTitle(nickname);
        } else {
            EmchatUserHelper.setUserAvatarAndNick(this, mToChatUsername, null, mAppTitle);
        }

        mInputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mRefreshLayout.setColorSchemeResources(R.color.primary_color);
        mRefreshLayout.setOnRefreshListener(this);

        initEmojicon();
        loadConversation();
        initChatList();

        mManagerOrderSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                result -> managerOrderResult(result));

        mGetSelectedCouponSubscription = RxBus.getInstance().toObservable(CouponSelectResult.class).subscribe(
                result -> {
                    for (CouponInfo couponInfo : result.mCouponList) {
                        sendCouponMessage(String.format("<i>%s</i><span>%d</span>元<b>%s</b>",
                                couponInfo.useTypeName, couponInfo.actValue, couponInfo.couponPeriod), couponInfo.actId);
                    }
                }
        );

        handleEmchatObject(getIntent().getExtras().getParcelable(EmchatConstant.EMCHAT_OBJECT));

        mGetEmchatMsgCountSubscription = RxBus.getInstance().toObservable(EmchatMsgResult.class).subscribe(
                emchatMsgResult -> handleNewMsg(emchatMsgResult.list)
        );
        mClubUserGetCouponSubscription = RxBus.getInstance().toObservable(UserGetCouponResult.class).subscribe(
                couponResult -> handleUserGetCoupon(couponResult)
        );
    }

    private void handleUserGetCoupon(UserGetCouponResult couponResult) {
        EMMessage message = couponResult.mMessage;
        if (couponResult.statusCode == 200) {
            if (Utils.isNotEmpty(couponResult.respData.userActId)) {
                message.setAttribute(EmchatConstant.KEY_COUPON_ACT_ID, couponResult.respData.userActId);
            }
        }
        sendMessage(message);
    }

    private void handleEmchatObject(Object object) {
        if (object != null) {
            if (object instanceof CouponSelectResult) {
                CouponSelectResult result = (CouponSelectResult) object;
                for (CouponInfo couponInfo : result.mCouponList) {
                    sendCouponMessage(String.format("<i>%s</i><span>%d</span>元<b>%s</b>",
                            couponInfo.useTypeName, couponInfo.actValue, couponInfo.couponPeriod), couponInfo.actId);
                }
            }
        }
    }

    private void handleNewMsg(List<EMMessage> list) {
        for (EMMessage message : list) {
            String username;
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
    protected void onResume() {
        super.onResume();
        mChatAdapter.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetSelectedCouponSubscription, mManagerOrderSubscription, mGetEmchatMsgCountSubscription, mClubUserGetCouponSubscription);
    }

    private void initEmojicon() {
        mEmojiconMenuContainer.setEmojiconMenuListener(new EmojiconMenu.EmojiconMenuListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                int index = mSendMsgEd.getSelectionStart();
                Editable edit = mSendMsgEd.getEditableText();
                edit.insert(index, SmileUtils.getSmiledText(ChatActivity.this, emojicon.getEmojiText()));
            }
        });
        mEmojiconMenuContainer.init(Arrays.asList(DefaultEmojiconDatas.getData()));
    }

    private void loadConversation() {
        // 获取当前conversation对象
        mConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, CommonUtils.getConversationType(mChatType), true);
        // 把此会话的未读数置为0
        mConversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mConversation.getAllMsgCount() && msgCount < MSG_COUNT) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, MSG_COUNT - msgCount);
        }
    }

    private void initChatList() {
        mChatAdapter = new ChatListAdapter(this, mToChatUsername, mChatType);
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
        mChatAdapter.refreshSelectLast();
    }


    @Override
    public void onRefresh() {
        if (!mIsLoading && mHaveMoreData) {
            List<EMMessage> messages;
            try {
                messages = mConversation.loadMoreMsgFromDB(mChatAdapter.getItem(0).getMsgId(), MSG_COUNT);
            } catch (Exception e) {
                mRefreshLayout.setRefreshing(false);
                return;
            }
            if (messages.size() > 0) {
                mChatAdapter.refreshSeekTo(messages.size() - 1);
                if (messages.size() != MSG_COUNT) {
                    mHaveMoreData = false;
                }
            } else {
                mHaveMoreData = false;
            }
            mIsLoading = false;
        } else {
            makeShortToast(getString(R.string.no_more_messages));
        }
        mRefreshLayout.setRefreshing(false);
    }


    private void managerOrderResult(OrderManageResult result) {
        String replyMessage = mChatAdapter.getOrderReplyMessage(result.orderId);
        if (!TextUtils.isEmpty(replyMessage)) {
            sendOrderMessage(replyMessage, result.orderId);
            mChatAdapter.refreshSelectLast();
        }
    }

    @OnClick(R.id.btn_send)
    public void onSendBtnClicked() {
        String s = mSendMsgEd.getText().toString().trim();
        mSendMsgEd.setText("");
        if (TextUtils.isEmpty(s)) {
            makeShortToast(getString(R.string.messages_cannot_be_empty));
        } else {
            sendTextMessage(s);
        }
        hideKeyboard();
    }

    @OnClick(R.id.et_sendmessage)
    public void onEditTextClicked() {
        hideExtendMenuContainer();
    }

    @OnClick(R.id.btn_face)
    public void onToggleEmojiconClicked(View view) {
        if (mEmojiconMenuContainer.getVisibility() == View.VISIBLE) {
            view.setSelected(false);
            mEmojiconMenuContainer.setVisibility(View.GONE);
        } else {
            hideKeyboard();
            view.setSelected(true);
            mEmojiconMenuContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_photo)
    public void onToggleExtendClicked() {
        hideKeyboard();
        hideExtendMenuContainer();
        Utils.selectPicFromLocal(this, Constant.REQUEST_CODE_FOR_LOCAL_SELECT_PICTURE);
    }

    @OnClick(R.id.btn_common_coupon)
    public void showCouponInfo(View view) {
        hideKeyboard();
        hideExtendMenuContainer();
        startActivity(new Intent(this, DeliveryCouponActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_FOR_LOCAL_SELECT_PICTURE) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            }
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
    }

    //发送消息方法

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
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

    private void sendCouponMessage(String content, String actId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setAttribute(EmchatConstant.KEY_CUSTOM_TYPE, "ordinaryCoupon");
        message.setAttribute(EmchatConstant.KEY_ACT_ID, actId);
        message.setAttribute(EmchatConstant.KEY_TECH_CODE, SharedPreferenceHelper.getUserInviteCode());
        CommonUtils.userGetCoupon(actId, "manager", mToChatUsername, message);
    }

    private void sendOrderMessage(String content, String orderId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setAttribute(EmchatConstant.KEY_CUSTOM_TYPE, "order");
        message.setAttribute(EmchatConstant.KEY_ORDER_ID, orderId);
        sendMessage(message);
    }

    protected void sendMessage(EMMessage message) {
        // message.setAttribute(EmchatConstant.KEY_NAME, ClubData.getInstance().getClubInfo().name);
        message.setAttribute(EmchatConstant.KEY_NAME, SharedPreferenceHelper.getUserName());
        message.setAttribute(EmchatConstant.KEY_HEADER, SharedPreferenceHelper.getUserAvatar());
        message.setAttribute(EmchatConstant.KEY_TIME, String.valueOf(System.currentTimeMillis()));
        message.setAttribute(EmchatConstant.EMCHAT_CLUB_AVATAR, ClubData.getInstance().getClubInfo().image);
        message.setAttribute(EmchatConstant.KEY_CLUB_ID, ClubData.getInstance().getClubInfo().clubId);
        message.setAttribute(EmchatConstant.KEY_CLUB_NAME, ClubData.getInstance().getClubInfo().name);

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.v("success");
                mChatAdapter.refreshSelectLast();
            }

            @Override
            public void onError(int i, String s) {
                Logger.v("onError:" + i + ", " + s);
                if (i == EmchatConstant.ERROR_CODE_USER_HAS_NOT_LOGIN) {
                    EmchatUserHelper.login(() -> resendMessage(message));
                } else {
                    mChatAdapter.refreshSelectLast();
                }
            }

            @Override
            public void onProgress(int i, String s) {
                Logger.v("onProgress:" + i + "," + s);
            }
        });
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        mChatAdapter.refreshSelectLast();
    }

    public void resendMessage(EMMessage message) {
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        mChatAdapter.refresh();
    }


}
