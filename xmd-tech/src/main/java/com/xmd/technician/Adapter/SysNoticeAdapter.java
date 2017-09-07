package com.xmd.technician.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.window.BrowserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 16-5-13.
 */
public class SysNoticeAdapter extends RecyclerView.Adapter {
    private static final byte TYPE_FOOTER = 0;
    private static final byte TYPE_ITEM = 1;

    private EMConversation mConversation;
    private EMMessage[] mChatMessages = null;

    private Context mContext;

    private View.OnClickListener mFooterClickListener;

    public SysNoticeAdapter(Context context, String username, int chatType) {
        mContext = context;
        mConversation = EMClient.getInstance().chatManager().getConversation(username, EMConversation.EMConversationType.Chat, true);
    }

    public void setOnFooterClickListener(View.OnClickListener listener) {
        mFooterClickListener = listener;
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

    public EMMessage getItem(int position) {
        if (mChatMessages != null && position < mChatMessages.length) {
            return mChatMessages[position];
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_notice_item, parent, false);
            return new NoticeViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoticeViewHolder) {
            NoticeViewHolder viewHolder = (NoticeViewHolder) holder;
            EMMessage message = mChatMessages[mChatMessages.length - 1 - position];

            viewHolder.mTitle.setText(message.getStringAttribute("title", ""));
            viewHolder.mSummary.setText(message.getStringAttribute("summary", ""));
            Glide.with(mContext).load(message.getStringAttribute("imageUrl", "")).into(viewHolder.mImage);

            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtra(BrowserActivity.EXTRA_SHOW_MENU, false);
                intent.putExtra(BrowserActivity.EXTRA_URL, message.getStringAttribute("linkUrl", ""));
                mContext.startActivity(intent);
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            String desc = mContext.getResources().getString(R.string.list_item_loading);
            if (mChatMessages == null || mChatMessages.length == 0) {
                desc = mContext.getResources().getString(R.string.list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mChatMessages.length == mConversation.getAllMsgCount()) {
                desc = mContext.getResources().getString(R.string.list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> {
                    if (mFooterClickListener != null) {
                        mFooterClickListener.onClick(v);
                    }
                });
            }
            footerHolder.itemFooter.setText(desc);
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages != null ? mChatMessages.length + 1 : 1;
    }


    public class NoticeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.summary)
        TextView mSummary;
        @BindView(R.id.image)
        ImageView mImage;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_footer)
        TextView itemFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
