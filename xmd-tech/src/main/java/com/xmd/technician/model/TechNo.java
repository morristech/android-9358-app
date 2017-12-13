package com.xmd.technician.model;


import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;

/**
 * Created by heyangya on 16-12-23.
 */

public class TechNo {
    public String name;
    public String id;

    public TechNo(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public static TechNo DEFAULT_TECH_NO = new TechNo(ResourceUtils.getString(R.string.tech_num_default), null);
}
