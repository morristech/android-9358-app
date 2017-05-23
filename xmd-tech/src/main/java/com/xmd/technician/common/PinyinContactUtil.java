package com.xmd.technician.common;

import com.xmd.technician.bean.PhoneContactor;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/7/15.
 */
public class PinyinContactUtil implements Comparator<PhoneContactor> {

    public int compare(PhoneContactor o1, PhoneContactor o2) {
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
