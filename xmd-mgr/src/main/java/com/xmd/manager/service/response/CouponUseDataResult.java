package com.xmd.manager.service.response;

import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.beans.ConsumeInfo;

import java.util.List;

/**
 * Created by sdcm on 15-12-15.
 */
public class CouponUseDataResult extends BaseResult {

    public Content respData;

    public class Content {
        public String total;
        public ClubInfo club;
        public List<ConsumeInfo> consumes;
    }

}
