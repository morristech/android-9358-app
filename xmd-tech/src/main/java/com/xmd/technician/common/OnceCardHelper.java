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
    private List<OnceCardItemBean> mOnceCardList;
    private List<OnceCardItemBean> mPackageList;
    private List<OnceCardItemBean> mCreditGiftList;

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
        mOnceCardList = new ArrayList<>();
        mPackageList = new ArrayList<>();
        mCreditGiftList = new ArrayList<>();
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
        mOnceCardList.addAll(getItemCardList(mItemCardBeanList));
        mPackageList.addAll(getItemCardList(mItemPackageBeanList));
        mCreditGiftList.addAll(getItemCardList(mCreditBeanList));
        mCardItemBeanList.addAll(mOnceCardList);
        mCardItemBeanList.addAll(mPackageList);
        mCardItemBeanList.addAll(mCreditGiftList);
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
        String discountPrice;
        for (int i = 0; i < itemCardBeanList.size(); i++) {
            bean = itemCardBeanList.get(i);
            onceCardDes = getComboDescription(bean);
            shareDescription = getShareDes(bean);
            depositRate = getDepositRate(bean);
            techRoyalty = getTechRoyalty(bean);
            sellingPrice = getOnceCardPrice(bean);
            discountPrice = getDiscountPrice(bean);
            mItemPackageList.add(new OnceCardItemBean(bean.id, bean.cardType, bean.type, i, bean.name, bean.imageUrl, onceCardDes, shareDescription, techRoyalty, bean.shareUrl, sellingPrice, discountPrice, depositRate, 1));
        }
        return mItemPackageList;
    }


    public List<OnceCardItemBean> getOnceCardList() {
        return mOnceCardList;
    }

    public List<OnceCardItemBean> getPackageList() {
        return mPackageList;
    }

    public List<OnceCardItemBean> getCreditList() {
        return mCreditGiftList;
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
        if (bean.cardType.equals(Constant.ITEM_CARD_TYPE))  {
          if(bean.type == 1){ //购买赠送
              for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                  mCombo.add(String.format("买%s送%s", bean.itemCardPlans.get(i).paidCount, bean.itemCardPlans.get(i).giveCount));
              }
              return  Utils.listToString(mCombo);
          }else{//直减
              for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                  mCombo.add(String.format("%s*%s次",bean.itemCardPlans.get(i).itemName , bean.itemCardPlans.get(i).paidCount));
              }
              return Utils.listToString(mCombo);
          }
        } else if(bean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
            if(bean.type == 3){//混合购买赠送
                for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                    mCombo.add(String.format("%s%s*%s次",bean.itemCardPlans.get(i).giveCount == 0 ? "(购买)":"(赠送)", bean.itemCardPlans.get(i).itemName, bean.itemCardPlans.get(i).paidCount == 0 ? bean.itemCardPlans.get(i).giveCount : bean.itemCardPlans.get(i).paidCount));
                }
                return Utils.listToString(mCombo);
            }else{//混合购买直减
                for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                    mCombo.add(String.format("%s*%s次", bean.itemCardPlans.get(i).itemName, bean.itemCardPlans.get(i).paidCount == 0 ? bean.itemCardPlans.get(i).giveCount : bean.itemCardPlans.get(i).paidCount));
                }
                return Utils.listToString(mCombo);
            }
        }else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mCombo.add(String.format("%s*%s次", bean.itemCardPlans.get(i).itemName, bean.itemCardPlans.get(i).paidCount == 0 ? bean.itemCardPlans.get(i).giveCount : bean.itemCardPlans.get(i).paidCount));
            }
            return Utils.listToString(mCombo);
        }
    }

    private String getComboShareDescription(OnceCardBean bean) {
        String desShare = "";
        //	1-单项购买赠数;2-单项购买直减;3-混合购买赠送;4-混合购买直减;5-积分礼品
        if(bean.cardType.equals(Constant.ITEM_CARD_TYPE)){
            if(bean.type == 1){//次卡(赠送):
                for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                    if(bean.itemCardPlans.get(i).optimal.equals("Y")){
                        desShare = String.format("原价%1.1f元,买%s送%s,折后%1.1f元",bean.itemCardPlans.get(i).originalAmount*1.0/100f,bean.itemCardPlans.get(i).paidCount,
                                bean.itemCardPlans.get(i).giveCount,bean.itemCardPlans.get(i).actAmount*1.0/100f);
                    }
                }
            }else{//次卡(直减):
                for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                    if(bean.itemCardPlans.get(i).optimal.equals("Y")){
                        desShare = String.format("原价%1.1f元,%s次(直减%1.1f元),折后%1.1f元",bean.itemCardPlans.get(i).originalAmount*1.0/100f,bean.itemCardPlans.get(i).paidCount,
                                (bean.itemCardPlans.get(i).originalAmount - bean.itemCardPlans.get(i).actAmount)*1.0/100f,bean.itemCardPlans.get(i).actAmount*1.0/100f);
                    }
                }
            }
        }else if(bean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)){
            List<String> mCombo = new ArrayList<>();

            if(bean.type == 3){//混合购买赠送
                for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                    mCombo.add(String.format("%s%s*%s次",bean.itemCardPlans.get(i).giveCount == 0 ? "买":"送", bean.itemCardPlans.get(i).itemName, bean.itemCardPlans.get(i).paidCount == 0 ? bean.itemCardPlans.get(i).giveCount : bean.itemCardPlans.get(i).paidCount));
                }
                return String.format("特价%1.1f元,%s,原价%1.1f元",bean.itemCardPlans.get(0).actAmount *1.0/ 100f,Utils.listToString(mCombo),bean.itemCardPlans.get(0).originalAmount *1.0/ 100f);
            }else{//混合购买直减

                return String.format("特价%1.1f元,直减%1.1f元,原价%1.1f元",bean.itemCardPlans.get(0).actAmount *1.0/ 100f,(bean.itemCardPlans.get(0).originalAmount - bean.itemCardPlans.get(0).actAmount)/100f,bean.itemCardPlans.get(0).originalAmount *1.0/ 100f);
            }
        }else {//积分:
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if(bean.itemCardPlans.get(i).optimal.equals("Y")){
                    desShare = String.format("%s积分可换,原价%1.1f元",bean.itemCardPlans.get(i).credits,bean.itemCardPlans.get(i).originalAmount*1.0/100f);
                }
            }
        }

        return desShare;
    }

    private String getTechRoyalty(OnceCardBean bean) {
        List<String> mTechAmount = new ArrayList<>();
        if (bean.cardType.equals(Constant.ITEM_CARD_TYPE)) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mTechAmount.add(String.format("%1.1f元", bean.itemCardPlans.get(i).techAmount *1.0/ 100f));
            }
            return String.format("提成：%s", Utils.listToString(mTechAmount));
        } else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    return String.format("提成：%1.1f 元", bean.itemCardPlans.get(i).techAmount *1.0/ 100f);
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

    private String getDiscountPrice(OnceCardBean bean) {
        List<String> mCombo = new ArrayList<>();
        if (bean.cardType.equals(Constant.ITEM_CARD_TYPE)) {

            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mCombo.add(String.format("%s 元", bean.itemCardPlans.get(i).actAmount / 100));
            }
            return String.format("特卖价：%s", Utils.listToString(mCombo));
        } else if (bean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
            String price = "";
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    price = String.valueOf(bean.itemCardPlans.get(i).actAmount / 100);
                }
            }
            return String.format("特卖价：%s 元", price);
        } else {
            String price = "";
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                    price = String.valueOf(bean.itemCardPlans.get(i).credits);
                }
            }
            return String.format("特卖价：%s 积分", price);


        }

    }


}
