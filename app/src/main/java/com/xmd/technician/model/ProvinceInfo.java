package com.xmd.technician.model;

import java.util.List;

/**
 * Created by SD_ZR on 15-6-6.
 * 省份信息封装
 */
public class ProvinceInfo {
    private String name;
    private String provinceCode;
    private List<CityInfo> cityInfoList;

    public ProvinceInfo() {
        super();
    }

    public ProvinceInfo(String name, String provinceCode, List<CityInfo> cityInfoList) {
        super();
        this.name = name;
        this.provinceCode = provinceCode;
        this.cityInfoList = cityInfoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceCode()
    {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode)
    {
        this.provinceCode = provinceCode;
    }

    public List<CityInfo> getCityInfoList() {
        return cityInfoList;
    }

    public void setCityInfoList(List<CityInfo> cityInfoList) {
        this.cityInfoList = cityInfoList;
    }
}
