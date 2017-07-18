package com.xmd.permission;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.SpConstants;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.event.EventRestartApplication;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.permission.event.EventRequestSyncPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscription;

/**
 * Created by heyangya on 17-3-14.
 */

public class BusinessPermissionManager implements IBusinessPermissionManager {
    private static final IBusinessPermissionManager ourInstance = new BusinessPermissionManager();

    public static IBusinessPermissionManager getInstance() {
        return ourInstance;
    }

    private Subscription subscription;
    private Set<String> mPermissionList = new HashSet<>();
    private static final int SYNC_INTERVAL = 10 * 60 * 1000;//打开时会检查权限间隔
    private XmdApp xmdApp = XmdApp.getInstance();

    private BusinessPermissionManager() {

    }

    public void init() {
        EventBusSafeRegister.register(this);
    }

    @Subscribe
    public void onRequestEvent(EventRequestSyncPermission event) {
        syncPermissionsImmediately(null);
    }

    @Subscribe
    public void onLogoutEvent(EventLogout eventLogout) {
        clearPermission();
    }

    @Subscribe
    public void onLoginEvent(EventLogin eventLogout) {

    }

    private void clearPermission() {
        mPermissionList.clear();
        savePermissionList(null);
    }

    //解析权限数据
    private void parsePermissionResult(List<Permission> permissionList) {
        if (permissionList == null || permissionList.size() == 0) {
            return;
        }
        for (Permission permission : permissionList) {
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
        Set<String> oldPermissions = getCachePermissionList();
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
        XLogger.i("----onPermissionChanged----");
        xmdApp.getSp().edit().putStringSet(SpConstants.KEY_PERMISSION, mPermissionList).apply();
        //重启应用
        EventBus.getDefault().post(new EventRestartApplication());
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
    }

    /******************
     * 其他模块调用
     ***********************************/
    //从缓存或网络加载权限
    @Override
    public void loadPermissions(Callback<Void> callback) {
        //优先从缓存加载权限
        Set<String> cachePermission = getCachePermissionList();
        if (cachePermission != null && cachePermission.size() > 0) {
            mPermissionList.addAll(cachePermission);
            callback.onResponse(null, null);
        } else {
            //从缓存加载失败，从网络拉取权限
            syncPermissionsImmediately(callback);
        }
    }

    //检查和同步权限
    @Override
    public void checkAndSyncPermissions() {
        if (getPermissionSyncDate() + SYNC_INTERVAL < System.currentTimeMillis()) {
            syncPermissionsImmediately(null);
        }
    }

    //立即同步权限，不能在loadPermissions返回前调用
    @Override
    public void syncPermissionsImmediately(final Callback<Void> callback) {
        XLogger.i("-----------sync permission -------------");
        if (subscription != null) {
            subscription.unsubscribe();
        }
        Observable<BaseBean<List<Permission>>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listRolePermission();
        subscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<Permission>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<Permission>> result) {
                mPermissionList.clear();
                savePermissionSyncDate(System.currentTimeMillis());
                parsePermissionResult(result.getRespData());
                if (callback != null) {
                    //为加载权限结果
                    callback.onResponse(null, null);
                    savePermissionList(mPermissionList);
                } else {
                    //为同步权限结果
                    checkPermissionChanged();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (callback != null) {
                    callback.onResponse(null, e);
                }
            }
        });
    }

    private Set<String> getCachePermissionList() {
        return xmdApp.getSp().getStringSet(SpConstants.KEY_PERMISSION, null);
    }

    private void savePermissionList(Set<String> permissionList) {
        xmdApp.getSp().edit().putStringSet(SpConstants.KEY_PERMISSION, permissionList).apply();
    }

    private void savePermissionSyncDate(Long utcTime) {
        xmdApp.getSp().edit().putLong(SpConstants.KEY_PERMISSION_SYNC_DATE, utcTime).apply();
    }

    private long getPermissionSyncDate() {
        return xmdApp.getSp().getLong(SpConstants.KEY_PERMISSION_SYNC_DATE, 0);
    }
}
