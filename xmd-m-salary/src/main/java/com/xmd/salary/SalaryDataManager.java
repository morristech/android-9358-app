package com.xmd.salary;

import android.support.v4.util.ArrayMap;

import com.xmd.salary.bean.CommissionBean;
import com.xmd.salary.bean.SalaryBean;

import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-11-22.
 */

public class SalaryDataManager {
    private Map<String, SalaryBean> mSalaryCache;
    private Map<String, List<CommissionBean>> mCommissionListCache;
    private static SalaryDataManager mSalaryDataInstance;

    public SalaryDataManager() {
        mSalaryCache = new ArrayMap<>();
        mCommissionListCache = new ArrayMap<>();
    }

    public static SalaryDataManager getSalaryDataInstance() {
        if (mSalaryDataInstance == null) {
            synchronized (SalaryDataManager.class) {
                if (mSalaryDataInstance == null) {
                    mSalaryDataInstance = new SalaryDataManager();
                }
            }
        }
        return mSalaryDataInstance;
    }

    public SalaryBean getSalaryBean(String timeKey) {
        return mSalaryCache.get(timeKey);
    }

    public List<CommissionBean> getCommissionList(String timeKey) {
        return mCommissionListCache.get(timeKey);
    }

    public void setSalaryBean(String timeKey, SalaryBean bean) {
        mSalaryCache.put(timeKey, bean);
    }

    public void setCommissionList(String timeKey, List<CommissionBean> list) {
        mCommissionListCache.put(timeKey, list);
    }

    public boolean salaryContainKey(String timeKey) {
        return mSalaryCache.containsKey(timeKey);
    }

    public boolean commissionContainKey(String timeKey) {
        return mCommissionListCache.containsKey(timeKey);
    }

    public void destroyData(){
        mSalaryDataInstance = null;
    }

}
