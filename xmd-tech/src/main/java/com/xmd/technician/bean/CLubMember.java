package com.xmd.technician.bean;

import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/7/7.
 */
public class CLubMember extends BaseResult  {
    public String userType;
    public String id;
    public String phoneNum;
    public String serialNo;
    public String name;
    public String avatarUrl;
    public String mPinyin;
    public String sortLetters;


    public CLubMember(String id, String userType, String avatarUrl, String name, String serialNo, String phoneNum) {
        this.userType = userType;
        this.avatarUrl = avatarUrl;
        this.name = (name==null?"匿名":name);
        this.serialNo = serialNo;
        this.phoneNum = phoneNum;
        this.id = id;
        this.sortLetters = CharacterParser.getInstance().getSelling(name).substring(0,1).toUpperCase().matches("[A-Z]")?CharacterParser.getInstance().getSelling(name).substring(0,1).toUpperCase():"#";
        }

    public String getSortLetters() {
        return sortLetters;
    }

}
