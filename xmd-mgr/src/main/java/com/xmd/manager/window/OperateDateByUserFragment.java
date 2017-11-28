package com.xmd.manager.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateDateByUserFragment extends BaseFragment {

    public static int REQUEST_CODE_CREATE_OPERATE = 0x0001;

    Unbinder unbinder;
    private OperateListFragment mOperateListFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operate_date_by_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mOperateListFragment = new OperateListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OperateListFragment.OPERATE_LIST_TYPE, OperateListFragment.OPERATE_LIST_BY_CUSTOM_TYPE);
        bundle.putString(OperateListFragment.OPERATE_LIST_DATE, "");
        mOperateListFragment.setArguments(bundle);
        initFragmentView();
    }

    private void initFragmentView() {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_operate_by_user, mOperateListFragment);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tv_add_operate)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), NewAddOperateActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_OPERATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && REQUEST_CODE_CREATE_OPERATE == requestCode) {
            mOperateListFragment.notifyDataChanged("");
        }
    }
}
