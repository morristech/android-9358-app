package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyphenate.chat.EMVoiceMessageBody;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.chat.R;
import com.xmd.chat.VoiceManager;
import com.xmd.chat.databinding.ChatRowVoiceBinding;
import com.xmd.chat.message.ChatMessage;

import java.util.Locale;


/**
 * Created by mo on 17-7-1.
 * 语音消息
 */

public class ChatRowViewModelVoice extends ChatRowViewModel {
    public ObservableBoolean loading = new ObservableBoolean();
    private int length = -1;
    private boolean playing;
    private ChatRowVoiceBinding binding;
    private AnimationDrawable receiveAnimationDrawable;
    private AnimationDrawable sendAnimationDrawable;

    public ChatRowViewModelVoice(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowVoiceBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.chat_row_voice, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public boolean isReceiveMessage() {
        return getChatMessage().isReceivedMessage();
    }

    public String voiceLength() {
        if (length == -1) {
            EMVoiceMessageBody body = (EMVoiceMessageBody) getChatMessage().getEmMessage().getBody();
            length = body.getLength();
        }
        return String.format(Locale.getDefault(), "%d\"", length);
    }

    @BindingAdapter("voiceIcon")
    public static void bindVoiceIcon(ImageView imageView, ChatRowViewModelVoice data) {
        if (data.isReceiveMessage()) {
            if (data.playing) {
                imageView.setBackgroundResource(R.drawable.message_void_receive_playing);
                data.receiveAnimationDrawable = (AnimationDrawable) imageView.getBackground();
                data.receiveAnimationDrawable.start();
            } else {
                if (data.receiveAnimationDrawable != null) {
                    data.receiveAnimationDrawable.stop();
                }
                imageView.setBackgroundResource(R.drawable.message_voice_receive);
            }
        } else {
            if (data.playing) {
                imageView.setBackgroundResource(R.drawable.message_void_send_playing);
                data.sendAnimationDrawable = (AnimationDrawable) imageView.getBackground();
                data.sendAnimationDrawable.start();
            } else {
                if (data.sendAnimationDrawable != null) {
                    data.sendAnimationDrawable.stop();
                }
                imageView.setBackgroundResource(R.drawable.message_voice_send);
            }
        }
    }

    public void playVoice(View v) {
        if (playing) {
            VoiceManager.getInstance().stopPlayVoice();
            return;
        }
        playing = true;
        binding.setData(ChatRowViewModelVoice.this);
        binding.executePendingBindings();
        EMVoiceMessageBody body = (EMVoiceMessageBody) getChatMessage().getEmMessage().getBody();
        VoiceManager.getInstance().play(body.getLocalUrl(), new Callback<Void>() {
            @Override
            public void onResponse(Void result, Throwable error) {
                playing = false;
                binding.setData(ChatRowViewModelVoice.this);
                binding.executePendingBindings();
                if (error != null) {
                    XToast.show("播放失败：" + error.getMessage());
                }
            }
        });
    }
}
