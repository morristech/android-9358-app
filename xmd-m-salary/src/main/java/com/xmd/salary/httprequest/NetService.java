package com.xmd.salary.httprequest;

import com.xmd.salary.httprequest.response.CommissionDetailResult;
import com.xmd.salary.httprequest.response.CommissionSettingResult;
import com.xmd.salary.httprequest.response.CommissionSumAmountResult;
import com.xmd.salary.httprequest.response.CommissionSumDataResult;
import com.xmd.salary.httprequest.response.SalarySettingResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lhj on 17-11-22.
 */

public interface NetService {

    @GET(RequestConstant.GET_COMMISSION_SALARY_SETTING)
    Observable<SalarySettingResult> commissionSalarySetting();

    @GET(RequestConstant.GET_COMMISSION_DETAIL_RECORDS)
    Observable<CommissionDetailResult> commissionDetailRecords(@Query("page") String page,
                                                               @Query("pageSize") String pageSize,
                                                               @Query("startDate") String startDate,
                                                               @Query("endDate") String endDate,
                                                               @Query("type") String type);

    @GET(RequestConstant.GET_COMMISSION_SUM_DATA)
    Observable<CommissionSumDataResult> commissionSumData(@Query("startDate") String startDate,
                                                          @Query("endDate") String endDate,
                                                          @Query("type") String type);

    @GET(RequestConstant.GET_COMMISSION_SUM_AMOUNT)
    Observable<CommissionSumAmountResult> commissionSumAmount(@Query("startDate") String startDate,
                                                              @Query("endDate") String endDate);

    @GET(RequestConstant.GET_COMMISSION_COMMISSION_SETTING)
    Observable<CommissionSettingResult> clubCommissionSetting();

}
