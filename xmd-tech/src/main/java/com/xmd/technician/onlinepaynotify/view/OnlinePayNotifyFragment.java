package com.xmd.technician.onlinepaynotify.view;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Adapter.CommonRecyclerViewAdapter;
import com.xmd.technician.BR;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Callback;
import com.xmd.technician.common.Logger;
import com.xmd.technician.databinding.FragmentOnlinePayNotifyBinding;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.event.PayNotifyArchiveEvent;
import com.xmd.technician.onlinepaynotify.event.PayNotifyNewDataEvent;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfoManager;
import com.xmd.technician.onlinepaynotify.viewmodel.PayNotifyInfoViewModel;
import com.xmd.technician.widget.CustomAlertDialog;
import com.xmd.technician.window.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * 显示用户在线支付情况
 */
public class OnlinePayNotifyFragment extends BaseFragment {
    private static final String ARG_START_TIME = "start_time";
    private static final String ARG_END_TIME = "end_time";
    private static final String ARG_STATUS = "mStatus";
    private static final String ARG_ONLY_NOT_ARCHIVED = "archived";
    private static final String ARG_LIMIT_COUNT = "limitCount";

    private long mStartTime;
    private long mEndTime;
    private int mStatus;
    private boolean mOnlyNotArchived;
    private int mLimitCount;
    private boolean mSetFooter;

    public ObservableBoolean showLoading = new ObservableBoolean();
    public ObservableField<String> errorString = new ObservableField<>();

    private FragmentOnlinePayNotifyBinding mBinding;
    private CommonRecyclerViewAdapter<PayNotifyInfo> mAdapter;

    private Subscription mPayNotifyArchiveEventSubscription;
    private Subscription mPayNotifyNewDataEventSubscription;

    private boolean mIsFirstArchived = false;

    public OnlinePayNotifyFragment() {
        // Required empty public constructor
        mIsFirstArchived = SharedPreferenceHelper.getPayNotifyIsFirstHide();
    }

    /**
     * 创建一个fragment，并设置显示的时间范围和状态
     *
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param status          状态
     * @param onlyNotArchived 只显示非归档的数据
     * @return
     */
    public static OnlinePayNotifyFragment newInstance(long startTime, long endTime, int status, boolean onlyNotArchived) {
        return newInstance(startTime, endTime, status, onlyNotArchived, -1);
    }

