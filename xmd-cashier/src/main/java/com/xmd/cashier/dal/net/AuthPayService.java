package com.xmd.cashier.dal.net;

import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.m.network.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zr on 18-3-28.
 */

public interface AuthPayService {

    @FormUrlEncoded
    @POST(RequestConstant.URL_AUTH_CODE_RECHARGE)
    Observable<MemberRecordResult> doAuthCodeRecharge(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                      @Field(RequestConstant.KEY_ORDER_ID) String orderId,
                                                      @Field(RequestConstant.KEY_AUTH_CODE) String authCode,
                                                      @Field(RequestConstant.KEY_SIGN) String requestSign);

    //收银主扫
    @FormUrlEncoded
    @POST(RequestConstant.URL_AUTH_CODE_ACTIVE)
    Observable<BaseBean> activeAuthPay(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_AMOUNT) String amount,
                                       @Field(RequestConstant.KEY_PAY_NO) String payNo,
                                       @Field(RequestConstant.KEY_AUTH_CODE) String authCode,
                                       @Field(RequestConstant.KEY_PAY_ORDER_ID) String payOrderId,
                                       @Field(RequestConstant.KEY_SIGN) String requestSign);
}
