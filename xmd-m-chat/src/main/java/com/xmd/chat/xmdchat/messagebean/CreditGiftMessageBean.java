package com.xmd.chat.xmdchat.messagebean;

/**
 * Created by Lhj on 18-1-26.
 */

public class CreditGiftMessageBean {

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftValue() {
        return giftValue;
    }

    public void setGiftValue(String giftValue) {
        this.giftValue = giftValue;
    }

    private String giftId;
    private String giftName;
    private String giftValue;
}
