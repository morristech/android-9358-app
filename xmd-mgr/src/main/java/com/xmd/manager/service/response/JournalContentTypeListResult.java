package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-8.
 */

public class JournalContentTypeListResult extends BaseListResult<JournalContentTypeListResult.Item> {
    public static class Item {
        public int id;
        public String constValue;
        public String constKey;
        public String constDescription;
    }
}
