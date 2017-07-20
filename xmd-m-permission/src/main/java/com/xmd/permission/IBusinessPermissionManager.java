package com.xmd.permission;

import com.shidou.commonlibrary.Callback;

/**
 * Created by heyangya on 17-3-16.
 */

public interface IBusinessPermissionManager {
    void init();

    //检查是否包含某个权限,一般来说不用调用，而是使用@CheckBusinessPermission注解
    boolean containPermissions(String[] permissions);

    boolean containPermission(String permission);

    //从缓存或网络加载权限
    void loadPermissions(Callback<Void> callback);

    //检查和同步权限
    void checkAndSyncPermissions();

    //立即同步权限，不能在loadPermissions返回前调用
    void syncPermissionsImmediately(Callback<Void> callback);
}
