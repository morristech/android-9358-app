package com.xmd.chat.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-7-24.
 * 快速回复
 */

public class FastReplySetting implements Serializable {
    public List<String> data;

    public FastReplySetting() {
        this.data = new ArrayList<>();
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
