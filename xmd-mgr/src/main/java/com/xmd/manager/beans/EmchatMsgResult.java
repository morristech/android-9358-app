package com.xmd.manager.beans;

import com.hyphenate.chat.EMMessage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-6-6.
 */
public class EmchatMsgResult implements Serializable {

    public List<EMMessage> list;

    public EmchatMsgResult(List<EMMessage> list) {
        this.list = list;
    }
}
