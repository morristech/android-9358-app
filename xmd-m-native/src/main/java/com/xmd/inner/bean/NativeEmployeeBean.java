package com.xmd.inner.bean;



/**
 * Created by Lhj on 17-11-30.
 */

public class NativeEmployeeBean {


    private Integer bellId; //上钟类型Id
    private String employeeId; //服务人员Id
    private String serviceType;//服务项目类型 goods，spa
    private String employeeName;


    public NativeEmployeeBean() {

    }

    public NativeEmployeeBean(int bellId, String employeeId) {
        this.bellId = bellId;
        this.employeeId = employeeId;
    }

    public NativeEmployeeBean(Integer bellId, String employeeName, String employeeId, String serviceType) {
        this.bellId = bellId;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.serviceType = serviceType;
    }

    public Integer getBellId() {
        return bellId;
    }

    public void setBellId(Integer bellId) {
        this.bellId = bellId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

}
