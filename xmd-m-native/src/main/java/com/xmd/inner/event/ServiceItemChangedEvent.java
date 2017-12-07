package com.xmd.inner.event;

/**
 * Created by Lhj on 17-12-5.
 */

public class ServiceItemChangedEvent {

    private String itemId;
    private String itemName;
    private int parentPosition;

    public ServiceItemChangedEvent(String itemId, String itemName, int parentPosition) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.parentPosition = parentPosition;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }
}
