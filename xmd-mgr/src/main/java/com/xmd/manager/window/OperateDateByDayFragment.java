package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.manager.R;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.DateUtils;
import com.xmd.manager.widget.EmptyView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by Lhj on 17-9-11.
 */

public class OperateDateByDayFragment extends BaseFragment {


    @BindView(R.id.tv_operate_time)
    TextView tvOperateTime;
    @BindView(R.id.operate_time_add)
    RelativeLayout operateTimeAdd;
    @BindView(R.id.operate_time_reduce)
    RelativeLayout operateTimeReduce;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    Unbinder unbinder;


    private OperateListFragment mOperateListFragment;
    private String mCurrentMonth;
    private String mSearchMonth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operate_date_by_day, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mCurrentMonth = DateUtil.getCurrentDate();
        mSearchMonth = mCurrentMonth;
        tvOperateTime.setText(mSearchMonth.substring(0,7));
        initFragmentView();
    }

    private void initFragmentView() {
        mOperateListFragment = new OperateListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OperateListFragment.OPERATE_LIST_TYPE, "bgDay");
        mOperateListFragment.setArguments(bundle);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_operate_by_day, mOperateListFragment);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.operate_time_add, R.id.operate_time_reduce})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.operate_time_add:
                if(mSearchMonth.substring(0,7).equals(mCurrentMonth.substring(0,7))){
                    XToast.show(ResourceUtils.getString(R.string.has_none_new_data));
                }else {
                    mSearchMonth = DateUtil.getFirstDayOfNextMonth(mSearchMonth,"yyyy-MM-dd");
                    tvOperateTime.setText(mSearchMonth.substring(0,7));
                }
                break;
            case R.id.operate_time_reduce:
                mSearchMonth = DateUtil.getFirstDayOfLastMonth(mSearchMonth,"yyyy-MM-dd");
                tvOperateTime.setText(mSearchMonth.substring(0,7));
                emptyView.setStatus(EmptyView.Status.Loading);
                emptyView.setLoadingTip("正在加载中...");
                break;

        }
    }
}
