package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.R;
import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.databinding.ChatRowCreditGiftBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CreditGiftChatMessage;


/**
 * Created by mo on 17-7-1.
 * 积分礼物
 */

public class ChatRowViewModelCreditGift extends ChatRowViewModel {
    private CreditGiftChatMessage giftChatMessage;

    public ChatRowViewModelCreditGift(ChatMessage chatMessage) {
        super(chatMessage);
        giftChatMessage = (CreditGiftChatMessage) chatMessage;
    }

    public static View createView(ViewGroup parent) {
        ChatRowCreditGiftBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_credit_gift, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        ChatRowCreditGiftBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public String getGiftId() {
        return giftChatMessage.getGiftId();
    }

    public int getCredit() {
        return giftChatMessage.getGiftCredit();
    }

    public String getGiftName(){
        return giftChatMessage.getGiftName();
    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }

    @BindingAdapter("giftImage")
    public static void bindGiftImage(ImageView view, ChatRowViewModelCreditGift data) {
        String giftId = data.getGiftId();
        if (TextUtils.isEmpty(giftId)) {
            view.setImageResource(R.drawable.message_credit_gift_default);
            return;
        }
        CreditGift creditGift = ChatMessageManager.getInstance().getGift(giftId);
        if (creditGift == null) {
            view.setImageResource(R.drawable.message_credit_gift_default);
            return;
        }
        if (creditGift.gifUrl.contains("gif")) {
            Glide.with(view.getContext())
                    .load(creditGift.gifUrl)
                    .asGif()
                    .error(R.drawable.message_credit_gift_default)
                    .into(view);
        } else {
            Glide.with(view.getContext())
                    .load(creditGift.gifUrl)
                    .error(R.drawable.message_credit_gift_default)
                    .into(view);
        }
    }
}
