package com.xmd.manager.beans;

import com.xmd.manager.Constant;
import com.xmd.manager.common.CharacterParserUtil;

import java.io.Serializable;

/**
 * Created by linms@xiaomodo.com on 16-5-18.
 */
public class Customer implements Serializable {

    /*
       These two fields for group header displaying, for normal item, the type is CUSTOMER_TYPE_ITEM,
       for active degree, the type will be CUSTOMER_TYPE_HEADER_ACTIVE_DEGREE,
       for techName group, the type will be CUSTOMER_TYPE_HEADER_TECHNICIAN
    */
    public int type = Constant.CUSTOMER_TYPE_ITEM;

    /**
     * only will be used when the type is CUSTOMER_TYPE_HEADER_ACTIVE_DEGREE or CUSTOMER_TYPE_HEADER_TECHNICIAN
     */
    public int groupCount;

    public String id;

    public String emchatId;

    public String phoneNum;
    public int badCommentCount;
    public String createTime;      //2015-10-27 10:38
    public String loginDate;
    public String loginDateTime;   //2015-10-27 10:38:41
    public String techSerialNo;
    public String techId;
    public String techName;
    public int active;

    public String userId;
    public String userName;
    public String userType;

    public String userHeadimgurl;

    public int commentCount;
    public int orderCount;
    public int couponCount;
    public String sortLetters;
    public int checkedState;

    public Customer(int type) {
        this.type = type;
    }

    public Customer(String name, String phoneNum, int badCommentsCount, String loginDate, String techName, int active, String techNo) {
        this.active = active;
        this.badCommentCount = badCommentsCount;
        this.loginDate = loginDate;
        this.userName = name;
        this.techName = techName;
        this.phoneNum = phoneNum;
        this.techSerialNo = techNo;
        this.sortLetters = CharacterParserUtil.getInstance().getSelling(userName).substring(0, 1).toUpperCase().matches("[A-Z]") ? CharacterParserUtil.getInstance().getSelling(userName).substring(0, 1).toUpperCase() : "#";
    }

    public Customer(String id, String name, String phoneNum, int badCommentsCount, String userType, String userHeadimgurl, int checkedState) {
        this.id = id;
        this.userName = name;
        this.phoneNum = phoneNum;
        this.badCommentCount = badCommentsCount;
        this.userType = userType;
        this.userHeadimgurl = userHeadimgurl;
        this.sortLetters = CharacterParserUtil.getInstance().getSelling(userName).substring(0, 1).toUpperCase().matches("[A-Z]") ? CharacterParserUtil.getInstance().getSelling(userName).substring(0, 1).toUpperCase() : "#";
        this.checkedState = checkedState;
    }

    public Customer(String head, String name) {
        this.userHeadimgurl = head;
        this.userName = name;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "techName='" + techName + '\'' +
                '}';
    }

    public String getSortLetters() {
        return sortLetters;
    }
}
