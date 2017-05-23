package com.xmd.manager.push;

/**
 * Created by sdcm on 15-12-7.
 * <p>
 * <p>
 * payload: {"msgType":"notice_msg",
 * "businessType":"new_order",
 * "msgContent":"您有新的订单，预约人12121，13760609008，请尽快处理。",
 * "msgTargetId":"93",
 * "msgDate":"2015-12-07 11:14:56",
 * "appType":"android",
 * "pushTemplateType":"transmission_template,notification_template",
 * "noticeTitle":"新订单",
 * "logo":"push.png",
 * "noticeUrl":"http://www.baidu.com",
 * "logoUrl":null,
 * "transmissionType":2,
 * "clearable":true,
 * "ring":true,
 * "vibrate":true
 * }
 */

public class WrapperMsg {
    public String msgType;
    public String businessType;
    public String msgContent;
    public String msgTargetId;
    public String msgDate;
    public String appType;
    public String pushTemplateType;
    public String noticeTitle;
    public String logo;
    public String logoUrl;
    public String noticeUrl;
    public String transmissionType;
    public boolean ring;
    public boolean clearable;
    public boolean vibrate;

}
