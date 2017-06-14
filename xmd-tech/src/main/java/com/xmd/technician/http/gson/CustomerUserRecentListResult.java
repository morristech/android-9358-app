package com.xmd.technician.http.gson;

import com.xmd.technician.bean.UserRecentBean;

import java.util.List;

/**
 * Created by sdcm on 17-6-6.
 */

public class CustomerUserRecentListResult extends BaseResult {

    /**
     * respData : {"userList":[{"avatar":"测试内容0111","avatarUrl":"http://wx.qlogo.cn/mmopen/Sia4gHVGFfgH60RjQV61ds9siaTVBWYNofUhSBYrLtXGUrY4u4yfpmb9XPCjXYNRCJMLMdGdT9qsw3xkQMQ9kicbKHkxEnjYjLq/0","createTime":"测试内容m27c","customerType":"wx_user","emchatId":"f48ee178b26ca1644b636b7799e2c2f5","id":"864665427496669184","name":"真","remark":"测试内容e713","userId":"752754129377431552","userNoteName":"","visitType":64108}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public List<UserRecentBean> userList;
    }
}
