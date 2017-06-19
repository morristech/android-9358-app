package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.utils.SmileUtils;
import com.xmd.technician.common.ResourceUtils;

/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowActivityView extends BaseEaseChatView {

    private TextView activityTitle;
    private TextView activityContent;
    private ImageView activityIcon;

    public ChatRowActivityView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.chat_row_received_activity_message : R.layout.chat_row_sent_activity_message, this);
    }

    @Override
    protected void onFindViewById() {
        activityIcon = (ImageView) findViewById(R.id.iv_activity_icon);
        activityTitle = (TextView) findViewById(R.id.tv_activity_title);
        activityContent = (TextView) findViewById(R.id.tv_activity_content);
    }

    protected void handleActivityMessage() {
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
        EMTextMessageBody txtBody = (EMTextMessageBody) mEMMessage.getBody();
        Spannable span = SmileUtils.getSmiledText(mContext, txtBody.getMessage());
        // 设置内容
        String subType = mEMMessage.getStringAttribute(ChatConstant.KEY_CUSTOM_TYPE, "");
        String title = mEMMessage.getStringAttribute(ChatConstant.KEY_ACTIVITY_ACT_NAME,"");
        String content = null;

        int iconRes = -1;
        switch (subType) {
            case ChatConstant.KEY_ACTIVITY_TIME_LIMIT_TYPE:
                content = ResourceUtils.getString(R.string.chat_indiana_message_des);

         //       title = ResourceUtils.getString(R.string.chat_indiana_message_type);
                iconRes = R.drawable.icon_seckill;
                break;
            case ChatConstant.KEY_ACTIVITY_ONE_YUAN_TYPE:
                content = ResourceUtils.getString(R.string.chat_seckill_message_des);
         //       title = ResourceUtils.getString(R.string.chat_seckill_message_type);
                iconRes = R.drawable.icon_indiana;
                break;
            case ChatConstant.KEY_ACTIVITY_LUCKY_WHEEL_TYPE:
                content = ResourceUtils.getString(R.string.chat_turntable_message_des);
         //       title = ResourceUtils.getString(R.string.chat_turntable_message_type);
                iconRes = R.drawable.icon_turntalbel;
                break;
            case ChatConstant.KEY_ACTIVITY_JOURNAL_TYPE:
                content = ResourceUtils.getString(R.string.chat_journal_message_des);
         //       title = ResourceUtils.getString(R.string.chat_journal_message_type);
                iconRes = R.drawable.icon_journal;
                break;
            case ChatConstant.KEY_ACTIVITY_ITEM_CARD_TYPE:
                String itemCardType = mEMMessage.getStringAttribute(ChatConstant.KEY_SUB_CARD_TYPE, "");
                if (itemCardType.equals("item_package")) {
                    content = ResourceUtils.getString(R.string.chat_package_message_des);
         //           title = ResourceUtils.getString(R.string.chat_package_message_type);
                    iconRes = R.drawable.icon_package;
                } else if (itemCardType.equals("credit_gift")) {
                    content = ResourceUtils.getString(R.string.chat_gift_message_des);
         //           title = ResourceUtils.getString(R.string.chat_gift_message_type);
                    iconRes = R.drawable.icon_credit_gift;
                } else {
                    content = ResourceUtils.getString(R.string.chat_timescard_message_des);
         //           title = ResourceUtils.getString(R.string.chat_timescard_message_type);
                    iconRes = R.drawable.icon_oncecard;
                }
                break;

            default:
                content = span.toString();
                title = null;
                break;
        }
        activityContent.setText(content);
        if (iconRes > 0) {
            activityIcon.setVisibility(VISIBLE);
            activityIcon.setImageResource(iconRes);
        } else {
            activityIcon.setVisibility(GONE);
        }

        if (title != null) {
            activityTitle.setText(title);
            activityTitle.setVisibility(VISIBLE);
        } else {
            activityTitle.setVisibility(GONE);
        }
        handleActivityMessage();
    }

    @Override
    protected void onBubbleClick() {

    }


}
