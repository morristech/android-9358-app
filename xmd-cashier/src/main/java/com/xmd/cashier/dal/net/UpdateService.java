package com.xmd.cashier.dal.net;


import com.xmd.cashier.dal.net.response.CheckUpdateResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sdcm on 16-1-11.
 */
public interface UpdateService {
    @GET(RequestConstant.URL_APP_UPDATE_CONFIG)
    Observable<CheckUpdateResult> getAppUpdateConfig(@Query(RequestConstant.KEY_APP_ID) String appId,
                                                     @Query(RequestConstant.KEY_USER_ID) String userId,
                                                     @Query(RequestConstant.KEY_VERSION) String version);
}
