package com.xmd.manager.beans;

/**
 * Created by Lhj on 2016/12/5.
 */

public class GroupBean {

    /**
     * id : 47
     * name : TEST001
     * createTime : 1483001317000
     * createUserId : null
     * createType : user
     * description : AAAAAAAAAAAAAAAAAA
     * totalCount : 16
     * userNames : mjf?????,wuli,mjflx,mjflx,Tracy3,老子,匿名用户,雪栩星枫,马金凤??,匿名用户,马金凤,铅华、落尽,建,符号哟,majf_2015,匿名用户
     */

    public String id;
    public String name;
    public long createTime;
    public Object createUserId;
    public String createType;
    public String description;
    public int totalCount;
    public String userNames;
    public Boolean isChecked;

    public GroupBean(String id, String name, String description, int totalCount, boolean checked) {
        this.id = id;
        this.name = name;
        this.isChecked = checked;
        this.totalCount = totalCount;
        this.description = description;
    }
}
