package com.xmd.chat.xmdchat.contract;

import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.chat.beans.Location;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.TipChatMessage;

/**
 * Created by Lhj on 18-1-22.
 */

public interface XmdChatMessageManagerInterface<T> {

    void init(UserInfoService userInfoService);

    //发送文本消息
    ChatMessage sendTextMessage(String remoteChatId, String text);

    //发送图片消息
    ChatMessage sendImageMessage(String remoteChatId, String filePath);

    //发送位置消息
    ChatMessage sendLocationMessage(User remoteUser, Location location);

    //发送邀请有礼
    ChatMessage sendInviteGiftMessage(String remoteChatId);

    //发送撤回命令
    void sendRevokeMessage(String remoteChatId, String msgId);

    //发送tip消息
    ChatMessage sendTipMessage(T conversation, User remoteUser, String tip);

    ChatMessage sendTipMessageWithoutUpdateUI(T conversation, User remoteUser, String tip);

    //发送tip消息
    ChatMessage sendTipMessage(TipChatMessage tipChatMessage);

    //发送空消息
    ChatMessage sendEmptyMessage(String remoteChatId,String msgId);

    /**
     * 发送优惠券消息
     *
     * @param remoteChatId
     * @param paid         是否为点钟券（需要支付）
     * @param content
     * @param actId
     * @param inviteCode
     */
    void sendCouponMessage(String remoteChatId, boolean paid, String content, String actId, String inviteCode,String typeName,String timeLimit);

    ChatMessage sendVoiceMessage(User remoteUser, String path, int length);

    ChatMessage sendMessage(ChatMessage chatMessage);

    ChatMessage sendMessage(ChatMessage chatMessage, boolean show);

    void resendMessage(ChatMessage chatMessage);

    //删除消息
    void removeMessage(ChatMessage chatMessage);

    //将消息保存到本地
    void saveMessage(final ChatMessage chatMessage);

    void setCurrentChatId(String chatId);

    int getUnReadMessageTotal();

}
