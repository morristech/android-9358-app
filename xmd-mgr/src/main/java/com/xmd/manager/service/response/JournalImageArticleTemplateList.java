package com.xmd.manager.service.response;

import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */

public class JournalImageArticleTemplateList extends BaseListResult<JournalImageArticleTemplateList.Item> {
    public static class Item {
        public String templateId;
        public List<String> templateArticles;
        public int templateImageCount;
        public String templateName;
        public String templateUrl;
    }
}
