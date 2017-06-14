package com.xmd.technician.chat.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.TextUtils;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.HanziToPinyin;
import com.hyphenate.util.PathUtil;
import com.xmd.chat.ChatConstants;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.chatview.BaseEaseChatView;
import com.xmd.technician.chat.chatview.ChatRowActivityView;
import com.xmd.technician.chat.chatview.ChatRowAppointmentRequestView;
import com.xmd.technician.chat.chatview.ChatRowAppointmentView;
import com.xmd.technician.chat.chatview.ChatRowBegRewardView;
import com.xmd.technician.chat.chatview.ChatRowCouponView;
import com.xmd.technician.chat.chatview.ChatRowGameReceivedAcceptOrRefusedView;
import com.xmd.technician.chat.chatview.ChatRowGameReceivedView;
import com.xmd.technician.chat.chatview.ChatRowGameSentInviteView;
import com.xmd.technician.chat.chatview.ChatRowGameSentResultView;
import com.xmd.technician.chat.chatview.ChatRowGiftView;
import com.xmd.technician.chat.chatview.ChatRowLocationView;
import com.xmd.technician.chat.chatview.ChatRowOrderView;
import com.xmd.technician.chat.chatview.ChatRowPaidCouponView;
import com.xmd.technician.chat.chatview.ChatRowWithdrawView;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-3-30.
 */

