package com.xmd.technician.bean;

/**
 * Created by Lhj on 17-12-20.
 */

public class InvitationRewardBean {

    /**
     * activityId : 102
     * activityName : AAA邀请有礼
     * registerPrize : {"name":"新人礼包","imageId":"144005","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/40/oYYBAFW19bSALcm5AAA6kPj9su0578.jpg?st=j8C_5c_FkGwP05I4EqMEwg&e=1515812989","contentType":"coupon","contentId":"917569828397522944","count":null,"grantCount":0,"dayCountLimit":null,"description":"【折扣券】user_invite_1","status":"valid"}
     * sharePrize : {"name":null,"imageId":null,"imageUrl":null,"contentType":"credit","contentId":"40","count":null,"grantCount":2,"dayCountLimit":2,"description":"【积分】40","status":"valid"}
     * invitePrize : {"name":null,"imageId":null,"imageUrl":null,"contentType":"coupon","contentId":"919769486154801152","count":null,"grantCount":0,"dayCountLimit":null,"description":"【礼品券】礼品券","status":"invalid"}
     * consumePrize : {"name":null,"imageId":null,"imageUrl":null,"contentType":"service_item","contentId":"621643230756216832","count":null,"grantCount":0,"dayCountLimit":null,"description":"【项目】精华SPA","status":"invalid"}
     * startTime : null
     * endTime : null
     * description : 新人礼包仅限会所新用户领取；
     * 每个新用户仅可领取一次新人礼包；
     * 领取礼包后，您可以分享活动邀请好友，每次分享可获得现金券【测试券】一张奖励；
     * 当您邀请的好友是会所的新客户时，您可以获得礼品券【礼品券】一张奖励，好友可获得新人礼包；
     * 当好友使用您的分享的礼包，您还可获得折扣券【2折券】一张奖励；
     * 好礼领取成功后，可在 个人中心 >> 邀请有礼 >> 我的奖励 进行查看；
     * 小摩豆会所保留法律范围内的最终解释权，如有其他疑问，请咨询小摩豆会所客服。
     * status : online
     * remark : null
     * operatorName : ami1206@163.com
     * modifyTime : 1508227141000
     */

    public String activityId;
    public String activityName;
    public RegisterPrizeBean registerPrize;
    public SharePrizeBean sharePrize;
    public InvitePrizeBean invitePrize;
    public ConsumePrizeBean consumePrize;
    public long startTime;
    public long endTime;
    public String description;
    public String status;
    public String remark;
    public String operatorName;
    public long modifyTime;
    public String activityLink;

    public static class RegisterPrizeBean {
        /**
         * name : 新人礼包
         * imageId : 144005
         * imageUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/40/oYYBAFW19bSALcm5AAA6kPj9su0578.jpg?st=j8C_5c_FkGwP05I4EqMEwg&e=1515812989
         * contentType : coupon
         * contentId : 917569828397522944
         * count : null
         * grantCount : 0
         * dayCountLimit : null
         * description : 【折扣券】user_invite_1
         * status : valid
         */

        public String name;
        public String imageId;
        public String imageUrl;
        public String contentType;
        public String contentId;
        public Object count;
        public int grantCount;
        public Object dayCountLimit;
        public String description;
        public String status;
    }

    public static class SharePrizeBean {
        /**
         * name : null
         * imageId : null
         * imageUrl : null
         * contentType : credit
         * contentId : 40
         * count : null
         * grantCount : 2
         * dayCountLimit : 2
         * description : 【积分】40
         * status : valid
         */

        public Object name;
        public Object imageId;
        public Object imageUrl;
        public String contentType;
        public String contentId;
        public Object count;
        public int grantCount;
        public int dayCountLimit;
        public String description;
        public String status;
    }

    public static class InvitePrizeBean {
        /**
         * name : null
         * imageId : null
         * imageUrl : null
         * contentType : coupon
         * contentId : 919769486154801152
         * count : null
         * grantCount : 0
         * dayCountLimit : null
         * description : 【礼品券】礼品券
         * status : invalid
         */

        public Object name;
        public Object imageId;
        public Object imageUrl;
        public String contentType;
        public String contentId;
        public Object count;
        public int grantCount;
        public Object dayCountLimit;
        public String description;
        public String status;
    }

    public static class ConsumePrizeBean {
        /**
         * name : null
         * imageId : null
         * imageUrl : null
         * contentType : service_item
         * contentId : 621643230756216832
         * count : null
         * grantCount : 0
         * dayCountLimit : null
         * description : 【项目】精华SPA
         * status : invalid
         */

        public Object name;
        public Object imageId;
        public Object imageUrl;
        public String contentType;
        public String contentId;
        public Object count;
        public int grantCount;
        public Object dayCountLimit;
        public String description;
        public String status;
    }
}
