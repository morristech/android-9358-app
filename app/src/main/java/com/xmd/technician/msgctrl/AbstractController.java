package com.xmd.technician.msgctrl;

import android.os.Handler;
import android.os.Message;

/**
 * Created by sdcm on 15-10-22.
 */
public abstract class AbstractController implements Handler.Callback{

    private Handler mHandler;

    protected AbstractController() {
        mHandler = new Handler(this);
    }

    public void sendMessage(Message msg){
        mHandler.sendMessage(msg);
    }

    public boolean handleMessage(Message msg){
        // implemented by detail controllers
        return true;
    }
}
