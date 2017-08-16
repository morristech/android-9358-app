package com.xmd.black;

import com.xmd.black.bean.PhoneContact;

import java.util.Comparator;

/**
 * Created by Lhj on 17-7-25.
 */

public class PinyinContactUtil implements Comparator<PhoneContact> {
    @Override
    public int compare(PhoneContact o1, PhoneContact o2) {
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
