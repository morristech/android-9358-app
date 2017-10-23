package com.xmd.manager.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.R;
import com.xmd.manager.beans.OperateReportBean;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateListFragment extends BaseListFragment<OperateReportBean> {

    public static final String OPERATE_LIST_TYPE = "operateListType";
    public static final int OPERATE_LIST_BY_DAY_TYPE = 0;
    public static final int OPERATE_LIST_BY_MONTH_TYPE = 1;
    public static final int OPERATE_LIST_BY_CUSTOM_TYPE = 2;

    private List<OperateReportBean> mReportList;
    private int mCurrentListType;
    private Map<String ,String> mParams;
    private String mDate;
    private String mType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operate_list, container, false);
    }

    @Override
    protected void dispatchRequest() {
        mParams.put(RequestConstant.KEY_REPORT_DATE,mDate);
        mParams.put(RequestConstant.KEY_REPORT_TYPE,mType);
        mParams.put(RequestConstant.KEY_PAGE,String.valueOf(mPageCount));
        mParams.put(RequestConstant.KEY_PAGE_SIZE,String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REPORT_LIST,mParams);

//        mReportList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            mReportList.add(new OperateReportBean());
//        }
//
//        onGetListSucceeded(1, mReportList);
    }

    @Override
    protected void initView() {
        mCurrentListType = getArguments().getInt(OPERATE_LIST_TYPE);
        mParams = new HashMap<>();
        switch (mCurrentListType){
            case OPERATE_LIST_BY_DAY_TYPE:
                mType = "day";
                break;
            case OPERATE_LIST_BY_MONTH_TYPE:
                mType = "month";
                break;
            case OPERATE_LIST_BY_CUSTOM_TYPE:
                mType = "custom";
                break;
        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }
}
