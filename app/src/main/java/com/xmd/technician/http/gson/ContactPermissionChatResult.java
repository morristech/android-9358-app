package com.xmd.technician.http.gson;

import com.hyphenate.chat.EMConversation;

/**
 * Created by zr on 17-3-28.
 */

public class ContactPermissionChatResult extends ContactPermissionResult {
    public EMConversation emConversation;

    public ContactPermissionChatResult(ContactPermissionResult result) {
        this.statusCode = result.statusCode;
        this.msg = result.msg;
        this.respData = result.respData;
        this.pageCount = result.pageCount;
    }
}
