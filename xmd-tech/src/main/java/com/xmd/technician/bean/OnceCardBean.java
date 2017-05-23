package com.xmd.technician.bean;

import java.util.List;

/**
 * Created by Lhj on 2017/2/9.
 */

public class OnceCardBean {

    /**
     * id : 158
     * name : 1
     * type : 2
     * startTime : null
     * endTime : null
     * totalCount : 1
     * personalLimit : 0
     * paidCount : 0
     * itemId : 751224719469977600
     * itemName : 8888
     * status : 1
     * subStatus : 2
     * image : 159848
     * itemCardPlans : [{"id":288,"activityId":158,"clubId":"601679316694081536","name":"A","type":2,"actAmount":1000,"credits":0,"originalAmount":1100,"paidCount":11,"giveCount":0,"itemId":"751224719469977600","itemName":"8888","optimal":"N","itemAmount":100,"techAmount":100},{"id":289,"activityId":158,"clubId":"601679316694081536","name":"B","type":2,"actAmount":11000,"credits":0,"originalAmount":11100,"paidCount":111,"giveCount":0,"itemId":"751224719469977600","itemName":"8888","optimal":"N","itemAmount":100,"techAmount":200},{"id":290,"activityId":158,"clubId":"601679316694081536","name":"C","type":2,"actAmount":1000,"credits":0,"originalAmount":1200,"paidCount":12,"giveCount":0,"itemId":"751224719469977600","itemName":"8888","optimal":"Y","itemAmount":100,"techAmount":300}]
     * shareUrl : http://w.url.cn/s/AD1i4NP
     * period : 1Y
     * description : good
     nihas
     okokk
     * cardType : item_card
     * imageUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/01/22/oIYBAFi07MuAb0q0AAAwrHBtAI05788325?st=JiyQWmAbb8gOx8Y7rQ_kEA&e=1497759643
     * statusName : 进行中
     */

    public String id;
    public String name;
    public int type; //	1-单项购买赠数;2-单项购买直减;3-混合购买赠送;4-混合购买直减;5-积分礼品
    public String startTime;
    public String endTime;
    public int totalCount;
    public int personalLimit;
    public int paidCount;
    public String itemId;
    public String itemName;
    public int status;
    public int subStatus;
    public String image;
    public String shareUrl;
    public String period;
    public String description;
    public String cardType; //tem_card-单项次卡;item_package-混合套餐；credit_gift-积分礼品
    public String imageUrl;
    public String statusName;
    public List<ItemCardPlansBeanX> itemCardPlans;

    public static class ItemCardPlansBeanX {
        /**
         * id : 288
         * activityId : 158
         * clubId : 601679316694081536
         * name : A
         * type : 2
         * actAmount : 1000
         * credits : 0
         * originalAmount : 1100
         * paidCount : 11
         * giveCount : 0
         * itemId : 751224719469977600
         * itemName : 8888
         * optimal : N
         * itemAmount : 100
         * techAmount : 100
         */

        public int id;
        public int activityId;
        public String clubId;
        public String name;
        public int type;  //	1-单项购买赠数;2-单项购买直减;3-混合购买赠送;4-混合购买直减;5-积分礼品
        public int actAmount; //优惠后套餐价
        public int credits;
        public int originalAmount;
        public int paidCount;
        public int giveCount;
        public String itemId;
        public String itemName;
        public String optimal;
        public int itemAmount;  //项目原价
        public int techAmount; //技师提成
    }


}
