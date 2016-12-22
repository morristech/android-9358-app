package com.xmd.technician.model;

import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyangya on 16-12-22.
 */
public class TechManager {
    private static TechManager ourInstance = new TechManager();

    public static TechManager getInstance() {
        return ourInstance;
    }

    private TechManager() {
    }


    //获取未使用的技师编号,返回UnusedTechNoListResult
    public void getUnusedTechNos(String inviteCode) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, LoginTechnician.getInstance().getToken());
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_UNUSED_TECH_NO, params);
    }
}
