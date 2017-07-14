package com.example.xmd_m_comment.bean;

import java.io.Serializable;

/**
 * Created by Lhj on 17-7-13.
 */

public class UserGroupListBean implements Serializable {
    /**
     * id : 50
     * name : 分组1
     * createTime : 1483523358000
     * createUserId : null
     * createType : user
     * description : 分组备注1
     * totalCount : 1
     * userNames : null
     */

    public int id;
    public String name;
    public long createTime;
    public Object createUserId;
    public String createType;
    public String description;
    public int totalCount;
    public Object userNames;

    @Override
    public String toString() {
        return "UserGroupListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
