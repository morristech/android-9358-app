package com.xmd.technician.model;

import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyangya on 16-12-22.
 * 会所相关的操作
 */
public class ClubManager {
    private static ClubManager ourInstance = new ClubManager();

    public static ClubManager getInstance() {
        return ourInstance;
    }

    private ClubManager() {
    }


    //获取未使用的技师编号,返回UnusedTechNoListResult
    public void getUnusedTechNos(String inviteCode) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, LoginTechnician.getInstance().getToken());
        params.put(RequestConstant.KEY_CLUB_CODE, inviteCode);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_UNUSED_TECH_NO, params);
    }

    //获取会所功能配置
    public void getCreditSupportStatus(String clubId) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_USER_CLUB_SWITCHES);
    }
}
