package com.xmd.contact.bean;

/**
 * Created by Lhj on 17-7-29.
 */

public class ContactAllBean {
    /**
     * id : 813660804388753408
     * name : 算法的技术
     * avatar : 151947
     * emchatId : e5a3c92d839bec160b194708a6780655
     * userId : 743718740591382528
     * userNoteName :
     * remark : null
     * createTime : 2016-12-27 16:20:27
     * customerType : fans_wx_user
     * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/58/oIYBAFdzdDKACJukAAA4JjMaJA4314.png?st=egagXnVFqEPGIQ152XVQvQ&e=1499323635
     */

    public String id;  //	可能为空
    public String name;
    public String avatar;
    public String emchatId;
    public String userId; //	可能为空
    public String userNoteName;
    public String remark;
    public String createTime;
    public String customerType;  //wx_user-微信用户;fans_user-手机用户;fans_wx_user-微信加手机用户;tech_add-通讯录联系人
    public String avatarUrl;
    public boolean isService;//true 是客服,false 不是客服
    public String tagName;
    public int distance;


    @Override
    public String toString() {
        return "ContactAllBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", emchatId='" + emchatId + '\'' +
                ", userId='" + userId + '\'' +
                ", userNoteName='" + userNoteName + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime='" + createTime + '\'' +
                ", customerType='" + customerType + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", isService=" + isService +
                '}';
    }
}
