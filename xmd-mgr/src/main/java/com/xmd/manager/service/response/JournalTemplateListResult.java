package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-8.
 */

public class JournalTemplateListResult extends BaseListResult<JournalTemplateListResult.Item> {
    public static class Item {
        public int id;
        public String name;
        public Object itemsConfiguration;
        public String coverImageUrl;
        public String previewUrl;
    }
}
