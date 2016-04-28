package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;

/**
 * Created by sdcm on 16-4-12.
 */
public class ChatViewReward extends BaseChatView{

    private TextView mReward;
    private TextView mRewardTip;
    public ChatViewReward(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onInflateView() {

    }

    @Override
    protected void onFindViewById() {
        mReward = (TextView) findViewById(R.id.user_reward);
        mReward.setVisibility(VISIBLE);

        mRewardTip = (TextView) findViewById(R.id.reward_tip);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        content = content.replaceAll("<i>|</i>|<span>|</span>","");
        mReward.setText(content.replaceAll("<br/>","\n"));

        if(mRewardTip != null){
            mRewardTip.setVisibility(VISIBLE);
            mRewardTip.setText(String.format("%s的打赏已存入您的账户", message.getStringAttribute(ChatConstant.KEY_NAME,"")));
        }
    }
}
