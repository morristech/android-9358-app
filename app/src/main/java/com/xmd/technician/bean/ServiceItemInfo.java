package com.xmd.technician.bean;

import java.util.List;

/**
 * Created by sdcm on 16-4-1.
 */
public class ServiceItemInfo {
    public String name;
    public List<ItemInfo> serviceItems;

    public class ItemInfo{
        public String id;
        public String name;
        public String price;
        public String duration;
        public int isSelected;
    }
}
