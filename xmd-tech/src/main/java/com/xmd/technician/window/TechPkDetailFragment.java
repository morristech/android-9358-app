package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Adapter.PKRankingDetailAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.DateChangedResult;
import com.xmd.technician.bean.PKDetailListBean;
import com.xmd.technician.bean.PkFilterTeamBean;
import com.xmd.technician.common.DateUtil;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.PKPersonalListResult;
import com.xmd.technician.http.gson.PKTeamListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ArrayBottomPopupWindow;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 17-4-9.
 */

public class TechPkDetailFragment extends BaseFragment implements BaseFragment.IFragmentCallback, SwipeRefreshLayout.OnRefreshListener, PKRankingDetailAdapter.TeamFilterListener {
    public static final String BIZ_TYPE = "type";
    public static final String PK_ACTIVITY_ID = "pkActivityId";
    @BindView(R.id.ranking_recycler_view)
    RecyclerView rankingRecyclerView;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view_widget)
    EmptyView emptyView;

    private String mRange;
    private Subscription mTeamRankingSubscription;
    private Subscription mTechRankingSubscription;
    private Subscription mDateChangedSubscription;
    private PKRankingDetailAdapter mDetailAdapter;
    private List<PKDetailListBean> mData;
    private int mTeamNumber;
    private Map<String, String> mParams;
    private String mActivityId;
    private String mSortKey;
    private String mStartDate, mEndDate;
    private String mPage;
    private String mPageSize;
    private View rootView;
    private String mCurrentFilterTeamId;
    private String mCurrentFilterTeamName;
    private List<PkFilterTeamBean> mPkFilterTeamList;
    private List<String> mStringList;
    private ArrayBottomPopupWindow<String> mArrayBottom;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tech_pk_detail, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        getRankingData();
        return rootView;
    }

    private void initView() {
        mRange = getArguments().getString(BIZ_TYPE);
        mActivityId = getArguments().getString(PK_ACTIVITY_ID);
        mData = new ArrayList<>();
        mPkFilterTeamList = new ArrayList<>();
        if (mRange.equals(Constant.KEY_CATEGORY_CUSTOMER_TYPE)) {
            mSortKey = RequestConstant.KEY_SORT_BY_CUSTOMER;
        } else if (mRange.equals(Constant.KEY_CATEGORY_SAIL_TYPE)) {
            mSortKey = RequestConstant.KEY_SORT_BY_SALE;
        } else if (mRange.equals(Constant.KEY_CATEGORY_COMMENT_TYPE)) {
            mSortKey = RequestConstant.KEY_SORT_BY_COMMENT;
        }else if(mRange.equals(Constant.KEY_CATEGORY_PANIC_BUY_TYPE)){
            mSortKey =  RequestConstant.KEY_SORT_BY_PANIC;
        } else {
            mSortKey = RequestConstant.KEY_SORT_BY_COUPON;
        }
        mStartDate = "2017-03-01";
        mEndDate = DateUtil.getDate(System.currentTimeMillis());
        mTeamRankingSubscription = RxBus.getInstance().toObservable(PKTeamListResult.class).subscribe(
                teamResult -> handleTeamRanking(teamResult)
        );
        mTechRankingSubscription = RxBus.getInstance().toObservable(PKPersonalListResult.class).subscribe(
                personalResult -> handlePersonalResult(personalResult)
        );
        mDateChangedSubscription = RxBus.getInstance().toObservable(DateChangedResult.class).subscribe(
                dateChangedResult -> handleDateChangedResult(dateChangedResult)
        );
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        mDetailAdapter = new PKRankingDetailAdapter(getActivity(), mData, mSortKey);
        rankingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rankingRecyclerView.setHasFixedSize(true);
        rankingRecyclerView.setAdapter(mDetailAdapter);
        mDetailAdapter.setTeamFilter(this);
        mCurrentFilterTeamId = "";
        mCurrentFilterTeamName = "全部队伍";

    }


    private void handlePersonalResult(PKPersonalListResult personalResult) {
        if (personalResult.statusCode == 200) {
            swipeRefreshLayout.setRefreshing(false);
            emptyView.setStatus(EmptyView.Status.Gone);
            if (personalResult.respData == null || personalResult.respData.size() == 0) {
                return;
            }
            if (personalResult.type.equals(mSortKey)) {
                for (int i = 0; i < personalResult.respData.size(); i++) {
                    personalResult.respData.get(i).isTeam = false;
                }
                mData.addAll(personalResult.respData);
                mDetailAdapter.setData(mData, mTeamNumber, mCurrentFilterTeamName);
            }

        } else {
            Utils.makeShortToast(getActivity(), personalResult.msg);
        }

    }

    private void handleTeamRanking(PKTeamListResult teamResult) {

        if (teamResult.statusCode == 200) {
            if (teamResult.respData == null || teamResult.respData.rankingList == null) {
                return;
            }
            if (teamResult.sortType.equals(mSortKey)) {
                mData.clear();
                mPkFilterTeamList.clear();
                for (int i = 0; i < teamResult.respData.rankingList.size(); i++) {
                    teamResult.respData.rankingList.get(i).isTeam = true;
                    mPkFilterTeamList.add(new PkFilterTeamBean(teamResult.respData.rankingList.get(i).teamId, teamResult.respData.rankingList.get(i).teamName));
                }
                mTeamNumber = teamResult.respData.rankingList.size();
                mPkFilterTeamList.add(0, new PkFilterTeamBean("", "全部队伍"));
                mData.addAll(teamResult.respData.rankingList);
            }
        } else {
            Utils.makeShortToast(getActivity(), teamResult.msg);
        }
    }

    private void handleDateChangedResult(DateChangedResult dateChangedResult) {
        mStartDate = dateChangedResult.startDate;
        mEndDate = dateChangedResult.endDate;
        getRankingData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mTeamRankingSubscription, mTechRankingSubscription, mDateChangedSubscription);
    }

    public void getRankingData() {
        emptyView.setStatus(EmptyView.Status.Loading);
        if (mParams == null) {
            mParams = new ArrayMap<>();
        } else {
            mParams.clear();
        }
        mPage = "1";
        mPageSize = "1000";
        mParams.put(RequestConstant.KEY_PK_ACTIVITY_ID, mActivityId);
        mParams.put(RequestConstant.KEY_SORT_KEY, mSortKey);
        mParams.put(RequestConstant.KEY_PAGE, mPage);
        mParams.put(RequestConstant.KEY_PAGE_SIZE, mPageSize);
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_PK_TEAM_RANKING_LIST, mParams);
        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
            @Override
            public void run() {
                mParams.put(RequestConstant.KEY_TEAM_ID, mCurrentFilterTeamId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_PK_PERSONAL_RANKING_LIST, mParams);
            }
        }, 300);
    }

    @Override
    public void onRefresh() {
        getRankingData();
    }

    @Override
    public void filterTeam(View view) {
        if (mStringList == null) {
            mStringList = new ArrayList<>();
            for (int i = 0; i < mPkFilterTeamList.size(); i++) {
                mStringList.add(mPkFilterTeamList.get(i).teamName);
            }
        }
        mArrayBottom = new ArrayBottomPopupWindow<>(view, mParams, Utils.dip2px(getActivity(), 100));
        mArrayBottom.setItemClickListener((parent, itemView, position, id) -> {
            mCurrentFilterTeamId = mPkFilterTeamList.get(position).teamId;
            mCurrentFilterTeamName = mStringList.get(position);
            getRankingData();

        });
        mArrayBottom.setDataSet(mStringList, mCurrentFilterTeamName);
        mArrayBottom.showAsDropDown();


    }


}
