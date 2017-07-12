package com.xmd.permission;

import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mo on 17-7-12.
 * 权限网络访问
 */

public interface NetService {
    @GET("/spa-manager/api/v2/menu/list")
    Observable<BaseBean<List<Permission>>> listRolePermission();

    /**
     * @param id     路径参数，默认为客户ID
     * @param idType ID类型，可选，customer:客户ID，emchat:环信ID
     * @return
     */
    @GET("/spa-manager/api/v2/tech/contact/permission/{id}")
    Observable<BaseBean<ContactPermissionInfo>> getContactPermissionInfo(@Path("id") String id,
                                                                         @Query("idType") String idType);
}
