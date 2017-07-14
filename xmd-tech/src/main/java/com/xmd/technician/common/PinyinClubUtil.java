package com.xmd.technician.common;

import com.xmd.technician.bean.CLubMember;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/7/13.
 */
public class PinyinClubUtil implements Comparator<CLubMember> {


    public int compare(CLubMember o1, CLubMember o2) {
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



