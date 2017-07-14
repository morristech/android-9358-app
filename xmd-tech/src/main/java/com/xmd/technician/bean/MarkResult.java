package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class MarkResult extends BaseResult {

    public List<RespDataBean> respData;

    public static class RespDataBean {
        public int id;
        public String tag;
        public String tagType;
    }
}
