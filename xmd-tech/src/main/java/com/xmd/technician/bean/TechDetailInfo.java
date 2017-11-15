package com.xmd.technician.bean;

import android.text.TextUtils;

/**
 * Created by sdcm on 16-3-14.
 */
public class TechDetailInfo {
    /**
     * id : 926299484709720064
     * name : 小03
     * roles : tech
     * status : free
     * clubId : 621280172275933185
     * phoneNum : 13137558103
     * gender : male
     * avatar : null
     * description : null
     * birthYear : 0
     * birthMon : 0
     * birthDay : 0
     * province : 辽宁省
     * city : 辽阳市
     * provinceCode : 210000
     * cityCode : 211000
     * serviceStatus : N
     * topOrders : 0
     * recommend : null
     * temporary : null
     * inviteCode : 4ku71s
     * openId : null
     * alipayUserId : null
     * headimgurl : null
     * createdDate : 1509638400000
     * sceneId : 1000909
     * commentCount : 0
     * badCommentCount : 0
     * viewClubType : null
     * serialNo : 12245
     * endTime : null
     * star : 0
     * techTags : null
     * impressions : null
     * viewAlbumType : 1
     * avatarUrl : null
     */

    public String id;
    public String name;
    public String roles;
    public String status;
    public String clubId;
    public String phoneNum;
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
    public String serviceStatus;
    public int topOrders;
    public String recommend;
    public String temporary;
    public String inviteCode;
    public String openId;
    public String alipayUserId;
    public String headimgurl;
    public long createdDate;
    public int sceneId;
    public int commentCount;
    public int badCommentCount;
    public String viewClubType;
    public String serialNo;
    public String endTime;
    public int star;
    public String techTags;
    public String impressions;


    public int viewAlbumType;
    public String avatarUrl;

    public String getCity() {
        return TextUtils.isEmpty(city) ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return TextUtils.isEmpty(province) ? "" : province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getGender() {
        return gender.equals("female") ? "女" : "男";
    }

    public void setGender(String gender) {
        this.gender = gender.equals("女") ? "female" : "male";
    }

    public String getDescription() {
        return TextUtils.isEmpty(description) ? "-" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getViewAlbumType() {
        return viewAlbumType;
    }

    public void setViewAlbumType(int viewAlbumType) {
        this.viewAlbumType = viewAlbumType;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\",\"gender\":\"" + gender + "\",\"viewAlbumType\":\"" + viewAlbumType + "\",\"serialNo\":\"" + serialNo + "\",\"provinceCode\":\"" + provinceCode + "\",\"cityCode\":\"" + cityCode + "\"," +
                "\"province\":\"" + province + "\",\"city\":\"" + city + "\",\"phoneNum\":\"" + phoneNum + "\",\"description\":\"" + description + "\"}";
    }

}
