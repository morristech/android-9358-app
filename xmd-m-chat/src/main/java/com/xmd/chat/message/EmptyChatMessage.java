package com.xmd.chat.message;

/**
 * Created by mo on 17-7-7.
 * 不显示的消息
 */

public class EmptyChatMessage<T> extends ChatMessage {

    public EmptyChatMessage(T message) {
        super(message);
    }


}
