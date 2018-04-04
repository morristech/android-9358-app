package com.xmd.cashier.common;

/**
 * Created by LYF on 2018/4/3.
 */

public class JError {
    public static final int DEFAULT_ERROR_CODE = -2147483648;
    public int errorCode;
    public String errorInfo;
    public String url;

    public JError(int errorCode, String errorInfo, String url) {
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
        this.url = url;
    }

    public String toString() {
        return "errorCode: " + this.errorCode + ",  errorInfo: " + this.errorInfo + ",  url: " + this.url;
    }
}
