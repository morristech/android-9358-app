package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-12-7.
 */

public class JournalArticlesResult extends BaseListResult<JournalArticlesResult.DATA> {

    public static class DATA {
        public int value; //文章ID
        public String desc;//文章标题
    }
}
