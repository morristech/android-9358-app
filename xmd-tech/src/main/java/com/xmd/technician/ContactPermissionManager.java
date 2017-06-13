package com.xmd.technician;

/**
 * Created by mo on 17-6-13.
 * 联系权限
 */

public class ContactPermissionManager {
    private static final ContactPermissionManager ourInstance = new ContactPermissionManager();

    public static ContactPermissionManager getInstance() {
        return ourInstance;
    }

    private ContactPermissionManager() {
    }


}
