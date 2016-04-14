package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;

/**
 * Created by sdcm on 16-4-12.
 */
public class ChatViewReward extends BaseChatView{

    private TextView mReward;

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

    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        mReward.setText(content.replaceAll("<i>|</i>|<span>|</span>",""));
    }
}
