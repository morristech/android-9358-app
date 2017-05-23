package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-7.
 */

public class JournalSaveResult extends BaseResult {
    public Data respData;

    public static class Data {
        public int journalId;
        public String previewUrl;
    }
}
