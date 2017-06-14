package com.xmd.technician.http.gson;

import com.xmd.technician.bean.ContactAllBean;

import java.util.List;

/**
 * Created by sdcm on 17-6-2.
 */

public class ContactRegisterListResult extends BaseResult {
    public RespDataBean respData;

    public static class RespDataBean {

        public int blackListCount;
        public int totalCount;
        public List<ContactAllBean> userList;

    }
}
