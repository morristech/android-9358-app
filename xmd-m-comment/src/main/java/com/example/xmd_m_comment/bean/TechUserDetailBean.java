package com.example.xmd_m_comment.bean;

import java.util.List;

/**
 * Created by Lhj on 17-7-11.
 */

public class TechUserDetailBean {

    /**
     * memberInfo : null
     * userDetailModel : {"id":"750661634275414016","userId":"743718740591382528","userName":"算法的技术","userLoginName":"13137558109","userNoteName":"啊啊啊","remark":"","techId":"748081899301244928","techLoginName":"13137558109","clubId":"621280172275933185","createdAt":1467806654000,"chanel":"chart","impression":"、肩颈、要大力、用轻力","emchatId":"e5a3c92d839bec160b194708a6780655","openId":"oPpRlwWgZtD1i2eCaNiDPR5ng3XU","belongsTechName":"程序猿诗人","belongsTechSerialNo":"12245","orderCount":0,"rewardAmount":0,"rewardAmounts":0,"registerDate":"2016-06-17 00:00:00","recentVisitDate":"2017-05-05 18:36:38","consumeDate":null,"shopCount":1,"consumeAmount":1000,"rewardCount":0,"commentCount":0,"visitCount":0,"customerType":"fans_wx_user","avatarUrl":""}
     * userTagList : []
     */

    public MemberInfo memberInfo;
    public TechUserDetailModelBean userDetailModel;
    public List<ManagerUserTagListBean> userTagList;

    public static class MemberInfo{
        public String memberTypeId;
        public String memberTypeName;
    }
}
