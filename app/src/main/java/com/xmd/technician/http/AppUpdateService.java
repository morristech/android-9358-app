package com.xmd.technician.http;

import com.xmd.technician.http.gson.AppUpdateConfigResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sdcm on 16-1-11.
 */
public interface AppUpdateService {
    @GET(RequestConstant.URL_APP_UPDATE_CONFIG)
    Call<AppUpdateConfigResult> getAppUpdateConfig(@Query(RequestConstant.KEY_APP_ID) String appId,
                                                   @Query(RequestConstant.KEY_CLUB_CODE) String clubCode,
                                                   @Query(RequestConstant.KEY_VERSION) String version);
}
