package com.xmd.cashier;

import com.xmd.cashier.dal.bean.CouponInfo;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CouponInfoTimeValidUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        CouponInfo couponInfo = new CouponInfo();
        couponInfo.useStartDate = "2016-08-25 10:00";
        couponInfo.useEndDate = "2016-08-26 10:00";
        assertFalse(couponInfo.isTimeValid());

        couponInfo.useStartDate = "2016-10-09 16:00";
        couponInfo.useEndDate = "2016-11-08 23:59";
        assertTrue(couponInfo.isTimeValid());

        couponInfo.useStartDate = "2016-08-25 10:00";
        couponInfo.useEndDate = "2016-09-01 10:00";
        couponInfo.useTimePeriod = "周一，周二，周四，周五，周六，周日 10:00 - 18:00";
        assertFalse(couponInfo.isTimeValid());

        couponInfo.useStartDate = "2016-08-25 10:00";
        couponInfo.useEndDate = "2016-09-01 10:00";
        couponInfo.useTimePeriod = "周一，周二，周三，周四，周五，周六，周日 10:00 - 18:00";
        assertTrue(couponInfo.isTimeValid());

        couponInfo.useStartDate = "2016-08-25 10:00";
        couponInfo.useEndDate = "2016-09-01 10:00";
        couponInfo.useTimePeriod = "周一，周二，周三，周四，周五，周六，周日 10:00 - 17:00";
        assertFalse(couponInfo.isTimeValid());
    }
}