package com.xmd.manager.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.R;
import com.xmd.manager.beans.OperateReportBean;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateListFragment extends BaseListFragment<OperateReportBean> {

    public static final String OPERATE_LIST_TYPE = "operateListType";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operate_list,container,false);
    }

    @Override
    protected void dispatchRequest() {

    }

    @Override
    protected void initView() {
        getArguments().get(OPERATE_LIST_TYPE);
        XLogger.i(">>>","type>"+getArguments().get(OPERATE_LIST_TYPE));
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean isPaged() {
        return false;
    }
}
