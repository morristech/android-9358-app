package com.xmd.technician.permission;

import com.xmd.app.event.EventLogout;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.Callback;
import com.xmd.technician.common.Logger;
import com.xmd.technician.event.EventExitClub;
import com.xmd.technician.event.EventJoinedClub;
import com.xmd.technician.http.gson.RolePermissionListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by heyangya on 17-3-14.
 */

public class BusinessPermissionManager implements IBusinessPermissionManager {
    private static final IBusinessPermissionManager ourInstance = new BusinessPermissionManager();

    public static IBusinessPermissionManager getInstance() {
        return ourInstance;
    }

    private Callback<Void> mCallback;
    private Set<String> mPermissionList = new HashSet<>();
    private static final int SYNC_INTERVAL = 10 * 60 * 1000;//打开时会检查权限间隔
    private boolean mIsLoaded;

    private BusinessPermissionManager() {
        //处理从网络加载权限
        RxBus.getInstance().toObservable(RolePermissionListResult.class).subscribe(
                result -> {
                    if (result.statusCode == 200 && result.respData != null) {
                        mPermissionList.clear();
                        parsePermissionResult(result.respData);
                        if (mCallback != null) {
                            //为加载权限结果
                            mCallback.onResult(null, null);
                            SharedPreferenceHelper.setPermissionList(mPermissionList);
                        } else {
                            //为同步权限结果
                            checkPermissionChanged();
                        }
                        mIsLoaded = true;
                    } else {
                        if (mCallback != null) {
                            mCallback.onResult(new Throwable(result.msg), null);
                        }
                    }
                    mCallback = null;
                }
        );

        //处理登出事件
        RxBus.getInstance().toObservable(EventLogout.class).subscribe(
                eventLogout -> {
                    Logger.i("permission center: process eventLogout");
                    clearPermission();
                }
        );

        //处理退出会所事件
        RxBus.getInstance().toObservable(EventExitClub.class).subscribe(
                eventExitClub -> {
                    Logger.i("permission center: process eventExitClub");
                    clearPermission();
                    //重启应用
                    TechApplication.restart();
                }
        );

        //处理加入会所成功事件
        RxBus.getInstance().toObservable(EventJoinedClub.class).subscribe(
                eventJoinedClub -> {
                    Logger.i("permission center: process eventJoinedClub");
                    if (mIsLoaded) {
                        clearPermission();
                        //重启应用
                        TechApplication.restart();
                    }
                }
        );
    }

    private void clearPermission() {
        mPermissionList.clear();
        SharedPreferenceHelper.setPermissionList(null);
    }

    //解析权限数据
    private void parsePermissionResult(List<RolePermissionListResult.Permission> permissionList) {
        if (permissionList == null || permissionList.size() == 0) {
            return;
        }
        for (RolePermissionListResult.Permission permission : permissionList) {
            if (!mPermissionList.contains(permission.code)) {
                mPermissionList.add(permission.code);
            }
            if (permission.children != null && permission.children.size() > 0) {
                parsePermissionResult(permission.children);
            }
        }
    }

    //检查权限是否变化
    private void checkPermissionChanged() {
        Set<String> oldPermissions = SharedPreferenceHelper.getPermissionList();
        if (oldPermissions == null || oldPermissions.size() != mPermissionList.size()) {
            onPermissionChanged();
            return;
        }
        for (String permission : oldPermissions) {
            if (!mPermissionList.contains(permission)) {
                onPermissionChanged();
                return;
            }
        }
    }

    private void onPermissionChanged() {
        Logger.i("----onPermissionChanged----");
        SharedPreferenceHelper.setPermissionList(mPermissionList);
        //重启应用
        TechApplication.restart();
    }

    //检查是否包含某个权限,一般来说不用调用，而是使用@CheckBusinessPermission注解
    @Override
    public boolean containPermission(String[] permissions) {
        boolean pass = true;

        for (String permissionCode : permissions) {
            if (!mPermissionList.contains(permissionCode)) {
                pass = false;
                break;
            }
        }
        return pass;
//        return true;
    }

    /******************
     * 其他模块调用
     ***********************************/
    //从缓存或网络加载权限
    @Override
    public void loadPermissions(Callback<Void> callback) {
        //优先从缓存加载权限
        Set<String> cachePermission = SharedPreferenceHelper.getPermissionList();
        if (cachePermission != null && cachePermission.size() > 0) {
            mPermissionList.addAll(cachePermission);
            callback.onResult(null, null);
            mIsLoaded = true;
        } else {
            //从缓存加载失败，从网络拉取权限
            mCallback = callback;
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ROLE_PERMISSION);
        }
    }

    //检查和同步权限
    @Override
    public void checkAndSyncPermissions() {
        if (SharedPreferenceHelper.getPermissionSyncDate() + SYNC_INTERVAL < System.currentTimeMillis()) {
            syncPermissionsImmediately();
        }
    }

    //立即同步权限，不能在loadPermissions返回前调用
    @Override
    public void syncPermissionsImmediately() {
        Logger.i("-----------sync permission -------------");
        mCallback = null;
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ROLE_PERMISSION);
    }
}
