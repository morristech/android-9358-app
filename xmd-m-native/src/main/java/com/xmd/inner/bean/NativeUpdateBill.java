package com.xmd.inner.bean;

import java.util.List;

/**
 * Created by Lhj on 17-12-6.
 */

public class NativeUpdateBill {
    private List<NativeItemBean> itemList;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<NativeItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<NativeItemBean> itemList) {
        this.itemList = itemList;
    }


}
