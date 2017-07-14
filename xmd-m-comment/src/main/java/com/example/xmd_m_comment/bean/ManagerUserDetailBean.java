package com.example.xmd_m_comment.bean;

import java.util.List;

/**
 * Created by Lhj on 17-7-10.
 */

public class ManagerUserDetailBean {

    /**
     * groupList : [{"id":77,"name":"个人资料创建","createTime":1483758942000,"createUserId":null,"createType":"user","description":"","totalCount":2,"userNames":null}]
     * creditStatInfo : {"id":"768365691333013504","amount":988012,"freezeAmount":0,"totalAmount":1067896,"usedAmount":20200,"rewardAmount":61833}
     * memberInfo : {"id":5,"name":"算法的技术","phoneNum":"13137558109","cardNo":"5895681612000001","accountId":"807043347448360960","accountAmount":999000,"memberTypeId":13,"memberTypeName":"普通","freezeAmount":0,"discount":1000,"clubId":"601679316694081536","createTime":"2016-12-09 10:05:02","modifyTime":null,"operatorName":null,"clubName":null,"clubImage":null,"styleId":1,"description":"请在会员结账时主动提供会员支付二维码","amount":999000}
     * userDetailModel : {"id":"768364810336210944","userId":"743718740591382528","userName":"算法的技术","userLoginName":"13137558109","userNoteName":"","remark":"","techId":null,"techLoginName":null,"clubId":"601679316694081536","createdAt":"2016-08-24","chanel":null,"impression":null,"emchatId":"e5a3c92d839bec160b194708a6780655","openId":"oPpRlwWgZtD1i2eCaNiDPR5ng3XU","belongsTechName":null,"belongsTechSerialNo":null,"orderCount":0,"rewardAmount":0,"rewardAmounts":0,"registerDate":"2016-06-17 16:15:40","recentVisitDate":"2017-06-08 10:17:03","consumeDate":null,"shopCount":1,"consumeAmount":0,"rewardCount":13,"commentCount":0,"visitCount":19,"avatarUrl":"","customerType":"fans_wx_user"}
     * userTagList : [{"id":966,"tagId":57,"tagName":"常客","clubId":"601679316694081536","userId":"743718740591382528"},{"id":1059,"tagId":58,"tagName":"未激活","clubId":"601679316694081536","userId":"743718740591382528"},{"id":1152,"tagId":59,"tagName":"大客","clubId":"601679316694081536","userId":"743718740591382528"}]
     */

    public ManagerCreditStatInfoBean creditStatInfo;
    public ManagerMemberInfoBean memberInfo;
    public ManagerUserDetailModelBean userDetailModel;
    public List<ManagerGroupListBean> groupList;
    public List<ManagerUserTagListBean> userTagList;
}
