package com.xmd.technician.permission;

import com.xmd.technician.common.Callback;

/**
 * Created by heyangya on 17-3-16.
 */

public interface IBusinessPermissionManager {
    //检查是否包含某个权限,一般来说不用调用，而是使用@CheckBusinessPermission注解
    boolean containPermission(String[] permissions);

    //从缓存或网络加载权限
    void loadPermissions(Callback<Void> callback);

    //检查和同步权限
    void checkAndSyncPermissions();

    //立即同步权限，不能在loadPermissions返回前调用
    void syncPermissionsImmediately();
}
