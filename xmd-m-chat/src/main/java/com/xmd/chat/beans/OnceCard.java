package com.xmd.chat.beans;

import com.xmd.app.BaseViewModel;

import java.util.List;

/**
 * Created by mo on 17-7-11.
 * 次卡数据
 */

public class OnceCard extends BaseViewModel {

    public static final String CARD_TYPE_SINGLE = "item_card"; //单项次卡
    public static final String CARD_TYPE_MIX = "item_package"; //混合套餐
    public static final String CARD_TYPE_CREDIT = "credit_gift"; //积分礼物


    public String id;
    public String cardType;  //类型 tem_card-单项次卡;item_package-混合套餐；credit_gift-积分礼品
    public String name;     //名称
    public String imageUrl; //图片
    private String comboDescription;//套餐描述
    private String techRoyalty; //技师提成
    //   public String price; //商品价格
    public String shareUrl; //分享链接
    public String shareDescription; //分享描述语言
    public String sellingPrice; //销售价格
    public String depositRate;//折扣率
    private String discountPrice;//特卖价
    public int type;  //1-单项购买赠数;2-单项购买直减;3-混合购买赠送;4-混合购买直减;5-积分礼品
    public int position;
    public int selectedStatus;
    public List<Item> itemCardPlans;

    //获取套餐描述
    public String getComboDescription() {
        if (comboDescription != null) {
            return comboDescription;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (cardType.equals(CARD_TYPE_SINGLE)) {
            if (type == 1) { //购买赠送
                for (int i = 0; i < itemCardPlans.size(); i++) {
                    stringBuilder.append(String.format("买%s送%s", itemCardPlans.get(i).paidCount, itemCardPlans.get(i).giveCount));
                    if (i != itemCardPlans.size() - 1) {
                        stringBuilder.append("、");
                    }
                }
                comboDescription = String.format("(%s)%s", itemCardPlans.get(0).itemName, stringBuilder.toString());
            } else {//直减
                for (int i = 0; i < itemCardPlans.size(); i++) {
                    stringBuilder.append(String.format("%s次", itemCardPlans.get(i).paidCount));
                    if (i != itemCardPlans.size() - 1) {
                        stringBuilder.append("、");
                    }
                }
                comboDescription = String.format("(%s)%s", itemCardPlans.get(0).itemName, stringBuilder.toString());
            }
        } else if (cardType.equals(CARD_TYPE_MIX)) {
            if (type == 3) {//混合购买赠送
                for (int i = 0; i < itemCardPlans.size(); i++) {
                    stringBuilder.append(String.format("%s%s*%s次", itemCardPlans.get(i).giveCount == 0 ? "(购买)" : "(赠送)", itemCardPlans.get(i).itemName, itemCardPlans.get(i).paidCount == 0 ? itemCardPlans.get(i).giveCount : itemCardPlans.get(i).paidCount));
                    if (i != itemCardPlans.size() - 1) {
                        stringBuilder.append("、");
                    }
                }
                comboDescription = stringBuilder.toString();
            } else {//混合购买直减
                for (int i = 0; i < itemCardPlans.size(); i++) {
                    stringBuilder.append(String.format("%s*%s次", itemCardPlans.get(i).itemName, itemCardPlans.get(i).paidCount == 0 ? itemCardPlans.get(i).giveCount : itemCardPlans.get(i).paidCount));
                    if (i != itemCardPlans.size() - 1) {
                        stringBuilder.append("、");
                    }
                }
                comboDescription = stringBuilder.toString();
            }
        } else {
            for (int i = 0; i < itemCardPlans.size(); i++) {
                stringBuilder.append(String.format("%s*%s次", itemCardPlans.get(i).itemName, itemCardPlans.get(i).paidCount == 0 ? itemCardPlans.get(i).giveCount : itemCardPlans.get(i).paidCount));
                if (i != itemCardPlans.size() - 1) {
                    stringBuilder.append("、");
                }
            }
            comboDescription = stringBuilder.toString();
        }
        return comboDescription;
    }

    //获取折扣描述
    public String getDiscountPrice() {
        if (discountPrice != null) {
            return discountPrice;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (cardType.equals(CARD_TYPE_SINGLE)) {

            for (int i = 0; i < itemCardPlans.size(); i++) {
                stringBuilder.append(String.format("%s 元", itemCardPlans.get(i).actAmount / 100));
                if (i != itemCardPlans.size() - 1) {
                    stringBuilder.append("、");
                }
            }
            discountPrice = String.format("特卖价：%s", stringBuilder.toString());
        } else if (cardType.equals(CARD_TYPE_MIX)) {
            String price = "";
            for (int i = 0; i < itemCardPlans.size(); i++) {
                if (itemCardPlans.get(i).optimal.equals("Y")) {
                    price = String.valueOf(itemCardPlans.get(i).actAmount / 100);
                }
            }
            discountPrice = String.format("特卖价：%s 元", price);
        } else {
            String price = "";
            for (int i = 0; i < itemCardPlans.size(); i++) {
                if (itemCardPlans.get(i).optimal.equals("Y")) {
                    price = String.valueOf(itemCardPlans.get(i).credits);
                }
            }
            discountPrice = String.format("特卖价：%s 积分", price);
        }
        return discountPrice;
    }

    //获取技师提成
    public String getTechRoyalty() {
        if (techRoyalty != null) {
            return techRoyalty;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (cardType.equals(CARD_TYPE_SINGLE)) {
            for (int i = 0; i < itemCardPlans.size(); i++) {
                stringBuilder.append(String.format("%1.1f元", itemCardPlans.get(i).techAmount * 1.0 / 100f));
                if (i != itemCardPlans.size() - 1) {
                    stringBuilder.append("、");
                }
            }
            techRoyalty = String.format("提成：%s", stringBuilder.toString());
        } else {
            for (int i = 0; i < itemCardPlans.size(); i++) {
                if (itemCardPlans.get(i).optimal.equals("Y")) {
                    techRoyalty = String.format("提成：%1.1f 元", itemCardPlans.get(i).techAmount * 1.0 / 100f);
                    break;
                }
            }
        }
        return techRoyalty == null ? "提成：0 元" : techRoyalty;
    }

    public static class Item {
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
