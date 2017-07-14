package com.xmd.m.comment.event;

/**
 * Created by Lhj on 17-7-13.
 */

public class EditCustomerRemarkSuccessEvent {
    public String phone;
    public String userName;
    public String remarkName;
    public String remarkMessage;
    public String remarkImpression;


    public EditCustomerRemarkSuccessEvent(String userName, String remarkName, String remarkMessage, String remarkImpression) {
        this.userName = userName;
        this.remarkName = remarkName;
        this.remarkMessage = remarkMessage;
        this.remarkImpression = remarkImpression;
    }
}
