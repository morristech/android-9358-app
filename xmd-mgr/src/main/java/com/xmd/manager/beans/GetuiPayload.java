package com.xmd.manager.beans;

/**
 * Created by sdcm on 16-5-27.
 */
public class GetuiPayload {
    public String msgType;
    public String businessType;
    public String msgContent;
    public String msgTargetId;
    public String msgDate;
    public String appType;
    public String pushTemplateType;

    @Override
    public String toString() {
        return "GetuiPayload{" +
                "msgType='" + msgType + '\'' +
                ", businessType='" + businessType + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", msgTargetId='" + msgTargetId + '\'' +
                ", msgDate='" + msgDate + '\'' +
                ", appType='" + appType + '\'' +
                ", pushTemplateType='" + pushTemplateType + '\'' +
                '}';
    }
}
