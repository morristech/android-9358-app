package com.xmd.salary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.salary.adapter.SalaryListAdapter;
import com.xmd.salary.bean.CommissionBean;
import com.xmd.salary.bean.CommissionSumBean;
import com.xmd.salary.httprequest.DataManager;
import com.xmd.salary.httprequest.response.CommissionDetailResult;
import com.xmd.salary.httprequest.response.CommissionSumDataResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-11-20.
 */

public class SalaryListDataFragment extends BaseFragment implements SalaryListAdapter.OnDayItemClickedInterface {


    @BindView(R2.id.salary_detail_list)
    RecyclerView salaryDetailList;
    Unbinder unbinder;

    public static final String INTENT_TIME = "intentTime";
    private List<CommissionBean> mCommissionBeans;
    private List<CommissionSumBean> mCommissionSumBeans;
    private SalaryDataManager mSalaryDataManager;
    private SalaryListAdapter mSalaryAdapter;
    private String mStartTime;
    private String mEndTime;
    private String mFilterType;
    private String timeKey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salary_list_data, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        if (mCommissionBeans == null) {
            mCommissionBeans = new ArrayList<>();
        }
        if (mCommissionSumBeans == null) {
            mCommissionSumBeans = new ArrayList<>();
        }
        mSalaryDataManager = SalaryDataManager.getSalaryDataInstance();
        mSalaryAdapter = new SalaryListAdapter(getActivity(), mCommissionBeans);
        mFilterType = ConstantResource.SALARY_TYPE_ALL;
        mStartTime = DateUtil.getFirstDayOfMonth();
        mEndTime = DateUtil.getCurrentDate();
        getCommissionList(mStartTime, mEndTime, mFilterType);
        salaryDetailList.setLayoutManager(new LinearLayoutManager(getActivity()));
        salaryDetailList.setHasFixedSize(true);
        salaryDetailList.setAdapter(mSalaryAdapter);
        mSalaryAdapter.setOnDayItemClickedListener(this);

    }

    //提成列表
    public void getCommissionList(String startTime, String endTime, String filterType) {
        mStartTime = startTime;
        mEndTime = endTime;
        mFilterType = TextUtils.isEmpty(filterType) ? mFilterType : filterType;
        DataManager.getInstance().getTechCommissionSumData(mStartTime, mEndTime, mFilterType, new NetworkSubscriber<CommissionSumDataResult>() {
            @Override
            public void onCallbackSuccess(CommissionSumDataResult result) {
                mSalaryAdapter.setData(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    public void refreshCommissionDetail(String workDate, String type) {
        getCommissionDetailList(workDate, type);
    }

    //提成明细列表(每天详情)
    private void getCommissionDetailList(String workDate, String type) {

 //       timeKey = workDate + type;
//        if (mSalaryDataManager.commissionContainKey(timeKey)) {
//            mSalaryAdapter.setData(mSalaryDataManager.getCommissionList(timeKey));
//            return;
//        }
        DataManager.getInstance().getTechCommissionDetail("1", "1000", workDate, workDate, type, new NetworkSubscriber<CommissionDetailResult>() {
            @Override
            public void onCallbackSuccess(CommissionDetailResult result) {
                mSalaryAdapter.setData(result.getRespData());
                mSalaryDataManager.setCommissionList(timeKey, result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemViewClicked(Object bean) {
        Intent intent = new Intent(getActivity(), SalaryDayDetailActivity.class);
        intent.putExtra(INTENT_TIME, ((CommissionSumBean) bean).workDate);
        startActivity(intent);
    }
}
