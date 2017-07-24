package com.xmd.chat.event;

/**
 * Created by mo on 17-7-22.
 * 骰子游戏拒绝
 */

public class EventGameDiceCancel {
    private String gameId;

    public EventGameDiceCancel(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
