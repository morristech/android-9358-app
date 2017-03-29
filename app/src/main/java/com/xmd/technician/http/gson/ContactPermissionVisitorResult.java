package com.xmd.technician.http.gson;

import com.xmd.technician.bean.RecentlyVisitorBean;

/**
 * Created by zr on 17-3-23.
 */

public class ContactPermissionVisitorResult extends ContactPermissionResult {
    public RecentlyVisitorBean bean;

    public ContactPermissionVisitorResult(ContactPermissionResult result) {
        this.statusCode = result.statusCode;
        this.msg = result.msg;
        this.respData = result.respData;
        this.pageCount = result.pageCount;
    }
}
