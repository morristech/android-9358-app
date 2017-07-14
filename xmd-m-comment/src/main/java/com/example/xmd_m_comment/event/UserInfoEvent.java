package com.example.xmd_m_comment.event;

import com.example.xmd_m_comment.bean.UserInfoBean;

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
