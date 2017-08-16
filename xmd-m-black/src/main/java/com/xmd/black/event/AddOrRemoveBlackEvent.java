package com.xmd.black.event;

/**
 * Created by Lhj on 17-7-24.
 */

public class AddOrRemoveBlackEvent {
    public boolean success;
    public boolean isAdd;
    public String msg;

    public AddOrRemoveBlackEvent(boolean success, boolean isAdd, String message) {
        this.success = success;
        this.isAdd = isAdd;
        this.msg = message;
    }
}
