package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowCouponBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CouponChatMessage;


/**
 * Created by mo on 17-7-1.
 * 优惠券消息
 */

public class ChatRowViewModelCoupon extends ChatRowViewModel {
    private CouponChatMessage couponChatMessage;

    public ChatRowViewModelCoupon(ChatMessage chatMessage) {
        super(chatMessage);
        couponChatMessage = (CouponChatMessage) chatMessage;
    }

    public static View createView(ViewGroup parent) {
        ChatRowCouponBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_coupon, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        ChatRowCouponBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public String getTypeText() {
        return couponChatMessage.getTypeText();
    }

    public String getCouponDescription() {
        return couponChatMessage.getCouponDescription();
    }

    public String getTimeLimit() {
        return couponChatMessage.getTimeLimit();
    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        if (!chatMessage.isReceivedMessage()) {
            return context.getResources().getDrawable(R.drawable.send_wrapper_coupon);
        }
        return super.getContentViewBackground(context);
    }
}
