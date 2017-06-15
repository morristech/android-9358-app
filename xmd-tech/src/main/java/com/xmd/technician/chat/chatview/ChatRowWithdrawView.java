package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.widget.CircleAvatarView;

/**
 * Created by Lhj on 17-5-11.
 */

public class ChatRowWithdrawView extends BaseEaseChatView {
    TextView withDrawText;
    CircleAvatarView avatar;

    public ChatRowWithdrawView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.chat_row_withdraw_view,this);
    }

    @Override
    protected void onFindViewById() {
        withDrawText = (TextView) findViewById(R.id.tv_withdraw);
        avatar = (CircleAvatarView) findViewById(R.id.avatar);
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {
        avatar.setVisibility(GONE);
        if(mEMMessage.getFrom().equals(SharedPreferenceHelper.getEmchatId())){
            withDrawText.setText("你撤回一条消息");
        }else{
            withDrawText.setText("对方撤回一条消息");
        }
    }

    @Override
    protected void onBubbleClick() {

    }
}
