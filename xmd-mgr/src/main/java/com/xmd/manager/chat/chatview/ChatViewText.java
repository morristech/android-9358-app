package com.xmd.manager.chat.chatview;

import android.content.Context;
import android.text.Spannable;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.xmd.manager.R;
import com.xmd.manager.chat.CommonUtils;
import com.xmd.manager.chat.SmileUtils;


/**
 * Created by sdcm on 16-4-11.
 */
public class ChatViewText extends BaseChatView {

    private TextView mContent;

    public ChatViewText(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onFindViewById() {
        mContent = (TextView) findViewById(R.id.content);
        mContent.setVisibility(VISIBLE);
    }

    @Override
    protected void onSetupView() {
        Spannable span = SmileUtils.getSmiledText(context, CommonUtils.getMessageDigest(message, context));
        // 设置内容
        mContent.setText(span, TextView.BufferType.SPANNABLE);
    }
}
