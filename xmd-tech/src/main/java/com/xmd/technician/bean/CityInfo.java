package com.xmd.technician.bean;

/**
 * Created by SD_ZR on 15-6-6.
 * 城市信息封装
 */
public class CityInfo {
    private String name;
    private String cityCode;

    public CityInfo() {
        super();
    }

    public CityInfo(String name, String cityCode) {
        super();
        this.name = name;
        this.cityCode = cityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
