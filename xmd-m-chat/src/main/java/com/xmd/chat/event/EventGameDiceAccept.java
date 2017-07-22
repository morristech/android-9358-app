package com.xmd.chat.event;

/**
 * Created by mo on 17-7-22.
 * 骰子游戏接受
 */

public class EventGameDiceAccept {
    private String gameId;

    public EventGameDiceAccept(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
