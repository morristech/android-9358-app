package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-3-15.
 */
public class InviteCodeResult extends BaseResult {
    public Content respData;

    public class Content{
        public String id;
        public String name;
    }
}
