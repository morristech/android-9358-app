package com.xmd.chat;

/**
 * Created by mo on 17-6-30.
 */

class ConversationManager {
    private static final ConversationManager ourInstance = new ConversationManager();

    static ConversationManager getInstance() {
        return ourInstance;
    }

    private ConversationManager() {
    }
}
