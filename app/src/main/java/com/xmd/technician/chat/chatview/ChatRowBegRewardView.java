package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;

import com.xmd.technician.chat.utils.EaseCommonUtils;
import com.xmd.technician.chat.utils.SmileUtils;

/**
 * Created by Lhj on 17-4-6.
 */

public class ChatRowBegRewardView extends BaseEaseChatView {

    private TextView contentView;
    private TextView rewardTip;
    public ChatRowBegRewardView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.chat_row_received_reward_view : R.layout.chat_row_sent_beg_reward_view, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.user_reward);
        rewardTip = (TextView) findViewById(R.id.reward_tip);
    }

    protected void handleTextMessage() {
        if (mEMMessage.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (mEMMessage.status()) {
                case CREATE:
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    String errorCode = mEMMessage.getStringAttribute(ChatConstant.KEY_ERROR_CODE, ChatConstant.ERROR_SERVER_NOT_REACHABLE);
                    if(ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)){
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                    }else {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                case INPROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mStatusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            if (!mEMMessage.isAcked() && mEMMessage.getChatType() == EMMessage.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(mEMMessage.getFrom(), mEMMessage.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        if(mEMMessage.direct() == EMMessage.Direct.RECEIVE){
            Spannable span = SmileUtils.getSmiledText(mContext, EaseCommonUtils.getMessageDigest(mEMMessage,mContext));
            // 设置内容
            contentView.setText(span, TextView.BufferType.SPANNABLE);
            rewardTip.setText(String.format("%s的打赏已存入您的账户", mEMMessage.getStringAttribute(ChatConstant.KEY_NAME,"")));
        }else{
            EMTextMessageBody body = (EMTextMessageBody) mEMMessage.getBody();
            String content = body.getMessage();
            content = content.replaceAll("<i>|</i>|<span>|</span>","");
            contentView.setText(content.replaceAll("<br/>","\n"));
        }
        handleTextMessage();
    }

    @Override
    protected void onBubbleClick() {

    }



}
