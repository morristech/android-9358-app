package com.xmd.manager.service.response;

import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */
public class GroupMessageEmChatResult extends BaseResult {

    /**
     * {
     * "respData": {"emchatIds": ["9f8db411b5cf9e0f31d81060ed21e7c2","8ed1ab7637e179a83eabc3f460a20e72],
     * "groupmessageId": "781012555379253248"},
     * "statusCode": 200,
     * "pageCount": 0,
     * "msg": "查询成功"
     * }
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public String groupmessageId;
        public List<users> users;
    }

    public static class users {

        /**
         * id : 633970688134221824
         * name : 符号哟
         * avatar : 150877
         * emchatId : 9f8db411b5cf9e0f31d81060ed21e7c2
         * avatarUrl : http://sdcm212:8489/s/group00/M00/00/4E/oIYBAFdHtwuAENbgAADHMnJm6IY403.png?st=YuwDAbFBzikPzM6G1JFr4w&e=1475925501
         */

        public String id;
        public String name;
        public String avatar;
        public String emchatId;
        public String avatarUrl;


    }
}
