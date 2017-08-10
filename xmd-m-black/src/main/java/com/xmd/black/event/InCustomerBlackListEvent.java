package com.xmd.black.event;

/**
 * Created by Lhj on 17-7-24.
 */

public class InCustomerBlackListEvent {
    boolean isInCustomerBlackList;
    String msg;

    public InCustomerBlackListEvent(boolean isInCustomerBlackList, String message) {
        this.isInCustomerBlackList = isInCustomerBlackList;
        this.msg = message;

    }
}
