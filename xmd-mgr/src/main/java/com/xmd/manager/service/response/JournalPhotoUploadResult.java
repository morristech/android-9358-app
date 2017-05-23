package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-16.
 */

public class JournalPhotoUploadResult extends BaseResult {
    public JournalPhotoUploadResult.Data respData;

    public class Data {
        public String imageId;
    }
}
