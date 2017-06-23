package com.xmd.m.network;

/**
 * Created by heyangya on 16-8-30.
 * 服务器返回错误
 */

public class ServerException extends Exception {
    public int statusCode;

    public ServerException(String e, int statusCode) {
        super(e);
        this.statusCode = statusCode;
    }
}
