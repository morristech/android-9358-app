package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.Adapter.EaseMessageAdapter;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatSentMessageHelper;
import com.xmd.technician.chat.chatrow.EaseCustomChatRowProvider;
import com.xmd.technician.chat.utils.EaseCommonUtils;


/**
 * Created by Lhj on 17-3-30.
 */

public class EaseChatMessageList extends RelativeLayout {

    protected static final String TAG = "EaseChatMessageList";
    protected ListView listView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected Context context;
    protected EMConversation conversation;
    protected int chatType;
    protected String toChatUsername;
    protected EaseMessageAdapter messageAdapter;
    protected boolean showUserNick;
    protected boolean showAvatar;
    protected Drawable myBubbleBg;
    protected Drawable otherBuddleBg;

    public EaseChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
        init(context);
    }

    public EaseChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
        listView = (ListView) findViewById(R.id.list);
    }

    /**
     * init widget
     *
     * @param toChatUsername
     * @param chatType
     * @param customChatRowProvider
     */
    public void init(String toChatUsername, int chatType, EaseCustomChatRowProvider customChatRowProvider, ChatSentMessageHelper sentMessageHelper) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        messageAdapter = new EaseMessageAdapter(context, toChatUsername, chatType, listView);
        messageAdapter.setShowAvatar(showAvatar);
        messageAdapter.setShowUserNick(showUserNick);
        messageAdapter.setMyBubbleBg(myBubbleBg);
        messageAdapter.setOtherBuddleBg(otherBuddleBg);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);
        messageAdapter.setSentMessageHelper(sentMessageHelper);
        // set message adapter
        listView.setAdapter(messageAdapter);

        refreshSelectLast();
    }

    protected void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
        showAvatar = ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true);
        myBubbleBg = ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground);
        otherBuddleBg = ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground);
        showUserNick = ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false);
        ta.recycle();
    }


    /**
     * refresh
     */
    public void refresh() {
        if (messageAdapter != null) {
            messageAdapter.refresh();
        }
    }

    /**
     * refresh and jump to the last
     */
    public void refreshSelectLast() {
        if (messageAdapter != null) {
            messageAdapter.refreshSelectLast();
        }
    }

    /**
     * refresh and jump to the position
     *
     * @param position
     */
    public void refreshSeekTo(int position) {
        if (messageAdapter != null) {
            messageAdapter.refreshSeekTo(position);
        }
    }

    public ListView getListView() {
        return listView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public EMMessage getItem(int position) {
        return messageAdapter.getItem(position);
    }

    public void setShowUserNick(boolean showUserNick) {
        this.showUserNick = showUserNick;
    }

    public boolean isShowUserNick() {
        return showUserNick;
    }


    /**
     * set click listener
     *
     * @param listener
     */
    public void setItemClickListener(EMessageListItemClickListener listener) {
        if (messageAdapter != null) {
            messageAdapter.setItemClickListener(listener);
        }
    }

    /**
     * set chat row provider
     *
     * @param rowProvider
     */
    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        if (messageAdapter != null) {
            messageAdapter.setCustomChatRowProvider(rowProvider);
        }
    }
}

