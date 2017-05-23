package com.xmd.manager.service.response;

import java.io.Serializable;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalListResult extends BaseListResult<JournalListResult.JournalListItem> {

    public static class JournalListItem implements Serializable {
        public int id;
        public int sequenceNo;
        public int likeCount;
        public int viewCount;
        public int shareCount;
        public int status;
        public String title;
        public String subTitle;
        public String modifyDate;
        public String previewUrl;
    }
}
