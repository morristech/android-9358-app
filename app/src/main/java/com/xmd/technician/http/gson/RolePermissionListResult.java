package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by heyangya on 17-3-15.
 */

public class RolePermissionListResult extends BaseResult {
    public List<Permission> respData;

    public static class Permission {
        public long id;
        public String name;
        public String code;
        public List<Permission> children;
    }
}
