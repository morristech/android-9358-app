package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-3-14.
 */
public class CommentOrderRedPkResutlt extends BaseResult{
    public Content respData;

    public class Content{
        public int commentCount;
        public int orderCount;
        public int redPacket;
    }
}
