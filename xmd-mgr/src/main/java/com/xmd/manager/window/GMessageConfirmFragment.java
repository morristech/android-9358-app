package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-23.
 */
public class GMessageConfirmFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_confirm, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.btn_previous_step, R.id.btn_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoEditContentFragment();
                break;
            case R.id.btn_send:
                break;
        }
    }
}
