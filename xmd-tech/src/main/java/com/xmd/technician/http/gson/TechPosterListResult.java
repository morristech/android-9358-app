package com.xmd.technician.http.gson;

import com.xmd.technician.bean.PosterBean;

import java.util.List;

/**
 * Created by Lhj on 17-6-23.
 */

public class TechPosterListResult extends BaseResult {


    public RespDataBean respData;

    public static class RespDataBean {


        public String qrCodeUrl;
        public String validDate;
        public List<PosterBean> list;

        public static class ListBean {

        }
    }
}
