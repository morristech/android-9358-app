package com.xmd.black.event;

/**
 * Created by Lhj on 17-7-24.
 */

public class InUserBlackListEvent {
    public boolean isInUserBlackList;
    public String msg;

    public InUserBlackListEvent(boolean isInUserBlackList, String message) {
        this.isInUserBlackList = isInUserBlackList;
        this.msg = message;

    }
}
