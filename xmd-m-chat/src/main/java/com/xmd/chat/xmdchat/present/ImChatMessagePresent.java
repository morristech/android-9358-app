package com.xmd.chat.xmdchat.present;

import android.text.TextUtils;

import com.shidou.commonlibrary.util.DateUtils;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.chat.R;
import com.xmd.chat.xmdchat.ImMessageParseManager;
import com.xmd.chat.xmdchat.constant.XmdChatConstant;
import com.xmd.chat.xmdchat.contract.XmdChatMessageInterface;

import java.util.Calendar;

/**
 * Created by Lhj on 18-1-22.
 */

public class ImChatMessagePresent implements XmdChatMessageInterface<TIMMessage> {

    private TIMMessage mMessage;
    protected String formatTime; //缓存格式化后的时间
    protected String relativeTime;
    //   private Spannable contentText; //缓存emoji格式化后的数据

    public ImChatMessagePresent(TIMMessage message) {
        mMessage = (TIMMessage) message;
    }


    @Override
    public String getMsgType() {
        return ImMessageParseManager.getInstance().getMessageType(mMessage);
    }

    @Override
    public long getMsgTime() {
        return mMessage.timestamp() * 1000;
    }

    @Override
    public void setMsgType(String msgType) {

    }

    @Override
    public String getMsgType(TIMMessage message) {
        return null;
    }

    @Override
    public String getToChatId() {
        return null;
    }

    @Override
    public String getFromChatId() {
        return mMessage.getSender();
    }

    @Override
    public void setUserId(String userId) {

    }

    @Override
    public String getUserRoles() {
        return null;
    }

    @Override
    public void setUserRoles(String userRoles) {

    }

    @Override
    public String getUserName() {
        User user = UserInfoServiceImpl.getInstance().getUserByChatId(mMessage.getSender());
        if (user == null) {
            return "用户";
        }
        return TextUtils.isEmpty(user.getName()) ? user.getName() : "用户";
    }

    @Override
    public void setUserName(String userName) {

    }

    @Override
    public String getUserAvatar() {
        return UserInfoServiceImpl.getInstance().getCurrentUser().getAvatar();
    }

    @Override
    public void setUserAvatar(String userAvatar) {

    }

    @Override
    public String getUserAvatarId() {
        return null;
    }

    @Override
    public void setUserAvatarId(String userAvatarId) {

    }

    @Override
    public String getTime() {
        return DateUtil.getDate(mMessage.timestamp() * 1000);
    }

    @Override
    public void setTime(String time) {

    }

    @Override
    public String getTechId() {
        return null;
    }

    @Override
    public void setTechId(String techId) {

    }

    @Override
    public String getTechNo() {
        return null;
    }

    @Override
    public void setTechNo(String techNo) {

    }

    @Override
    public String getClubId() {
        return null;
    }

    @Override
    public void setClubId(String clubId) {

    }

    @Override
    public String getClubName() {
        return null;
    }

    @Override
    public void setClubName(String clubName) {

    }

    @Override
    public void addTag(String tag) {

    }

    @Override
    public void clearTag() {

    }

    @Override
    public String getUserId() {
        return ImMessageParseManager.getIndexByMessage(mMessage, XmdChatConstant.BASE_INDEX_USER_ID);
    }

    @Override
    public CharSequence getContentText() {
        TIMCustomElem elem;
        if (mMessage.status() == TIMMessageStatus.HasRevoked) {
            return ResourceUtils.getString(R.string.has_revoke_message);
        }
        if (mMessage.getElement(0) instanceof TIMCustomElem) {
            elem = (TIMCustomElem) mMessage.getElement(0);
        } else {
            elem = (TIMCustomElem) mMessage.getElement(1);
        }
        return ImMessageParseManager.getInstance().getContentParse(elem.getData());
    }

