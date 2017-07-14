package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.ListRecycleViewAdapter;
import com.xmd.manager.adapter.TechnicianRecycleViewAdapter;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.beans.Technician;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CustomerFilterResult;
import com.xmd.manager.service.response.CustomerListResult;
import com.xmd.manager.service.response.TechListResult;
import com.xmd.manager.stickyview.StickyHeaderInterface;
import com.xmd.manager.stickyview.StickyRecyclerHeadersDecoration;
import com.xmd.manager.widget.DividerDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-18.
 */
public class CustMgmtDetailListFragment extends BaseFragment implements ListRecycleViewAdapter.Callback<Customer>, SwipeRefreshLayout.OnRefreshListener, TechnicianRecycleViewAdapter.Callback {
    protected static final int PAGE_START = 0;
    protected static final int PAGE_SIZE = 20;

    //默认排序
    private static final String SORT_DESC = "desc";
    private static final String SORT_ASC = "asc";

    public static final String BIZ_TYPE = "bizType";
    public static final int TAB_TECHNICIAN = 0;
    public static final int TAB_ACTIVE_DEGREE = 1;
    public static final int TAB_BAD_COMMENT = 2;

    @BindView(R.id.technicain_list)
    RecyclerView mRvTechnician;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list)
    RecyclerView mListView;

    protected LinearLayoutManager mLayoutManager;
    protected ListRecycleViewAdapter mListAdapter;

    protected int mPages = PAGE_START;
    protected boolean mIsLoadingMore = false;
    protected int mLastVisibleItem;
    protected int mPageCount = -1;
    protected List<Customer> mData = new ArrayList<>();

    private int mType;
    private List<Technician> mTechnicianDataList = new ArrayList<>();
    private TechnicianRecycleViewAdapter mTechnicianListAdapter;

    private int mCurrentTechPosition = 0;
    /**
     * 记录最后一次上滑加载的技师数据信息
     */
    private int mTopTechPosition = 0;
    private int mTopPosPage = PAGE_START;
    private int mTopPosPageCount = -1;
    /**
     * 记录最后一次下滑加载的技师数据信息
     */
    private int mBelowTechPosition = 0;
    private int mBelowPosPage = PAGE_START;
    private int mBelowPosPageCount = -1;
    private int mTechListLastVisibleItem;

    private boolean mIsLoadingMoreTech = false;
    private int mTechPageCount = -1;
    private int mTechPages = PAGE_START;
    private String mCustomerTypeFilter = "";
    private String mSortType = SORT_DESC;

    private StickyRecyclerHeadersDecoration mHeaderDecoration;

    private Subscription mCustomerFilterSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cust_mgmt_detail_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListLayout();
    }

    protected void initListLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(mLayoutManager);
        mListAdapter = new ListRecycleViewAdapter(getActivity(), mData, this);
        mListView.setAdapter(mListAdapter);
        mListView.setItemAnimator(new DefaultItemAnimator());
        if (TAB_BAD_COMMENT != mType) {  //添加置顶悬浮栏
            mHeaderDecoration = new StickyRecyclerHeadersDecoration(mStickyHeaderInterface);
            mListView.addItemDecoration(mHeaderDecoration);
        }
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mLastVisibleItem + 1 == mListAdapter.getItemCount() && mLayoutManager.findFirstVisibleItemPosition() != 0) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                int position = mLayoutManager.findFirstVisibleItemPosition();
                if (position > 0) {
                    setCheckedTechView(position);
                }
            }
        });

        initPositionParams(0);
        onRefreshTechCustomers(false, SORT_DESC);
    }

    @Override
    protected void initView() {
        //mListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mType = mArguments.getInt(BIZ_TYPE);
        if (TAB_TECHNICIAN == mType) {
            mRvTechnician.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRvTechnician.setVisibility(View.VISIBLE);
            mRvTechnician.setHasFixedSize(true);
            mRvTechnician.addItemDecoration(new DividerDecoration(DividerDecoration.VERTICAL_LIST, ResourceUtils.getDrawable(R.drawable.list_item_divider_tech)));
            mTechnicianListAdapter = new TechnicianRecycleViewAdapter(mTechnicianDataList, this);
            mRvTechnician.setAdapter(mTechnicianListAdapter);
            mRvTechnician.setItemAnimator(new DefaultItemAnimator());
            mRvTechnician.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        LinearLayoutManager lm = (LinearLayoutManager) mRvTechnician.getLayoutManager();
                        if (mTechListLastVisibleItem == mTechnicianListAdapter.getItemCount() - 1) {
                            // TODO: 16-7-27 load more tech
                            loadMoreTechData();
                        } else if (lm.findFirstVisibleItemPosition() == 0 && lm.findViewByPosition(lm.findFirstVisibleItemPosition()).getTop() == 0) {
                            onRefreshClubTechs();
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mTechListLastVisibleItem = ((LinearLayoutManager) mRvTechnician.getLayoutManager()).findLastVisibleItemPosition();
                }
            });
            mTechnicianListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    mHeaderDecoration.invalidateHeaders();
                }
            });
            onRefreshClubTechs();
        } else {
            mRvTechnician.setVisibility(View.GONE);
        }

        mCustomerFilterSubscription = RxBus.getInstance().toObservable(CustomerFilterResult.class).subscribe(
                result -> handleCustomerFilterChange(result));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mCustomerFilterSubscription);
    }

    private void handleCustomerFilterChange(CustomerFilterResult result) {
        mSwipeRefreshLayout.setRefreshing(true);
        mCustomerTypeFilter = result.userType;
        if (TAB_TECHNICIAN == mType) {
            mTechnicianListAdapter.setCheckedPosition(0);
            mTechnicianDataList.clear();
            onRefreshClubTechs();
            initPositionParams(0);
            mLayoutManager.scrollToPositionWithOffset(0, 0);
        }
        onRefreshTechCustomers(false, SORT_DESC);
    }

    private void initPositionParams(int position) {
        mTopPosPage = PAGE_START;
        mTopPosPageCount = -1;
        mTopTechPosition = position;

        mBelowPosPage = PAGE_START;
        mBelowPosPageCount = -1;
        mBelowTechPosition = position;

        mCurrentTechPosition = position;
    }

    public void setTechnicianList(TechListResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        mTechPageCount = result.pageCount;
        if (result.respData != null) {
            if (!mIsLoadingMoreTech) {
                mTechnicianDataList.clear();
            }
            mTechnicianDataList.addAll(result.respData);
            mTechnicianListAdapter.setData(mTechnicianDataList, mCurrentTechPosition);
        }
    }

    public void setCustomerList(CustomerListResult result) {
        if (result.statusCode == 200 && result.respData != null) {
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            onGetListFailed(result.msg);
        }
    }

    protected void onGetListSucceeded(int pageCount, List<Customer> list) {
        for (Customer customer : list) {
            User user = new User(customer.userId);
            user.setName(customer.userName);
            user.setChatId(customer.emchatId);
            user.setAvatar(customer.userHeadimgurl);
            UserInfoServiceImpl.getInstance().saveUser(user);
        }
        mSwipeRefreshLayout.setRefreshing(false);
        mPageCount = pageCount;
        if (list != null) {
            if (!mIsLoadingMore) {
                mData.clear();
            }
            if (SORT_ASC.equals(mSortType)) {
                Collections.reverse(list);
                mData.addAll(0, list);
            } else {
                mData.addAll(list);
            }
            //mData.addAll(list);
            if (SORT_DESC.equals(mSortType))
                mListAdapter.setIsNoMore((mPages == mPageCount) || (mPageCount == 0));
            mListAdapter.setData(mData);

            if (TAB_TECHNICIAN == mType) {
                if (SORT_ASC.equals(mSortType)) {//上滑加载更多用户时
                    setCheckedTechView(mData.get(0).techId);

                    /*if(mPages == mPageCount){
                        mTopPosPage = PAGE_START;
                        mTopPosPageCount = -1;
                    }else*/
                    {
                        mTopPosPage = mPages;
                        mTopPosPageCount = mPageCount;
                    }
                } else if (SORT_DESC.equals(mSortType)) {//下滑
                    /*if(mPages == mPageCount){
                        mBelowPosPage = PAGE_START;
                        mBelowPosPageCount = -1;
                    }else*/
                    {
                        mBelowPosPage = mPages;
                        mBelowPosPageCount = mPageCount;
                    }

                    if (mData.size() < 8) {
                        loadMore();
                    }
                }
            }
        }
    }

    protected void onGetListFailed(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        Utils.makeShortToast(getActivity(), errorMsg);
    }

    protected void dispatchRequest() {
        switch (mType) {
            case TAB_TECHNICIAN:
                dispatchCustomersRequest("createTime", mTechnicianDataList.isEmpty() ? "-1" : mTechnicianDataList.get(mCurrentTechPosition).techId);
                break;
            case TAB_ACTIVE_DEGREE:
                dispatchCustomersRequest("loginDate", "");
                break;
            case TAB_BAD_COMMENT:
                dispatchCustomersRequest("badCommentCount", "");
                break;
        }
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    public void onSlideDeleteItem(Customer bean) {

    }

    @Override
    public boolean isSlideable() {
        return false;
    }

    @Override
    public void onItemClicked(int position, Technician bean) {
        if (mType != TAB_TECHNICIAN) {
            return;
        }

        initPositionParams(position);
        //mTechnicianListAdapter.setCheckedPosition(position);
        //int id =mLayoutManager.findFirstVisibleItemPosition();
        mLayoutManager.scrollToPositionWithOffset(0, 0);
        onRefreshTechCustomers(false, SORT_DESC);
    }

    @Override
    public void onItemClicked(Customer bean) {
//        Intent intent = new Intent(getActivity(), CustomerActivity.class);
//        intent.putExtra(CustomerActivity.ARG_CUSTOMER, bean);
//        startActivity(intent);
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.userId, "manger", false);
    }

    @Override
    public void onNegativeButtonClicked(Customer bean) {

    }

    @Override
    public void onPositiveButtonClicked(Customer bean) {

    }

    @Override
    public void onLoadMoreButtonClicked() {
        loadMore();
    }

    @Override
    public void onRefresh() {
        if (TAB_TECHNICIAN == mType) {
            //加载更多上一个技师的用户信息，方向一致
            if (/*SORT_DESC.equals(mSortType) && */mTopPosPageCount > 0) {
                mPages = mTopPosPage;
                mPageCount = mTopPosPageCount;
                mCurrentTechPosition = mTopTechPosition;
                mSortType = SORT_ASC;
                if (mData.size() != PAGE_SIZE && getTechCustomerList()) {
                    return;
                }
            }

            //开始加载上一个技师的用户信息
            if (loadLastTechCustomers()) return;

            //下拉列表时重新定位当前技师
            initPositionParams(mTechnicianListAdapter.getCheckedPosition());

            if (mTechnicianDataList.isEmpty()) {
                onRefreshClubTechs();
            }
        }

        onRefreshTechCustomers(false, SORT_DESC);
    }

    private void loadMore() {
        //上次加载方向一致
        if (/*SORT_ASC.equals(mSortType) && */mBelowPosPageCount > 0) {
            mPages = mBelowPosPage;
            mPageCount = mBelowPosPageCount;
            mCurrentTechPosition = mBelowTechPosition;
            mSortType = SORT_DESC;
        }

        if (getTechCustomerList()) {
            //上拉刷新，加载更多数据
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        } else {
            loadNextTechCustomers();
        }
    }

    private boolean loadNextTechCustomers() {
        if (TAB_TECHNICIAN == mType) {
            //mCurrentTechPosition = Math.max(mTechnicianListAdapter.getCheckedPosition(), mCurrentTechPosition);
            if (mBelowTechPosition < (mTechnicianListAdapter.getItemCount() - 1)) {
                if (mTechnicianDataList.get(mBelowTechPosition + 1).count == 0) {
                    return false;
                }
                mBelowTechPosition++;
                mCurrentTechPosition = mBelowTechPosition;
                //mTechnicianListAdapter.setCheckedView(mRvTechnician.findViewHolderForAdapterPosition(mCheckedPosition).itemView);
                mSwipeRefreshLayout.setRefreshing(true);
                onRefreshTechCustomers(true, SORT_DESC);
                return true;
            }
        }
        return false;
    }

    private boolean loadLastTechCustomers() {
        if (TAB_TECHNICIAN == mType) {
            //mCurrentTechPosition = Math.min(mTechnicianListAdapter.getCheckedPosition(), mCurrentTechPosition);
            if (mTopTechPosition > 0) {
                mTopTechPosition--;
                mCurrentTechPosition = mTopTechPosition;
                //mTechnicianListAdapter.setCheckedView(mRvTechnician.findViewHolderForAdapterPosition(mCheckedPosition).itemView);
                mSwipeRefreshLayout.setRefreshing(true);
                onRefreshTechCustomers(true, SORT_ASC);
                return true;
            }
        }
        return false;
    }

    private boolean getClubTechList() {
        if (mTechPageCount < 0 || mTechPages + 1 <= mTechPageCount) {
            mTechPages++;
            dispatchTechsRequest();
            return true;
        }
        return false;
    }

    private boolean getTechCustomerList() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            dispatchRequest();
            return true;
        }
        return false;
    }

    private void loadMoreTechData() {
        if (getClubTechList()) {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMoreTech = true;
        }
    }

    /**
     * 加载会所技师列表
     */
    private void onRefreshClubTechs() {
        mIsLoadingMoreTech = false;
        mTechPages = PAGE_START;
        mTechPageCount = -1;
        getClubTechList();
    }

    /**
     * 加载会所客户信息
     *
     * @param loadMore
     * @param sortType
     */
    private void onRefreshTechCustomers(boolean loadMore, String sortType) {
        mIsLoadingMore = loadMore;
        mPages = PAGE_START;
        mPageCount = -1;
        mSortType = sortType;
        getTechCustomerList();
    }

    private void dispatchTechsRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mTechPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(1000));
        params.put(RequestConstant.KEY_USER_TYPE, mCustomerTypeFilter);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_TECH_LIST, params);
    }

    private void dispatchCustomersRequest(String sort, String techId) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_USER_TYPE, mCustomerTypeFilter);
        params.put(RequestConstant.KEY_SORT, sort);
        params.put(RequestConstant.KEY_SORT_TYPE, mSortType);
        params.put(RequestConstant.KEY_TECH_ID, techId);
        params.put(RequestConstant.KEY_TYPE, String.valueOf(mType));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_CUSTOMER_LIST, params);

    }

    private StickyHeaderInterface mStickyHeaderInterface = new StickyHeaderInterface() {
        @Override
        protected String getHeaderId(int position) {
            try {
                if (TAB_TECHNICIAN == mType) {
                    String headId = mData.get(position).techId;
                    if (TextUtils.isEmpty(headId)) headId = "-1";
                    return headId;
                } else if (TAB_ACTIVE_DEGREE == mType) {
                    return String.valueOf(mData.get(position).active);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item_header, parent, false);
            return new CustomerHeaderItemViewHolder(view);
        }

        @Override
        protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                CustomerHeaderItemViewHolder viewHolder = (CustomerHeaderItemViewHolder) holder;
                if (TAB_TECHNICIAN == mType) {
                    String techId;
                    if (Utils.isNotEmpty(mData.get(position).techId)) {
                        techId = mData.get(position).techId;
                    } else {
                        techId = "-1";
                    }
                    Iterator<Technician> iterator = mTechnicianDataList.iterator();
                    while (iterator.hasNext()) {
                        Technician technician = iterator.next();
                        String technicianId = Utils.isEmpty(technician.techId) ? "-1" : technician.techId;
                        if (technicianId.equals(techId)) {
                            viewHolder.mTvHeader.setText(technician.techName);
                            viewHolder.mTvGroupCount.setText(String.format(ResourceUtils.getString(R.string.person_unit_format), technician.count));
                            break;
                        }
                    }
                } else if (TAB_ACTIVE_DEGREE == mType) {
                    viewHolder.mTvHeader.setText(Constant.ACTIVE_DEGREES.get(mData.get(position).active));
                    viewHolder.mTvGroupCount.setText("");
                }
            } catch (IndexOutOfBoundsException e) {

            }
        }

        @Override
        protected int getItemCount() {
            return mData.size();
        }

        @Override
        protected boolean showItemHeader() {
            return true;
        }
    };

    private void setCheckedTechView(int position) {
        setCheckedTechView(mData.get(position).techId);
    }

    private void setCheckedTechView(String techId) {
        try {
            if (TAB_TECHNICIAN == mType && !mTechnicianDataList.isEmpty()) {
                if (mTechnicianDataList.get(mTechnicianListAdapter.getCheckedPosition()).techId.equals(techId)) {
                    return;
                }

                if (TextUtils.isEmpty(techId)) techId = "-1";
                for (int i = 0; i < mTechnicianDataList.size(); i++) {
                    if (mTechnicianDataList.get(i).techId.equals(techId)) {
                        mRvTechnician.scrollToPosition(i);
                        mTechnicianListAdapter.setCheckedPosition(i);
                        mTechnicianListAdapter.setCheckedView(mRvTechnician.findViewHolderForAdapterPosition(i).itemView);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CustomerHeaderItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header)
        TextView mTvHeader;
        @BindView(R.id.tv_group_count)
        TextView mTvGroupCount;

        public CustomerHeaderItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onLongClicked(Customer bean) {

    }

    @Override
    public boolean showStatData() {
        return false;
    }
}
