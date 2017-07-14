package com.xmd.technician.chat.event;

import com.hyphenate.chat.EMMessage;

/**
 * Created by Lhj on 2016/8/31.
 */
public class CancelGame {
    public EMMessage message;

    public CancelGame(EMMessage message) {
        this.message = message;
    }
}
