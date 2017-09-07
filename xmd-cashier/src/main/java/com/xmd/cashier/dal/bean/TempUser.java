package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-9-6.
 */

public class TempUser {
    public String userName;
    public String userPhone;

    public TempUser(String userPhone, String userName) {
        this.userPhone = userPhone;
        this.userName = userName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof TempUser)) {
            return false;
        }

        TempUser other = (TempUser) obj;
        return userName.equals(other.userName) && userPhone.equals(other.userPhone);
    }
}
