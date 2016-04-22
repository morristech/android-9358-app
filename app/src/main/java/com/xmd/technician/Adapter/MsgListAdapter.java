package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.chat.chatview.BaseChatView;
import com.xmd.technician.chat.chatview.ChatUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-16.
 */
public class MsgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface onMsgItemClickListener{
        void onMsgItemClick(EMConversation conversation);
    }

    private List<EMConversation> mConversationList;
    private List<EMConversation> mCopyConversationList;
    private onMsgItemClickListener mOnMsgItemClickListener;
    private Context mContext;
    private Filter mFilter;


    public MsgListAdapter(Context context, List<EMConversation> conversationList,onMsgItemClickListener onMsgItemClickListener){
        mContext = context;
        mConversationList = new ArrayList<>();
        mConversationList.addAll(conversationList);
        mCopyConversationList = new ArrayList<>();
        mCopyConversationList.addAll(conversationList);
        mOnMsgItemClickListener = onMsgItemClickListener;
    }

    public void setData(List<EMConversation> conversationList){
        mConversationList.clear();
        mConversationList.addAll(conversationList);

        mCopyConversationList.clear();
        mCopyConversationList.addAll(mConversationList);
        notifyDataSetChanged();
    }

    public Filter getFilter(){
        if(mFilter == null){
            mFilter = new ConversationFilter(mConversationList);
        }
        return mFilter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ConversationViewHolder){
            ConversationViewHolder conversationHolder = (ConversationViewHolder) holder;
            EMConversation conversation = getItem(position);

            conversationHolder.mName.setText(conversation.getUserName());
            if(conversation.getUnreadMsgCount() > 0){
                conversationHolder.mUnread.setText(String.valueOf(conversation.getUnreadMsgCount()));
                conversationHolder.mUnread.setVisibility(View.VISIBLE);
            }else {
                conversationHolder.mUnread.setVisibility(View.INVISIBLE);
            }

            if(conversation.getAllMsgCount() != 0){
                // 把最后一条消息的内容作为item的message内容
                EMMessage lastMessage = conversation.getLastMessage();
                Spannable span = SmileUtils.getSmiledText(mContext, CommonUtils.getMessageDigest(lastMessage, mContext));
                conversationHolder.mContent.setText(span, TextView.BufferType.SPANNABLE);
                conversationHolder.mTime.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                try {
                    if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                        ChatUser user;
                        user = new ChatUser(conversation.getUserName());
                        user.setAvatar(lastMessage.getStringAttribute(ChatConstant.KEY_HEADER));
                        user.setNick(lastMessage.getStringAttribute(ChatConstant.KEY_NAME));
                        UserUtils.saveUser(user);
                    }
                    UserUtils.setUserAvatar(mContext, conversation.getUserName(), conversationHolder.mAvatar);
                    UserUtils.setUserNick(conversation.getUserName(), conversationHolder.mName);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){

                }
            }
            if(mOnMsgItemClickListener != null){
                holder.itemView.setOnClickListener(v -> mOnMsgItemClickListener.onMsgItemClick(conversation));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mConversationList.size();
    }

    public EMConversation getItem(int position){
        try {
            if(mConversationList != null ){
                return mConversationList.get(position);
            }
        }catch (Exception e){

        }

        return null;
    }

    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = mCopyConversationList;
                results.count = mCopyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();

                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if(group != null){
                        username = group.getGroupName();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else{
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mConversationList.clear();
            mConversationList.addAll((List<EMConversation>) results.values);
            notifyDataSetChanged();
        }

    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.avatar) ImageView mAvatar;
        @Bind(R.id.name) TextView mName;
        @Bind(R.id.content) TextView mContent;
        @Bind(R.id.time) TextView mTime;
        @Bind(R.id.unread) TextView mUnread;
        public ConversationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
