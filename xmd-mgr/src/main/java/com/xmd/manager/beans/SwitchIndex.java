package com.xmd.manager.beans;

/**
 * Created by Administrator on 2016/12/26.
 */

public class SwitchIndex {
    public int index;
    public int selectedIndex;
    public String startTime;
    public String endTime;

    public SwitchIndex(int index, int selectedIndex, String st, String ed) {
        this.index = index;
        this.selectedIndex = selectedIndex;
        this.startTime = st;
        this.endTime = ed;
    }
}
