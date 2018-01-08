package com.m.pk;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m.pk.bean.TechRankingBean;
import com.m.pk.databinding.FragmentTechCommonRankingDetailBinding;
import com.m.pk.httprequest.DataManager;
import com.m.pk.httprequest.RequestConstant;
import com.m.pk.httprequest.response.TechRankingListResult;
import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.m.network.NetworkSubscriber;

import java.util.Date;
import java.util.List;


/**
 * Created by Lhj on 17-3-9.
 */

public class TechCommonRankingDetailFragment extends BaseFragment {
    public static final String BIZ_TYPE = "type";
    public static final String USER_TYPE = "user_type";

    private int mRange;
    private String mUserType;
    private String mSortKey;
    private String mStartDate;
    private String mEndDate;
    private List<TechRankingBean> mTechRankList;
    private int mPage;
    private int mPageSize = 20;

    private FragmentTechCommonRankingDetailBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_common_ranking_detail, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mRange = getArguments().getInt(BIZ_TYPE);
        mUserType = getArguments().getString(USER_TYPE);
        switch (mRange) {
            case Constant.BIZ_TYPE_REGISTER:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_USER;
                break;
            case Constant.BIZ_TYPE_SALE:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_PAID;
                break;
            case Constant.BIZ_TYPE_SERVICE:
                mSortKey = RequestConstant.KEY_SORT_BY_COMMENT;
                break;

        }
        mStartDate = DateUtil.getFirstDayOfWeek(new Date(), Constant.FORMAT_YEAR);
        mEndDate = DateUtil.getCurrentDate(System.currentTimeMillis(), Constant.FORMAT_YEAR);
        getTechCommonRankingList();
    }

    public void DataChangeEvent(String strDate, String endDate) {
        mStartDate = strDate;
        mEndDate = endDate;

    }

    private void getTechCommonRankingList() {
        DataManager.getInstance().getTechRankingList(mUserType,mSortKey, mStartDate, mEndDate, String.valueOf(mPage), String.valueOf(mPageSize), new NetworkSubscriber<TechRankingListResult>() {
            @Override
            public void onCallbackSuccess(TechRankingListResult result) {

            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }
}
