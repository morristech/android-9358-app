package com.xmd.chat;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.DiceGameChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.viewmodel.ChatRowViewModelAppointment;
import com.xmd.chat.viewmodel.ChatRowViewModelCoupon;
import com.xmd.chat.viewmodel.ChatRowViewModelCreditGift;
import com.xmd.chat.viewmodel.ChatRowViewModelDiceGameAccept;
import com.xmd.chat.viewmodel.ChatRowViewModelDiceGameInvite;
import com.xmd.chat.viewmodel.ChatRowViewModelDiceGameResult;
import com.xmd.chat.viewmodel.ChatRowViewModelImage;
import com.xmd.chat.viewmodel.ChatRowViewModelLocation;
import com.xmd.chat.viewmodel.ChatRowViewModelNewOrder;
import com.xmd.chat.viewmodel.ChatRowViewModelOrderRequest;
import com.xmd.chat.viewmodel.ChatRowViewModelReward;
import com.xmd.chat.viewmodel.ChatRowViewModelRewardRequest;
import com.xmd.chat.viewmodel.ChatRowViewModelShare;
import com.xmd.chat.viewmodel.ChatRowViewModelText;
import com.xmd.chat.viewmodel.ChatRowViewModelTip;
import com.xmd.chat.viewmodel.ChatRowViewModelVoice;

import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_COUPON;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_CREDIT_GIFT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_DICE_GAME_ACCEPT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_DICE_GAME_INVITE;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_DICE_GAME_RESULT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_IMAGE;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_LOCATION;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_NEW_ORDER;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_REWARD;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_REWARD_REQUEST;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_SHARE;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TEXT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TIP;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_APPOINTMENT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_VOICE;

/**
 * Created by heyangya on 17-6-7.
 * 聊天视图工厂
 */

public class ChatRowViewFactory {
    //使用消息类型获取view类型
    public static int getViewType(ChatMessage chatMessage) {
        int baseType;
        switch (chatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_TIP:
                baseType = CHAT_ROW_VIEW_TIP;
                break;
            case ChatMessage.MSG_TYPE_ORIGIN_TXT:
                baseType = CHAT_ROW_VIEW_TEXT;
                break;
            case ChatMessage.MSG_TYPE_ORIGIN_IMAGE:
                baseType = CHAT_ROW_VIEW_IMAGE;
                break;
            case ChatMessage.MSG_TYPE_ORIGIN_VOICE:
                baseType = CHAT_ROW_VIEW_VOICE;
                break;
            case ChatMessage.MSG_TYPE_CLUB_LOCATION:
                baseType = CHAT_ROW_VIEW_LOCATION;
                break;
            case ChatMessage.MSG_TYPE_JOURNAL:
            case ChatMessage.MSG_TYPE_ONCE_CARD:
            case ChatMessage.MSG_TYPE_TIME_LIMIT_TYPE:
            case ChatMessage.MSG_TYPE_ONE_YUAN_TYPE:
            case ChatMessage.MSG_TYPE_LUCKY_WHEEL_TYPE:
                baseType = CHAT_ROW_VIEW_SHARE;
                break;
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                baseType = CHAT_ROW_VIEW_TYPE_APPOINTMENT;
                break;
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                baseType = CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;
                break;
            case ChatMessage.MSG_TYPE_PAID_COUPON:
            case ChatMessage.MSG_TYPE_COUPON:
                baseType = CHAT_ROW_VIEW_COUPON;
                break;
            case ChatMessage.MSG_TYPE_CREDIT_GIFT:
                baseType = CHAT_ROW_VIEW_CREDIT_GIFT;
                break;
            case ChatMessage.MSG_TYPE_NEW_ORDER:
                baseType = CHAT_ROW_VIEW_NEW_ORDER;
                break;
            case ChatMessage.MSG_TYPE_REQUEST_REWARD:
                baseType = CHAT_ROW_VIEW_REWARD_REQUEST;
                break;
            case ChatMessage.MSG_TYPE_REWARD:
                baseType = CHAT_ROW_VIEW_REWARD;
                break;
            case ChatMessage.MSG_TYPE_DICE_GAME: {
                String gameStatus = ((DiceGameChatMessage) chatMessage).getGameStatus();
                switch (gameStatus) {
                    case DiceGameChatMessage.STATUS_REQUEST:
                        if (chatMessage.isReceivedMessage()) {
                            baseType = CHAT_ROW_VIEW_DICE_GAME_ACCEPT; //收到游戏请求
                        } else {
                            baseType = CHAT_ROW_VIEW_DICE_GAME_INVITE; //发起游戏请求
                        }
                        break;
                    case DiceGameChatMessage.STATUS_OVER:
                        baseType = CHAT_ROW_VIEW_DICE_GAME_RESULT; //游戏结果
                        break;
                    case DiceGameChatMessage.STATUS_REJECT:
                    case DiceGameChatMessage.STATUS_CANCEL:
                        baseType = CHAT_ROW_VIEW_TIP;
                        break;
                    default:
                        baseType = CHAT_ROW_VIEW_TEXT;
                }
                break;
            }
            default:
                baseType = CHAT_ROW_VIEW_TEXT;
                break;
        }

        return chatMessage.getEmMessage().direct() == EMMessage.Direct.RECEIVE ? receiveType(baseType) : sendType(baseType);
    }

