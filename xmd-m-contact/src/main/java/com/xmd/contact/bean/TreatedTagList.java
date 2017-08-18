package com.xmd.contact.bean;

import java.util.List;

/**
 * Created by Lhj on 17-8-9.
 */

public class TreatedTagList {

    public String title;
    public String type;
    public List<TagBean> list;
    //    public boolean isOpen;
//
//    public TreatedTagList(String title, String type, List<TagBean> list, boolean isOpen) {
//
//        this.title = title;
//        this.type = type;
//        this.list = list;
//        this.isOpen = isOpen;
//    }
    public String isOpen;//0关，1开

    public TreatedTagList(String title, String type, List<TagBean> list, String isOpen) {

        this.title = title;
        this.type = type;
        this.list = list;
        this.isOpen = isOpen;
    }

}
