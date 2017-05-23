package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-7.
 */

public class JournalUpdateStatusResult extends BaseResult {
    public Data respData;

    public static class Data {
        public int sequenceNo;
        public String modifyDate;
    }
}
