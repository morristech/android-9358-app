package com.xmd.technician.bean;

import java.util.List;

/**
 * Created by Lhj on 17-6-5.
 */

public class ClubRoleBean {
    /**
     * id : 1
     * name : 店长
     * code : club_manager
     */

    public int id;
    public String name;
    public String code;

    public List<ClubUserListBean> mUserListBeans;

    public ClubRoleBean(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;

    }

    public ClubRoleBean(int id, String name, String code, List<ClubUserListBean> userListBeans) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.mUserListBeans = userListBeans;
    }

}
