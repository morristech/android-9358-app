package com.xmd.black.event;

/**
 * Created by Lhj on 17-7-24.
 */

public class AddToBlackEvent {
    public boolean addSuccess;
    public String msg;
    public AddToBlackEvent(boolean addSuccess,String message){
        this.addSuccess = addSuccess;
        this.msg = message;
    }
}
