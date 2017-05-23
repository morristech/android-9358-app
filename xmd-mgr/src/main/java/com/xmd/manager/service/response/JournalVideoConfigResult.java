package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-12-8.
 */

public class JournalVideoConfigResult extends BaseResult {
    public DATA respData;

    public static class DATA {
        public String bucketName;
        public String journalDir;
        public int width;
        public int height;
        public int frameRate;
        public int videoLength;
        public int bitrate;
    }
}
