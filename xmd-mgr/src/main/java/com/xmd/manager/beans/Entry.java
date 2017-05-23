package com.xmd.manager.beans;

/**
 * Created by sdcm on 16-2-2.
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
