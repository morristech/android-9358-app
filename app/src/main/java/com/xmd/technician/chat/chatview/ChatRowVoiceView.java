package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.chatrow.EaseChatRowVoicePlayClickListener;
import com.xmd.technician.common.Logger;

/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowVoiceView extends ChatRowFileView {

    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStatusView;

    public ChatRowVoiceView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.chat_row_received_voice : R.layout.chat_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ImageView) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);
    }

    @Override
    protected void onSetUpView() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) mEMMessage.getBody();
        int len = voiceBody.getLength();
        if (len > 0) {
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        if (EaseChatRowVoicePlayClickListener.playMsgId != null && EaseChatRowVoicePlayClickListener.playMsgId.equals(mEMMessage.getMsgId()) && EaseChatRowVoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
                voiceImageView.setImageResource(R.drawable.voice_from_icon);
            } else {
                voiceImageView.setImageResource(R.drawable.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
            voiceAnimation.start();
        } else {
            if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
                voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
            } else {
                voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
            }
        }
        if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (mEMMessage.isListened()) {
                // hide the unread icon
                readStatusView.setVisibility(View.INVISIBLE);
            } else {
                readStatusView.setVisibility(View.VISIBLE);
            }
            Logger.i("voice>>>", "it is receive msg");
            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                mProgressBar.setVisibility(View.VISIBLE);
                setMessageReceiveCallback();
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);

            }
            return;
        }
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {

        new EaseChatRowVoicePlayClickListener(mEMMessage, voiceImageView, readStatusView, mAdapter, mActivity).onClick(mBubbleLayout);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EaseChatRowVoicePlayClickListener.currentPlayListener != null && EaseChatRowVoicePlayClickListener.isPlaying) {
            EaseChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }
    }
}
