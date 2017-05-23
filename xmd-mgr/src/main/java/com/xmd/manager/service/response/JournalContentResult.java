package com.xmd.manager.service.response;

import java.util.List;

/**
 * Created by heyangya on 16-11-7.
 */

public class JournalContentResult extends BaseResult {
    public Data respData;

    public static class Data {
        public int id;
        public String title;
        public String subTitle;
        public int templateId;
        public List<JournalItem> journalItemData;
    }

    public static class JournalItem {
        public String title;
        public int itemOrder;
        public String itemKey;
        public String content;
    }
}
