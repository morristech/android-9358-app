package com.xmd.cashier.dal.net.response;


import com.xmd.cashier.dal.bean.UpdateConfigInfo;
import com.xmd.m.network.BaseBean;

/**
 * Created by sdcm on 16-1-22.
 */
public class CheckUpdateResult extends BaseBean {
    public boolean update;
    public UpdateConfigInfo config;
}
