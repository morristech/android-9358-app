package com.xmd.contact.bean;

/**
 * Created by Lhj on 17-7-27.
 */

public class VisitorBean {

    /**
     * avatar : 测试内容0111
     * avatarUrl : http://wx.qlogo.cn/mmopen/Sia4gHVGFfgH60RjQV61ds9siaTVBWYNofUhSBYrLtXGUrY4u4yfpmb9XPCjXYNRCJMLMdGdT9qsw3xkQMQ9kicbKHkxEnjYjLq/0
     * createTime : 测试内容m27c
     * customerType : wx_user
     * emchatId : f48ee178b26ca1644b636b7799e2c2f5
     * id : 864665427496669184
     * name : 真
     * remark : 测试内容e713
     * userId : 752754129377431552
     * userNoteName :
     * visitType : 64108
     */
    public String canSayHello; //"Y"可打招呼,"N",不可打招呼
    public String avatar;
    public String avatarUrl;
    public String createTime;   //	yyyy-MM-dd HH:mm:ss
    public String customerType; //	wx_user-微信用户;fans_user-手机用户;fans_wx_user-微信加手机用户;tech_add-通讯录联系人
    public String emchatId;
    public String id;
    public String name;
    public String remark;
    public String userId;
    public String userNoteName;
    public int visitType;  //0-技师页;1-评论;2-收藏;3-领券;4-点钟券;5-打赏
    public int intListPosition; //在列表中的位置
}
