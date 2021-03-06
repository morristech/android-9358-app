package com.xmd.technician.window;

import android.app.DialogFragment;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.technician.Adapter.TechNoRecyclerViewAdapter;
import com.xmd.technician.R;
import com.xmd.technician.contract.JoinClubContract;
import com.xmd.technician.databinding.FragmentTechNoBinding;
import com.xmd.technician.http.gson.UnusedTechNoListResult;
import com.xmd.technician.model.ClubManager;
import com.xmd.technician.model.TechNo;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-22.
 */

public class TechNoDialogFragment extends DialogFragment {
    public ObservableField<String> mClubName = new ObservableField<>();
    public ObservableBoolean mShowProgressView = new ObservableBoolean();
    public ObservableField<String> mErrorString = new ObservableField<>();
    public ObservableBoolean mDataLoadError = new ObservableBoolean();
    public String mSelectedTechNo;
    private JoinClubContract.Presenter mPresenter;
    private FragmentTechNoBinding mBinding;
    private TechNoRecyclerViewAdapter mAdapter;
    private Subscription mSubscription;
    private String mInviteCode;

    public static TechNoDialogFragment newInstance() {
        return new TechNoDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = RxBus.getInstance()
                .toObservable(UnusedTechNoListResult.class)
                .subscribe(this::handleUnusedTechNoListResult);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.fragment_tech_no, container, false);
        FragmentTechNoBinding mBinding = FragmentTechNoBinding.bind(rootView);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new TechNoRecyclerViewAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);
        mClubName.set("选择技师编号");
        mBinding.setFragment(this);
        loadData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() / 2);
    }

    public void setPresenter(JoinClubContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setParams(String inviteCode, String selectedTechNo) {
        mInviteCode = inviteCode;
        mSelectedTechNo = selectedTechNo;
    }

    public void loadData() {
        mShowProgressView.set(true);
        ClubManager.getInstance().getUnusedTechNos(mInviteCode);
    }

    private void handleUnusedTechNoListResult(UnusedTechNoListResult result) {
        mShowProgressView.set(false);
        List<TechNo> data = new ArrayList<>();
        if (result.statusCode != 200) {
            //错误
            mDataLoadError.set(true);
            mErrorString.set(result.msg);
        } else {
            mDataLoadError.set(false);
            mClubName.set(result.respData.clubName);
            for (UnusedTechNoListResult.ListItem item : result.respData.techNos) {
                if (!TextUtils.isEmpty(item.serialNo) && !TextUtils.isEmpty(item.id)) {
                    data.add(new TechNo(item.serialNo, item.id));
                }
            }
        }
        Collections.sort(data, new Comparator<TechNo>() {
            @Override
            public int compare(TechNo o1, TechNo o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        data.add(TechNo.DEFAULT_TECH_NO);
        mAdapter.setData(mPresenter, data, mSelectedTechNo);
        mAdapter.notifyDataSetChanged();
    }
}
