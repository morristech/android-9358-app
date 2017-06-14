package com.xmd.technician.http.gson;

import com.xmd.technician.bean.ContactAllBean;

import java.util.List;

/**
 * Created by sdcm on 17-6-2.
 */

public class ContactAllListResult extends BaseResult {


    /**
     * respData : {"blackListCount":0,"totalCount":1,"userList":[{"avatar":"测试内容laxc","avatarUrl":"http://wx.qlogo.cn/mmopen/Sia4gHVGFfgH60RjQV61ds9siaTVBWYNofUhSBYrLtXGUrY4u4yfpmb9XPCjXYNRCJMLMdGdT9qsw3xkQMQ9kicbKHkxEnjYjLq/0","customerType":"wx_user","emchatId":"f48ee178b26ca1644b636b7799e2c2f5","id":"864665427496669184","name":"真","remark":"测试内容hjo7","userId":"752754129377431552","userNoteName":""}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {

        public int blackListCount;
        public int totalCount;
        public String serviceStatus; //"Y":是客服,"N":不是客服
        public String today;
        public List<ContactAllBean> userList;

    }
}
