package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;

/**
 * Created by sdcm on 16-4-12.
 */
public class ChatViewReward extends BaseChatView{

    private TextView mRewardTip;
    public ChatViewReward(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onInflateView() {
        LayoutInflater.from(context).inflate(
                R.layout.chat_received_reward , this);
    }

    @Override
    protected void onFindViewById() {
        mRewardTip = (TextView) findViewById(R.id.reward_tip);
    }

    @Override
    protected void onSetUpView() {
        userAvatarView.setVisibility(GONE);
        timeStampView.setVisibility(GONE);
        mRewardTip.setText(String.format("%s的打赏已存入您的账户", message.getStringAttribute(ChatConstant.KEY_NAME,"")));
    }
}
