package com.xmd.m.comment;

import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventClickAvatar;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.m.comment.httprequest.ConstantResources;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mo on 17-7-25.
 * 处理外部事件
 */

public class XmdComment {
    private static final XmdComment ourInstance = new XmdComment();

    public static XmdComment getInstance() {
        return ourInstance;
    }

    private XmdComment() {
    }

    public void init() {
        EventBusSafeRegister.register(this);
    }

    //点击头像事件，需要跳转到客户详情页面
    @Subscribe
    public void OnEventClickAvatar(EventClickAvatar event) {
        User user = event.getUser();
        User currentUser = UserInfoServiceImpl.getInstance().getCurrentUser();
        if (user == null || currentUser == null || currentUser.getId().equals(user.getId())) {
            return;
        }
        String fromType;
        if (currentUser.getUserRoles().contains(User.ROLE_MANAGER)) {
            fromType = ConstantResources.INTENT_TYPE_MANAGER;
        } else if (currentUser.getUserRoles().contains(User.ROLE_TECH)
                || currentUser.getUserRoles().contains(User.ROLE_FLOOR)) {
            fromType = ConstantResources.INTENT_TYPE_TECH;
        } else {
            return;
        }
        boolean customerIsTech = false;
        if (user.getUserRoles() != null && (user.getUserRoles().contains(User.ROLE_TECH)
                || user.getUserRoles().contains(User.ROLE_FLOOR))) {
            customerIsTech = true;
        }
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(XmdApp.getInstance().getContext(), user.getId(), fromType, customerIsTech);
    }
}