    @Override
    public CharSequence getLastMessageContent() {
        TIMCustomElem customElem;
        if(mMessage.status() == TIMMessageStatus.HasRevoked){
            return ResourceUtils.getString(R.string.has_revoke_message);
        }
        if(mMessage.getElement(0) instanceof TIMCustomElem){
            customElem = (TIMCustomElem) mMessage.getElement(0);
        }else {
            customElem = (TIMCustomElem) mMessage.getElement(1);
        }
        return ImMessageParseManager.getInstance().getLastMessageContentParse(customElem.getData());
    }

    @Override
    public String getOriginContentText() {
        return ImMessageParseManager.getContent(mMessage, null).toString();
    }

    @Override
    public boolean isCustomerService() {
        return false;
    }

    @Override
    public String getRemoteChatId() {
        return mMessage.getSender();
    }

    @Override
    public String getFormatTime() {
        if (formatTime == null) {
            long msgTime = mMessage.timestamp() * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTime);
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)){
                formatTime = DateUtils.longToDate(msgTime);
            }else if(now.get(Calendar.DAY_OF_YEAR) - 2 >= calendar.get(Calendar.DAY_OF_YEAR)){
                formatTime = DateUtil.longToDate(msgTime).substring(5);
            }else if (now.get(Calendar.DAY_OF_YEAR) - 1 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                formatTime = DateUtils.doLong2String(msgTime, "昨天 HH:mm");
            } else {
                formatTime = DateUtils.doLong2String(msgTime, "HH:mm");
            }

        }
        return formatTime;
    }

    @Override
    public String getChatRelativeTime() {
        if (relativeTime == null) {
            long msgTime = mMessage.timestamp() * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTime);
            int relativeHour = calendar.get(Calendar.HOUR_OF_DAY);
            Calendar now = Calendar.getInstance();
            String timeDistinguish = "";
            if (0 <= relativeHour && relativeHour < 6) {
                timeDistinguish = "凌晨";
            } else if (6 <= relativeHour && relativeHour < 12) {
                timeDistinguish = "早上";
            } else if (12 <= relativeHour && relativeHour < 13) {
                timeDistinguish = "中午";
            } else if (12 <= relativeHour && relativeHour < 18) {
                timeDistinguish = "下午";
            } else if (18 <= relativeHour && relativeHour < 24) {
                timeDistinguish = "晚上";
            }
            if (now.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)
                    || now.get(Calendar.DAY_OF_YEAR) - 2 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                relativeTime = DateUtils.doLong2RelativeString(msgTime);
            } else if (now.get(Calendar.DAY_OF_YEAR) - 1 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                relativeTime = DateUtils.doLong2String(msgTime, "昨天 " + timeDistinguish + "hh:mm");
            } else {
                relativeTime = DateUtils.doLong2String(msgTime, timeDistinguish + "hh:mm");
            }

        }
        return relativeTime;
    }

    @Override
    public String getSafeStringAttribute(String key) {
        return TextUtils.isEmpty(ImMessageParseManager.getContent(mMessage, key)) ? "" : ImMessageParseManager.getContent(mMessage, key).toString();
    }

    @Override
    public Integer getSafeIntegerAttribute(String key) {
        return Integer.parseInt(ImMessageParseManager.getContent(mMessage, key).toString());
    }

    @Override
    public Long getSafeLongAttribute(String key) {
        return Long.parseLong(ImMessageParseManager.getInstance().parseToAppointmentInfo(mMessage, key));
    }

    @Override
    public void setInnerProcessed(String processedDesc) {
        TIMMessageExt ext = new TIMMessageExt(mMessage);
        ext.setCustomStr(processedDesc);
    }

    @Override
    public String getAttrType() {
        return null;
    }

    @Override
    public void setAttr(String key, String value) {

    }

    @Override
    public void setAttr(String key, Integer value) {

    }

    @Override
    public void setAttr(String key, Boolean value) {

    }

    @Override
    public void setAttr(String key, Long value) {

    }

    @Override
    public long getMessageTime() {
        return mMessage.timestamp() * 1000;
    }

    @Override
    public boolean isReceivedMessage() {
        return !mMessage.isSelf();
    }


}
