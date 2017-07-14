package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;

/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowGiftView extends BaseEaseChatView {
    private ImageView mGifeView;
    private TextView mGiftAmount;


    public ChatRowGiftView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.chat_row_received_gift, this);
    }

    @Override
    protected void onFindViewById() {
        mGifeView = (ImageView) findViewById(R.id.credit_img);
        mGiftAmount = (TextView) findViewById(R.id.credit_gift_amount);
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
                    if (ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)) {
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                    } else {
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
        EMTextMessageBody body = (EMTextMessageBody) mEMMessage.getBody();
        String content = body.getMessage();
        try {
            String giftId = mEMMessage.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_ID, "");
            String giftUrl = SharedPreferenceHelper.getGiftImageById(giftId);

            if (giftUrl.contains("gif")) {
                Glide.with(mContext).load(giftUrl).asGif().error(R.drawable.gift_default).diskCacheStrategy(DiskCacheStrategy.RESULT).into(mGifeView);
            } else {
                Glide.with(mContext).load(giftUrl).error(R.drawable.gift_default).diskCacheStrategy(DiskCacheStrategy.RESULT).into(mGifeView);
            }

            String giftValue = mEMMessage.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_VALUE);
            mGiftAmount.setText(String.format("收到%s,获得%s积分", content.substring(4, content.length() - 1), giftValue));
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

        handleTextMessage();
    }

    @Override
    protected void onBubbleClick() {

    }


}
