package com.m.pk;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m.pk.adapter.PkRankingListAdapter;
import com.m.pk.bean.PKDetailListBean;
import com.m.pk.bean.PkFilterTeamBean;
import com.m.pk.databinding.FragmentTechPkRankingListBinding;
import com.m.pk.event.DateChangedEvent;
import com.m.pk.httprequest.DataManager;
import com.m.pk.httprequest.RequestConstant;
import com.m.pk.httprequest.response.PKPersonalListResult;
import com.m.pk.httprequest.response.PKTeamListResult;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 18-1-9.
 */

public class TechPKRankingListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String BIZ_TYPE = "type";
    public static final String PK_ACTIVITY_ID = "pkActivityId";

    private String mRange;

    private PkRankingListAdapter mDetailAdapter;
    private List<PKDetailListBean> mData;
    private int mTeamNumber;
    private Map<String, String> mParams;
    private String mActivityId;
    private String mSortKey;
    private String mStartDate, mEndDate;
    private String mCurrentFilterTeamId;
    private String mCurrentFilterTeamName;
    private List<PkFilterTeamBean> mPkFilterTeamList;
    private List<String> mStringList;
    // private ArrayBottomPopupWindow<String> mArrayBottom;
    FragmentTechPkRankingListBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_pk_ranking_list, container, false);
        initView();
        getRankingData();
        mBinding.setFragment(this);
        return mBinding.getRoot().getRootView();
    }

    private void initView() {
        mRange = getArguments().getString(BIZ_TYPE);
        mActivityId = getArguments().getString(PK_ACTIVITY_ID);
        mData = new ArrayList<>();
        mCurrentFilterTeamId = "";
        mPkFilterTeamList = new ArrayList<>();
        switch (mRange) {
            case Constant.KEY_CATEGORY_CUSTOMER_TYPE:
                mSortKey = RequestConstant.KEY_SORT_BY_CUSTOMER;
                break;
            case Constant.KEY_CATEGORY_SAIL_TYPE:
                mSortKey = RequestConstant.KEY_SORT_BY_SALE;
                break;
            case Constant.KEY_CATEGORY_COMMENT_TYPE:
                mSortKey = RequestConstant.KEY_SORT_BY_COMMENT;
                break;
            case Constant.KEY_CATEGORY_PANIC_BUY_TYPE:
                mSortKey = RequestConstant.KEY_SORT_BY_PANIC;
                break;
            case Constant.KEY_CATEGORY_PAID_TYPE:
                mSortKey = RequestConstant.KEY_SORT_BY_COUPON;
                break;
        }
        mStartDate = Constant.PK_RANKING_DEFAULT_START_TIME;
        mEndDate = DateUtil.getDate(System.currentTimeMillis());
        mBinding.swipeTechPkRanking.setColorSchemeColors(0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff, 0xff000000);
        mBinding.swipeTechPkRanking.setOnRefreshListener(this);
        mDetailAdapter = new PkRankingListAdapter(getActivity(), mData, mSortKey);
        mBinding.recyclerTechPKRankingList.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerTechPKRankingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerTechPKRankingList.setHasFixedSize(true);
        mBinding.recyclerTechPKRankingList.setAdapter(mDetailAdapter);
        mDetailAdapter.setTeamFilter(new PkRankingListAdapter.TeamFilterListener() {
            @Override
            public void filterTeam(View view) {

            }
        });
        mCurrentFilterTeamId = "";
        mCurrentFilterTeamName = "全部队伍";
    }

    @Subscribe
    public void onDateChangedSubScribe(DateChangedEvent event) {
        XLogger.i(">>>", event.startDate);
        XLogger.i(">>>", event.endDate);
    }


    @Override
    public void onRefresh() {
        getRankingData();
    }

    public void getRankingData() {
        DataManager.getInstance().getPkTeamList(mActivityId, mSortKey, mStartDate, mEndDate, String.valueOf(1), String.valueOf(1000), new NetworkSubscriber<PKTeamListResult>() {
            @Override
            public void onCallbackSuccess(PKTeamListResult result) {
                mData.clear();
                mPkFilterTeamList.clear();
                mTeamNumber = result.getRespData().rankingList.size();
                for (int i = 0; i < result.getRespData().rankingList.size(); i++) {
                    result.getRespData().rankingList.get(i).setTeam(true);
                    result.getRespData().rankingList.get(i).setPosition(i);
                    result.getRespData().rankingList.get(i).setTeamSize(mTeamNumber);
                    result.getRespData().rankingList.get(i).setSortType(mSortKey);
                    mPkFilterTeamList.add(new PkFilterTeamBean(result.getRespData().rankingList.get(i).getTeamId(), result.getRespData().rankingList.get(i).getTeamName()));
                }

                mPkFilterTeamList.add(0, new PkFilterTeamBean("", "全部队伍"));
                mData.addAll(result.getRespData().rankingList);
                getPersonalRankingData();
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void getPersonalRankingData() {
        DataManager.getInstance().getPkPersonalList(mActivityId, mSortKey, mCurrentFilterTeamId, mStartDate, mEndDate, String.valueOf(1), String.valueOf(1000), new NetworkSubscriber<PKPersonalListResult>() {
            @Override
            public void onCallbackSuccess(PKPersonalListResult result) {
                for (int i = 0; i < result.getRespData().size(); i++) {
                    result.getRespData().get(i).setTeam(false);
                    result.getRespData().get(i).setPosition(mTeamNumber + i);
                    result.getRespData().get(i).setTeamSize(mTeamNumber);
                    result.getRespData().get(i).setSortType(mSortKey);
                }
                mData.addAll(result.getRespData());
                mDetailAdapter.setData(mData, mTeamNumber, mCurrentFilterTeamName);
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

}
