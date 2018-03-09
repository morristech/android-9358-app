package com.xmd.chat.xmdchat.messagebean;

/**
 * Created by Lhj on 18-1-26.
 */

public class TextMessageBean{


    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public String toString() {
        return "TextMessageBean{" +
                "content='" + content + '\'' +
                '}';
    }
}
