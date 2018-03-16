package com.xmd.black.httprequest;


import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.black.bean.AddToBlacklistResult;
import com.xmd.black.bean.BlackListResult;
import com.xmd.black.bean.CreateCustomerResult;
import com.xmd.black.bean.EditCustomerResult;
import com.xmd.black.bean.InBlacklistResult;
import com.xmd.black.bean.InUserBlacklistResult;
import com.xmd.black.bean.ManagerEditCustomerResult;
import com.xmd.black.bean.MarkResult;
import com.xmd.black.bean.RemoveFromBlacklistResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lhj on 17-7-21.
 */

public interface NetService {


    @GET(HttpRequestConstant.URL_IN_BLACKLIST)
    Observable<InBlacklistResult> inBlacklist(@Query("friendId") String friendId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_ADD_TO_BLACKLIST)
    Observable<AddToBlacklistResult> addToBlacklist(@Field("friendId") String friendId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_REMOVE_FROM_BLACKLIST)
    Observable<RemoveFromBlacklistResult> removeFromBlacklist(@Field("friendId") String friendId);

    @GET(HttpRequestConstant.URL_GET_TECH_BLACKLIST)
    Observable<BlackListResult> getBlacklist(@Query("page") String page,
                                             @Query("pageSize") String pageSize);

    @GET(HttpRequestConstant.URL_IN_USER_BLACKLIST)
    Observable<InUserBlacklistResult> inUserBlacklist(@Query("friendChatId") String friendChatId);

    @GET(HttpRequestConstant.URL_GET_CONTACT_MARK)
    Observable<MarkResult> getContactMark(@Query("tagType") String tagType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_ADD_CUSTOMER_CREATE)
    Observable<CreateCustomerResult> addCreateCustomer(@Field("noteName") String noteName,
                                                       @Field("phoneNum") String phoneNum,
                                                       @Field("impression") String impression,
                                                       @Field("remark") String remark);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_EDIT_CUSTOMER)
    Observable<EditCustomerResult> addOrEditCustomer(@Field("userId") String userId,
                                                     @Field("id") String id,
                                                     @Field("phoneNum") String phoneNum,
                                                     @Field("remark") String remark,
                                                     @Field("noteName") String noteName,
                                                     @Field("impression") String impression);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_MANAGER_USER_EDIT)
    Observable<ManagerEditCustomerResult> managerEditCustomer(@Field("id") String id,
                                                              @Field("noteName") String noteName,
                                                              @Field("phoneNum") String phoneNum,
                                                              @Field("remark") String remark);
}
