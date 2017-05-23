package com.xmd.technician.common;


import com.xmd.technician.Constant;
import com.xmd.technician.bean.OnceCardBean;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.http.gson.OnceCardResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 2017/2/20.
 */

public class OnceCardHelper {

    private List<OnceCardItemBean> mCardItemBeanList;
    private List<OnceCardBean> mItemCardBeanList; //次卡
    private List<OnceCardBean> mItemPackageBeanList;
    private List<OnceCardBean> mCreditBeanList;
    private List<OnceCardItemBean> mItemPackageList;

    public int itemCardSize, packageSize, creditSize;
    public static OnceCardHelper mOnceCardHelper = new OnceCardHelper();

    public static OnceCardHelper getInstance() {
        return mOnceCardHelper;
    }

    public List<OnceCardItemBean> getCardItemBeanList(OnceCardResult onceCardResult) {
        if (mCardItemBeanList == null) {
            mCardItemBeanList = new ArrayList<>();
        }
        mItemCardBeanList = new ArrayList<>();
        mItemPackageBeanList = new ArrayList<>();
        mCreditBeanList = new ArrayList<>();
        mCardItemBeanList.clear();
        for (OnceCardBean cardBean : onceCardResult.respData.activityList) {
            if (cardBean.cardType.equals(Constant.ITEM_CARD_TYPE)) {
                mItemCardBeanList.add(cardBean);
            } else if (cardBean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
                mItemPackageBeanList.add(cardBean);
            } else {
                mCreditBeanList.add(cardBean);
            }
        }
        itemCardSize = mItemCardBeanList.size();
        packageSize = mItemPackageBeanList.size();
        creditSize = mCreditBeanList.size();

        mCardItemBeanList.addAll(getItemCardList(mItemCardBeanList));
        mCardItemBeanList.addAll(getItemCardList(mItemPackageBeanList));
        mCardItemBeanList.addAll(getItemCardList(mCreditBeanList));
        return mCardItemBeanList;
    }


    public List<OnceCardItemBean> getItemCardList(List<OnceCardBean> itemCardBeanList) {
        if (null == mItemPackageList) {
            mItemPackageList = new ArrayList<>();
        } else {
            mItemPackageList.clear();
        }
        OnceCardBean bean;
        String onceCardDes;   //描述
        String shareDescription; //分享描述
        String techRoyalty; //技师提成
        String sellingPrice; //销售价格
        String depositRate; //折扣描述
        for (int i = 0; i < itemCardBeanList.size(); i++) {
            bean = itemCardBeanList.get(i);
            onceCardDes = getComboDescription(bean);
            shareDescription = getShareDes(bean);
            depositRate = getDepositRate(bean);
            techRoyalty = getTechRoyalty(bean);
            sellingPrice = getOnceCardPrice(bean);
            mItemPackageList.add(new OnceCardItemBean(bean.id, bean.cardType, bean.type, i, bean.name, bean.imageUrl, onceCardDes, shareDescription, techRoyalty, bean.shareUrl, sellingPrice, depositRate));
        }
        return mItemPackageList;
    }

    public List<OnceCardItemBean> getOnceCardList() {
        return getItemCardList(mItemCardBeanList);
    }

    public List<OnceCardItemBean> getPackageList() {
        return getItemCardList(mItemPackageBeanList);
    }

    public List<OnceCardItemBean> getCreditList() {
        return getItemCardList(mCreditBeanList);
    }

    /*
      卡券分享时的描述语言
   */
    private String getShareDes(OnceCardBean bean) {
        String des = "";
        des = getComboShareDescription(bean);
        return des;
    }

    /*
        折扣描述
     */
    private String getDepositRate(OnceCardBean bean) {
        String des = "";
        String depositRate;
        if (bean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    depositRate = df.format(bean.itemCardPlans.get(i).actAmount * 10 / bean.itemCardPlans.get(i).originalAmount);
                    des = String.format("%s折", depositRate);
                }
            }
        } else {
            des = "";
        }

        return des;
    }


    /*
        卡券描述语言
     */
    private String getComboDescription(OnceCardBean bean) {
        List<String> mCombo = new ArrayList<>();
        if (bean.cardType == Constant.ITEM_CARD_TYPE) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mCombo.add(String.format("买%s送%s", bean.itemCardPlans.get(i).paidCount, bean.itemCardPlans.get(i).giveCount));
            }
            return String.format("套餐：%s", Utils.listToString(mCombo));
        } else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mCombo.add(String.format("%s%s次", bean.itemCardPlans.get(i).itemName, bean.itemCardPlans.get(i).paidCount == 0 ? bean.itemCardPlans.get(i).giveCount : bean.itemCardPlans.get(i).paidCount));
            }
            return String.format(Utils.listToString(mCombo));
        }
    }

    private String getComboShareDescription(OnceCardBean bean) {
        String desShare = "";
        if (bean.type == 1) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    desShare = String.format("，原价%s元", bean.itemCardPlans.get(i).itemAmount / 100)
                            + String.format("(买%s送%s)，", bean.itemCardPlans.get(i).paidCount, bean.itemCardPlans.get(i).giveCount) + String.format("折后%1.1f元", bean.itemCardPlans.get(i).actAmount * 1.0 / (100f * (bean.itemCardPlans.get(i).paidCount + bean.itemCardPlans.get(i).giveCount)));
                }
            }
        } else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    desShare = String.format("，原价%s元", bean.itemCardPlans.get(i).itemAmount / 100) + String.format("（%s次，直减%s元）", bean.itemCardPlans.get(i).paidCount, (bean.itemCardPlans.get(i).paidCount * bean.itemCardPlans.get(i).itemAmount - bean.itemCardPlans.get(i).actAmount) / 100)
                            + String.format("折后%1.1f元", bean.itemCardPlans.get(i).actAmount * 1.0 / (100f * (bean.itemCardPlans.get(i).paidCount + bean.itemCardPlans.get(i).giveCount)));
                }
            }
        }
        return desShare;
    }

    private String getTechRoyalty(OnceCardBean bean) {
        List<String> mTechAmount = new ArrayList<>();
        if (bean.cardType.equals(Constant.ITEM_CARD_TYPE)) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mTechAmount.add(String.format("%s元", bean.itemCardPlans.get(i).techAmount / 100));
            }
            return String.format("提成：%s", Utils.listToString(mTechAmount));
        } else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    return String.format("提成：%s 元", bean.itemCardPlans.get(i).techAmount / 100);
                }
            }
        }
        return "提成：0 元";
    }

    private String getOnceCardPrice(OnceCardBean bean) {
        double price = 0;
        if (bean.cardType.equals(Constant.ITEM_CARD_TYPE) || bean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    price = bean.itemCardPlans.get(i).actAmount / 100d;
                }
            }
            return String.format("%s元", String.valueOf(price));
        } else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    price = bean.itemCardPlans.get(i).credits;
                }
            }
            return String.format("%s 积分", String.valueOf(price));
        }

    }

}
