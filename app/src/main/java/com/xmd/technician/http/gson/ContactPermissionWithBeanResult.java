package com.xmd.technician.http.gson;

import com.xmd.technician.bean.ContactPermissionInfo;
import com.xmd.technician.bean.RecentlyVisitorBean;

/**
 * Created by zr on 17-3-23.
 */

public class ContactPermissionWithBeanResult extends BaseResult {
    public ContactPermissionInfo respData;
    public RecentlyVisitorBean bean;

    public ContactPermissionWithBeanResult(ContactPermissionResult result) {
        this.statusCode = result.statusCode;
        this.msg = result.msg;
        this.respData = result.respData;
        this.pageCount = result.pageCount;
    }
}
