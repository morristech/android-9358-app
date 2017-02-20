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

    public  OnceCardHelper getInstance() {
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
            if (onceCardResult.respData.optimalActivity.id == onceCardResult.respData.activityList.get(i).id) {
                isPreferential = true;
            } else {
                isPreferential = false;
            }
            comboDescription = getComboDescription(onceCardResult.respData.activityList.get(i));
            techRoyalty = getTechRoyalty(onceCardResult.respData.activityList.get(i));
            price = getOnceCardPrice(onceCardResult.respData.activityList.get(i));
            mCardItemBeanList.add(new OnceCardItemBean(onceCardResult.respData.activityList.get(i).id,onceCardResult.respData.activityList.get(i).name, onceCardResult.respData.activityList.get(i).imageUrl,
                    isPreferential, comboDescription, techRoyalty, price, onceCardResult.respData.activityList.get(i).shareUrl));
        }
        return mCardItemBeanList;
    }

    private String getComboDescription(OnceCardBean bean) {
        List<String> mCombo = new ArrayList<>();
        for (int i = 0; i < bean.onceCardPlans.size(); i++) {
            mCombo.add(String.format("买%s送%s", bean.onceCardPlans.get(i).paidCount, bean.onceCardPlans.get(i).giveCount));
        }
        return String.format("套餐：%s", Utils.listToString(mCombo));
    }

    private String getTechRoyalty(OnceCardBean bean) {
        List<String> mTechAmount = new ArrayList<>();
        for (int i = 0; i < bean.onceCardPlans.size(); i++) {
            mTechAmount.add(String.format("%s元", bean.onceCardPlans.get(i).techAmount / 100));
        }
        return String.format("提成：%s", Utils.listToString(mTechAmount));
    }

    private String getOnceCardPrice(OnceCardBean bean) {
        double price = 0;
        for (int i = 0; i < bean.onceCardPlans.size(); i++) {
            if (bean.onceCardPlans.get(i).optimal.equals("Y")) {
                price = bean.onceCardPlans.get(i).actAmount / 100d;
            }
        }
        return String.format("%s元", String.valueOf(price));
    }

}
