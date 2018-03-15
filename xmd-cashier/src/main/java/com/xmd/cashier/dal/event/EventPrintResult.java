package com.xmd.cashier.dal.event;

/**
 * Created by zr on 18-3-14.
 */

public class EventPrintResult {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventPrintResult(String message) {
        this.message = message;
    }
}
