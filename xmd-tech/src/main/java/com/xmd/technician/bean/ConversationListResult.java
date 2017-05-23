package com.xmd.technician.bean;

import com.hyphenate.chat.EMConversation;
import com.xmd.technician.http.gson.BaseResult;

import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-5-6.
 */
public class ConversationListResult extends BaseResult {
    public List<EMConversation> respData;

    public ConversationListResult(List<EMConversation> content) {
        this.statusCode = 200;
        this.respData = content;
    }
}
