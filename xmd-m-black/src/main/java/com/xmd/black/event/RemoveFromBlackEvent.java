package com.xmd.black.event;

/**
 * Created by Lhj on 17-7-24.
 */

public class RemoveFromBlackEvent {
    public boolean removeSuccess;
    public String msg;

    public RemoveFromBlackEvent(boolean removeSuccess, String message) {
        this.removeSuccess = removeSuccess;
        this.msg = message;
    }
}
