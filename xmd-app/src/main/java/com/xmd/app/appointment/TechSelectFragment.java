package com.xmd.app.appointment;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.BaseDialogFragment;
import com.xmd.app.Constants;
import com.xmd.app.R;
import com.xmd.app.databinding.FragmentTechSelectBinding;

/**
 * Created by heyangya on 17-5-24.
 * 技师选择界面
 */

public class TechSelectFragment extends BaseDialogFragment {
    public static TechSelectFragment newInstance(String techId) {
        TechSelectFragment fragment = new TechSelectFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_DATA, techId);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentTechSelectBinding mBinding;

    public ObservableBoolean loading;
    public ObservableField<String> loadingError = new ObservableField<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_select, container, false);
        return mBinding.getRoot();
    }

    public void onClickOK() {

    }

    public void onClickClean() {

    }

    public void onClickCancel() {

    }

    public interface Callback {
        void onSelect();
    }
}
