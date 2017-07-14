package com.xmd.technician.http.gson;

import com.xmd.technician.bean.TechAccountBean;

import java.util.List;

/**
 * Created by sdcm on 17-3-10.
 */

public class TechAccountListResult extends BaseResult {
    public Content respData;

    public class Content {
        public String withdrawal;
        public List<TechAccountBean> accountList;
    }
}
