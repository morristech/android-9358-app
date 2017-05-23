package com.xmd.manager.service;


import com.xmd.manager.service.response.AppUpdateConfigResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sdcm on 16-1-11.
 */
public interface AppUpdateService {
    @GET(RequestConstant.URL_APP_UPDATE_CONFIG)
    Call<AppUpdateConfigResult> getAppUpdateConfig(@Query(RequestConstant.KEY_UPDATE_APP_ID) String appId,
                                                   @Query(RequestConstant.KEY_UPDATE_USER_ID) String userId,
                                                   @Query(RequestConstant.KEY_UPDATE_VERSION) String version);
}
