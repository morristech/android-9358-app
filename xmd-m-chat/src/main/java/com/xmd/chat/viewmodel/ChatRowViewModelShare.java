package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xmd.chat.R;
import com.xmd.chat.beans.OnceCard;
import com.xmd.chat.databinding.ChatRowShareBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.ShareChatMessage;


/**
 * Created by mo on 17-7-1.
 * 分享的消息
 */

public class ChatRowViewModelShare extends ChatRowViewModel {
    public ChatRowViewModelShare(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowShareBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_share, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onBindView(View view) {
        ChatRowShareBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
    }

    @Override
    public void onUnbindView() {

    }

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, ChatRowViewModelShare data) {
        switch (data.getChatMessage().getMsgType()) {
            case ChatMessage.MSG_TYPE_JOURNAL:
                imageView.setImageResource(R.drawable.message_share_journal);
                break;
            case ChatMessage.MSG_TYPE_ONCE_CARD: {
                switch (((ShareChatMessage) data.getChatMessage()).getCardType()) {
                    case OnceCard.CARD_TYPE_SINGLE:
                        imageView.setImageResource(R.drawable.message_share_once_card_single);
                        break;
                    case OnceCard.CARD_TYPE_MIX:
                        imageView.setImageResource(R.drawable.message_share_once_card_mix);
                        break;
                    case OnceCard.CARD_TYPE_CREDIT:
                        imageView.setImageResource(R.drawable.message_share_once_card_credit);
                        break;
                }
            }
            break;
        }
    }


    public CharSequence content() {
        return chatMessage.getContentText();
    }

    public CharSequence name() {
        return ((ShareChatMessage) chatMessage).getActName();
    }
}
