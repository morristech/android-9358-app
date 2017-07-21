package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-21.
 * 骰子游戏
 */

public class DiceGameChatMessage extends ChatMessage {

    /*
    msgType : “diceGame”,//消息类型
    gameStatus : “request”,//当前游戏状态--请求
    gameInvite : userChatId,//游戏发起方的聊天id
    gameId : “dice_”+gameId,//gameId是接口返回的游戏id
    gameResult : “0:0”//游戏的结果 邀请方比被邀请方
     */
    public static final String STATUS_REQUEST = "request";//发起
    public static final String STATUS_ACCEPT = "accept";//接受
    public static final String STATUS_CANCEL = "cancel";//取消
    public static final String STATUS_REFUSED = "refused";//拒绝
    public static final String STATUS_OVERTIME = "overtime";//超时
    public static final String STATUS_OVER = "over"; //结束

    public DiceGameChatMessage(EMMessage emMessage) {
        super(emMessage);
    }

    public String getGameStatus() {
        return getSafeStringAttribute("gameStatus");
    }

    public void setGameStatus(String gameStatus) {
        setAttr("gameStatus", gameStatus);
    }

    public String getGameInvite() {
        return getSafeStringAttribute("gameInvite");
    }

    public void setGameInvite(String gameInvite) {
        setAttr("gameInvite", gameInvite);
    }

    public String getGameId() {
        return getSafeStringAttribute("gameId");
    }

    public void setGameId(String gameId) {
        setAttr("gameId", gameId);
    }

    public String getGameResult() {
        return getSafeStringAttribute("gameResult");
    }

    public void setGameResult(String gameResult) {
        setAttr("gameId", gameResult);
    }
}
