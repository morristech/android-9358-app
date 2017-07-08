package com.xmd.chat;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.viewmodel.ChatRowViewModelAppointment;
import com.xmd.chat.viewmodel.ChatRowViewModelImage;
import com.xmd.chat.viewmodel.ChatRowViewModelLocation;
import com.xmd.chat.viewmodel.ChatRowViewModelOrderRequest;
import com.xmd.chat.viewmodel.ChatRowViewModelText;
import com.xmd.chat.viewmodel.ChatRowViewModelTip;

import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_IMAGE;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_LOCATION;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TEXT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TIP;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;

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
            case ChatMessage.MSG_TYPE_CLUB_LOCATION:
                baseType = CHAT_ROW_VIEW_LOCATION;
                break;
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                baseType = CHAT_ROW_VIEW_TYPE_ORDER;
                break;
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                baseType = CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;
                break;
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

    private static boolean isSendViewType(int viewType) {
        return (viewType & (0x1 << 31)) == ChatConstants.CHAT_VIEW_DIRECT_SEND;
    }

    //获取包含view id
    public static int getWrapperLayout(int viewType) {
        int baseType = viewType & ~(0x1 << 31);
        if (baseType == CHAT_ROW_VIEW_TIP) {
            return R.layout.list_item_message_tip;
        }
        return isSendViewType(viewType) ? R.layout.list_item_message_send : R.layout.list_item_message_receive;
    }

    //创建view
    public static View createView(ViewGroup parent, int viewType) {
        int baseType = viewType & ~(0x1 << 31);
        switch (baseType) {
            case CHAT_ROW_VIEW_IMAGE:
                return ChatRowViewModelImage.createView(parent);
            case CHAT_ROW_VIEW_LOCATION:
                return ChatRowViewModelLocation.createView(parent);
            case CHAT_ROW_VIEW_TIP:
                return ChatRowViewModelTip.createView(parent);
            case CHAT_ROW_VIEW_TYPE_ORDER:
                return ChatRowViewModelAppointment.createView(parent);
            default:
                return ChatRowViewModelText.createView(parent);
        }
    }

    //创建viewModel
    public static ChatRowViewModel createViewModel(ChatMessage message) {
        switch (message.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORIGIN_TXT:
                return new ChatRowViewModelText(message);
            case ChatMessage.MSG_TYPE_ORIGIN_IMAGE:
                return new ChatRowViewModelImage(message);
            case ChatMessage.MSG_TYPE_CLUB_LOCATION:
                return new ChatRowViewModelLocation(message);
            case ChatMessage.MSG_TYPE_TIP:
                return new ChatRowViewModelTip(message);
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                return new ChatRowViewModelOrderRequest(message);
            case ChatMessage.MSG_TYPE_ORDER:
                return new ChatRowViewModelAppointment(message);
            default:
                return new ChatRowViewModelText(message);
        }
    }
}
