package com.xmd.manager.chat;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 16-3-22.
 */
public class CommonUtils {

    /**
     * @param emchatId
     * @param nickname
     * @param avatar
     * @return
     */
    public static Map<String, Object> wrapChatParams(String emchatId, String nickname, String avatar, Object object, String userType) {
        Map<String, Object> params = new HashMap<>();
        params.put(EmchatConstant.EMCHAT_ID, emchatId);
        params.put(EmchatConstant.EMCHAT_NICKNAME, nickname);
        params.put(EmchatConstant.EMCHAT_AVATAR, avatar);
        params.put(EmchatConstant.EMCHAT_OBJECT, object);
        params.put(EmchatConstant.MESSAGE_CHAT_USER_TYPE, userType);
        return params;
    }

    public static void userGetCoupon(String actId, String channel, String emchatId, EMMessage emMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_COUPON_ACT_ID, actId);
        params.put(RequestConstant.KEY_USER_COUPON_CHANEL, channel);
        params.put(RequestConstant.KEY_USER_COUPON_EMCHAT_ID, emchatId);
        params.put(RequestConstant.KEY_USER_COUPON_EMCHAT_MESSAGE, emMessage);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_USER_GET_COUPON, params);
    }

    /**
     * 将应用的会话类型转化为SDK的会话类型
     *
     * @param chatType
     * @return
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == EmchatConstant.CHATTYPE_SINGLE) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == EmchatConstant.CHATTYPE_GROUP) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getString(context, R.string.voice_prefix);
                break;
            case VIDEO: // 视频消息
                digest = getString(context, R.string.video);
                break;
            case TXT: // 文本消息
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            /*if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).isRobotMenuMessage(message)){
                digest = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getRobotMenuMessageDigest(message);
            }else */
                if (message.getBooleanAttribute(EmchatConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(EmchatConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(EmchatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    if (!TextUtils.isEmpty(txtBody.getMessage())) {

                        digest = txtBody.getMessage();
                    } else {
                        digest = getString(context, R.string.dynamic_expression);
                    }
                } /*else if (!TextUtils.isEmpty(message.getStringAttribute(EmchatConstant.KEY_CUSTOM_TYPE, ""))) {
                    digest = txtBody.getMessage().replaceAll("<b>|</b>|</br>|<br>|<i>|</i>|<span>|</span>|<br/>", "");
                }*/ else {
                    digest = Html.fromHtml(txtBody.getMessage()).toString();
                }
                break;
            case FILE: //普通文件消息
                digest = getString(context, R.string.file);
                break;
            default:
                return "";
        }

        return digest;
    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

    public static int getCustomChatType(EMMessage message) {
        int type = 0;
        if (message.getType() == EMMessage.Type.TXT) {
            try {
                String msgType = message.getStringAttribute("msgType");
                if (msgType.equals("reward")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? EmchatConstant.MESSAGE_TYPE_RECV_REWARD : EmchatConstant.MESSAGE_TYPE_SENT_REWARD);
                } else if (msgType.equals("order")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? EmchatConstant.MESSAGE_TYPE_RECV_ORDER : EmchatConstant.MESSAGE_TYPE_SENT_ORDER);
                } else if (msgType.equals("paidCouponTip")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? EmchatConstant.MESSAGE_TYPE_RECV_PAID_COUPON_TIP : EmchatConstant.MESSAGE_TYPE_SENT_PAID_COUPON_TIP);
                } else if (msgType.equals("begReward")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? EmchatConstant.MESSAGE_TYPE_RECV_BEG_REWARD : EmchatConstant.MESSAGE_TYPE_SENT_BEG_REWARD);
                } else if (msgType.equals("paidCoupon")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? EmchatConstant.MESSAGE_TYPE_RECV_PAID_COUPON : EmchatConstant.MESSAGE_TYPE_SENT_PAID_COUPON);
                } else if (msgType.equals("ordinaryCoupon")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? EmchatConstant.MESSAGE_TYPE_RECV_ORDINARY_COUPON : EmchatConstant.MESSAGE_TYPE_SENT_ORDINARY_COUPON);
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        return type;
    }


    public static int verifyInfoTypeToViewType(int infoType) {
        switch (infoType) {
            case CheckInfo.INFO_TYPE_TICKET:
                return Constant.VERIFICATION_VIEW_COUPON;
            case CheckInfo.INFO_TYPE_ORDER:
                return Constant.VERIFICATION_VIEW_ORDER;
            default:
                return Constant.VERIFICATION_VIEW_COMMON;
        }
    }
}

