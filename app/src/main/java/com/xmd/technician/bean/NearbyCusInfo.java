package com.xmd.technician.bean;

/**
 * Created by ZR on 17-3-2.
 * 附近的人
 * TODO:需要互动动态,预约,打赏信息
 */

public class NearbyCusInfo {
    public int orderCount;          //预约次数
    public int rewardAmount;        //打赏金额
    public boolean techHelloRecently; //是否打过招呼
    public String userEmchatId;
    public String lastTechHelloTime;   //最近一次打招呼时间
    public String userAvatar;       //客户头像
    public double userClubDistance;    //距离
    public String userId;           //客户id
    public int userLeftHelloCount;  //客户剩余可打招呼数量
    public String userName;         //客户名称
    public String userPosition;     //客户位置
    public String userPositionUpdateTime;   //最近一次更新时间
    public String userType;         //客户类型
}
