package com.xmd.chat.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
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
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.ExCommonRecyclerViewAdapter;
import com.xmd.app.SpConstants;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.chat.BR;
import com.xmd.chat.ChatConstants;
import com.xmd.chat.ChatMenu;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.VoiceManager;
import com.xmd.chat.XmdChat;
import com.xmd.chat.databinding.ChatActivityBinding;
import com.xmd.chat.event.EventDeleteMessage;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventRevokeMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天对话框
 */

public class ChatActivity extends BaseActivity {
    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    private ChatActivityBinding mBinding;

    private List<ChatRowViewModel> mDataList = new ArrayList<>();

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private User mRemoteUser;

    private final int PAGE_SIZE = 20;

    private ImageView mFocusMenuView;

    public ObservableField<String> textMessageContent = new ObservableField<>();
    public ObservableBoolean showSubMenu = new ObservableBoolean();
    public ObservableBoolean voiceInputMode = new ObservableBoolean();
    public ObservableBoolean voiceRecording = new ObservableBoolean();

    private InputMethodManager mInputMethodManager;
    public static final int REQUEST_CODE_PERMISSION_REQUEST = 0x800;

    private LinearLayoutManager layoutManager;
    private int softwareKeyboardHeight = ScreenUtils.dpToPx(300);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.chat_activity);

        String chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        if (TextUtils.isEmpty(chatId)) {
            XToast.show("必须传入聊天ID!");
            finish();
            return;
        }
        ConversationManager.getInstance().markAllMessagesRead(chatId);
        MessageManager.getInstance().setCurrentChatId(chatId);
        mRemoteUser = userInfoService.getUserByChatId(chatId);
        if (mRemoteUser == null) {
            XToast.show("无法找到用户信息!");
            finish();
            return;
        }

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

        setTitle(mRemoteUser.getShowName());

        layoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mAdapter.getDataList().get(0).getChatMessage().getEmMessage().getMsgId());
                mBinding.refreshLayout.setRefreshing(false);
            }
        });

        mBinding.setData(this);

        mBinding.inputVoice.setOnTouchListener(voiceButtonListener);

        initMenu();

        loadData(null);

        mBinding.recyclerView.setOnSizeChangedListener(new ChatRecyclerView.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                if (oldh > h) {
                    //small size, need to scroll to last visible position
                    mBinding.recyclerView.scrollToPosition(layoutManager.findLastVisibleItemPosition());
                    if (mFocusMenuView == null) {
                        //当前显示的是键盘，那么更新高度
                        if (softwareKeyboardHeight == 0) {
                            //首次更新，需要设置聊天子菜单高度
                            mBinding.submenuLayout.getLayoutParams().height = oldh - h;
                        }
                        softwareKeyboardHeight = oldh - h;
                        XLogger.d("softwareKeyboardHeight=" + softwareKeyboardHeight);
                        XmdApp.getInstance().getSp().edit().putInt(SpConstants.KEY_KEYBOARD_HEIGHT, softwareKeyboardHeight).apply();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager.getInstance().cleanResource();
        XmdChat.getInstance().getMenuFactory().cleanMenus();
        MessageManager.getInstance().setCurrentChatId(null);
    }

    private EMConversation getConversation() {
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //加载聊天记录
    private void loadData(String msgId) {
        EMConversation conversation = getConversation();
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
            ChatMessage chatMessage = ChatMessageFactory.create(message);
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
//            ChatRowViewModel data = getDataList().create(position);
//            data.onUnbindView();
        }
    };

    //处理新的UI消息
    @Subscribe
    public void onNewUiMessages(EventNewUiMessage message) {
        addNewChatMessageToUi(message.getChatMessage());
    }

    //处理删除消息
    @Subscribe
    public void onDeleteMessage(final EventDeleteMessage deleteMessage) {
        getConversation().removeMessage(deleteMessage.getChatRowViewModel().getChatMessage().getEmMessage().getMsgId());
        removeMessageFromUi(deleteMessage.getChatRowViewModel());
    }

    //处理撤回消息
    @Subscribe
    public void onRevokeMessage(EventRevokeMessage event) {
        ChatMessage revokeMsg = event.getChatRowViewModel().getChatMessage();
        long originMsgTime = revokeMsg.getEmMessage().getMsgTime();
        if (System.currentTimeMillis() - originMsgTime > ChatConstants.REVOKE_LIMIT_TIME) {
            XToast.show("发送时间超过" + ChatConstants.REVOKE_LIMIT_TIME / 1000 / 60 + "分钟的消息不能被撤回");
            return;
        }
        //移除原来消息
        int index = mDataList.indexOf(event.getChatRowViewModel());

        String msgId = revokeMsg.getEmMessage().getMsgId();
        getConversation().removeMessage(msgId);
        removeMessageFromUi(event.getChatRowViewModel());
        //发送撤回命令
        MessageManager.getInstance().sendRevokeMessage(mRemoteUser.getChatId(), msgId);
        //增加提示信息
        ChatMessage chatMessage = MessageManager.getInstance().sendTipMessage(getConversation(), mRemoteUser, "你撤回了一条消息");
        chatMessage.getEmMessage().setMsgTime(revokeMsg.getEmMessage().getMsgTime());
        insertNewChatMessageToUi(index, chatMessage);
    }

    //中转预约消息
    @Subscribe
    public void onAppointmentMessage(AppointmentEvent event) {
        XmdChat.getInstance().getMenuFactory().processAppointmentEvent(event);
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

    //发送消息
    public void sendTextMessage() {
        ChatMessage chatMessage = MessageManager.getInstance().sendTextMessage(mRemoteUser.getChatId(), textMessageContent.get());
        if (chatMessage != null) {
            textMessageContent.set(null);
            mBinding.sendEditText.getText().clear();
        }
    }


    //增加一条消息到列表
    public void addNewChatMessageToUi(ChatMessage chatMessage) {
        ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
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
                case MotionEvent.ACTION_MOVE:
                    XLogger.d("move " + event.getX() + "," + event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    onVoiceButtonUp(event);
                    break;
            }
            return true;
        }
    };

    //按下录音键
    private void onVoiceButtonDown(MotionEvent event) {
        XLogger.d("onVoiceButtonDown");
        if (voiceRecording.get()) {
            return;
        }

        if (VoiceManager.getInstance().startRecord()) {
            voiceRecording.set(true);
            mBinding.inputVoice.setBackgroundResource(R.drawable.shape_r4_solid_green);
        }
    }

    //放到录音键
    private void onVoiceButtonUp(MotionEvent event) {
        XLogger.d("onVoiceButtonUp");
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
        if (event.getY() < -50) {
            XLogger.d("取消发送，y=" + event.getY());
            file.delete();
        } else {
            MessageManager.getInstance()
                    .sendVoiceMessage(mRemoteUser, path, (int) VoiceManager.getInstance().getRecordTime() / 1000);
        }
    }
}
