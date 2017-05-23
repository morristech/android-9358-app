package com.xmd.technician.http.gson;

import com.xmd.technician.bean.TechRankingBean;

import java.util.List;

/**
 * Created by sdcm on 17-4-14.
 */

public class TechRankingListResult extends BaseResult {
    public List<TechRankingBean> respData;
    public String sortType;
}
