package com.xmd.technician.http.gson;

import java.io.Serializable;

/**
 * Created by sdcm on 15-10-30.
 */
public class BaseResult implements Serializable {

    public int statusCode;
    public String msg;
    public int pageCount;

    @Override
    public String toString() {
        return "BaseResult{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", pageCount=" + pageCount +
                '}';
    }
}
