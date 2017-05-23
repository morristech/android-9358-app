package com.xmd.cashier.exceptions;

/**
 * Created by heyangya on 16-8-30.
 */

public class ServerException extends Exception {
    public int statusCode;

    public ServerException(String e, int statusCode) {
        super(e);
        this.statusCode = statusCode;
    }
}
