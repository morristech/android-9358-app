package com.xmd.inner.event;

/**
 * Created by Lhj on 17-12-4.
 */

public class CategoryChangedEvent {


    private String name;
    private int position;
    private String type;
    private int selectedPosition;

    public CategoryChangedEvent(String name,String type, int position,  int selectedPosition) {
        this.name = name;
        this.position = position;
        this.type = type;
        this.selectedPosition = selectedPosition;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


}
