package com.xmd.appointment.beans;

import android.databinding.ObservableBoolean;

import java.util.List;

/**
 * Created by heyangya on 17-5-27.
 * 服务数据，包含分类和项目信息
 */

public class ServiceData {
    public ServiceCategory categoryBean;
    public List<ServiceItem> itemList;

    public ObservableBoolean viewSelected = new ObservableBoolean();
}