public class EaseCommonUtils {

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * change the chat type to EMConversationType
     *
     * @param chatType
     * @return
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == ChatConstant.CHAT_TYPE_SINGLE) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == ChatConstant.CHAT_TYPE_GROUP) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode) {
        EMMessage message = EMMessage.createTxtSendMessage("[" + expressioName + "]", toChatUsername);
        if (identityCode != null) {
            message.setAttribute(ChatConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
        }
        message.setAttribute(ChatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
    }

    public static void userGetCoupon(String content, String actId, String channel, String emchatId) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_COUPON_CONTENT, content);
        params.put(RequestConstant.KEY_USER_COUPON_ACT_ID, actId);
        params.put(RequestConstant.KEY_USER_COUPON_CHANEL, channel);
        params.put(RequestConstant.KEY_USER_COUPON_EMCHAT_ID, emchatId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_USER_GET_COUPON, params);
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
                digest = getString(context, R.string.location_message);
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
                if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(ChatConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    if (Utils.isNotEmpty(txtBody.getMessage())) {
                        digest = txtBody.getMessage();
                    } else {
                        digest = getString(context, R.string.recent_status_expression);
                    }
                } else {
                    if (Utils.isNotEmpty(message.getStringAttribute(ChatConstant.KEY_GAME_ID, ""))) {
                        digest = ResourceUtils.getString(R.string.dice_game);
                    } else {
                        digest = txtBody.getMessage();
                        digest = Html.fromHtml(digest).toString();
                    }
                }
                if (digest.contains("点钟券&")) {
                    digest = "购买了" + digest.substring(0, digest.indexOf("&"));
                }
                if (digest.contains("位置&")) {
                    digest = "[位置信息]";
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

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * get top activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param user
     */
    public static void setUserInitialLetter(ChatUser user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter {
            String getLetter(String name) {
                if (TextUtils.isEmpty(name)) {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0)) {
                    return DefaultLetter;
                }
                ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
                    HanziToPinyin.Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z') {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if (!TextUtils.isEmpty(user.getNick())) {
            letter = new GetInitialLetter().getLetter(user.getNick());
            user.setInitialLetter(letter);
            return;
        }
        if (letter.equals(DefaultLetter) && !TextUtils.isEmpty(user.getUsername())) {
            letter = new GetInitialLetter().getLetter(user.getUsername());
        }
        user.setInitialLetter(letter);
    }


    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     *
     * @param message return
     *                <p>
     *                \~english
     *                check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     * @param message
     * @return
     */
    public static boolean isSilentMessage(EMMessage message) {
        return message.getBooleanAttribute("em_ignore_notification", false);
    }

    public static int getCustomChatType(EMMessage message) {
        ChatMessage chatMessage = ChatMessageFactory.get(message);
        int viewType = ChatRowViewFactory.getViewType(chatMessage);
        if (viewType != ChatRowViewFactory.sendType(ChatConstants.CHAT_ROW_VIEW_DEFAULT)
                && viewType != ChatRowViewFactory.receiveType(ChatConstants.CHAT_ROW_VIEW_DEFAULT)) {
            return viewType;
        }
        int type = 0;
        if (message.getType() == EMMessage.Type.TXT) {
            String msgType = message.getStringAttribute("msgType", "");
            if (Utils.isNotEmpty(msgType)) {
                if (msgType.equals("reward")) {
                    type = ChatConstant.MESSAGE_RECEIVE_REWARD_TYPE;
                } else if (msgType.equals("begReward")) {
                    type = ChatConstant.MESSAGE_SENT_REWARD_TYPE;
                } else if (msgType.equals("order")) {
                    type = message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_ORDER_TYPE : ChatConstant.MESSAGE_SENT_ORDER_TYPE;
                } else if (msgType.equals("paidCouponTip")) {
                    type = ChatConstant.MESSAGE_RECEIVE_PAID_COUPON_TYPE;
                } else if (msgType.equals("paidCoupon")) {
                    type = ChatConstant.MESSAGE_SENT_PAID_PAID_COUPON_TYPE;
                } else if (msgType.equals("ordinaryCoupon")) {
                    type = ChatConstant.MESSAGE_SENT_COUPON_TYPE;
                } else if (msgType.equals("couponTip")) {
                    type = ChatConstant.MESSAGE_RECEIVE_COUPON_TYPE;
                } else if (msgType.equals("diceGame")) {
                    String gameStatus = message.getStringAttribute(ChatConstant.KEY_GAME_STATUS, "");
                    if (gameStatus.equals(ChatConstant.KEY_REQUEST_GAME)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_GAME_REQUEST_TYPE : ChatConstant.MESSAGE_SENT_GAME_REQUEST_TYPE);
                    } else if (gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_GAME_CANCEL_TYPE : ChatConstant.MESSAGE_SENT_GAME_CANCEL_TYPE);
                    } else if (gameStatus.equals(ChatConstant.KEY_ACCEPT_GAME)) {
                        type = message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_GAME_ACCEPT_TYPE : ChatConstant.MESSAGE_SENT_GAME_ACCEPT_TYPE;
                    } else if (gameStatus.equals(ChatConstant.KEY_REFUSED_GAME) || gameStatus.equals(ChatConstant.KEY_GAME_REJECT)) {
                        type = message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_GAME_REFUSED_TYPE : ChatConstant.MESSAGE_SENT_GAME_REFUSED_TYPE;
                    } else if (gameStatus.equals(ChatConstant.KEY_OVERTIME_GAME)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_GAME_OVER_TIME_TYPE : ChatConstant.MESSAGE_SENT_GAME_OVER_TIME_TYPE);
                    } else if (gameStatus.equals(ChatConstant.KEY_OVER_GAME_TYPE)) {
                        type = (message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_GAME_OVER_TYPE : ChatConstant.MESSAGE_SENT_GAME_OVER_TYPE);
                    }
                } else if (msgType.equals("gift")) {
                    type = ChatConstant.MESSAGE_CREDIT_GIFT_TYPE;
                } else if (msgType.equals("clubLocation")) {
                    type = message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_LOCATION_TYPE : ChatConstant.MESSAGE_SENT_LOCATION_TYPE;
                } else if (msgType.equals("mark")) {
                    type = ChatConstant.MESSAGE_SENT_REVOKE_MESSAGE_TYPE;
                } else {
                    type = message.direct() == EMMessage.Direct.RECEIVE ? ChatConstant.MESSAGE_RECEIVE_ACTIVITY_TYPE : ChatConstant.MESSAGE_SENT_ACTIVITY_TYPE;
                }
            }
        }
        return type;
    }

    public static BaseEaseChatView getCustomChatView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        BaseEaseChatView chatRow = null;
        ChatMessage chatMessage = ChatMessageFactory.get(message);
        switch (chatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return new ChatRowAppointmentView(context, chatMessage.getEmMessage(), position, adapter);
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                return new ChatRowAppointmentRequestView(context, chatMessage.getEmMessage(), position, adapter);
            default:
                break;
        }
        String messageCustomType = message.getStringAttribute(ChatConstant.KEY_CUSTOM_TYPE, "");
        if (Utils.isNotEmpty(messageCustomType)) {
            if (messageCustomType.equals(ChatConstant.KEY_CHAT_RECEIVE_REWARD) || messageCustomType.equals(ChatConstant.KEY_CHAT_SENT_REWARD_TYPE)) {
                chatRow = new ChatRowBegRewardView(context, message, position, adapter);
            } else if (messageCustomType.equals(ChatConstant.KEY_CHAT_SENT_COUPON_TYPE) || messageCustomType.equals(ChatConstant.KEY_CHAT_SENT_COUPON_TYPE)) {
                chatRow = new ChatRowCouponView(context, message, position, adapter);
            } else if (messageCustomType.equals(ChatConstant.KEY_CHAT_RECEIVE_PAID_COUPON_TYPE) || messageCustomType.equals(ChatConstant.KEY_CHAT_SENT_PAID_COUPON_TYPE)) {
                chatRow = new ChatRowPaidCouponView(context, message, position, adapter);
            } else if (messageCustomType.equals(ChatConstant.KEY_CHAT_RECEIVE_ORDER_TYPE)) {
                chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowOrderView(context, message, position, adapter) : new ChatRowOrderView(context, message, position, adapter);
            } else if (messageCustomType.equals(ChatConstant.KEY_CHAT_DICE_GAME)) {
                String gameStatus = message.getStringAttribute(ChatConstant.KEY_GAME_STATUS, "");
                if (Utils.isNotEmpty(gameStatus)) {
                    if (gameStatus.equals(ChatConstant.KEY_REQUEST_GAME_STATUS)) {
                        //发起游戏邀请
                        chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowGameReceivedView(context, message, position, adapter, ChatConstant.KEY_REQUEST_GAME_STATUS) : new ChatRowGameSentInviteView(context, message, position, adapter, ChatConstant.KEY_REQUEST_GAME_STATUS);
                    } else if (gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_STATUS)) {
                        //取消游戏邀请
                        chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_CANCEL_GAME_STATUS) : new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_CANCEL_GAME_STATUS);
                    } else if (gameStatus.equals(ChatConstant.KEY_REFUSED_GAME_STATUS) || gameStatus.equals(ChatConstant.KEY_GAME_REJECT)) {
                        //拒绝游戏邀请
                        chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_REFUSED_GAME_STATUS) : new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_REFUSED_GAME_STATUS);
                    } else if (gameStatus.equals(ChatConstant.KEY_ACCEPT_GAME_STATUS)) {
                        //接受游戏邀请
                        chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_ACCEPT_GAME_STATUS) : new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_ACCEPT_GAME_STATUS);
                    } else if (gameStatus.equals(ChatConstant.KEY_OVER_TIME_GAME_STATUS)) {
                        //发送游戏超时
                        chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_OVER_TIME_GAME_STATUS) : new ChatRowGameReceivedAcceptOrRefusedView(context, message, position, adapter, ChatConstant.KEY_OVER_TIME_GAME_STATUS);
                    } else if (gameStatus.equals(ChatConstant.KEY_OVER_GAME_STATUS)) {
                        //发送游戏结果
                        chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowGameSentResultView(context, message, position, adapter, ChatConstant.KEY_OVER_GAME_STATUS) : new ChatRowGameSentResultView(context, message, position, adapter, ChatConstant.KEY_OVER_GAME_STATUS);
                    }
                }
            } else if (messageCustomType.equals(ChatConstant.KEY_CHAT_GIFT_TYPE)) {
                chatRow = new ChatRowGiftView(context, message, position, adapter);
            } else if (messageCustomType.equals(ChatConstant.KEY_CHAT_LOCATION_TYPE)) {
                chatRow = message.direct() == EMMessage.Direct.RECEIVE ? new ChatRowLocationView(context, message, position, adapter) : new ChatRowLocationView(context, message, position, adapter);
            } else if (messageCustomType.equals(ChatConstant.KEY_REVOKE_TYPE)) {
                chatRow = new ChatRowWithdrawView(context, message, position, adapter);
            } else {
                chatRow = new ChatRowActivityView(context, message, position, adapter);
            }
        } else {
            Logger.e("9358", "未获取的类型");
        }
        return chatRow;
    }

    public static String getMessageStringAttribute(EMMessage message, String key) {
        String value = null;
        try {
            value = message.getStringAttribute(key);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return value;
    }

}



