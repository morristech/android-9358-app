package com.xmd.technician.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.window.ChatActivity;

/**
 * Created by sdcm on 16-4-14.
 */
public class ChatController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MsgDef.MSG_DEF_START_CHAT:
                doStartChatActivity((String)msg.obj);
                break;
        }

        return true;
    }

    /**
     * start the ChatActivity
     * @param emchatId
     */
    private void doStartChatActivity(String emchatId) {
        Activity activity = ActivityHelper.getInstance().getCurrentActivity();
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatConstant.EXTRA_USER_ID, emchatId);
        activity.startActivity(intent);
    }
}
