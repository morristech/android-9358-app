package com.xmd.technician.onlinepaynotify.view;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.BR;
import com.xmd.technician.R;
import com.xmd.technician.common.Callback;
import com.xmd.technician.databinding.FragmentOnlinePayNotifyBinding;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfoManager;
import com.xmd.technician.onlinepaynotify.viewmodel.PayNotifyInfoViewModel;
import com.xmd.technician.widget.CommonRecyclerViewAdapter;
import com.xmd.technician.window.BaseFragment;

import java.util.List;

/**
 * 显示用户在线支付情况
 */
public class OnlinePayNotifyFragment extends BaseFragment {
    private static final String ARG_START_TIME = "start_time";
    private static final String ARG_END_TIME = "end_time";
    private static final String ARG_STATUS = "status";

    private long startTime;
    private long endTime;
    private int status;

    private FragmentOnlinePayNotifyBinding mBinding;
    private CommonRecyclerViewAdapter<PayNotifyInfo> mAdapter;


    public OnlinePayNotifyFragment() {
        // Required empty public constructor
    }

    /**
     * 创建一个fragment，并设置显示的时间范围和状态
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    状态
     * @return
     */
    public static OnlinePayNotifyFragment newInstance(long startTime, long endTime, int status) {
        OnlinePayNotifyFragment fragment = new OnlinePayNotifyFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_START_TIME, startTime);
        args.putLong(ARG_END_TIME, endTime);
        args.putInt(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startTime = getArguments().getLong(ARG_START_TIME);
            endTime = getArguments().getLong(ARG_END_TIME);
            status = getArguments().getInt(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentOnlinePayNotifyBinding.inflate(inflater, container, false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 1, 0, 1);
            }
        });
        mAdapter = new CommonRecyclerViewAdapter<>();
        mAdapter.setDataTranslator(new CommonRecyclerViewAdapter.DataTranslator() {
            @Override
            public Object translate(Object originData) {
                return new PayNotifyInfoViewModel((PayNotifyInfo) originData);
            }
        });
        mBinding.recyclerView.setAdapter(mAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(startTime, endTime, status);
    }

    public void onClickArchive(Uri uri) {

    }

    //设置过滤器
    public void setFilter(long startTime, long endTime, int status) {
        loadData(startTime, endTime, status);
    }

    private void loadData(long startTime, long endTime, int status) {
        //加载数据
        PayNotifyInfoManager.getInstance().getNotifyInfo(startTime, endTime, status, new Callback<List<PayNotifyInfo>>() {
            @Override
            public void onResult(Throwable error, List<PayNotifyInfo> result) {
                if (error == null) {
                    mAdapter.setData(R.layout.list_item_pay_notify, BR.payNotify, result);
                    mAdapter.notifyDataSetChanged();
                } else {
                    //TODO
                }
            }
        });
    }
}
