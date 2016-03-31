package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.common.ThreadManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-21.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int RECEIVED_TYPE = 1;
    private static final int SENT_TYPE =  2;

    private EMConversation mConversation;
    private EMMessage[] mChatMessages = null;

    private Context mContext;
    private RecyclerView mListView;

    public ChatListAdapter(Context context, RecyclerView view , String username, int chatType){
        mContext = context;
        mListView = view;
        mConversation = EMClient.getInstance().chatManager().getConversation(username, CommonUtils.getConversationType(chatType), true);
    }

    public void refreshList(){
        mChatMessages = mConversation.getAllMessages().toArray(new EMMessage[0]);
        mConversation.markAllMessagesAsRead();
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void setSelectLast(){
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                if(mChatMessages.length > 0){
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
    public void refreshSeekTo(int position){
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                if(mChatMessages.length > position){
                    mListView.scrollToPosition(position);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage msg = getItem(position);
        if(msg != null){
            return  msg.direct() == EMMessage.Direct.RECEIVE?RECEIVED_TYPE:SENT_TYPE;
        }
        return SENT_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == RECEIVED_TYPE ?
                R.layout.chat_received_item : R.layout.chat_sent_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChatViewHolder){
            EMMessage message = getItem(position);
            ChatViewHolder chatHolder = (ChatViewHolder) holder;
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            Spannable span = SmileUtils.getSmiledText(mContext, txtBody.getMessage());
            chatHolder.mContent.setText(span, TextView.BufferType.SPANNABLE);
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages != null ? mChatMessages.length: 0;
    }

    public EMMessage getItem(int position) {
        if (mChatMessages != null && position < mChatMessages.length) {
            return mChatMessages[position];
        }
        return null;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.avatar) ImageView mAvatar;
        @Bind(R.id.content) TextView mContent;
        @Bind(R.id.time) TextView mTime;
        public ChatViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
