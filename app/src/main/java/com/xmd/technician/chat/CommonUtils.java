package com.xmd.technician.chat;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

/**
 * Created by sdcm on 16-3-22.
 */
public class CommonUtils {
    /**
     * 将应用的会话类型转化为SDK的会话类型
     *
     * @param chatType
     * @return
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == ChatConstant.CHATTYPE_SINGLE) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == ChatConstant.CHATTYPE_GROUP) {
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
                if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    if (!TextUtils.isEmpty(txtBody.getMessage())) {

                        digest = txtBody.getMessage();
                    } else {
                        digest = getString(context, R.string.dynamic_expression);
                    }
                }else try {
                    if(Utils.isNotEmpty(message.getStringAttribute(ChatConstant.KEY_GAME_ID))){
                     digest = ResourceUtils.getString(R.string.dice_game);
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    digest = txtBody.getMessage();
                    digest = Html.fromHtml(digest).toString();
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
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_REWARD : ChatConstant.MESSAGE_TYPE_SENT_REWARD);
                } else if (msgType.equals("order")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_ORDER : ChatConstant.MESSAGE_TYPE_SENT_ORDER);
                } else if (msgType.equals("paidCouponTip")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_PAID_COUPON_TIP : ChatConstant.MESSAGE_TYPE_SENT_PAID_COUPON_TIP);
                } else if (msgType.equals("begReward")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_BEG_REWARD : ChatConstant.MESSAGE_TYPE_SENT_BEG_REWARD);
                } else if (msgType.equals("paidCoupon")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_PAID_COUPON : ChatConstant.MESSAGE_TYPE_SENT_PAID_COUPON);
                } else if (msgType.equals("ordinaryCoupon")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_ORDINARY_COUPON : ChatConstant.MESSAGE_TYPE_SENT_ORDINARY_COUPON);
                } else if (msgType.equals("couponTip")) {
                    type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_COUPON_TIP : ChatConstant.MESSAGE_TYPE_SENT_COUPON_TIP);
                } else if (msgType.equals("diceGame")) {
                    String gameStatus = message.getStringAttribute(ChatConstant.KEY_GAME_STATUS);
                    if (gameStatus.equals(ChatConstant.KEY_REQUEST_GAME)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_GAME_INVITE : ChatConstant.MESSAGE_TYPE_SEND_GAME_INVITE);
                    } else if (gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_GAME_INVITE : ChatConstant.MESSAGE_TYPE_SEND_GAME_INVITE);
                    } else if (gameStatus.equals(ChatConstant.KEY_ACCEPT_GAME)) {
                        type = ChatConstant.MESSAGE_TYPE_SEND_GAME_REJECT;
                    } else if (gameStatus.equals(ChatConstant.KEY_GAME_REJECT) || gameStatus.equals(ChatConstant.KEY_REFUSED_GAME)) {
                        type = ChatConstant.MESSAGE_TYPE_SEND_GAME_REJECT;
                    } else if (gameStatus.equals(ChatConstant.KEY_OVERTIME_GAME)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_GAME_OVERTIME : ChatConstant.MESSAGE_TYPE_SEND_GAME_OVERTIME);
                    } else if (gameStatus.equals(ChatConstant.KEY_OVER_GAME_TYPE)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_TYPE_RECV_GAME_OVER : ChatConstant.MESSAGE_TYPE_SEND_GAME_OVER);
                    }
                }else if(msgType.equals("gift")){
              //    String giftValue = message.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_VALUE);
              //      String giftName = message.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_NAME)
              //    String giftId = message.getStringAttribute(ChatConstant.KEY_CREDIT_GIFT_ID);
                    type = ChatConstant.MESSAGE_TYPE_RECV_CREDIT_GIFT;
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

}
