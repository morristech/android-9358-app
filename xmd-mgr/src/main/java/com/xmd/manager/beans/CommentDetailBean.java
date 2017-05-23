package com.xmd.manager.beans;

import java.util.List;

/**
 * Created by Lhj on 17-4-19.
 */

public class CommentDetailBean {


    /**
     * obj : {"id":"859670706781159424","userId":"804221955724021760","userName":"vianra","techId":"854957848801579008","orderId":null,"clubId":"777344513239687168","commentType":"tech","rewardAmount":0,"comment":"","rate":100,"createdAt":"2017-05-03 15:27:23","modifyAt":null,"techName":"死心吧不约","techAvatar":"160477","techSerialNo":"23333","loginName":"15377711660","impression":"","techAvatarUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/26/oIYBAFj4YWqAYCVhAAWUazqJgEI172.png?st=BDUQrBLMDLWJK34AKp2DNg&e=1496399574","commentTypeName":"粉丝评论"}
     * commentRateList : [{"id":2480,"userId":"804221955724021760","userName":"vianra","techId":"854957848801579008","techName":"死心吧不约","clubId":"777344513239687168","clubName":"DWF测试","commentId":"859670706781159424","commentRate":100,"commentTagId":12,"commentTagName":"技师态度","commentTagType":1,"createTime":1493796443000},{"id":2481,"userId":"804221955724021760","userName":"vianra","techId":"854957848801579008","techName":"死心吧不约","clubId":"777344513239687168","clubName":"DWF测试","commentId":"859670706781159424","commentRate":100,"commentTagId":13,"commentTagName":"技师仪容","commentTagType":1,"createTime":1493796443000},{"id":2482,"userId":"804221955724021760","userName":"vianra","techId":"854957848801579008","techName":"死心吧不约","clubId":"777344513239687168","clubName":"DWF测试","commentId":"859670706781159424","commentRate":100,"commentTagId":15,"commentTagName":"技师足钟","commentTagType":1,"createTime":1493796443000}]
     */

    public ObjBean obj;
    public List<CommentRateListBean> commentRateList;

    public static class ObjBean {
        /**
         * id : 859670706781159424
         * userId : 804221955724021760
         * userName : vianra
         * techId : 854957848801579008
         * orderId : null
         * clubId : 777344513239687168
         * commentType : tech
         * rewardAmount : 0
         * comment :
         * rate : 100
         * createdAt : 2017-05-03 15:27:23
         * modifyAt : null
         * techName : 死心吧不约
         * techAvatar : 160477
         * techSerialNo : 23333
         * loginName : 15377711660
         * impression :
         * techAvatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/01/26/oIYBAFj4YWqAYCVhAAWUazqJgEI172.png?st=BDUQrBLMDLWJK34AKp2DNg&e=1496399574
         * commentTypeName : 粉丝评论
         */

        public String id;
        public String userId;
        public String userName;
        public String techId;
        public String orderId;
        public String clubId;
        public String commentType;
        public int rewardAmount;
        public String comment;
        public int rate;
        public String createdAt;
        public String modifyAt;
        public String techName;
        public String techAvatar;
        public String techSerialNo;
        public String loginName;
        public String impression;
        public String techAvatarUrl;
        public String commentTypeName;
    }


}
