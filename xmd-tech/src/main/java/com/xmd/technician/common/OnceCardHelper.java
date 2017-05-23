package com.xmd.technician.common;


import com.xmd.technician.bean.OnceCardBean;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.http.gson.OnceCardResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 2017/2/20.
 */

public class OnceCardHelper {

    private List<OnceCardItemBean> mCardItemBeanList;
    public static OnceCardHelper mOnceCardHelper;

    public OnceCardHelper getInstance() {
        if (mOnceCardHelper == null) {
            mOnceCardHelper = new OnceCardHelper();
        }
        return mOnceCardHelper;
    }

    public List<OnceCardItemBean> getCardItemBeanList(OnceCardResult onceCardResult) {
        if (mCardItemBeanList == null) {
            mCardItemBeanList = new ArrayList<>();
        }
        mCardItemBeanList.clear();
        for (int i = 0; i < onceCardResult.respData.activityList.size(); i++) {
            String comboDescription;
            String techRoyalty;
            String price;
            boolean isPreferential;
            if ((onceCardResult.respData.optimalActivity.id).equals(onceCardResult.respData.activityList.get(i).id)) {
                isPreferential = true;
            } else {
                isPreferential = false;
            }

            comboDescription = getComboDescription(onceCardResult.respData.activityList.get(i));
            techRoyalty = getTechRoyalty(onceCardResult.respData.activityList.get(i));
            price = getOnceCardPrice(onceCardResult.respData.activityList.get(i));
            mCardItemBeanList.add(new OnceCardItemBean(onceCardResult.respData.activityList.get(i).id, onceCardResult.respData.activityList.get(i).name, onceCardResult.respData.activityList.get(i).imageUrl,
                    isPreferential, comboDescription, onceCardResult.respData.activityList.get(i).itemName + getComboShareDescription(onceCardResult.respData.activityList.get(i)), techRoyalty, price, onceCardResult.respData.activityList.get(i).shareUrl));
        }
        return mCardItemBeanList;
    }

    private String getComboDescription(OnceCardBean bean) {
        List<String> mCombo = new ArrayList<>();
        if (bean.type == 1) {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mCombo.add(String.format("买%s送%s", bean.itemCardPlans.get(i).paidCount, bean.itemCardPlans.get(i).giveCount));
            }
        } else {
            for (int i = 0; i < bean.itemCardPlans.size(); i++) {
                mCombo.add(String.format("%s次", bean.itemCardPlans.get(i).paidCount));
            }
        }

        return String.format("套餐：%s", Utils.listToString(mCombo));
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
        for (int i = 0; i < bean.itemCardPlans.size(); i++) {
            mTechAmount.add(String.format("%s元", bean.itemCardPlans.get(i).techAmount / 100));
        }
        return String.format("提成：%s", Utils.listToString(mTechAmount));
    }

    private String getOnceCardPrice(OnceCardBean bean) {
        double price = 0;
        for (int i = 0; i < bean.itemCardPlans.size(); i++) {
            if (bean.itemCardPlans.get(i).optimal.equals("Y")) {
                price = bean.itemCardPlans.get(i).actAmount / 100d;
            }
        }
        return String.format("%s元", String.valueOf(price));
    }

}
