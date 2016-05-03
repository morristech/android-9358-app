package com.xmd.technician.bean;

/**
 * Created by sdcm on 16-3-14.
 */
public class TechDetailInfo {
    public String id;
    public String name;
    public String status;
    public String clubId;
    public String gender;
    public String avatar;
    public String description;
    public int birthYear;
    public int birthMon;
    public int birthDay;
    public String province;
    public String city;
    public String provinceCode;
    public String cityCode;
    public String temporary;
    public int redPacket;
    public String inviteCode;
    public String clientId;
    public String alias;
    public String clientTags;
    public String bindFlag;
    public String needBindFlag;
    public String appType;
    public String serialNo;
    public int star;
    public int commentCount;
    public String techTags;
    public String avatarUrl;
    public String phoneNum;


    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\",\"gender\":\"" + gender + "\",\"serialNo\":\"" + serialNo + "\",\"provinceCode\":\"" + provinceCode + "\",\"cityCode\":\"" + cityCode + "\",\"province\":\"" + province + "\",\"city\":\"" + city + "\",\"phoneNum\":\"" + phoneNum + "\",\"description\":\"" + description + "\"}";
    }
}
