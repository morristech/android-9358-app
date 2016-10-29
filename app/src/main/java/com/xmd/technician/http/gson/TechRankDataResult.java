package com.xmd.technician.http.gson;

/**
 * Created by Administrator on 2016/10/26.
 */
public class TechRankDataResult extends BaseResult {

    /**
     * commentRanking : {"id":"748081899301244928","name":"7777","avatar":"153296","serialNo":"","counts":"1","avatarUrl":null}
     * userRanking : {"id":"748081899301244928","name":"7777","avatar":"153296","serialNo":"","counts":"1","avatarUrl":null}
     * paidRanking : {"id":"748081899301244928","name":"7777","avatar":"153296","serialNo":"","counts":"1","avatarUrl":null}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * id : 748081899301244928
         * name : 7777
         * avatar : 153296
         * serialNo : 
         * counts : 1
         * avatarUrl : null
         */

        public CommentRankingBean commentRanking;
        /**
         * id : 748081899301244928
         * name : 7777
         * avatar : 153296
         * serialNo : 
         * counts : 1
         * avatarUrl : null
         */

        public UserRankingBean userRanking;
        /**
         * id : 748081899301244928
         * name : 7777
         * avatar : 153296
         * serialNo : 
         * counts : 1
         * avatarUrl : null
         */

        public PaidRankingBean paidRanking;


        public static class CommentRankingBean {
            public String id;
            public String name;
            public String avatar;
            public String serialNo;
            public String counts;
            public Object avatarUrl;
    
        }

        public static class UserRankingBean {
            public String id;
            public String name;
            public String avatar;
            public String serialNo;
            public String counts;
            public Object avatarUrl;

        }

        public static class PaidRankingBean {
            public String id;
            public String name;
            public String avatar;
            public String serialNo;
            public String counts;
            public Object avatarUrl;

        }
    }
}