    public static OnlinePayNotifyFragment newInstance(long startTime, long endTime, int status, boolean onlyNotArchived, int limitCount) {
        OnlinePayNotifyFragment fragment = new OnlinePayNotifyFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_START_TIME, startTime);
        args.putLong(ARG_END_TIME, endTime);
        args.putInt(ARG_STATUS, status);
        args.putBoolean(ARG_ONLY_NOT_ARCHIVED, onlyNotArchived);
        args.putInt(ARG_LIMIT_COUNT, limitCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStartTime = getArguments().getLong(ARG_START_TIME);
            mEndTime = getArguments().getLong(ARG_END_TIME);
            mStatus = getArguments().getInt(ARG_STATUS);
            mOnlyNotArchived = getArguments().getBoolean(ARG_ONLY_NOT_ARCHIVED);
            mLimitCount = getArguments().getInt(ARG_LIMIT_COUNT);
            if (mLimitCount <= 0) {
                mLimitCount = Integer.MAX_VALUE;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentOnlinePayNotifyBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mOnlyNotArchived) {
            mBinding.recyclerView.setNestedScrollingEnabled(false);
        } else {
            mBinding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    Logger.i("#####" + mBinding.recyclerView.canScrollVertically(1) + "," + mBinding.recyclerView.canScrollVertically(-1));
                    if (!mSetFooter && (mBinding.recyclerView.canScrollVertically(1) || mBinding.recyclerView.canScrollVertically(-1))) {
                        mAdapter.setFooter(R.layout.list_footer_no_more, -1, null);
                        mAdapter.notifyDataSetChanged();
                        mSetFooter = true;
                    }
                }
            });
        }
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(300);
        mBinding.recyclerView.setItemAnimator(itemAnimator);
        mAdapter = new CommonRecyclerViewAdapter<>();
        mAdapter.setDataTranslator(mDataTranslator);
        mAdapter.setShowDataCountLimit(mLimitCount);
        mBinding.recyclerView.setAdapter(mAdapter);
        mPayNotifyArchiveEventSubscription = RxBus.getInstance().toObservable(PayNotifyArchiveEvent.class).subscribe(this::handlePayNotifyArchiveEvent);
        mPayNotifyNewDataEventSubscription = RxBus.getInstance().toObservable(PayNotifyNewDataEvent.class).subscribe(this::handlePayNotifyNewDataEvent);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPayNotifyArchiveEventSubscription != null) {
            mPayNotifyArchiveEventSubscription.unsubscribe();
        }
        if (mPayNotifyNewDataEventSubscription != null) {
            mPayNotifyNewDataEventSubscription.unsubscribe();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(false);
    }

    //设置过滤器
    public void setFilter(long startTime, long endTime, int status, boolean onlyNotArchived) {
        mStartTime = startTime;
        mEndTime = endTime;
        mStatus = status;
        mOnlyNotArchived = onlyNotArchived;
    }

    //加载数据
    public void loadData(boolean forceNetwork) {
        if (!TextUtils.isEmpty(LoginTechnician.getInstance().getClubId())) {
            loadData(forceNetwork, mStartTime, mEndTime, mStatus, mOnlyNotArchived);
        } else {
            mAdapter.setData(R.layout.list_item_pay_notify, BR.payNotify, new ArrayList<>());
            removeFooter();
            mAdapter.notifyDataSetChanged();
            errorString.set("您暂未加入会所，无法查看数据～");
        }
    }


    private void loadData(boolean forceNetwork, long startTime, long endTime, int status, boolean onlyNotArchived) {
        //加载数据
        showLoading.set(true);
        errorString.set(null);
        PayNotifyInfoManager.getInstance().getNotifyInfo(forceNetwork, startTime, endTime, status, onlyNotArchived, Integer.MAX_VALUE, new Callback<List<PayNotifyInfo>>() {
            @Override
            public void onResult(Throwable error, List<PayNotifyInfo> result) {
                showLoading.set(false);

                if (error == null) {
                    mAdapter.setData(R.layout.list_item_pay_notify, BR.payNotify, result);
                    removeFooter();
                    mAdapter.notifyDataSetChanged();
                    if (result.size() == 0) {
                        errorString.set("暂无新记录，尝试下拉刷新～");
                    } else {
                        errorString.set(null);
                    }
                } else {
                    errorString.set("加载失败，尝试下拉刷新～");
                }
            }
        });
    }

    private void removeFooter() {
        mAdapter.setFooter(0, -1, null);
        mSetFooter = false;
    }

    //数据转换器
    private CommonRecyclerViewAdapter.DataTranslator mDataTranslator = new CommonRecyclerViewAdapter.DataTranslator() {
        @Override
        public Object translate(Object originData) {
            return new PayNotifyInfoViewModel((PayNotifyInfo) originData);
        }
    };

    //接收买单通知被归档的事件
    public void handlePayNotifyArchiveEvent(PayNotifyArchiveEvent event) {
        PayNotifyInfo info = event.info;
        if (mOnlyNotArchived && mIsFirstArchived) {
            new CustomAlertDialog.Builder(getContext())
                    .setTitle("温馨提示")
                    .setMessage("确认或超过12小时后，本通知将不再保留在首页，如有需要请查看全部。")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .build()
                    .show();
            mIsFirstArchived = false;
            SharedPreferenceHelper.setPayNotifyIsFirstHide(false);
        }
        int position = 0;
        if (mAdapter == null || mAdapter.getDataList() == null) {
            return;
        }
        for (; position < mAdapter.getDataList().size(); position++) {
            if (mAdapter.getData(position).id.equals(info.id)) {
                if (mOnlyNotArchived) {
                    mAdapter.getDataList().remove(position);
                    if (position <= mLimitCount) {
                        mAdapter.notifyItemRemoved(position);
                        if (mAdapter.getDataList().size() == 0) {
                            errorString.set("暂无新记录，尝试下拉刷新～");
                        }
                    }
                } else {
                    if (position <= mLimitCount) {
                        mAdapter.notifyItemChanged(position);
                    }
                }
            }
        }
    }

    //接收最新数据变化的通知
    public void handlePayNotifyNewDataEvent(PayNotifyNewDataEvent event) {
        loadData(false);
    }
}
