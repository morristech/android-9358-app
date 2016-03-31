package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-24.
 */
public class OrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.list_view) RecyclerView mOrderListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.bind(this, getView());

        initView();
    }

    private void initView(){
        ((TextView)getView().findViewById(R.id.toolbar_title)).setText(R.string.order_fragment_title);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
