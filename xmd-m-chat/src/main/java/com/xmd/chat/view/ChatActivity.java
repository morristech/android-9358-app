package com.xmd.chat.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crazyman.library.PermissionTool;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.ExCommonRecyclerViewAdapter;
import com.xmd.app.SpConstants;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.black.BlackListManager;
import com.xmd.black.event.InCustomerBlackListEvent;
import com.xmd.chat.BR;
import com.xmd.chat.ChatConstants;
import com.xmd.chat.ChatMenu;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.ChatSettingManager;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.R;
import com.xmd.chat.VoiceManager;
import com.xmd.chat.XmdChat;
import com.xmd.chat.databinding.ChatActivityBinding;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.chat.event.EventDeleteMessage;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventReplayDiceGame;
import com.xmd.chat.event.EventRevokeMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.xmdchat.contract.XmdChatActivityInterface;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmChatActivityPresent;
import com.xmd.chat.xmdchat.present.ImChatActivityPresent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聊天对话框
 */

public class ChatActivity extends BaseActivity {

    public static final String EXTRA_CHAT_ID = "extra_chat_id";
    private ChatActivityBinding mBinding;
    private List<ChatRowViewModel> mDataList = new ArrayList<>();
    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private User mRemoteUser; //对方信息
    private final int PAGE_SIZE = 20;
    private ImageView mFocusMenuView;

    public ObservableField<String> textMessageContent = new ObservableField<>();
    public ObservableBoolean showSubMenu = new ObservableBoolean();
    public ObservableBoolean voiceInputMode = new ObservableBoolean();
    public ObservableBoolean voiceRecording = new ObservableBoolean();
    public ObservableBoolean showSubMenuFastReply = new ObservableBoolean();
    public ObservableBoolean showCancelView = new ObservableBoolean();

    private InputMethodManager mInputMethodManager;
    public static final int REQUEST_CODE_PERMISSION_REQUEST = 0x800;
    public static final int REQUEST_CODE_FAST_REPLY = 0x801;

    private LinearLayoutManager layoutManager;
    private int softwareKeyboardHeight = 0;

