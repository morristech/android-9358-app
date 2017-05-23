package com.xmd.manager.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */
public class ServiceItemInfo implements Serializable {

    public String categoryName;
    public List<ServiceItem> serviceItems;
    public static List<ServiceItem> serviceSelectedItems = new ArrayList<>();

    public String getCateGoryName;

    public List<ServiceItem> getServiceList() {
        return serviceItems;
    }

    public void addServiceSelectedItems(ServiceItem item) {
        serviceSelectedItems.add(item);
    }

    public void removeSelectedItems(ServiceItem item) {
        serviceSelectedItems.remove(item);
    }


}
