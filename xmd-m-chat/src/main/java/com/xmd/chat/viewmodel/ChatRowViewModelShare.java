package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.xmd.chat.R;
import com.xmd.chat.beans.OnceCard;
import com.xmd.chat.databinding.ChatRowShareBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.ShareChatMessage;
import com.xmd.chat.xmdchat.constant.XmdMessageType;


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
    public ViewDataBinding onBindView(View view) {
        if (chatMessage.getMessage() instanceof TIMMessage && ((TIMMessage) chatMessage.getMessage()).status() == TIMMessageStatus.HasRevoked) {
            return null;
        }
        view.getLayoutParams().width = ScreenUtils.getScreenWidth() * 3 / 5;
        ChatRowShareBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
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
            case ChatMessage.MSG_TYPE_TIME_LIMIT_TYPE:
                imageView.setImageResource(R.drawable.message_share_time_limit);
                break;
            case ChatMessage.MSG_TYPE_ONE_YUAN_TYPE:
                imageView.setImageResource(R.drawable.message_share_one_yuan);
                break;
            case ChatMessage.MSG_TYPE_LUCKY_WHEEL_TYPE:
                imageView.setImageResource(R.drawable.message_share_luck_wheel);
                break;
            case ChatMessage.MSG_TYPE_INVITE_GIFT_TYPE:
                imageView.setImageResource(R.drawable.message_share_invite_gift);
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

    @Override
    public Drawable getContentViewBackground(Context context) {
        return chatMessage.isReceivedMessage() ?
                context.getResources().getDrawable(R.drawable.receive_wrapper)
                : context.getResources().getDrawable(R.drawable.send_wrapper_white);
    }

    public CharSequence name() {
        if (chatMessage.getMsgType().equals(XmdMessageType.INVITE_GIFT_TYPE)) {
            return "邀请有礼";
        } else {
            return ((ShareChatMessage) chatMessage).getActName();
        }

    }
}
