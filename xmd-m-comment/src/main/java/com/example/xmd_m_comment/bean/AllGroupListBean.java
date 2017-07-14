package com.example.xmd_m_comment.bean;

import java.io.Serializable;

/**
 * Created by Lhj on 17-7-13.
 */

public class AllGroupListBean implements Serializable {
    /**
     * id : 47
     * name : TEST001
     * createTime : 1483001317000
     * createUserId : null
     * createType : user
     * description : AAAAAAAAAAAAAAAAAA
     * totalCount : 15
     * userNames : null
     */

    public String id;
    public String name;
    public long createTime;
    public Object createUserId;
    public String createType;
    public String description;
    public int totalCount;
    public Object userNames;

    public AllGroupListBean(String id, String name) {
        this.name = name;
        this.id = id;
    }
}

