package com.xmd.manager.window;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.adapter.VerificationRecordAdapter;
import com.xmd.manager.beans.TypeBean;
import com.xmd.manager.beans.VerificationDetailBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.RecordTypeListResult;
import com.xmd.manager.service.response.VerificationRecordListResult;
import com.xmd.manager.widget.ArrayBottomPopupWindow;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by Lhj on 2017/1/12.
 */
public class VerificationRecordListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.search_verification)
    ClearableEditText searchVerification;
    @BindView(R.id.btn_search)
    ImageView mBtnSearch;
    @BindView(R.id.record_list_view)
    RecyclerView mRecordListView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.record_month)
    TextView mRecordMonth;
    @BindView(R.id.record_total)
    TextView mRecordTotal;
    @BindView(R.id.rl_record_total)
    RelativeLayout mRlRecordTotal;

    private int PAGE_SIZE = 20;
    private ArrayBottomPopupWindow<String> mVerificationPopupWindow;
    private List<TypeBean> mTypeList; //核销类型列表
    private Map<String, String> mTypeMap; //核销类型键值对
    private HashMap<String ,String> mDateTotalMap; //月份总数键值对
    private List<VerificationDetailBean> mRecordBeanList; //核销数据列表
    private String mStartTime;//起始时间
    private String mEndTime;//结束时间
    private String mSearchPhone;//查询电话
    private String mFilterType; //当前过滤的类型
    private LinearLayoutManager mLayoutManager;
    private VerificationRecordAdapter mRecordAdapter;
    private int mFirstVisibleItemPosition;//第一个可见核销记录
    private int mLastVisibleItemPosition; //最后一个可见核销记录
    private int mRemainDerTotal; //剩余未加载数量
    private boolean isRefresh; //是否将刷新重置数据
    private int mCurrentLoadTotal = 0; //加载总数
    private Subscription mGetAllTypeSubscription;
    private Subscription mGetRecordListSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_record);
        ButterKnife.bind(this);
        mRightText = (TextView) findViewById(R.id.toolbar_right_text);
        initView();
        initRecordListLayout();
    }

    private void initView() {
        mTypeMap = new LinkedHashMap<String, String>();
        mTypeList = new ArrayList<>();
        mRecordBeanList = new ArrayList<>();
        mDateTotalMap = new HashMap<>();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_INFO_TYPE_LIST);
        Utils.hideKeyboard(VerificationRecordListActivity.this);
        setRightVisible(true, ResourceUtils.getString(R.string.layout_technician_ranking_check_all), ResourceUtils.getDrawable(R.drawable.ic_record_filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerificationPopupWindow.showAsDownCenter();
            }
        }, false);
        searchVerification.setClearEditTextListener(new ClearableEditText.ClearEditText() {
            @Override
            public void clearListener() {
                if(Utils.isNotEmpty(mSearchPhone)){
                    mSearchPhone = "";
                    onRefresh();
                }
            }
        });
        mGetAllTypeSubscription = RxBus.getInstance().toObservable(RecordTypeListResult.class).subscribe(
                listResult -> handlerRecordTypeListResult(listResult)
        );

        mGetRecordListSubscription = RxBus.getInstance().toObservable(VerificationRecordListResult.class).subscribe(
                recordListResult -> handlerRecordListResult(recordListResult)
        );
    }




    private void initRecordListLayout() {
        mFilterType = "";
        mSearchPhone = "";
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecordListView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecordListView.setLayoutManager(mLayoutManager);
        mRecordListView.setItemAnimator(new DefaultItemAnimator());
        mRecordAdapter = new VerificationRecordAdapter(VerificationRecordListActivity.this, mRecordBeanList, new VerificationRecordAdapter.ClickedCallback() {
            @Override
            public void loadMore() {
                getData();
            }

            @Override
            public void onItemClicked(VerificationDetailBean bean) {
                Intent intent = new Intent(VerificationRecordListActivity.this, VerificationRecordDetailActivity.class);
                intent.putExtra(RequestConstant.KEY_VERIFICATION_RECORD_ID, bean.id);
                startActivity(intent);
            }

        });
        mRecordListView.setAdapter(mRecordAdapter);
        mRecordListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItemPosition + 1 == mRecordAdapter.getItemCount()) {
                    if (mRemainDerTotal > 0) {
                        getData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if(mRecordBeanList.size()>0){
                    String date = mRecordBeanList.get(mFirstVisibleItemPosition).currentMonth.substring(0, 4) + "年" + mRecordBeanList.get(mFirstVisibleItemPosition).currentMonth.substring(5, 7) + "月";
                    mRecordMonth.setText(date);
                    mRecordTotal.setText(String.format("核销数： %s",mDateTotalMap.get(date)));
                }
            }
        });
        onRefresh();
    }

    private void handlerRecordTypeListResult(RecordTypeListResult listResult) {
        if (listResult.statusCode == 200) {
            mTypeList.addAll(listResult.respData);
            mTypeList.add(0, new TypeBean("", ResourceUtils.getString(R.string.layout_technician_ranking_check_all)));
            mTypeMap.clear();
            for (int i = 0; i < mTypeList.size(); i++) {
                mTypeMap.put(mTypeList.get(i).value, mTypeList.get(i).key);
            }
            initTypeView();
        }
    }


    private void initTypeView() {
        mVerificationPopupWindow = new ArrayBottomPopupWindow<String>(mRightText, null, ResourceUtils.getDimenInt(R.dimen.order_type_item_width), true);
        mVerificationPopupWindow.setDataSet(new ArrayList<>(mTypeMap.keySet()), mRightText.getText().toString());
        mVerificationPopupWindow.setItemClickListener((parent, view, position, id) -> {
            String sTitle = (String) parent.getAdapter().getItem(position);
            mFilterType = mTypeList.get(position).key;
            initTitleData(sTitle);
        });
    }

    private void initTitleData(String sTitle) {
        mRightText.setText(sTitle);
        mVerificationPopupWindow.setDataSet(new ArrayList<>(mTypeMap.keySet()), mRightText.getText().toString());
        isRefresh = true;
        onRefresh();
    }

    private void handlerRecordListResult(VerificationRecordListResult recordListResult) {
        if (isRefresh) {
            mRecordBeanList.clear();
            isRefresh = false;
        }

        if (recordListResult.respData == null) {
            XToast.show("暂无数据！");
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        mRemainDerTotal = recordListResult.respData.remainderCount; //剩余的未加载总数
        mCurrentLoadTotal += recordListResult.respData.data.size(); //加载20条条件限制，当前共加载了多少条

        if(mRemainDerTotal>0){
            if(recordListResult.respData.data.size()>0){
                String date = recordListResult.respData.data.get(0).verifyTime.substring(0, 4) + "年" + recordListResult.respData.data.get(0).verifyTime.substring(5, 7) + "月";
                if(!mDateTotalMap.containsKey(date)){
                    mDateTotalMap.put(date,recordListResult.respData.total);
                }
                for (int i = 0; i < recordListResult.respData.data.size(); i++) {
                    recordListResult.respData.data.get(i).currentMonth = mStartTime.substring(0, 7);
                    recordListResult.respData.data.get(i).currentMonthTotal = recordListResult.respData.total;
                }
                mRecordBeanList.addAll(recordListResult.respData.data);
                if (mCurrentLoadTotal == 20) {
                    //加载当月的满足20条，开始时间为当月初，结束时间为最后一条加1毫秒pageSize = 20；mCurrentSizeTotal = 0;
                    PAGE_SIZE = 20;
                    mCurrentLoadTotal = 0;
                    mEndTime = DateUtil.long2Date(DateUtil.stringDateToLong(recordListResult.respData.data.get(recordListResult.respData.data.size() - 1).verifyTime) - 1000);
                    mRecordAdapter.setIsNoMore(false);
                    mRecordAdapter.setData(mRecordBeanList);
                    mSwipeRefreshLayout.setRefreshing(false);

                } else if ((mCurrentLoadTotal > 0 && mCurrentLoadTotal < 20)) {
                    //加载当月剩余部分，然后加载下个月的不足部分 pageSize = 20 - mCurrentSizeTotal,开始时间为上月月初，结束时间为上月月底
                    PAGE_SIZE = 20 - mCurrentLoadTotal;
                    mEndTime = DateUtil.getLastDayOfLastMonth(mStartTime);
                    mStartTime = DateUtil.getFirstDayOfLastMonth(mStartTime);
                    ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    },50);

                }
            }else{
                mCurrentLoadTotal += 1;
                String date = mEndTime.substring(0, 4) + "年" + mEndTime.substring(5, 7) + "月";
                mDateTotalMap.put(date,recordListResult.respData.total);
                mRecordBeanList.add(new VerificationDetailBean(mEndTime.substring(0, 7), "0"));
                mEndTime = DateUtil.getLastDayOfLastMonth(mStartTime);
                mStartTime = DateUtil.getFirstDayOfLastMonth(mStartTime);
                if(mCurrentLoadTotal == 20){
                    PAGE_SIZE = 20;
                    mCurrentLoadTotal = 0;
                    mRecordAdapter.setIsNoMore(false);
                    mRecordAdapter.setData(mRecordBeanList);
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{
                    PAGE_SIZE = 20 - mCurrentLoadTotal;
                    getData();
                }

            }
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            if (recordListResult.respData.data.size() > 0) {
                String date = recordListResult.respData.data.get(0).verifyTime.substring(0, 4) + "年" + recordListResult.respData.data.get(0).verifyTime.substring(5, 7) + "月";
                if (!mDateTotalMap.containsKey(date)) {
                    mDateTotalMap.put(date, recordListResult.respData.total);
                }
                for (int i = 0; i < recordListResult.respData.data.size(); i++) {
                    recordListResult.respData.data.get(i).currentMonth = mStartTime.substring(0, 7);
                    recordListResult.respData.data.get(i).currentMonthTotal = recordListResult.respData.total;
                }
                mRecordBeanList.addAll(recordListResult.respData.data);
            }
            mRecordAdapter.setData(mRecordBeanList);
            mRecordAdapter.setIsNoMore(true);
        }

        if(mRecordBeanList.size()>0){
            mRlRecordTotal.setVisibility(View.VISIBLE);
        }else{
            mRlRecordTotal.setVisibility(View.GONE);
        }


    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        mDateTotalMap.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        mStartTime = DateUtil.getFirstDayOfMonth() + " 00:00:01";
        mEndTime = DateUtil.getCurrentDate() + " 23:59:59";
        mCurrentLoadTotal = 0;
        PAGE_SIZE = 20;
        getData();
    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_START_DATE, mStartTime);
        params.put(RequestConstant.KEY_END_DATE, mEndTime);
        params.put(RequestConstant.KEY_VERIFICATION_TYPE, mFilterType);
        params.put(RequestConstant.KEY_SEARCH_TELEPHONE, mSearchPhone);
        params.put(RequestConstant.KEY_IS_TIME, "Y");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_INFO_RECORD_LIST, params);
    }

    @OnClick(R.id.btn_search)
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                mSearchPhone = searchVerification.getText().toString();
                if (Utils.isNotEmpty(mSearchPhone)) {
                    onRefresh();
                } else {
                    makeShortToast(ResourceUtils.getString(R.string.search_phone_alert));
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetAllTypeSubscription, mGetRecordListSubscription);
    }

}