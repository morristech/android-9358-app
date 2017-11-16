package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-16.
 */

public class EmployeeGroupInfo {
    public long groupId;
    public String groupName;
    public String groupRole;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof EmployeeGroupInfo)) {
            return false;
        }
        EmployeeGroupInfo other = (EmployeeGroupInfo) obj;
        return groupId == other.groupId && groupName.equals(other.groupName);
    }

    @Override
    public int hashCode() {
        return (int) groupId;
    }
}
