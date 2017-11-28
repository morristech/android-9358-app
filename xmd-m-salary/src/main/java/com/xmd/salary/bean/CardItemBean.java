package com.xmd.salary.bean;

/**
 * Created by Lhj on 17-11-24.
 * 会员卡
 */

public class CardItemBean {

    public String cardName;
    public int cardPay;
    public int cardPayDisCount;
    public String carItem;
    public int techCommission;

    public CardItemBean(String cardName, int cardPay, int cardPayDisCount, String carItem, int techCommission) {
        this.cardName = cardName;
        this.cardPay = cardPay;
        this.cardPayDisCount = cardPayDisCount;
        this.carItem = carItem;
        this.techCommission = techCommission;
    }

    public CardItemBean(String cardName, int cardPay,  String carItem, int techCommission) {
        this.cardName = cardName;
        this.cardPay = cardPay;
        this.carItem = carItem;
        this.techCommission = techCommission;
    }

}
