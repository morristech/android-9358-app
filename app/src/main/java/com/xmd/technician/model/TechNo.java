package com.xmd.technician.model;

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

    public static TechNo DEFAULT_TECH_NO = new TechNo("管理员指定", null);
}