    public static int sendType(int baseType) {
        return baseType | ChatConstants.CHAT_VIEW_DIRECT_SEND << 31;
    }

    public static int receiveType(int baseType) {
        return baseType | ChatConstants.CHAT_VIEW_DIRECT_RECEIVE << 31;
    }

    public static int baseType(int viewType) {
        return viewType & ~(0x1 << 31);
    }

    private static boolean isSendViewType(int viewType) {
        return (viewType & (0x1 << 31)) == ChatConstants.CHAT_VIEW_DIRECT_SEND;
    }

    //获取包含view id
    public static int getWrapperLayout(int viewType) {
        int baseType = baseType(viewType);
        if (baseType == CHAT_ROW_VIEW_TIP) {
            return R.layout.list_item_message_center;
        } else if (baseType == CHAT_ROW_VIEW_DICE_GAME_RESULT) {
            return R.layout.list_item_message_center;
        }
        return isSendViewType(viewType) ? R.layout.list_item_message_send : R.layout.list_item_message_receive;
    }

    //创建view
    public static View createView(ViewGroup parent, int viewType) {
        int baseType = baseType(viewType);
        switch (baseType) {
            case CHAT_ROW_VIEW_IMAGE:
                return ChatRowViewModelImage.createView(parent);
            case CHAT_ROW_VIEW_LOCATION:
                return ChatRowViewModelLocation.createView(parent);
            case CHAT_ROW_VIEW_TIP:
                return ChatRowViewModelTip.createView(parent);
            case CHAT_ROW_VIEW_TYPE_APPOINTMENT:
                return ChatRowViewModelAppointment.createView(parent);
            case CHAT_ROW_VIEW_TYPE_ORDER_REQUEST:
                return ChatRowViewModelOrderRequest.createView(parent);
            case CHAT_ROW_VIEW_VOICE:
                return ChatRowViewModelVoice.createView(parent);
            case CHAT_ROW_VIEW_SHARE:
                return ChatRowViewModelShare.createView(parent);
            case CHAT_ROW_VIEW_COUPON:
                return ChatRowViewModelCoupon.createView(parent);
            case CHAT_ROW_VIEW_CREDIT_GIFT:
                return ChatRowViewModelCreditGift.createView(parent);
            case CHAT_ROW_VIEW_NEW_ORDER:
                return ChatRowViewModelNewOrder.createView(parent);
            case CHAT_ROW_VIEW_DICE_GAME_INVITE:
                return ChatRowViewModelDiceGameInvite.createView(parent);
            case CHAT_ROW_VIEW_DICE_GAME_ACCEPT:
                return ChatRowViewModelDiceGameAccept.createView(parent);
            case CHAT_ROW_VIEW_DICE_GAME_RESULT:
                return ChatRowViewModelDiceGameResult.createView(parent);
            case CHAT_ROW_VIEW_REWARD_REQUEST:
                return ChatRowViewModelRewardRequest.createView(parent);
            case CHAT_ROW_VIEW_REWARD:
                return ChatRowViewModelReward.createView(parent);
            default:
                return ChatRowViewModelText.createView(parent);
        }
    }

    //创建viewModel
    public static ChatRowViewModel createViewModel(ChatMessage message) {
        int viewType = getViewType(message);
        int baseType = viewType & ~(0x1 << 31);
        switch (baseType) {
            case CHAT_ROW_VIEW_IMAGE:
                return new ChatRowViewModelImage(message);
            case CHAT_ROW_VIEW_LOCATION:
                return new ChatRowViewModelLocation(message);
            case CHAT_ROW_VIEW_TIP:
                return new ChatRowViewModelTip(message);
            case CHAT_ROW_VIEW_TYPE_APPOINTMENT:
                return new ChatRowViewModelAppointment(message);
            case CHAT_ROW_VIEW_TYPE_ORDER_REQUEST:
                return new ChatRowViewModelOrderRequest(message);
            case CHAT_ROW_VIEW_VOICE:
                return new ChatRowViewModelVoice(message);
            case CHAT_ROW_VIEW_SHARE:
                return new ChatRowViewModelShare(message);
            case CHAT_ROW_VIEW_COUPON:
                return new ChatRowViewModelCoupon(message);
            case CHAT_ROW_VIEW_CREDIT_GIFT:
                return new ChatRowViewModelCreditGift(message);
            case CHAT_ROW_VIEW_NEW_ORDER:
                return new ChatRowViewModelNewOrder(message);
            case CHAT_ROW_VIEW_REWARD_REQUEST:
                return new ChatRowViewModelRewardRequest(message);
            case CHAT_ROW_VIEW_REWARD:
                return new ChatRowViewModelReward(message);
            case CHAT_ROW_VIEW_DICE_GAME_INVITE:
                return new ChatRowViewModelDiceGameInvite(message);
            case CHAT_ROW_VIEW_DICE_GAME_ACCEPT:
                return new ChatRowViewModelDiceGameAccept(message);
            case CHAT_ROW_VIEW_DICE_GAME_RESULT:
                return new ChatRowViewModelDiceGameResult(message);
            default:
                return new ChatRowViewModelText(message);
        }
    }
}
