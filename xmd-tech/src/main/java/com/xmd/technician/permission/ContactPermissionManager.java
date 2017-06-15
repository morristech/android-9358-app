package com.xmd.technician.permission;

/**
 * Created by mo on 17-6-15.
 * 技师与客服的联系权限
 */

class ContactPermissionManager {
    private static final ContactPermissionManager ourInstance = new ContactPermissionManager();

    static ContactPermissionManager getInstance() {
        return ourInstance;
    }

    private ContactPermissionManager() {
    }


}
