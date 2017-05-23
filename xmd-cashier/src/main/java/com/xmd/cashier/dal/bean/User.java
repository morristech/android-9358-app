package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by heyangya on 16-9-1.
 */

public class User implements Serializable {
    private static final long serialVersionUID = 0x894c1764L;
    public String userId;
    public String token;
    public String loginName;
    public String userName;

    public String clubName;
    public String clubIconUrl;
    public String clubId;

    @Override
    public String toString() {
        return "userId=" + userId + ",token=" + token + ",loginName=" + loginName + ",userName=" + userName + ",clubIconUrl:" + clubIconUrl;
    }
}
