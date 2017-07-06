package com.xmd.technician.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.R;
import com.xmd.technician.bean.DateChangedResult;
import com.xmd.technician.bean.TechRankingBean;
import com.xmd.technician.common.DateUtil;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.TechRankingListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;


/**
 * Created by Lhj on 17-3-9.
 */

public class TechPersonalRankingDetailFragment extends BaseListFragment<TechRankingBean> {
    public static final String BIZ_TYPE = "type";

    private View rootView;
    private int mRange;
    private Subscription mTechRankingSubscription;
    private Subscription mTimeChangedSubscription;
    private String mSortKey;
    private Map<String, String> mParams;
    private String mStartDate;
    private String mEndDate;
    private List<TechRankingBean> mTechRankList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tech_personal_ranking_detail, container, false);
        return rootView;
    }

    @Override
    protected void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_TECH_RANKING_SOR_TYPE, mSortKey);
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_RANKING_LIST, mParams);

    }

    @Override
    protected void initView() {
        mRange = getArguments().getInt(BIZ_TYPE);
        mTechRankingSubscription = RxBus.getInstance().toObservable(TechRankingListResult.class).subscribe(
                listResult -> handleTechRanking(listResult)
        );
        mTimeChangedSubscription = RxBus.getInstance().toObservable(DateChangedResult.class).subscribe(
                dateChangedResult -> handleDateChanged(dateChangedResult)
        );
        switch (mRange) {
            case 0:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_USER;
                break;
            case 1:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_PAID;
                break;
            case 2:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_COMMENT;
                break;
        }

        mStartDate = DateUtil.getFirstDayOfWeek(new Date(), TechPersonalRankingDetailActivity.FORMAT_YEAR);
        mEndDate = DateUtil.getCurrentDate(System.currentTimeMillis(), TechPersonalRankingDetailActivity.FORMAT_YEAR);
    }

    private void handleDateChanged(DateChangedResult dateChangedResult) {
        mStartDate = dateChangedResult.startDate;
        mEndDate = dateChangedResult.endDate;
        onRefresh();
    }

    private void handleTechRanking(TechRankingListResult listResult) {
        if (listResult.statusCode == 200 && listResult.sortType.equals(mSortKey)) {
            if (mTechRankList == null) {
                mTechRankList = new ArrayList<>();
            } else {
                mTechRankList.clear();
            }
            if (listResult.respData != null) {
                for (TechRankingBean bean : listResult.respData) {
                    bean.type = mSortKey;
                    mTechRankList.add(bean);
                }
            }
            onGetListSucceeded(listResult.pageCount, mTechRankList);
        } else if (listResult.statusCode != 200) {
            onGetListFailed(listResult.msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mTechRankingSubscription, mTimeChangedSubscription);
    }
}
