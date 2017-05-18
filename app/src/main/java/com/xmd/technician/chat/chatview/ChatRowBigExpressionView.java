package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;

/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowBigExpressionView extends ChatRowTextView {

    public ChatRowBigExpressionView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }
}
