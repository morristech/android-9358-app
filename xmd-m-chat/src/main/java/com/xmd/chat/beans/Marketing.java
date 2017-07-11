package com.xmd.chat.beans;

import android.text.TextUtils;

import com.xmd.app.BaseViewModel;

/**
 * Created by mo on 17-7-11.
 * 营销活动
 */

public class Marketing extends BaseViewModel {
    private String category;

    public int actPrice;
    public int unitPrice;
    public int amount;
    public String id;
    public String itemId;
    public String price;
    public String usePeriod;
    public String itemName;
    public int credits;
    public boolean limitedUse;
    public String actId;
    public String actName;
    public String endTime;
    public String startTime;
    public String firstPrizeName;
    public String image;
    public String shareUrl;
    public int maxPeriod; //总连期数，1表示不连期，0表示无限
    public int currentPeriod; //当前期数
    public int totalPaidCount; //总期数
    public int canPaidCount; //可购买次数
    public int paidCount; //购买次数

    private String showText1;
    private String showText2;
    private String showText3;

    public String oneYuanMark;

    public void setCategory(String category) {
        this.category = category;
        switch (category) {
            case MarketingCategory.TIME_LIMIT:
                showText1 = itemName;
                showText2 = amount + "元" + (credits > 0 ? "（或" + credits + "积分）" : "");
                showText3 = !TextUtils.isEmpty(price) ? "原价：" + price + "元" : "";
                break;
            case MarketingCategory.ONE_YUAN:
                showText1 = actName + "（" + currentPeriod + "/" + maxPeriod + "期）";
                showText2 = "单价：" + unitPrice + "元";
                showText3 = "已售：" + paidCount + "/" + totalPaidCount;
                oneYuanMark = unitPrice == 1 ? "一元夺" : "限时抢";
                break;
            case MarketingCategory.LUCKY_WHEEL:
                showText1 = actName;
                showText2 = "羸取" + firstPrizeName;
                break;
        }
    }

    public String getCategory() {
        return category;
    }

    public String getShowText1() {
        return showText1;
    }

    public String getShowText2() {
        return showText2;
    }

    public String getShowText3() {
        return showText3;
    }
}
