package com.xmd.manager.service.response;

import com.xmd.manager.beans.TechRankingBean;

import java.util.List;

/**
 * Created by Lhj on 17-4-27.
 */

public class TechRankingListResult extends BaseResult {
    public List<TechRankingBean> respData;
    public String sortType;
}

