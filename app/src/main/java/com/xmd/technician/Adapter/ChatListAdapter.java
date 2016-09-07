package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.bean.AcceptOrRejectGame;
import com.xmd.technician.bean.PlayDiceGame;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.bean.CancelGame;
import com.xmd.technician.chat.chatview.BaseChatView;
import com.xmd.technician.chat.chatview.ChatViewBegReward;
import com.xmd.technician.chat.chatview.ChatViewCoupon;
import com.xmd.technician.chat.chatview.ChatViewCouponTip;
import com.xmd.technician.chat.chatview.ChatViewPlayDiceGame;
import com.xmd.technician.chat.chatview.ChatViewReceiveDiceGame;
import com.xmd.technician.chat.chatview.ChatViewImage;
import com.xmd.technician.chat.chatview.ChatViewOrder;
import com.xmd.technician.chat.chatview.ChatViewPaidCoupon;
import com.xmd.technician.chat.chatview.ChatViewPaidCouponTip;
import com.xmd.technician.chat.chatview.ChatViewReward;
import com.xmd.technician.chat.chatview.ChatViewSendGame;
import com.xmd.technician.chat.chatview.ChatViewText;
import com.xmd.technician.chat.chatview.EMessageListItemClickListener;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 16-3-21.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_RECV_TXT = 2;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 3;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 4;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 5;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 6;
    private static final int MESSAGE_TYPE_SENT_VOICE = 7;
    private static final int MESSAGE_TYPE_RECV_VOICE = 8;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 9;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 10;
    private static final int MESSAGE_TYPE_SENT_FILE = 11;
    private static final int MESSAGE_TYPE_RECV_FILE = 12;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 13;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 14;

    private EMConversation mConversation;
    private EMMessage[] mChatMessages = null;

    private Context mContext;
    private RecyclerView mListView;
    private Map<String, String> mOrderReplyMessage;

    private EMessageListItemClickListener mItemClickListener;

    public ChatListAdapter(Context context, RecyclerView view, String username, int chatType) {
        mContext = context;
        mListView = view;
        mConversation = EMClient.getInstance().chatManager().getConversation(username, CommonUtils.getConversationType(chatType), true);
        mOrderReplyMessage = new HashMap<>();
    }

    public void setListItemClickListener(EMessageListItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void refreshList() {
        mChatMessages = mConversation.getAllMessages().toArray(new EMMessage[0]);
        mConversation.markAllMessagesAsRead();
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void setSelectLast() {
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                if (mChatMessages.length > 0) {
                    mListView.scrollToPosition(mChatMessages.length - 1);
                }
            }
        });
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        // avoid refresh too frequently when receiving large amount offline messages
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        refreshList();
        setSelectLast();
    }

    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        refreshList();
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                if (mChatMessages.length > position) {
                    mListView.scrollToPosition(position);
                }
            }
        });
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createChatRow(parent.getContext(), viewType);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ((ChatViewHolder) holder).setUpView(getItem(position), mItemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages != null ? mChatMessages.length : 0;
    }

    public EMMessage getItem(int position) {
        if (mChatMessages != null && position < mChatMessages.length) {
            return mChatMessages[position];
        }
        return null;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatViewHolder(View itemView) {
            super(itemView);
        }

        public void setUpView(EMMessage emMessage, EMessageListItemClickListener listener) {
            ((BaseChatView) itemView).setUpView(emMessage, listener);
        }
    }

    protected BaseChatView createChatRow(Context context, int type) {
        BaseChatView chatRow = null;
        /*if(customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null){
            return customRowProvider.getCustomChatRow(message, position, this);
        }*/
        switch (type) {
            case MESSAGE_TYPE_SENT_TXT:
                chatRow = new ChatViewText(context, EMMessage.Direct.SEND);
                break;
            case MESSAGE_TYPE_RECV_TXT:
                chatRow = new ChatViewText(context, EMMessage.Direct.RECEIVE);
                break;
            case MESSAGE_TYPE_RECV_IMAGE:
                chatRow = new ChatViewImage(context, EMMessage.Direct.RECEIVE);
                break;
            case MESSAGE_TYPE_SENT_IMAGE:
                chatRow = new ChatViewImage(context, EMMessage.Direct.SEND);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_ORDER:
                chatRow = new ChatViewOrder(context, EMMessage.Direct.RECEIVE);
                ((ChatViewOrder) chatRow).setOrderManagerListener(mOrderManagerListener);
                break;
            case ChatConstant.MESSAGE_TYPE_SENT_ORDER:
                chatRow = new ChatViewOrder(context, EMMessage.Direct.SEND);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_GAME_INVITE:
                chatRow = new ChatViewReceiveDiceGame(context, EMMessage.Direct.RECEIVE, mConversation);
                ((ChatViewReceiveDiceGame) chatRow).setGameManagerListener(mGameManagerListener);
                break;
            case ChatConstant.MESSAGE_TYPE_SEND_GAME_INVITE:
                chatRow = new ChatViewSendGame(context, EMMessage.Direct.SEND, mConversation);
                ((ChatViewSendGame) chatRow).setGameCancelListener(mGameCancelListener);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_REWARD:
                chatRow = new ChatViewReward(context, EMMessage.Direct.RECEIVE);
                break;
            case ChatConstant.MESSAGE_TYPE_SENT_BEG_REWARD:
                chatRow = new ChatViewBegReward(context, EMMessage.Direct.SEND);
                break;
            case ChatConstant.MESSAGE_TYPE_SENT_ORDINARY_COUPON:
                chatRow = new ChatViewCoupon(context, EMMessage.Direct.SEND);
                break;
            case ChatConstant.MESSAGE_TYPE_SENT_PAID_COUPON:
                chatRow = new ChatViewPaidCoupon(context, EMMessage.Direct.SEND);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_PAID_COUPON_TIP:
            case ChatConstant.MESSAGE_TYPE_SENT_PAID_COUPON_TIP:
                chatRow = new ChatViewPaidCouponTip(context, EMMessage.Direct.RECEIVE);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_COUPON_TIP:
            case ChatConstant.MESSAGE_TYPE_SENT_COUPON_TIP:
                chatRow = new ChatViewCouponTip(context, EMMessage.Direct.RECEIVE);
                break;
            case ChatConstant.MESSAGE_TYPE_SENT_REWARD:
                chatRow = new ChatViewText(context, EMMessage.Direct.SEND);
                break;
            case ChatConstant.MESSAGE_TYPE_SEND_GAME_REJECT:
            case ChatConstant.MESSAGE_TYPE_RECV_GAME_REJECT:
                chatRow = new ChatViewSendGame(context, EMMessage.Direct.SEND, mConversation);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_GAME_ACCEPT:
            case ChatConstant.MESSAGE_TYPE_SEND_GAME_ACCEPT:
                chatRow = new ChatViewSendGame(context, EMMessage.Direct.SEND, mConversation);
                break;
            case ChatConstant.MESSAGE_TYPE_RECV_GAME_OVER:
            case ChatConstant.MESSAGE_TYPE_SEND_GAME_OVER:
                chatRow = new ChatViewPlayDiceGame(context, EMMessage.Direct.SEND, mConversation);
                ((ChatViewPlayDiceGame) chatRow).setGameManagerListener(gameManagerListener);
                break;
            default:
                chatRow = new ChatViewText(context, EMMessage.Direct.RECEIVE);
                break;
        }

        return chatRow;
    }

    private ChatViewSendGame.GameCancelListener mGameCancelListener = new ChatViewSendGame.GameCancelListener() {
        @Override
        public void onCancel(EMMessage message) {
            RxBus.getInstance().post(new CancelGame(message));
        }
    };
    private ChatViewReceiveDiceGame.GameManagerListener mGameManagerListener = new ChatViewReceiveDiceGame.GameManagerListener() {
        @Override
        public void onAccept(EMMessage message) {
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            try {
                RxBus.getInstance().post(new AcceptOrRejectGame(body.getMessage(), message.getStringAttribute(ChatConstant.KEY_GAME_ID), ChatConstant.KEY_ACCEPT_GAME, message.getFrom()));
            } catch (HyphenateException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onRefuse(EMMessage message) {
            Map<String, String> params = new HashMap<>();
            try {
                String gameId = message.getStringAttribute(ChatConstant.KEY_GAME_ID);
                params.put(RequestConstant.KEY_DICE_GAME_ID, message.getStringAttribute(RequestConstant.KEY_DICE_GAME_ID).substring(5));
                params.put(RequestConstant.KEY_DICE_GAME_STATUS, ChatConstant.KEY_GAME_REJECT);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GAME_ACCEPT_OR_REJECT, params);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    };

    private ChatViewOrder.OrderManagerListener mOrderManagerListener = new ChatViewOrder.OrderManagerListener() {
        @Override
        public void onAccept(EMMessage message) {
            try {
                String orderId = message.getStringAttribute(ChatConstant.KEY_ORDER_ID);
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
                String orderId = message.getStringAttribute(ChatConstant.KEY_ORDER_ID);
                String content = ((EMTextMessageBody) message.getBody()).getMessage();
                content = content.replace("发起预约", "拒绝预约");
                mOrderReplyMessage.put(orderId, content);
                doManageOrder(orderId, Constant.ORDER_STATUS_REJECTED, "");
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    };
    private ChatViewPlayDiceGame.GameManagerListener gameManagerListener = new ChatViewPlayDiceGame.GameManagerListener() {
        @Override
        public void playAgain(EMMessage message) {
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            String content = body.getMessage();
            RxBus.getInstance().post(new PlayDiceGame(content));
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
