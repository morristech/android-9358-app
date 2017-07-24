package com.xmd.chat.message;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.AccountManager;

/**
 * Created by mo on 17-7-21.
 * 骰子游戏
 */

public class DiceGameChatMessage extends TipChatMessage {

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
    public static final String STATUS_REJECT = "reject";//拒绝
    public static final String STATUS_OVERTIME = "overtime";//超时
    public static final String STATUS_OVER = "over"; //结束

    private Integer myPoint;
    private Integer remotePoint;

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
        String text = getSafeStringAttribute("gameId");
        if (!TextUtils.isEmpty(text) && text.contains("dice_")) {
            return text.substring("dice_".length(), text.length());
        }
        return null;
    }

    public void setGameId(String gameId) {
        setAttr("gameId", "dice_" + gameId);
    }

    public String getGameResult() {
        return getSafeStringAttribute("gameResult");
    }

    public void setGameResult(String gameResult) {
        setAttr("gameResult", gameResult);
    }

    public String getCredit() {
        return getOriginContentText();
    }

    public int getMyPoint() {
        if (myPoint == null) {
            parseResult();
        }
        return myPoint == null ? 0 : myPoint;
    }

    public int getRemotePoint() {
        if (remotePoint == null) {
            parseResult();
        }
        return remotePoint == null ? 0 : remotePoint;
    }

    private void parseResult() {
        try {
            String result = getGameResult();
            if (result != null && result.contains(":")) {
                String[] points = result.split(":");
                int srcPoint = Integer.parseInt(points[0]);
                int dstPoint = Integer.parseInt(points[1]);
                if (getGameInvite().equals(AccountManager.getInstance().getChatId())) {
                    //我是发起方
                    myPoint = srcPoint;
                    remotePoint = dstPoint;
                } else {
                    myPoint = dstPoint;
                    remotePoint = srcPoint;
                }
            }
        } catch (Exception e) {
            XLogger.e("error dice result: " + getGameResult());
        }
    }

    @Override
    public CharSequence getContentText() {
        return getTip();
    }

    @Override
    public CharSequence getTip() {
        String status = getGameStatus();
        switch (status) {
            case DiceGameChatMessage.STATUS_REJECT:
                if (getFromChatId().equals(getRemoteChatId())) {
                    return "游戏" + getStatusText(status) + ",返还" + getCredit() + "积分";
                }
            case DiceGameChatMessage.STATUS_CANCEL:
                if (!getFromChatId().equals(getRemoteChatId())) {
                    return "游戏" + getStatusText(status) + ",返还" + getCredit() + "积分";
                }
            default:
                return "骰子游戏：" + getStatusText(status);
        }
    }

    public static String getStatusText(String status) {
        switch (status) {
            case DiceGameChatMessage.STATUS_OVERTIME:
                return "已超时";
            case DiceGameChatMessage.STATUS_REQUEST:
                return "等待接受";
            case DiceGameChatMessage.STATUS_REJECT:
                return "已拒绝";
            case DiceGameChatMessage.STATUS_ACCEPT:
                return "已接受";
            case DiceGameChatMessage.STATUS_CANCEL:
                return "已取消";
            case DiceGameChatMessage.STATUS_OVER:
                return "已结束";
        }
        return "";
    }

    public static DiceGameChatMessage createMessage(String gameStatus, String currentChatId, String remoteChatId, String gameId, int credit) {
        return createMessage(gameStatus, currentChatId, remoteChatId, gameId, credit, 0, 0);
    }

    public static DiceGameChatMessage createMessage(String gameStatus, String currentChatId, String remoteChatId,
                                                    String gameId, int credit, int srcPoint, int dstPoint) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(String.valueOf(credit), remoteChatId);
        DiceGameChatMessage chatMessage = new DiceGameChatMessage(emMessage);
        chatMessage.setMsgType(ChatMessage.MSG_TYPE_DICE_GAME);
        chatMessage.setGameId(gameId);
        chatMessage.setGameStatus(gameStatus);
        chatMessage.setGameInvite(currentChatId);
        chatMessage.setGameResult(srcPoint + ":" + dstPoint);
        return chatMessage;
    }
}
