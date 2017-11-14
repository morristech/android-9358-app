package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-8.
 */

public class InnerTechInfo {
    public String avatar;
    public String avatarUrl;
    public EmployeeGroupInfo employeeGroupInfo;
    public String id;
    public String name;
    public String status;
    public String techNo;
    public String telephone;

    public class EmployeeGroupInfo {
        public long groupId;
        public String groupName;
        public String groupRole;
    }

    public boolean selected;
}
