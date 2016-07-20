package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.widget.SideBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/1.
 */
public class ContactsFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {
    @Bind(R.id.customer)
    RadioButton mCustomer;
    @Bind(R.id.mine_store)
    RadioButton myStore;
    @Bind(R.id.radio_group)
    RadioGroup mRadioGroup;
    @Bind(R.id.framelayout)
    FrameLayout framelayout;
    @Bind(R.id.iv_add_friend)
    ImageView btnAddFriend;
    private Fragment currentFragment;
    private Activity ac;
    private ContactsFragment instance;
    boolean isConflict;
    boolean isMyStore = false;

    private ContactsFragment getInstance() {
        if (instance == null) {
            instance = new ContactsFragment();
        }
        return instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_constacts, container, false);
        ac = getActivity();
        return view;
    }

    private void initView() {
        isMyStore = (getActivity().getIntent().hasExtra("isConflict")) ? getActivity().getIntent().getBooleanExtra("isConflict", false) : false;
        mCustomer.setChecked(true);
        mRadioGroup.setOnCheckedChangeListener(this);
        if (isConflict) {
            showMyClubListFragmentFrag();
        } else {
            showCustomerListFragmentFrag();
        }
    }
    @OnClick(R.id.iv_add_friend)
    public void addFriend() {
        Intent intent = new Intent(ac, AddFriendActivity.class);
        ac.startActivity(intent);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.customer:
                showCustomerListFragmentFrag();
                break;
            case R.id.mine_store:
                showMyClubListFragmentFrag();
                break;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }
    public void showCustomerListFragmentFrag() {
        mCustomer.setChecked(true);
        boolean isAdd = false;
        String tag = "customerListFragmentFrag";
        currentFragment = getChildFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            currentFragment = CustomerListFragment.getInstance();

        }
        replaceFragment(tag, currentFragment, isAdd);
    }
    public void showMyClubListFragmentFrag() {
        myStore.setChecked(true);
        boolean isAdd = false;
        String tag = "myClubListFragment";
        currentFragment = getChildFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            currentFragment = MyClubListFragment.getInstance();

        }
        replaceFragment(tag, currentFragment, isAdd);
    }
    public void replaceFragment(String tag, Fragment tempFragment, boolean isAdd) {
        currentFragment = tempFragment;
        FragmentTransaction tran = getChildFragmentManager().beginTransaction();
        tran.replace(R.id.framelayout, tempFragment, tag);
        if (isAdd) {
            tran.addToBackStack(tag);
        }
        tran.commit();
        }

        }
