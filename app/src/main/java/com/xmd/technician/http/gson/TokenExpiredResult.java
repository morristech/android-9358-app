package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-1-12.
 */
public class TokenExpiredResult {

    public String expiredReason;

    public TokenExpiredResult(String expiredReason) {
        this.expiredReason = expiredReason;
    }
}
