package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by zr on 17-3-28.
 */

public class SayHiBaseResult extends BaseResult {
    public String userName;
    public String userEmchatId;
    public String userType;
    public String userAvatar;

    public DATA respData;

    public static class DATA {
        public int customerLeft;
        public int technicianLeft;
    }
}
