package com.xmd.technician.common;

import com.xmd.technician.bean.CustomerInfo;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/7/13.
 */
public class PinyinCompartorUtil implements Comparator<CustomerInfo> {

    public int compare(CustomerInfo o1, CustomerInfo o2) {
        if (o1.sortLetters.equals("@")
                || o2.sortLetters.equals("#")) {
            return -1;
        } else if (o1.sortLetters.equals("#")
                || o2.sortLetters.equals("@")) {
            return 1;
        } else {
            return o1.sortLetters.compareTo(o2.sortLetters);
        }
    }

}



