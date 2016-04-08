package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-3-14.
 */
public class CommentOrderRedPkResutlt extends BaseResult{
    public Content respData;

    public class Content{
        public int unreadCommentCount;
        public int allCommentCount;
        public int accountAmount;
        public int orderCount;
        public String techStatus;
        public String techStatusDesc;
    }
}
