package com.xmd.manager.service.response;

import com.xmd.manager.beans.CommissionTechInfo;

import java.util.List;

/**
 * Created by zr on 17-11-23.
 */

public class TechCommissionListResult extends BaseResult {
    public List<CommissionTechInfo> respData;
    public String requestType;
}
