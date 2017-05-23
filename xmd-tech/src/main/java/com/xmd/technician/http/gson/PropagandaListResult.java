package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by Lhj on 2017/2/9.
 */

public class PropagandaListResult extends BaseResult {

    public List<RespDataBean> respData;

    public static class RespDataBean {
        /**
         * actName : 测试内容6ouk
         * actType : 测试内容vcj8
         * count : 测试内容k16l
         */

        public String proName;
        public String proType;
        public String count;
    }
}
