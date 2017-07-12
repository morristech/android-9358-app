package com.xmd.permission;

import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by mo on 17-7-12.
 * 权限网络访问
 */

public interface NetService {
    @GET("/spa-manager/api/v2/manager/current/menu/list")
    Observable<BaseBean<List<Permission>>> listRolePermission();
}
