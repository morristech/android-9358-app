package com.xmd.manager.common;

/**
 * Created by Administrator on 2016/11/30.
 */

public enum ReturnVisitMenu {
    callPhone(1, "打电话"), toChat(2, "发消息"), markState(3, "标记为已回访"), markStateUn(4, "标记为未回访");
    private int type;
    private String text;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return text;
    }

    public void setName(String text) {
        this.text = text;
    }

    ReturnVisitMenu(int type, String text) {
        this.type = type;
        this.text = text;
    }
}
