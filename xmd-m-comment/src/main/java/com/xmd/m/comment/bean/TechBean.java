package com.xmd.m.comment.bean;

import java.io.Serializable;

/**
 * Created by Lhj on 17-7-6.
 */

public class TechBean implements Serializable {
    /**
     * avatarUrl : 测试内容sy37
     * techNo : 测试内容rmv2
     * techId : 656360260637822976
     * techName : 小摩豆技师
     */

    public String avatarUrl;
    public String techNo;
    public String techId;
    public String techName;
    public boolean isSelected;

    public TechBean(String techNo, boolean isSelected) {
        this.techNo = techNo;
        this.isSelected = isSelected;
    }
}
