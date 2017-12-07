package com.xmd.inner.event;

/**
 * Created by Lhj on 17-12-4.
 */

public class EmployeeChangedEvent {

    private String techNo;
    private String techName;
    private int parentPosition;
    private int position;
    private String techId;

    public EmployeeChangedEvent(String techId,String techNo, String techName, int parentPosition, int position) {
        this.techNo = techNo;
        this.techName = techName;
        this.parentPosition = parentPosition;
        this.position = position;
        this.techId = techId;
    }


    public String getTechNo() {
        return techNo;
    }

    public void setTechNo(String techNo) {
        this.techNo = techNo;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

}
