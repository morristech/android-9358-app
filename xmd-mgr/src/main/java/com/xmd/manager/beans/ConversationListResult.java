package com.xmd.manager.beans;

import com.hyphenate.chat.EMConversation;
import com.xmd.manager.service.response.BaseListResult;

import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class ConversationListResult extends BaseListResult<EMConversation> {

    public ConversationListResult(List<EMConversation> content) {
        this.statusCode = 200;
        this.respData = content;
    }
}
