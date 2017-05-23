package com.xmd.technician.msgctrl;

import android.os.Message;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdcm on 15-10-22.
 */
public class MsgDispatcher {

    private static SparseArray<int[]> mControllers = new SparseArray<>();

    /**
     * Register the messages to controller
     * @see ControllerRegister
     * @param controllerId
     * @param msgIds
     */
    public static void register(int controllerId, int[] msgIds){
        mControllers.put(controllerId, msgIds);
    }

    /**
     * find the controllers which this msgId registered to, @see ControllerRegister.java
     * @param msgId
     * @return
     */
    private static List<Integer> findControllerId(int msgId){
        List<Integer> controllers = new ArrayList<>();
        int len = mControllers.size();
        for(int i = 0; i < len; i++){
            int[] msgIds = mControllers.valueAt(i);
            int valLen = msgIds.length;
            for(int k = 0; k < valLen; k++){
                if(msgIds[k] == msgId){
                    controllers.add(mControllers.keyAt(i));
                }
            }
        }
        return controllers;
    }

    public static void dispatchMessage(Message msg){
        List<Integer> controllerIds = findControllerId(msg.what);
        for(Integer controllerId : controllerIds){
            AbstractController controller = ControllerFactory.createController(controllerId);
            controller.sendMessage(msg);
        }
    }

    public static void dispatchMessage(int msgId, int arg1, int arg2, Object obj){
        Message msg = Message.obtain();
        msg.what = msgId;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        dispatchMessage(msg);
    }

    public static void dispatchMessage(int msgId, Object obj){
        dispatchMessage(msgId, -1, -1, obj);
    }

    public static void dispatchMessage(int msgId){
        dispatchMessage(msgId, null);
    }
}
