package com.xmd.technician.chat;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.chatview.EaseChatMessageList;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.io.File;

/**
 * Created by Lhj on 17-4-1.
 */

public class ChatSentMessageHelper {

    public Context mContext;
    public String mToChatEmchatId;
    public EaseChatMessageList messageList;
    public boolean isMessageListInited;
    public boolean inUserBlacklist;


    public ChatSentMessageHelper(Context context, String toChatId, EaseChatMessageList messageList, boolean isMessageListInited) {
        this.mContext = context;
        this.mToChatEmchatId = toChatId;
        this.messageList = messageList;
        this.isMessageListInited = isMessageListInited;


    }

    public void setInUserBlackList(boolean inUserBlacklist){
        this.inUserBlacklist = inUserBlacklist;
    }


    public void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        sendMessage(message);
    }


    public void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Utils.makeShortToast(mContext, ResourceUtils.getString(R.string.cant_find_pictures));
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                //  makeShortToast(getString(R.string.cant_find_pictures));
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    public void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, mToChatEmchatId);
        sendMessage(message);
    }

    public void sendBegRewardMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "begReward");
        sendMessage(message);
    }

    public void sendCouponMessage(String content, String actId, String techCode, String userActId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "ordinaryCoupon");
        message.setAttribute(ChatConstant.KEY_ACT_ID, actId);
        message.setAttribute(ChatConstant.KEY_TECH_CODE, techCode);
        if (Utils.isNotEmpty(userActId)) {
            message.setAttribute(ChatConstant.KEY_COUPON_ACT_ID, userActId);
        }
        sendMessage(message);
    }

    public void sendOrderMessage(String content, String orderId) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "order");
        message.setAttribute(ChatConstant.KEY_ORDER_ID, orderId);
        sendMessage(message);
    }

    public void sendPaidCouponMessage(String content, String actId, String techCode) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, "paidCoupon");
        message.setAttribute(ChatConstant.KEY_ACT_ID, actId);
        message.setAttribute(ChatConstant.KEY_TECH_CODE, techCode);
        sendMessage(message);
    }

    public void sendDiceGameMessage(String content, String gameId, String gameState, String gameResult, String gameInvite, String adverseName) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_GAME_CLUB_NAME, SharedPreferenceHelper.getUserClubName());
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, ChatConstant.KEY_MSG_GAME_TYPE);
        message.setAttribute(ChatConstant.KEY_GAME_STATUS, gameState);
        message.setAttribute(ChatConstant.KEY_GAME_INVITE, gameInvite);
        message.setAttribute(ChatConstant.KEY_GAME_ID, "dice_" + gameId);
        message.setAttribute(ChatConstant.KEY_GAME_RESULT, gameResult);
        message.setAttribute(ChatConstant.KEY_ADVERSE_NAME, adverseName);
        sendMessage(message);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT);
    }

    public void sendLocationMessage(String latitude, String longitude, String locationAddress, String staticMap) {
        EMMessage message = EMMessage.createTxtSendMessage("位置&", mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, ChatConstant.KEY_CHAT_LOCATION_TYPE);
        message.setAttribute(ChatConstant.KEY_LOCATION_LAT, latitude);
        message.setAttribute(ChatConstant.KEY_LOCATION_LNG, longitude);
        message.setAttribute(ChatConstant.KEY_LOCATION_ADDRESS, locationAddress);
        message.setAttribute(ChatConstant.KEY_LOCATION_STATIC_MAP, staticMap);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, mToChatEmchatId);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, mToChatEmchatId);
        sendMessage(message);
    }

    public void resendMessage(EMMessage message) {
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageList.refresh();
    }

    public void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mToChatEmchatId);
        sendMessage(message);
    }


    public void sendCmdMessage(String messageId, Long time) {
        String action = ChatConstant.KEY_CHAT_CMD_REVOKE_ACTION;
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMessage.setAttribute(ChatConstant.KEY_MESSAGE_ID, messageId);
        cmdMessage.setAttribute(ChatConstant.KEY_TIME, time);
        cmdMessage.setAttribute(ChatConstant.KEY_REVOKE_TYPE, "revoke");
        cmdMessage.addBody(cmdBody);
        cmdMessage.setTo(mToChatEmchatId);
        EMClient.getInstance().chatManager().sendMessage(cmdMessage);
    }

    public EMMessage createInsertMessage(String userId, long time) {

        EMMessage message;
        if (userId.equals(SharedPreferenceHelper.getEmchatId())) {
            message = EMMessage.createTxtSendMessage("你撤回了一条消息", mToChatEmchatId);
        } else {
            message = EMMessage.createTxtSendMessage("对方撤回了一条消息", mToChatEmchatId);
        }
        message.setMsgTime(time);
        message.setFrom(userId);
        message.setTo(mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, ChatConstant.KEY_REVOKE_TYPE);
        return message;
    }

    public void sendActivityMessage(String actId, String subType, String templateId) {

        String content;
        String activityType = "";
        String cardType = "";
        switch (subType) {
            case ChatConstant.KEY_SUB_TYPE_INDIANA:
                content = ResourceUtils.getString(R.string.chat_indiana_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_TIME_LIMIT_TYPE;
                break;
            case ChatConstant.KEY_SUB_TYPE_SECKILL:
                content = ResourceUtils.getString(R.string.chat_seckill_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_ONE_YUAN_TYPE;
                break;
            case ChatConstant.KEY_SUB_TYPE_TURNTABLE:
                content = ResourceUtils.getString(R.string.chat_turntable_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_LUCKY_WHEEL_TYPE;
                break;
            case ChatConstant.KEY_SUB_TYPE_JOURNAL:
                content = ResourceUtils.getString(R.string.chat_journal_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_JOURNAL_TYPE;
                break;
            case ChatConstant.KEY_SUB_TYPE_TIMES_SCARD:
                content = ResourceUtils.getString(R.string.chat_timescard_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_ITEM_CARD_TYPE;
                cardType = "item_card";
                break;
            case ChatConstant.KEY_SUB_TYPE_PACKAGE:
                content = ResourceUtils.getString(R.string.chat_package_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_ITEM_CARD_TYPE;
                cardType = "item_package";
                break;
            case ChatConstant.KEY_SUB_TYPE_GIFT:
                content = ResourceUtils.getString(R.string.chat_gift_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_ITEM_CARD_TYPE;
                cardType = "credit_gift";
                break;
            default:
                content = ResourceUtils.getString(R.string.chat_timescard_message_des);
                activityType = ChatConstant.KEY_ACTIVITY_ITEM_CARD_TYPE;
                cardType = "item_card";
        }

        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatEmchatId);
        message.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, activityType);
        if (activityType.equals(ChatConstant.KEY_ACTIVITY_JOURNAL_TYPE)) {
            message.setAttribute(ChatConstant.KEY_SUB_TEMPLATE_ID, templateId);
        }
        if (activityType.equals(ChatConstant.KEY_ACTIVITY_ITEM_CARD_TYPE)) {
            message.setAttribute(ChatConstant.KEY_SUB_CARD_TYPE, cardType);
        }
        message.setAttribute(ChatConstant.KEY_ACT_ID, actId);
        sendMessage(message);
    }

    public void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        message.setAttribute(ChatConstant.KEY_CLUB_NAME, SharedPreferenceHelper.getUserClubName());
        message.setAttribute(ChatConstant.KEY_TECH_ID, SharedPreferenceHelper.getUserId());
        message.setAttribute(ChatConstant.KEY_CLUB_ID, SharedPreferenceHelper.getUserClubId());
        message.setAttribute(ChatConstant.KEY_NAME, SharedPreferenceHelper.getUserName());
        message.setAttribute(ChatConstant.KEY_HEADER, SharedPreferenceHelper.getUserAvatar());
        message.setAttribute(ChatConstant.KEY_TIME, String.valueOf(System.currentTimeMillis()));
        message.setAttribute(ChatConstant.KEY_SERIAL_NO, SharedPreferenceHelper.getSerialNo());
        message.setAttribute(ChatConstant.KEY_CURRENT_USER_ID, SharedPreferenceHelper.getUserId());
        if (inUserBlacklist) {
            message.setAttribute(ChatConstant.KEY_ERROR_CODE, ChatConstant.ERROR_IN_BLACKLIST);
        }
        //send message
        EMClient.getInstance().chatManager().sendMessage(message);
        //refresh ui
        if (isMessageListInited) {
            messageList.refreshSelectLast();
        }
    }
}
