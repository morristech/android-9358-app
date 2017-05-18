package com.xmd.technician.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.chatrow.EaseCustomChatRowProvider;
import com.xmd.technician.chat.chatview.BaseEaseChatView;
import com.xmd.technician.chat.chatview.ChatRowBigExpressionView;
import com.xmd.technician.chat.chatview.ChatRowFileView;
import com.xmd.technician.chat.chatview.ChatRowImageView;
import com.xmd.technician.chat.chatview.ChatRowTextView;
import com.xmd.technician.chat.chatview.ChatRowVideoView;
import com.xmd.technician.chat.chatview.ChatRowVoiceView;
import com.xmd.technician.chat.chatview.EMessageListItemClickListener;
import com.xmd.technician.chat.utils.EaseCommonUtils;


/**
 * Created by lhj on 17-3-30.
 */

public class EaseMessageAdapter extends BaseAdapter {

    private Context context;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 12;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 13;

    // reference to conversation object in chatsdk
    private EMConversation conversation;
    EMMessage[] messages = null;

    private String toChatUsername;

    private EMessageListItemClickListener itemClickListener;
    private EaseCustomChatRowProvider customRowProvider;

    private boolean showUserNick;
    private boolean showAvatar;
    private Drawable myBubbleBg;
    private Drawable otherBuddleBg;

    private ListView listView;

    public EaseMessageAdapter(Context context, String username, int chatType, ListView listView) {
        this.context = context;
        this.listView = listView;
        toChatUsername = username;
        this.conversation = EMClient.getInstance().chatManager().getConversation(username, EaseCommonUtils.getConversationType(chatType), true);
    }

    Handler handler = new Handler() {
        private void refreshList() {
            // you should not call getAllMessages() in UI thread
            // otherwise there is problem when refreshing UI and there is new message arrive
            java.util.List<EMMessage> var = conversation.getAllMessages();
            messages = var.toArray(new EMMessage[var.size()]);
            conversation.markAllMessagesAsRead();
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (messages.length > 0) {
                        listView.setSelection(messages.length - 1);
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    listView.setSelection(position);
                    break;
                default:
                    break;
            }
        }
    };

    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * refresh and select the last
     */
    public void refreshSelectLast() {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * refresh and seek to the position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }


    public EMMessage getItem(int position) {
        if (messages != null && position < messages.length) {
            return messages[position];
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * get count of messages
     */
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }

    /**
     * get number of message type, here 14 = (EMMessage.Type) * 2
     */
    public int getViewTypeCount() {
        if (customRowProvider != null && customRowProvider.getCustomChatRowTypeCount() > 0) {
            return customRowProvider.getCustomChatRowTypeCount() + 14;
        }
        return 14;
    }


    /**
     * get type of item
     */
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }

        if (customRowProvider != null && customRowProvider.getCustomChatRowType(message) > 0) {
            return customRowProvider.getCustomChatRowType(message) + 13;
        }

        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;
            }
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }

    protected BaseEaseChatView createChatRow(Context context, EMMessage message, int position) {
        BaseEaseChatView chatRow = null;
        if (customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null) {
            return customRowProvider.getCustomChatRow(message, position, this);
        }
        switch (message.getType()) {
            case TXT:
                if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    chatRow = new ChatRowBigExpressionView(context, message, position, this);
                } else {
                    chatRow = new ChatRowTextView(context, message, position, this);
                }

                break;
            case VIDEO:
                chatRow = new ChatRowVideoView(context, message, position, this);
                break;
            case LOCATION:

                break;
            case FILE:
                chatRow = new ChatRowFileView(context, message, position, this);
                break;
            case IMAGE:
                chatRow = new ChatRowImageView(context, message, position, this);
                break;
            case VOICE:
                chatRow = new ChatRowVoiceView(context, message, position, this);
                break;
            default:
                chatRow = new ChatRowTextView(context, message, position, this);
                break;
        }

        return chatRow;
    }


    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);

        if (convertView == null) {
            convertView = createChatRow(context, message, position);
        }
        //refresh ui with messages
        ((BaseEaseChatView) convertView).setUpView(message, position, itemClickListener);

        return convertView;
    }


    public String getToChatUsername() {
        return toChatUsername;
    }


    public void setShowUserNick(boolean showUserNick) {
        this.showUserNick = showUserNick;
    }


    public void setShowAvatar(boolean showAvatar) {
        this.showAvatar = showAvatar;
    }


    public void setMyBubbleBg(Drawable myBubbleBg) {
        this.myBubbleBg = myBubbleBg;
    }


    public void setOtherBuddleBg(Drawable otherBuddleBg) {
        this.otherBuddleBg = otherBuddleBg;
    }


    public void setItemClickListener(EMessageListItemClickListener listener) {
        itemClickListener = listener;
    }

    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        customRowProvider = rowProvider;
    }


    public boolean isShowUserNick() {
        return showUserNick;
    }


    public boolean isShowAvatar() {
        return showAvatar;
    }


    public Drawable getMyBubbleBg() {
        return myBubbleBg;
    }


    public Drawable getOtherBuddleBg() {
        return otherBuddleBg;
    }

}
