package com.xmd.chat.event;

import com.xmd.chat.message.DiceGameChatMessage;

/**
 * Created by mo on 17-7-22.
 * 骰子游戏状态更改
 */

public class EventGameDiceStatusChange {
    private DiceGameChatMessage message;

    public EventGameDiceStatusChange(DiceGameChatMessage message) {
        this.message = message;
    }

    public DiceGameChatMessage getMessage() {
        return message;
    }

    public String getGameId() {
        return message.getGameId();
    }
}
