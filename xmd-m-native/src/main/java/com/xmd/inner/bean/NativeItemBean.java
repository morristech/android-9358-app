package com.xmd.inner.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-30.
 */

public class NativeItemBean {

    private List<NativeEmployeeBean> employeeList;
    private String itemId;           //服务项Id
    private int itemCount;       //数量
    private String consumeName; //消费项
    private String serviceName; //服务项
    private String itemType; // spa,goods
    private int selectedPosition;
    public NativeItemBean(){

    }

    public NativeItemBean(List<NativeEmployeeBean> employeeList, String itemId, int itemCount, String consumeName, String serviceName, String itemType) {
        this.employeeList = employeeList;
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.consumeName = consumeName;
        this.serviceName = serviceName;
        this.itemType = itemType;
    }

    public List<NativeEmployeeBean> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<NativeEmployeeBean> employeeList) {
        this.employeeList = employeeList;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getConsumeName() {
        return consumeName;
    }

    public void setConsumeName(String consumeName) {
        this.consumeName = consumeName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
