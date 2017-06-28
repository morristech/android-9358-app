package com.xmd.technician.http.gson;

/**
 * Created by Lhj on 17-6-23.
 */

public class UploadTechPosterImageResult extends BaseResult {
    public RespDataBean respData;

    public static class RespDataBean {
        public String category;
        public String imageId;
    }
}
