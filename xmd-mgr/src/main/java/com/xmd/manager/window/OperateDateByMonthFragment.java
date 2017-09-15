package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.manager.R;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.widget.EmptyView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateDateByMonthFragment extends BaseFragment {

    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.tv_operate_time)
    TextView tvOperateTime;
    Unbinder unbinder;
    private View view;
    private OperateListFragment mOperateListFragment;
    private int mCurrentYear;
    private int mSearchYear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_operate_date_by_month, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mCurrentYear = DateUtil.getCurrentYear();
        mSearchYear = mCurrentYear;
        tvOperateTime.setText(String.valueOf(mSearchYear));
        initFragmentView();
    }

    private void initFragmentView() {
        mOperateListFragment = new OperateListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OperateListFragment.OPERATE_LIST_TYPE, "bgMonth");
        mOperateListFragment.setArguments(bundle);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_operate_by_month, mOperateListFragment);
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
                if(mCurrentYear == mSearchYear){
                    XToast.show(ResourceUtils.getString(R.string.has_none_new_data));
                }else{
                    mSearchYear++;
                    tvOperateTime.setText(String.valueOf(mSearchYear));
                }

                break;
            case R.id.operate_time_reduce:
                mSearchYear--;
                tvOperateTime.setText(String.valueOf(mSearchYear));

                break;
        }
    }
}
