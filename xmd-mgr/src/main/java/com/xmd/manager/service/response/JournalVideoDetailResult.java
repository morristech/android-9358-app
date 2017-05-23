package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-12-6.
 */

public class JournalVideoDetailResult extends BaseResult {
    public DATA respData;

    public static class DATA {
        public String coverUrl;
        public String playUrl;
    }
}