    private AudioManager audioManager;
    private String chatId;
    private XmdChatActivityInterface mInterface;
    private float mCurPosY = 0;
    private float mCurPosDown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.chat_activity);
        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        if (TextUtils.isEmpty(chatId)) {
            XToast.show("必须传入聊天ID!");
            finish();
            return;
        }
        BlackListManager.getInstance().judgeInCustomerBlackList(chatId);
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            mInterface = new EmChatActivityPresent();
        } else {
            mInterface = new ImChatActivityPresent();
        }
        ConversationManager.getInstance().markAllMessagesRead(chatId);
        mBinding.recyclerView.setOnSizeChangedListener(new ChatRecyclerView.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                if (oldh > h && oldh - h > 200) {
                    //small size, need to scroll to last visible position
                    mBinding.recyclerView.scrollToPosition(layoutManager.findLastVisibleItemPosition());
                    if (mFocusMenuView == null) {
                        //当前显示的是键盘，那么更新高度
                        softwareKeyboardHeight = oldh - h;
                        mBinding.submenuLayout.getLayoutParams().height = softwareKeyboardHeight;
                        XLogger.d("softwareKeyboardHeight=" + softwareKeyboardHeight);
                        XmdApp.getInstance().getSp().edit().putInt(SpConstants.KEY_KEYBOARD_HEIGHT, softwareKeyboardHeight).apply();
                    }
                }
            }
        });

        //根据有无键盘高度设置进入时显示/隐藏键盘
        softwareKeyboardHeight = XmdApp.getInstance().getSp().getInt(SpConstants.KEY_KEYBOARD_HEIGHT, 0);
        if (softwareKeyboardHeight == 0) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mBinding.sendEditText.requestFocus();
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mBinding.submenuLayout.getLayoutParams().height = softwareKeyboardHeight;
        }
        layoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.inputVoice.setOnTouchListener(voiceButtonListener);

        userInfoService.loadUserInfoByChatId(chatId, new Callback<User>() {
            @Override
            public void onResponse(User result, Throwable error) {
                if (error != null) {
                    XToast.show("无法找到用户：" + chatId);
                    finish();
                    return;
                }
                mRemoteUser = result;
                setTitle(mRemoteUser.getShowName());
                setBackVisible(true);
                ChatMessageManager.getInstance().setCurrentChatId(chatId);
                initMenu();
                mBinding.setData(ChatActivity.this);
                loadData(null);

                mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (mAdapter.getDataList().size() > 0) {
                            if (XmdChatModel.getInstance().chatModelIsEm()) {
                                loadData(((EMMessage) mAdapter.getDataList().get(0).getChatMessage().getMessage()).getMsgId());
                            } else {
                                //加载IM会话
                                //    isAddMore = true;
                                loadData(null);
                            }
                        }
                        mBinding.refreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager.getInstance().cleanResource();
        XmdChat.getInstance().getMenuFactory().cleanMenus();
        ChatMessageManager.getInstance().setCurrentChatId(null);
    }

    private EMConversation getEMConversation() {
        return EMClient.getInstance().chatManager().getConversation(mRemoteUser.getChatId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (XmdChat.getInstance().getMenuFactory().onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == REQUEST_CODE_PERMISSION_REQUEST) {
            if (resultCode == RESULT_OK) {
                voiceInputMode.set(true);
                hideSoftwareInput();
                hideFocusMenu();
            }
        } else if (requestCode == REQUEST_CODE_FAST_REPLY) {
            if (resultCode == RESULT_OK) {
                ChatMenu fastReplyMenu = XmdChat.getInstance().getMenuFactory().findMenuByName("快捷回复");
                if (fastReplyMenu != null) {
                    fastReplyMenu.setSubMenuList(XmdChat.getInstance().getMenuFactory().createFastReplySubMenu(mRemoteUser.getChatId()));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //加载聊天记录
    private void loadData(final String msgId) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMConversation conversation = getEMConversation();
            if (conversation == null || conversation.getAllMsgCount() == 0) {
                mAdapter.setData(BR.data, mDataList);
                mAdapter.notifyDataSetChanged();
                return;
            }

            List<ChatRowViewModel> newDataList = new ArrayList<>();
            List<EMMessage> messageList = conversation.loadMoreMsgFromDB(msgId, PAGE_SIZE);
            if (msgId != null && messageList.size() == 0) {
                XToast.show("没有更多消息了");
                return;
            }
            ChatRowViewModel beforeData = null;
            for (EMMessage message : messageList) {
                ChatMessage chatMessage = ChatMessageFactory.createMessage(message);
                ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
                setShowTime(beforeData, data);
                beforeData = data;
                newDataList.add(data);
            }
            if (mDataList.size() > 0 && newDataList.size() > 0) {
                beforeData = newDataList.get(newDataList.size() - 1);
                ChatRowViewModel current = mDataList.get(0);
                setShowTime(beforeData, current);
            }
            mDataList.addAll(0, newDataList);
            mAdapter.setData(BR.data, mDataList);
            mAdapter.notifyDataSetChanged();
            mBinding.recyclerView.scrollToPosition(newDataList.size() - 1);
        } else {
            if (mInterface.conversationIsEmpty(mRemoteUser)) {
                mAdapter.setData(BR.data, mDataList);
                mAdapter.notifyDataSetChanged();
                return;
            }

            TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, mRemoteUser.getChatId());
            final TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
            timConversationExt.getMessage(PAGE_SIZE, mDataList.size() > 0 ? (TIMMessage) mDataList.get(0).getChatMessage().getMessage() : null, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    XToast.show("获取会话列表失败:" + s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    if (mDataList.size() > 0 && timMessages.size() == 0) {
                        XToast.show("没有更多消息了");
                        return;
                    }
                    ChatRowViewModel beforeData = null;
                    List<ChatRowViewModel> newDateList = new ArrayList<>();
                    Collections.reverse(timMessages);
                    for (TIMMessage message : timMessages) {
                        ChatMessage<TIMMessage> chatMessage = ChatMessageFactory.createMessage(message);
                        ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
                        setShowTime(beforeData, data);
                        beforeData = data;
                        newDateList.add(data);
                    }
                    if (mDataList.size() > 0 && newDateList.size() > 0) {
                        beforeData = newDateList.get(newDateList.size() - 1);
                        ChatRowViewModel current = mDataList.get(0);
                        setShowTime(beforeData, current);
                    }
                    mDataList.addAll(0, newDateList);
                    mAdapter.setData(BR.data, mDataList);
                    mAdapter.notifyDataSetChanged();
                    mBinding.recyclerView.scrollToPosition(newDateList.size() - 1);
                }
            });
        }
    }

    //聊天消息展示
    private ExCommonRecyclerViewAdapter<ChatRowViewModel> mAdapter = new ExCommonRecyclerViewAdapter<ChatRowViewModel>() {

        @Override
        public int getViewType(int position) {
            ChatRowViewModel data = getDataList().get(position);
            return ChatRowViewFactory.getViewType(data.getChatMessage());
        }

        @Override
        public ViewDataBinding createViewDataBinding(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ViewDataBinding binding;
            binding = DataBindingUtil.inflate(layoutInflater, ChatRowViewFactory.getWrapperLayout(viewType), parent, false);
            FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.contentView);
            layout.addView(ChatRowViewFactory.createView(parent, viewType));
            return binding;
        }

        @Override
        public void onDataBinding(ViewDataBinding binding, int position) {
            ChatRowViewModel data = getDataList().get(position);
            FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.contentView);
            data.bindSubView(layout.getChildAt(0));
        }

        @Override
        public void onDataUnBinding(ViewDataBinding binding, int position) {
//            ChatRowViewModel data = getDataList().get(position);
//            data.onUnbindView();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //处理新的UI消息
    @Subscribe
    public void onNewUiMessages(EventNewUiMessage event) {
        addNewChatMessageToUi(event.getViewModel());
    }

    //处理删除消息
    @Subscribe
    public void onDeleteMessage(final EventDeleteMessage deleteMessage) {
        removeMessageFromUi(deleteMessage.getChatRowViewModel());
    }

    @Subscribe
    public void onReplayDice(EventReplayDiceGame event) {
        XmdChat.getInstance().getMenuFactory().playDiceGame(getSupportFragmentManager(), mRemoteUser.getId());
    }

    //处理撤回消息
    @Subscribe
    public void onRevokeMessage(final EventRevokeMessage event) {
        ChatMessage revokeMsg = event.getChatRowViewModel().getChatMessage();
        long originMsgTime = revokeMsg.getMessageTime();
        if (System.currentTimeMillis() - originMsgTime > ChatConstants.REVOKE_LIMIT_TIME) {
            XToast.show("发送时间超过" + ChatConstants.REVOKE_LIMIT_TIME / 1000 / 60 + "分钟的消息不能被撤回");
            return;
        }
        final int index = mDataList.indexOf(event.getChatRowViewModel());
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            String msgId = ((EMMessage) revokeMsg.getMessage()).getMsgId();
            getEMConversation().removeMessage(msgId);
            removeMessageFromUi(event.getChatRowViewModel());
            //发送撤回命令
            ChatMessageManager.getInstance().sendRevokeMessage(mRemoteUser.getChatId(), msgId);
            //增加提示信息
            ChatMessage chatMessage = ChatMessageManager.getInstance().sendTipMessageWithoutUpdateUI(getEMConversation(), mRemoteUser, "你撤回了一条消息");
            ((EMMessage) chatMessage.getMessage()).setMsgTime(((EMMessage) revokeMsg.getMessage()).getMsgTime());
            insertNewChatMessageToUi(index, chatMessage);
        } else {
            //  removeMessageFromUi(event.getChatRowViewModel());
            final TIMMessage message = (TIMMessage) event.getChatRowViewModel().getChatMessage().getMessage();
            TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, mRemoteUser.getChatId());
            TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
            timConversationExt.revokeMessage(message, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    XToast.show("撤回消息失败");
                }

                @Override
                public void onSuccess() {
                    String messageId = String.valueOf(message.getMsgUniqueId());
                    ChatMessageManager.getInstance().sendRevokeMessage(mRemoteUser.getChatId(), messageId);
                    mAdapter.notifyItemChanged(index);
                }
            });
        }

    }

    //中转预约消息
    @Subscribe
    public void onAppointmentMessage(AppointmentEvent event) {
        XLogger.i(">>>", "中转预约消息");
        XmdChat.getInstance().getMenuFactory().processAppointmentEvent(event);
    }

    @Subscribe
    public void inCustomerBlackList(InCustomerBlackListEvent event) {
        ChatSettingManager.getInstance().judgeInCustomerBlack(chatId, event.isInCustomerBlackList);
    }

    /**
     * 设置是否需要显示时间
     *
     * @param before  前一个气泡
     * @param current 当前气泡
     */
    private void setShowTime(ChatRowViewModel before, ChatRowViewModel current) {
        if (before == null) {
            current.showTime.set(true);
            return;
        }
        current.showTime.set(current.getTime() - before.getTime() > ChatConstants.TIME_SHOW_INTERVAL);
    }

    //设置要发送的文本消息
    public void setTextMessageContent(Editable s) {
        textMessageContent.set(s.toString());
    }

    //通过输入框发送消息
    public void sendChatTextMessage() {
        EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_INVITATION));
        sendTextMessage();
    }

    //发送消息
    public void sendTextMessage() {
        ChatMessage chatMessage = ChatMessageManager.getInstance().sendTextMessage(mRemoteUser.getChatId(), textMessageContent.get());
        if (chatMessage != null) {
            textMessageContent.set(null);
            mBinding.sendEditText.getText().clear();
        }
    }

    //增加一条消息到列表
    public void addNewChatMessageToUi(ChatRowViewModel data) {
        setShowTime(mDataList.size() > 0 ? mDataList.get(mDataList.size() - 1) : null, data);
        mDataList.add(data);
        mAdapter.notifyItemInserted(mDataList.size() - 1);
        mBinding.recyclerView.scrollToPosition(mDataList.size() - 1);
    }

    //增加一条消息到列表
    public void insertNewChatMessageToUi(int position, ChatMessage chatMessage) {
        ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        mDataList.add(position, data);
        setShowTime(position - 1 >= 0 ? mDataList.get(position - 1) : null, data);
        mAdapter.notifyItemInserted(position);
    }

    //从列表删除一条消息
    public void removeMessageFromUi(ChatRowViewModel chatRowViewModel) {
        int index = mDataList.indexOf(chatRowViewModel);
        mDataList.remove(index);
        if (mDataList.size() > index) {
            setShowTime(index - 1 >= 0 ? mDataList.get(index - 1) : null, mDataList.get(index));
        }
        mAdapter.notifyItemRemoved(index);
    }

    //初始化菜单
    public void initMenu() {
        mBinding.pageIndicator.setViewPager(mBinding.submenuContentLayout, R.drawable.chat_menu_indicator_normal, R.drawable.chat_menu_indicator_focus);
        mBinding.submenuContentLayout.addOnPageChangeListener(mBinding.pageIndicator);
        List<ChatMenu> chatMenuList = XmdChat.getInstance().getMenuFactory()
                .createMenuList(this, mRemoteUser, mBinding.sendEditText.getText());
        for (final ChatMenu chatMenu : chatMenuList) {
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(chatMenu.icon);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSubMenuFastReply.set(false);
                    if (voiceInputMode.get()) {
                        voiceInputMode.set(false);
                    }
                    if (chatMenu.subMenuList != null && chatMenu.subMenuList.size() > 0) {
                        showSubMenu(imageView, chatMenu);
                    } else {
                        if (mFocusMenuView != null) {
                            mFocusMenuView.setSelected(false);
                            mFocusMenuView = null;
                            showSubMenu.set(false);
                        }
                    }
                    if (chatMenu.listener != null) {
                        chatMenu.listener.onClick(v);
                    }
                }
            });
            mBinding.menuLayout.addView(imageView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(24);
            layoutParams.height = ScreenUtils.dpToPx(24);
            layoutParams.weight = 1;
            layoutParams.leftMargin = ScreenUtils.dpToPx(8);
            layoutParams.rightMargin = ScreenUtils.dpToPx(8);
        }
    }

    //显示子菜单
    public void showSubMenu(ImageView menuView, final ChatMenu chatMenu) {
        if (chatMenu.getSubMenuList().get(0) instanceof SubmenuEmojiFragment) {
            EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_EMOJI_CLICK));
        }

        if (mFocusMenuView != null) {
            //当前显示聊天菜单
            mFocusMenuView.setSelected(false);
        } else if (!mBinding.recyclerView.isMaxHeight()) {
            //当前显示键盘
            lockRecyclerViewHeight();
            hideSoftwareInput();
            ThreadPoolManager.postToUIDelayed(new Runnable() {
                @Override
                public void run() {
                    unLockRecyclerViewHeight();
                }
            }, 100);
        }

        if (mFocusMenuView == menuView) {
            hideFocusMenu();
            return;
        }

        mBinding.submenuContentLayout.setAdapter(chatMenu.adapter);
        mBinding.submenuContentLayout.getAdapter().notifyDataSetChanged();
        if (chatMenu.subMenuList.size() > 1) {
            mBinding.pageIndicator.setVisibility(View.VISIBLE);
            mBinding.pageIndicator.drawIcons(chatMenu.subMenuList.size());
        } else {
            mBinding.pageIndicator.setVisibility(View.GONE);
        }

        if ("快捷回复".equals(chatMenu.getName())) {
            showSubMenuFastReply.set(true);
            EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_QUICK_CLICK));
        }

        mFocusMenuView = menuView;
        mFocusMenuView.setSelected(true);
        showSubMenu.set(true);
    }

    //转换文字和语音
    public void onSwitchInputMode() {
        if (voiceInputMode.get()) {
            voiceInputMode.set(false);
        } else {
            PermissionTool.requestPermission(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    new String[]{"发送语音消息需要录音权限"},
                    REQUEST_CODE_PERMISSION_REQUEST);
        }
    }

    //点击输入框
    public boolean onTouchEditText(View v, MotionEvent event) {
        if (mFocusMenuView != null) {
            lockRecyclerViewHeight();
            hideFocusMenu();
            ThreadPoolManager.postToUIDelayed(new Runnable() {
                @Override
                public void run() {
                    unLockRecyclerViewHeight();
                }
            }, 100);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mFocusMenuView != null) {
            hideFocusMenu();
            return;
        }
        super.onBackPressed();
    }

    //关闭输入法
    private void hideSoftwareInput() {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mInputMethodManager.hideSoftInputFromWindow(mBinding.sendEditText.getWindowToken(), 0);
    }

    //关闭聊天子菜单
    private void hideFocusMenu() {
        if (mFocusMenuView != null) {
            mFocusMenuView.setSelected(false);
            mFocusMenuView = null;
            showSubMenu.set(false);
        }
    }

    //锁定聊天列表高度
    private void lockRecyclerViewHeight() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mBinding.refreshLayout.getLayoutParams();
        lp.height = mBinding.recyclerView.getHeight();
        lp.weight = 0;
    }

    //解锁聊天列表高度
    private void unLockRecyclerViewHeight() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mBinding.refreshLayout.getLayoutParams();
        lp.height = 0;
        lp.weight = 1;
    }

    private View.OnTouchListener voiceButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onVoiceButtonDown(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onVoiceButtonUp(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onVoiceButtonMove(event);
                    break;
            }
            return true;
        }
    };



    //按下录音键
    private void onVoiceButtonDown(MotionEvent event) {
        if (voiceRecording.get()) {
            return;
        }
        mBinding.voiceView.showRecording();
        if (VoiceManager.getInstance().startRecord()) {
            voiceRecording.set(true);
            mBinding.inputVoice.setBackgroundResource(R.drawable.shape_r4_solid_green);
        }
        mCurPosDown = event.getY();
    }

    //放到录音键
    private void onVoiceButtonUp(MotionEvent event) {
        mBinding.voiceView.release();
        showCancelView.set(false);
        if (!voiceRecording.get()) {
            return;
        }
        voiceRecording.set(false);
        mBinding.inputVoice.setBackgroundResource(R.drawable.shape_r4_stoke_gray);
        VoiceManager.getInstance().stopRecord();
        String path = VoiceManager.getInstance().getRecordFile();
        File file = new File(path);
        if (!file.exists()) {
            XToast.show("录制失败！");
            return;
        }
        int duration = (int) VoiceManager.getInstance().getRecordTime() / 1000;
        if (duration < 1) {
            XToast.show("录音时间太短");
            file.delete();
            return;
        }
        if (mCurPosY - mCurPosDown <0 && (Math.abs(mCurPosY - mCurPosDown) > 100)) {
            XLogger.d("取消发送，y=" + event.getY());
            file.delete();
            return;
        }

        ChatMessageManager.getInstance().sendVoiceMessage(mRemoteUser, path, duration);
    }
    //滑动监听
    private void onVoiceButtonMove(MotionEvent event) {
        mCurPosY = event.getY();
        if(mCurPosY - mCurPosDown <0 && (Math.abs(mCurPosY - mCurPosDown) > 100)){
            showCancelView.set(true);
            voiceRecording.set(false);
        }else {
            showCancelView.set(false);
            voiceRecording.set(true);
        }
    }

    public void onClickEditFastReply() {
        hideFocusMenu();
        Intent intent = new Intent(this, ChatFastReplySettingActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FAST_REPLY);
    }
}
