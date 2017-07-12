package com.xmd.permission;

import java.util.List;

/**
 * Created by mo on 17-7-12.
 * 权限
 */

public class Permission {
    public long id;
    public String name;
    public String code;
    public List<Permission> children;
}
