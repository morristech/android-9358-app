package com.xmd.manager.service.response;

import java.io.Serializable;

/**
 * Created by sdcm on 15-10-30.
 */
public class BaseResult implements Serializable {

    public int statusCode;
    public String msg;
    public int pageCount;
}
