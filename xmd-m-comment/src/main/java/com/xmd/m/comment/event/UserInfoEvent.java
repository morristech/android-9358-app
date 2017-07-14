package com.xmd.m.comment.event;

import com.xmd.m.comment.bean.UserInfoBean;

/**
 * Created by Lhj on 17-7-12.
 */

public class UserInfoEvent {
    public int toDoType;//0:打电话,1:聊天,2:发短信,3:打招呼
    public UserInfoBean bean;

    public UserInfoEvent(int toDoType, UserInfoBean bean) {
        this.toDoType = toDoType;
        this.bean = bean;
    }

}
