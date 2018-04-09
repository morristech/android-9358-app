package com.xmd.cashier.dal.net;

import com.xmd.cashier.dal.net.response.TradeBatchResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zr on 18-3-28.
 */

public interface GeneOrderService {
    //生成交易记录
    @FormUrlEncoded
    @POST(RequestConstant.URL_GENERATE_BATCH_ORDER)
    Observable<TradeBatchResult> generateBatchOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Field(RequestConstant.KEY_BATCH_NO) String batchNo,
                                                    @Field(RequestConstant.KEY_MEMBER_ID) String memberId,
                                                    @Field(RequestConstant.KEY_ORDER_IDS) String orderIds,
                                                    @Field(RequestConstant.KEY_PAY_CHANNEL) String payChannel,
                                                    @Field(RequestConstant.KEY_VERIFY_CODES) String verifyCodes,
                                                    @Field(RequestConstant.KEY_REDUCTION_AMOUNT) String reductionAmount,
                                                    @Field(RequestConstant.KEY_ORI_AMOUNT) String oriAmount,
                                                    @Field(RequestConstant.KEY_AMOUNT) String amount);
}
