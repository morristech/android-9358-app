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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.m.pk.adapter.PkRankingListAdapter;
import com.m.pk.bean.PKDetailListBean;
import com.m.pk.bean.PkFilterTeamBean;
import com.m.pk.databinding.FragmentTechPkRankingListBinding;
import com.m.pk.event.DateChangedEvent;
import com.m.pk.httprequest.DataManager;
import com.m.pk.httprequest.RequestConstant;
import com.m.pk.httprequest.response.PKPersonalListResult;
import com.m.pk.httprequest.response.PKTeamListResult;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.Utils;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

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
    private String mActivityId;
    private String mSortKey;
    private String mStartDate, mEndDate;
    private String mCurrentFilterTeamId;
    private String mCurrentFilterTeamName;
    private List<PkFilterTeamBean> mPkFilterTeamList;
    FragmentTechPkRankingListBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_pk_ranking_list, container, false);
        initView();
        getRankingData();
        mBinding.setFragment(this);
        EventBus.getDefault().register(this);
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
        mDetailAdapter = new PkRankingListAdapter(getActivity(), mData);
        mBinding.recyclerTechPKRankingList.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerTechPKRankingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerTechPKRankingList.setHasFixedSize(true);
        mBinding.recyclerTechPKRankingList.setAdapter(mDetailAdapter);
        mDetailAdapter.setTeamFilter(new PkRankingListAdapter.TeamFilterListener() {
            @Override
            public void filterTeam(View view) {
                final PopupWindow popupWindow = new BasePopupWindow(getActivity());
                popupWindow.setWidth(Utils.dip2px(getActivity(),120));
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_array_list,null);
                ListView listView = (ListView) popupView.findViewById(R.id.list_team);
                FiltrateAdapter filtrateAdapter = new FiltrateAdapter();
                listView.setAdapter(filtrateAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentFilterTeamId = mPkFilterTeamList.get(position).teamId;
                        mCurrentFilterTeamName = mPkFilterTeamList.get(position).teamName;
                        getRankingData();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.setContentView(popupView);
                popupWindow.showAsDropDown(view);
            }
        });
        mCurrentFilterTeamId = "";
        mCurrentFilterTeamName = "全部队伍";
    }

    @Subscribe
    public void onDateChangedSubScribe(DateChangedEvent event) {
        mStartDate = event.startDate;
        mEndDate = event.endDate;
        getRankingData();
    }

    @Override
    public void onRefresh() {
        getRankingData();
    }

    public void getRankingData() {
        mBinding.swipeTechPkRanking.setRefreshing(true);
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
                mBinding.swipeTechPkRanking.setRefreshing(false);
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void getPersonalRankingData() {
        DataManager.getInstance().getPkPersonalList(mActivityId, mSortKey, mCurrentFilterTeamId, mStartDate, mEndDate, String.valueOf(1), String.valueOf(1000), new NetworkSubscriber<PKPersonalListResult>() {
            @Override
            public void onCallbackSuccess(PKPersonalListResult result) {
                mBinding.swipeTechPkRanking.setRefreshing(false);
                for (int i = 0; i < result.getRespData().size(); i++) {
                    result.getRespData().get(i).setTeam(false);
                    result.getRespData().get(i).setPosition(mTeamNumber + i);
                    result.getRespData().get(i).setTeamSize(mTeamNumber);
                    result.getRespData().get(i).setSortType(mSortKey);
                }
                mData.addAll(result.getRespData());
                mDetailAdapter.setData(mData, mCurrentFilterTeamName);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
                mBinding.swipeTechPkRanking.setRefreshing(false);
            }
        });
    }

    private class FiltrateAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mPkFilterTeamList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPkFilterTeamList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_array_popup_window, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_item);
            textView.setText(mPkFilterTeamList.get(position).teamName);
            if (mCurrentFilterTeamName.equals(mPkFilterTeamList.get(position))) {
                textView.setSelected(true);
            } else {
                textView.setSelected(false);
            }
            return view;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
