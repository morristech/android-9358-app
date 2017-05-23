package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.chat.CommonUtils;
import com.xmd.manager.chat.EmchatConstant;
import com.xmd.manager.chat.chatview.BaseChatView;
import com.xmd.manager.chat.chatview.ChatViewCoupon;
import com.xmd.manager.chat.chatview.ChatViewImage;
import com.xmd.manager.chat.chatview.ChatViewOrder;
import com.xmd.manager.chat.chatview.ChatViewText;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sdcm on 16-3-21.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_RECV_TXT = 2;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 3;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 4;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 13;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 14;

    private EMConversation mConversation;
    private List<EMMessage> mChatMessages = null;

    private Context mContext;
    private RecyclerView mListView;
    private Map<String, String> mOrderReplyMessage;

    public ChatListAdapter(Context context, String username, int chatType) {
        mContext = context;
        mConversation = EMClient.getInstance().chatManager().getConversation(username);
        mOrderReplyMessage = new HashMap<>();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mListView = recyclerView;
    }

    public void refresh() {
        mChatMessages = mConversation.getAllMessages();
        mConversation.markAllMessagesAsRead();
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> notifyDataSetChanged());
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        // avoid refresh too frequently when receiving large amount offline messages
        refresh();
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
            if (mChatMessages.size() > 0) {
                mListView.scrollToPosition(mChatMessages.size() - 1);
            }
        });
    }

    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        refresh();
        if (mChatMessages.size() > position) {
            mListView.scrollToPosition(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }

        if (CommonUtils.getCustomChatType(message) > 0) {
            return CommonUtils.getCustomChatType(message);
        }

        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(EmchatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;
            }
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }

        return -1;// invalid
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createChatRow(parent.getContext(), viewType);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            EMMessage message = getItem(position);
            ((ChatViewHolder) holder).setupView(message);
            ((ChatViewHolder) holder).itemView.setOnLongClickListener(v -> {
                new AlertDialogBuilder(mContext).setTitle(ResourceUtils.getString(R.string.alert_tips_title))
                        .setMessage(ResourceUtils.getString(R.string.message_remove_confirm_tips))
                        .setPositiveButton(ResourceUtils.getString(R.string.confirm), view -> {
                            mConversation.removeMessage(message.getMsgId());
                            refreshSelectLast();
                        })
                        .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                        .show();
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages != null ? mChatMessages.size() : 0;
    }

    public EMMessage getItem(int position) {
        if (mChatMessages != null && position < mChatMessages.size()) {
            return mChatMessages.get(position);
        }
        return null;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        public ChatViewHolder(View itemView) {
            super(itemView);
        }

        public void setupView(EMMessage emMessage) {
            ((BaseChatView) itemView).setupView(emMessage);
        }
    }

    protected BaseChatView createChatRow(Context context, int type) {
        BaseChatView chatRow = null;
        switch (type) {
            case MESSAGE_TYPE_SENT_TXT:
                chatRow = new ChatViewText(context, EMMessage.Direct.SEND);
                break;
            case MESSAGE_TYPE_RECV_TXT:
                chatRow = new ChatViewText(context, EMMessage.Direct.RECEIVE);
                break;
            case MESSAGE_TYPE_RECV_IMAGE:
                chatRow = new ChatViewImage(context, EMMessage.Direct.RECEIVE, mConversation, (RecyclerView.Adapter) ChatListAdapter.this);
                break;
            case MESSAGE_TYPE_SENT_IMAGE:
                chatRow = new ChatViewImage(context, EMMessage.Direct.SEND, mConversation, (RecyclerView.Adapter) ChatListAdapter.this);
                break;
            case EmchatConstant.MESSAGE_TYPE_RECV_ORDER:
                chatRow = new ChatViewOrder(context, EMMessage.Direct.RECEIVE);
                ((ChatViewOrder) chatRow).setOrderManagerListener(mOrderManagerListener);
                break;
            case EmchatConstant.MESSAGE_TYPE_SENT_ORDER:
                chatRow = new ChatViewOrder(context, EMMessage.Direct.SEND);
                break;
            case EmchatConstant.MESSAGE_TYPE_SENT_ORDINARY_COUPON:
                chatRow = new ChatViewCoupon(context, EMMessage.Direct.SEND);
                break;
            default:
                chatRow = new ChatViewText(context, EMMessage.Direct.RECEIVE);
                break;
        }

        return chatRow;
    }

    private ChatViewOrder.OrderManagerListener mOrderManagerListener = new ChatViewOrder.OrderManagerListener() {

        @Override
        public void onAccept(EMMessage message) {
            try {
                String orderId = message.getStringAttribute(EmchatConstant.KEY_ORDER_ID);
                String content = ((EMTextMessageBody) message.getBody()).getMessage();
                content = content.replace("发起预约", "接受预约");
                mOrderReplyMessage.put(orderId, content);
                doManageOrder(orderId, Constant.ORDER_STATUS_ACCEPT, "");
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRefuse(EMMessage message) {
            try {
                String orderId = message.getStringAttribute(EmchatConstant.KEY_ORDER_ID);
                String content = ((EMTextMessageBody) message.getBody()).getMessage();
                content = content.replace("发起预约", "拒绝预约");
                mOrderReplyMessage.put(orderId, content);
                doManageOrder(orderId, Constant.ORDER_STATUS_REJECTED, "");
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    };

    private void doManageOrder(String orderId, String type, String reason) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }

    public String getOrderReplyMessage(String orderId) {
        return mOrderReplyMessage.get(orderId);
    }
}
