package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-12-7.
 */

public class JournalArticleDetailResult extends BaseResult {
    public DATA respData;

    public static class DATA {
        public int id;
        public String title;
        public String content;
    }
}
