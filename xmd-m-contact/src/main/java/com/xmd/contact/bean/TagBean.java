package com.xmd.contact.bean;

/**
 * Created by Lhj on 17-8-9.
 */

public class TagBean {

    public String tagTitle;
    public String tagString;
    public String tagType;
    public boolean isSelected;

    public TagBean(String tagTitle, String tagString, String tagType, boolean isSelected) {
        this.tagTitle = tagTitle;
        this.tagString = tagString;
        this.tagType = tagType;
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "TagBean{" +
                "tagTitle='" + tagTitle + '\'' +
                ", tagString='" + tagString + '\'' +
                ", tagType='" + tagType + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }


}
