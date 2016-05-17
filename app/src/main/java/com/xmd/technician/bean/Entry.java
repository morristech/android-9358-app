package com.xmd.technician.bean;

/**
 * Created by linms@xiaomodo.com on 16-5-5.
 */
public class Entry {
    public String key;
    public String value;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return key + " : " + value;
    }
}
