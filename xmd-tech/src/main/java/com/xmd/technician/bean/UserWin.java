package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/8/31.
 */
public class UserWin extends BaseResult {
    public String messageId;

    public UserWin(String gameId) {
        this.messageId = gameId;

    }
}
