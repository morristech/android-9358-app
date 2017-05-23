package com.xmd.manager.service.response;


import com.xmd.manager.beans.VerificationDetailBean;
import com.xmd.manager.beans.VerificationRecordCouponDetailBean;

import java.util.List;


/**
 * Created by Lhj on 2017/1/12.
 */

public class VerificationRecordDetailResult extends BaseResult {

    /**
     * respData : {"record":{"id":516,"userId":"648764903829540864","userName":"mjflx","avatar":null,"telephone":"17727979912","businessType":"coupon","businessId":"702703270044377088","verifyCode":"176163999427","operatorId":"601634063966539776","operatorName":"刘德华","verifyTime":"2016-02-25 11:54:49","amount":10000,"originalAmount":10000,"paidType":"free","status":"Y","sourceType":"registered","description":"100元现金券","businessTypeName":"优惠券","avatarUrl":"http://wx.qlogo.cn/mmopen/KZUTxZLF6c0EWiaSetyz1pQ2Jk7k4HWqibKIKat2yVf1d6ry25HFdCUottj7tS8D7nSQ6Z770vbDosGYBLnXhDZl1fGTrjVhDZ/0","sourceTypeName":"注册有礼券"},"detail":[{"title":"优惠券名称","text":"100元现金券"},{"title":"优惠券类型","text":"现金券（注册有礼券）"},{"title":"有效期","text":"2016-02-25 11:00 至 长期有效"},{"title":"分享者","text":"sdadm"}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * record : {"id":516,"userId":"648764903829540864","userName":"mjflx","avatar":null,"telephone":"17727979912","businessType":"coupon","businessId":"702703270044377088","verifyCode":"176163999427","operatorId":"601634063966539776","operatorName":"刘德华","verifyTime":"2016-02-25 11:54:49","amount":10000,"originalAmount":10000,"paidType":"free","status":"Y","sourceType":"registered","description":"100元现金券","businessTypeName":"优惠券","avatarUrl":"http://wx.qlogo.cn/mmopen/KZUTxZLF6c0EWiaSetyz1pQ2Jk7k4HWqibKIKat2yVf1d6ry25HFdCUottj7tS8D7nSQ6Z770vbDosGYBLnXhDZl1fGTrjVhDZ/0","sourceTypeName":"注册有礼券"}
         * detail : [{"title":"优惠券名称","text":"100元现金券"},{"title":"优惠券类型","text":"现金券（注册有礼券）"},{"title":"有效期","text":"2016-02-25 11:00 至 长期有效"},{"title":"分享者","text":"sdadm"}]
         */

        public VerificationDetailBean record;
        public List<VerificationRecordCouponDetailBean> detail;

    }
}
