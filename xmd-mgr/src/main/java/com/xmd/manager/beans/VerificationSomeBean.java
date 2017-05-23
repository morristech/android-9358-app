package com.xmd.manager.beans;

/**
 * Created by sdcm on 17-3-6.
 */

public class VerificationSomeBean {
    public String couponNo;
    public boolean verificationSucceed;
    public String message;

    public VerificationSomeBean(String couponNo, String message, boolean verificationResult) {
        this.couponNo = couponNo;
        this.message = message;
        this.verificationSucceed = verificationResult;
    }

}
