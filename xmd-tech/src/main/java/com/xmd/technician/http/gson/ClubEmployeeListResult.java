package com.xmd.technician.http.gson;

import com.xmd.technician.bean.ClubRoleBean;
import com.xmd.technician.bean.ClubUserListBean;

import java.util.List;

/**
 * Created by Lhj on 17-6-5.
 */

public class ClubEmployeeListResult extends BaseResult {

    public RespDataBean respData;

    public static class RespDataBean {
        public List<ClubRoleBean> roleList;
        public List<ClubUserListBean> userList;
    }
}
