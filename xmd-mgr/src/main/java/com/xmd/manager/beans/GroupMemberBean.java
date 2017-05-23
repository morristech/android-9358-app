package com.xmd.manager.beans;

import java.io.Serializable;

/**
 * Created by Lhj on 2016/12/30.
 */

public class GroupMemberBean implements Serializable {

    /**
     * id : 633970688134221824
     * name : 符号哟
     * telephone : 18670658890
     * avatar : 150877
     * badCommentCount : 0
     * userType : user
     * avatarUrl :
     */

    public String id;
    public String name;
    public String telephone;
    public String avatar;
    public String badCommentCount;
    public String userType;
    public String avatarUrl;
    public int isSelected; //1,已包含，2，可被选中，3，未被选中

    public GroupMemberBean(String id, String name, String telephone, String avatar, String badCommentCount, String userType, String avatarUrl, int isSelected) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.avatar = avatar;
        this.badCommentCount = badCommentCount;
        this.userType = userType;
        this.avatarUrl = avatarUrl;
        this.isSelected = isSelected;
    }

    public GroupMemberBean(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatar;
    }
}
