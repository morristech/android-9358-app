package com.xmd.manager.auth;

import com.xmd.manager.ClubData;
import com.xmd.manager.beans.AuthData;
import com.xmd.permission.BusinessPermissionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-8-17.
 */
public class AuthHelper {

    private static Map<String, AuthData> cache = new HashMap<>();

    /**
     * 清除缓存
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * check whether this role is able to display this menu
     *
     * @param authCode
     * @return
     */
    public static AuthData checkAuth(String authCode) {
        if (cache.containsKey(authCode)) {
            return cache.get(authCode);
        }
        List<AuthData> menus = ClubData.getInstance().getAuthDataList();
        if (menus != null && !menus.isEmpty()) {
            return doCheckAuth(menus, authCode);
        }
        return null;
    }

    /**
     * Whether the authCode exists
     *
     * @param authCode
     * @return
     */
    public static boolean checkAuthorized(String authCode) {
        return BusinessPermissionManager.getInstance().containPermission(authCode);
    }

    /**
     * @param list
     * @param authCode
     * @return
     */
    private static AuthData doCheckAuth(List<AuthData> list, String authCode) {
        for (AuthData authData : list) {
            if (authCode.equals(authData.code)) {
                return authData;
            }
            if (authData.children != null) {
                AuthData child = doCheckAuth(authData.children, authCode);
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }
}
