package com.xmd.chat.xmdchat.model;

/**
 * Created by Lhj on 18-1-22.
 */

public class XmdChatModel {

    private int chatModel;
    private static XmdChatModel xmdChatModel;

    public XmdChatModel(){

    }

    public static XmdChatModel getInstance(){
        if(xmdChatModel == null){
            synchronized (XmdChatModel.class){
                if(xmdChatModel == null){
                    xmdChatModel = new XmdChatModel();
                }
            }
        }
        return xmdChatModel;
    }

    public int getChatModel() {
        return chatModel;
    }

    public void setChatModel(int chatModel) {
        this.chatModel = chatModel;
    }

    public boolean chatModelIsEm(){
        //return chatModel == XmdControlId.CHAT_MODEL_EM;
        return false;
    }
}
