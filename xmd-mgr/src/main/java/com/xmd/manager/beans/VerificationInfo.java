package com.xmd.manager.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lhj on 2016/12/9.
 */

public class VerificationInfo implements Serializable {
    public String amount;
    public String first;
    public String remark;

    public List<Info> list;

    public static class Info implements Serializable {
        public String title;
        public String text;
    }

}
