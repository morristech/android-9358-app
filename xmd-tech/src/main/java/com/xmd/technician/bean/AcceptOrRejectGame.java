package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/8/29.
 */
public class AcceptOrRejectGame extends BaseResult {
    // private void sendDiceGameMessage(String content,String gameId,String gameStatus){
    public  String gameStatus;
    public  String gameContent;
    public  String gameId;
    public  String chatId;
    public  boolean isAccept;


    public AcceptOrRejectGame(String content,String gameId,String gameStatus,String chatId){
        this.gameContent = content;
        this.gameStatus = gameStatus;
        this.gameId = gameId;
        this.chatId = chatId;
        isAccept = true;
    }
}
