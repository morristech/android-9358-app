package com.xmd.m.comment.event;

import com.xmd.m.comment.bean.UserInfoBean;

/**
 * Created by Lhj on 17-7-12.
 */

public class UserInfoEvent {
    public int appType; //0:管理者,1:技师
    public int toDoType;//0:打电话,1:聊天,2:发短信,3:打招呼
    public UserInfoBean bean;

    public UserInfoEvent(int appType,int toDoType, UserInfoBean bean) {
        this.appType = appType;
        this.toDoType = toDoType;
        this.bean = bean;
    }

}
