package com.xmd.manager.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/24.
 */
public class ItemBean implements Serializable {
    public String time;
    public String number;

    public ItemBean(String time, String number) {
        this.time = time;
        this.number = number;
    }
}
