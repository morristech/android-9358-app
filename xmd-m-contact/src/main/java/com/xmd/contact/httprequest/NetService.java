package com.xmd.contact.httprequest;


import com.xmd.contact.bean.ClubEmployeeListResult;
import com.xmd.contact.bean.ContactAllListResult;
import com.xmd.contact.bean.ContactRecentListResult;
import com.xmd.contact.bean.ContactRegisterListResult;
import com.xmd.contact.bean.ManagerContactAllListResult;
import com.xmd.contact.bean.ManagerContactRecentListResult;
import com.xmd.contact.bean.NearbyCusCountResult;
import com.xmd.contact.bean.TagListResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Lhj on 17-7-1.
 */

public interface NetService {

    // 获取会所附近客户数量
    @GET(RequestConstant.URL_GET_NEARBY_CUS_COUNT)
    Observable<NearbyCusCountResult> getNearbyCusCount();

    //会所全部客户
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_CUSTOMER_USER_ALL_LIST)
    Observable<ContactAllListResult> getAllContactList(@Field(RequestConstant.KEY_PAGE) String page,
                                                       @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                       @Field(RequestConstant.KEY_CUSTOMER_LEVEL) String customerLevel,
                                                       @Field(RequestConstant.KEY_CUSTOMER_TYPE) String customerType,
                                                       @Field(RequestConstant.KEY_REMARK) String remark,
                                                       @Field(RequestConstant.KEY_TECH_NO) String serialNo,
                                                       @Field(RequestConstant.KEY_USER_GROUP) String userGroup,
                                                       @Field(RequestConstant.KEY_USER_NAME) String userName);

    //我的拓客
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_CUSTOMER_USER_REGISTER_LIST)
    Observable<ContactRegisterListResult> getRegisterContactList(@Field(RequestConstant.KEY_PAGE) String page,
                                                                 @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                                 @Field(RequestConstant.KEY_CUSTOMER_LEVEL) String customerLevel,
                                                                 @Field(RequestConstant.KEY_CUSTOMER_TYPE) String customerType,
                                                                 @Field(RequestConstant.KEY_REMARK) String remark,
                                                                 @Field(RequestConstant.KEY_TECH_NO) String serialNo,
                                                                 @Field(RequestConstant.KEY_USER_GROUP) String userGroup,
                                                                 @Field(RequestConstant.KEY_USER_NAME) String userName);

    //最近访客
    @GET(RequestConstant.URL_GET_CLUB_CUSTOMER_USER_RECENT_LIST)
    Observable<ContactRecentListResult> getRecentContactList();

    //会所联系人
    @GET(RequestConstant.URL_GET_CLUB_EMPLOYEE_LIST)
    Observable<ClubEmployeeListResult> clubEmployeeList();

    //会所全部客户
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MANAGER_CUSTOMER_USER_ALL_LIST)
    Observable<ManagerContactAllListResult> getManagerAllContactList(@Field(RequestConstant.KEY_PAGE) String page,
                                                                     @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                                     @Field(RequestConstant.KEY_CUSTOMER_LEVEL) String customerLevel,
                                                                     @Field(RequestConstant.KEY_CUSTOMER_TYPE) String customerType,
                                                                     @Field(RequestConstant.KEY_REMARK) String remark,
                                                                     @Field(RequestConstant.KEY_TECH_NO) String serialNo,
                                                                     @Field(RequestConstant.KEY_USER_GROUP) String userGroup,
                                                                     @Field(RequestConstant.KEY_USER_NAME) String userName);

    //    //会所全部最近访客
//    @GET(RequestConstant.URL_GET_MANAGER_CUSTOMER_USER_RECENT_LIST)
//    Observable<ManagerContactRecentListResult> getManagerRecentContactList(@Query(RequestConstant.KEY_PAGE) String page,
//                                                                           @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize,
//                                                                           @Query(RequestConstant.KEY_CUSTOMER_TYPE) String customerType,
//                                                                           @Query(RequestConstant.KEY_USER_NAME) String userName);
    @GET(RequestConstant.URL_GET_MANAGER_CUSTOMER_USER_RECENT_LIST)
    Observable<ManagerContactRecentListResult> getManagerRecentContactList();

    //标签列表
    @GET(RequestConstant.URL_GET_MANAGER_TAG_ALL_LIST)
    Observable<TagListResult> getTagList();


}
