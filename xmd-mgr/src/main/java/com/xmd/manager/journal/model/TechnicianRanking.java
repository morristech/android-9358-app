package com.xmd.manager.journal.model;

import java.util.List;

/**
 * Created by heyangya on 16-11-15.
 */

public class TechnicianRanking {
    private String mType;
    private List<Technician> mData;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public List<Technician> getData() {
        return mData;
    }

    public void setData(List<Technician> data) {
        this.mData = data;
    }
}
