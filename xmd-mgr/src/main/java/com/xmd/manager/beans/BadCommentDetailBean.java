package com.xmd.manager.beans;

import java.util.List;

/**
 * Created by Lhj on 17-4-24.
 */

public class BadCommentDetailBean {
    /**
     * id : 855354091000299520
     * comment : 会所差评
     * techScore : 0
     * status : valid
     * returnStatus : N
     * createdAt : 1492767281000
     * techId : null
     * techName : null
     * techNo : null
     * orderId : null
     * commentType : club
     * phoneNum : 13137558109
     * userId : 743718740591382528
     * userName : 算法的技术
     * avatar : 151947
     * userEmchatId : e5a3c92d839bec160b194708a6780655
     * commentRateList : [{"id":33,"userId":"743718740591382528","userName":"算法的技术","techId":null,"techName":null,"clubId":"601679316694081536","clubName":"刘哥会所","commentId":"855354091000299520","commentRate":20,"commentTagId":16,"commentTagName":"会所环境","commentTagType":2,"createTime":1492767281000},{"id":34,"userId":"743718740591382528","userName":"算法的技术","techId":null,"techName":null,"clubId":"601679316694081536","clubName":"刘哥会所","commentId":"855354091000299520","commentRate":40,"commentTagId":17,"commentTagName":"接待服务","commentTagType":2,"createTime":1492767281000}]
     * impression : null
     * isAnonymous : N
     * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/58/oIYBAFdzdDKACJukAAA4JjMaJA4314.png?st=us2ixiyCG328RD-KVAPKsg&e=1493031144
     */

    public String id;
    public String comment;
    public int techScore;
    public String status;
    public String returnStatus;
    public long createdAt;
    public String techId;
    public String techName;
    public String techNo;
    public String orderId;
    public String commentType;
    public String phoneNum;
    public String userId;
    public String userName;
    public String avatar;
    public String userEmchatId;
    public String impression;
    public String isAnonymous;
    public String avatarUrl;
    public List<CommentRateListBean> commentRateList;
}
